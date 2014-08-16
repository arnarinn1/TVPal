package is.gui.movies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import is.gui.base.BaseActivity;
import is.contracts.datacontracts.trakt.TraktMovieDetailedData;
import is.handlers.adapters.TraktRelatedMoviesAdapter;
import is.tvpal.R;
import is.webservices.ITraktService;
import is.webservices.RetrofitUtil;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RelatedMovieActivity extends BaseActivity
{
    @InjectView(R.id.relatedMovies)     GridView mGridView;
    @InjectView(R.id.progressIndicator) ProgressBar mProgressBar;

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
        ButterKnife.inject(this);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String imdbId = intent.getStringExtra(DetailedMovieActivity.EXTRA_MOVIEID);

        RestAdapter restAdapter = RetrofitUtil.TraktRestAdapterInstance();
        ITraktService service = restAdapter.create(ITraktService.class);

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
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
