package is.gui.movies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

import java.util.List;

import is.gui.base.BaseActivity;
import is.contracts.datacontracts.trakt.TraktMovieDetailedData;
import is.handlers.adapters.TraktRelatedMoviesAdapter;
import is.tvpal.R;
import is.webservices.RetrofitUtil;
import is.webservices.TraktService;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RelatedMovieActivity extends BaseActivity
{
    private GridView mGridView;
    private ProgressBar mProgressBar;

    private Context getContext() { return this;}

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_movies);

        Initialize();
    }

    private void Initialize()
    {
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String imdbId = intent.getStringExtra(DetailedMovieActivity.EXTRA_MOVIEID);

        mGridView = (GridView) findViewById(R.id.relatedMovies);
        mProgressBar = (ProgressBar) findViewById(R.id.progressIndicator);

        RestAdapter restAdapter = RetrofitUtil.RetrofitRestAdapterInstance();
        TraktService service = restAdapter.create(TraktService.class);

        service.getRelatedMovies(imdbId, relatedMoviesCallback);
    }

    Callback<List<TraktMovieDetailedData>> relatedMoviesCallback = new Callback<List<TraktMovieDetailedData>>()
    {
        @Override
        public void success(List<TraktMovieDetailedData> movies, Response response)
        {
            if (movies == null || movies.size() != 0)
                mGridView.setAdapter(new TraktRelatedMoviesAdapter(getContext(), R.layout.listview_related_movie, movies));

            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void failure(RetrofitError retrofitError)
        {
            mProgressBar.setVisibility(View.GONE);
        }
    };

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
