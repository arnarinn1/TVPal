package is.gui.movies;

import android.content.Intent;
import android.os.AsyncTask;
import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import is.gui.base.BaseFragment;
import is.contracts.datacontracts.trakt.TraktMovieData;
import is.handlers.adapters.TraktMoviesAdapter;
import is.handlers.database.DbMovies;
import is.parsers.trakt.TraktParser;
import is.tvpal.R;
import is.utilities.StringUtil;
import is.webservices.RetrofitUtil;
import is.webservices.TraktService;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * An fragment to search for movies
 * @author Arnar
 * @see is.gui.shows.MyShowsActivity
 */

public class SearchMovieFragment extends BaseFragment implements AdapterView.OnItemClickListener
{
    private EditText mEditSearch;
    private Context mContext;
    private TraktMoviesAdapter mAdapter;
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private TraktService mService;

    public SearchMovieFragment() {}

    public static SearchMovieFragment newInstance()
    {
        SearchMovieFragment fragment = new SearchMovieFragment();

        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mContext = activity.getContext();
        mEditSearch = (EditText) getView().findViewById(R.id.traktSearchMovie);
        mGridView = (GridView) getView().findViewById(R.id.traktMovieResults);
        mGridView.setOnItemClickListener(this);
        mProgressBar = (ProgressBar) getView().findViewById(R.id.traktProgressIndicator);

        registerForContextMenu(mGridView);

        RestAdapter restAdapter = RetrofitUtil.RetrofitRestAdapterInstance();
        mService = restAdapter.create(TraktService.class);

        InitializeEditTextSearch();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_search_movie, container, false);
    }

    private void InitializeEditTextSearch()
    {
        mEditSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();

                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); //Close the keyboard

                    return true;
                }
                return false;
            }
        });
    }

    private void performSearch()
    {
        String userEntry = mEditSearch.getText().toString();
        EnforceViewBehavior(null, View.VISIBLE);

        mService.getSearchMovie(userEntry, searchMovieCallback);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        if (getUserVisibleHint())
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
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
            DbMovies db = new DbMovies(mContext);
            db.AddMovieToWatchList(movie);

            Toast.makeText(mContext, String.format("Added %s to your watchlist", movie.getTitle()), Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            Log.e(getClass().getName(), ex.getMessage());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        TraktMovieData movie = mAdapter.getItem(position);

        String posterUrl = StringUtil.formatTrendingPosterUrl(movie.getImage().getPoster(), "-300");

        Intent intent = new Intent(mContext, DetailedMovieActivity.class);
        intent.putExtra(TrendingMoviesFragment.EXTRA_MOVIEID, movie.getImdbId());
        intent.putExtra(TrendingMoviesFragment.EXTRA_MOVIEPOSTER, posterUrl);
        startActivity(intent);
    }

    Callback<List<TraktMovieData>> searchMovieCallback = new Callback<List<TraktMovieData>>() {
        @Override
        public void success(List<TraktMovieData> movies, Response response)
        {
            mAdapter = new TraktMoviesAdapter(mContext, R.layout.listview_trakt_movies, movies);
            EnforceViewBehavior(mAdapter, View.GONE);
        }

        @Override
        public void failure(RetrofitError retrofitError)
        {
            EnforceViewBehavior(null, View.GONE);
        }
    };

    private void EnforceViewBehavior(TraktMoviesAdapter adapter, int progressBarVisibility)
    {
        mGridView.setAdapter(adapter);
        mProgressBar.setVisibility(progressBarVisibility);
    }
}