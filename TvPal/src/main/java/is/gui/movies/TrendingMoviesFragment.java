package is.gui.movies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import is.gui.base.TraktThumbnailSize;
import is.gui.base.BaseFragment;
import is.contracts.datacontracts.trakt.TraktMovieData;
import is.handlers.adapters.TraktMoviesAdapter;
import is.handlers.database.DbMovies;
import is.tvpal.R;
import is.utilities.ListUtil;
import is.utilities.StringUtil;
import is.webservices.ITraktService;
import is.webservices.RetrofitUtil;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TrendingMoviesFragment extends BaseFragment implements AdapterView.OnItemClickListener
{
    public static final String EXTRA_MOVIEID = "is.activites.movieActivities.MOVIEID";
    public static final String EXTRA_MOVIEPOSTER = "is.activites.movieActivities.MOVIEPOSTER";

    @InjectView(R.id.trendingTrakt)     GridView mGridView;
    @InjectView(R.id.progressIndicator) ProgressBar mProgressBar;
    @InjectView(R.id.traktNoResults)    TextView mNoResults;

    private TraktMoviesAdapter mAdapter;

    public TrendingMoviesFragment() {}

    public static TrendingMoviesFragment newInstance()
    {
        TrendingMoviesFragment fragment = new TrendingMoviesFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mGridView.setOnItemClickListener(this);

        registerForContextMenu(mGridView);

        RestAdapter restAdapter = RetrofitUtil.TraktRestAdapterInstance();
        ITraktService service = restAdapter.create(ITraktService.class);

        service.getTrendingMovies(trendingMoviesCallback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_trakt_trending, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        TraktMovieData data = mAdapter.getItem(position);
        String posterUrl = StringUtil.formatTrendingPosterUrl(data.getImage().getPoster(), TraktThumbnailSize.Medium);

        Intent intent = new Intent(activity.getContext(), DetailedMovieActivity.class);
        intent.putExtra(EXTRA_MOVIEID, data.getImdbId());
        intent.putExtra(EXTRA_MOVIEPOSTER, posterUrl);
        startActivity(intent);
        ((Activity)activity.getContext()).overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        if (getUserVisibleHint())
        {
            AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
            int position = info.position;

            switch (item.getItemId())
            {
                case R.id.add_to_watchlist:
                    AddMovieToWatchList(position);
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }

        return false;
    }

    private void AddMovieToWatchList(int position)
    {
        try
        {
            TraktMovieData movie = mAdapter.getItem(position);
            DbMovies db = new DbMovies(activity.getContext());
            db.AddMovieToWatchList(movie);

            Toast.makeText(activity.getContext(), String.format("Added %s to your watchlist", movie.getTitle()), Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            Log.e("TrendingMoviesFragment", ex.getMessage());
        }
    }

    Callback<List<TraktMovieData>> trendingMoviesCallback = new Callback<List<TraktMovieData>>()
    {
        @Override
        public void success(List<TraktMovieData> movies, Response response)
        {
            if (movies == null)
            {
                SetViewVisibility(View.GONE, View.VISIBLE);
            }
            else
            {
                ListUtil.FilterGenre(movies, "Horror");
                mAdapter = new TraktMoviesAdapter(activity.getContext(), R.layout.listview_trakt_movies, movies);
                mGridView.setAdapter(mAdapter);
                SetViewVisibility(View.GONE, View.GONE);
            }
        }

        @Override
        public void failure(RetrofitError retrofitError)
        {
            SetViewVisibility(View.GONE, View.VISIBLE);
        }
    };

    private void SetViewVisibility(int progressBarVisibility, int noResultsVisibility)
    {
        mProgressBar.setVisibility(progressBarVisibility);
        mNoResults.setVisibility(noResultsVisibility);
    }
}
