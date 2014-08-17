package is.gui.movies;

import android.app.Activity;
import android.content.Context;
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
import is.datacontracts.trakt.TraktMovieData;
import is.handlers.adapters.TraktMoviesAdapter;
import is.handlers.database.DbMovies;
import is.presentation.presenter.TraktTrendingPresenter;
import is.presentation.view.ITraktTrendingView;
import is.tvpal.R;
import is.utilities.StringUtil;

public class TrendingMoviesFragment extends BaseFragment implements AdapterView.OnItemClickListener, ITraktTrendingView
{
    public static final String EXTRA_MOVIEID = "is.activites.movieActivities.MOVIEID";
    public static final String EXTRA_MOVIEPOSTER = "is.activites.movieActivities.MOVIEPOSTER";

    @InjectView(R.id.trendingTrakt)     GridView mGridView;
    @InjectView(R.id.progressIndicator) ProgressBar mProgressBar;
    @InjectView(R.id.traktNoResults)    TextView mNoResults;

    private TraktMoviesAdapter mAdapter;

    private TraktTrendingPresenter mPresenter;

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

        mPresenter.GetTrendingMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mPresenter = new TraktTrendingPresenter(this);

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

    @Override
    public void SetViewVisibility(int progressBarVisibility, int noResultsVisibility)
    {
        mProgressBar.setVisibility(progressBarVisibility);
        mNoResults.setVisibility(noResultsVisibility);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void SetAdapter(List<T> movies)
    {
        mAdapter = new TraktMoviesAdapter(activity.getContext(), R.layout.listview_trakt_movies, (List<TraktMovieData>)movies);
        mGridView.setAdapter(mAdapter);
    }

    @Override
    public Context GetContext()
    {
        return activity.getContext();
    }
}