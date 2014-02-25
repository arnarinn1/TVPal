package is.gui.movies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.haarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;

import is.contracts.datacontracts.cinema.CinemaResults;
import is.gui.base.BaseActivity;
import is.contracts.datacontracts.cinema.CinemaMovie;
import is.handlers.adapters.CinemaAdapter;
import is.tvpal.R;
import is.webservices.ApisService;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CinemaActivity extends BaseActivity implements AdapterView.OnItemClickListener
{
    public static final String EXTRA_MOVIE = "is.activites.cinemaActivities.MOVIE";
    public static final String ApisUrl = "http://apis.is";

    private CinemaAdapter mAdapter;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private TextView mNoResults;

    private Context getContext() { return this;}

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema);

        Initialize();
    }

    private void Initialize()
    {
        mListView = (ListView) findViewById(R.id.cinemaSchedules);
        mListView.setOnItemClickListener(this);
        mProgressBar = (ProgressBar) findViewById(R.id.progressIndicator);
        mNoResults = (TextView) findViewById(R.id.cinemaNoResults);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ApisUrl)
                .build();

        ApisService service = restAdapter.create(ApisService.class);
        service.getMovies("cinema", cinemaCallback);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        CinemaMovie movie = mAdapter.getItem(position);

        Intent intent = new Intent(this, DetailedCinemaActivity.class);
        intent.putExtra(EXTRA_MOVIE, movie);
        startActivity(intent);
    }

    Callback<CinemaResults> cinemaCallback = new Callback<CinemaResults>() {
        @Override
        public void success(CinemaResults movies, Response response)
        {
            mAdapter = new CinemaAdapter(getContext(), R.layout.listview_cinema_schedules, movies.getMovies());

            //Animations boy
            ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(mAdapter);
            scaleInAnimationAdapter.setAbsListView(mListView);
            mListView.setAdapter(scaleInAnimationAdapter);

            mProgressBar.setVisibility(View.GONE);
            mNoResults.setVisibility(View.GONE);
        }

        @Override
        public void failure(RetrofitError retrofitError)
        {
            mNoResults.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    };
}
