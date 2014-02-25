package is.gui.movies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import is.gui.base.BaseActivity;
import is.contracts.datacontracts.trakt.TraktMovieData;
import is.contracts.datacontracts.trakt.TraktMovieDetailedData;
import is.handlers.database.DbMovies;
import is.tvpal.R;
import is.utilities.ExternalIntents;
import is.utilities.PictureTask;
import is.utilities.StringUtil;
import is.webservices.RetrofitUtil;
import is.webservices.TraktService;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DetailedMovieActivity extends BaseActivity
{
    public static final String EXTRA_MOVIEID = "is.activites.movieActivities.EXTRA_MOVIEID";

    private ProgressBar mProgressBar;
    private TextView mOverview;
    private TextView mTitle;
    private ImageView mPoster;
    private TextView mRuntime;
    private TextView mGenres;
    private Button mYoutubeIntent;
    private Button mImdbIntent;
    private LinearLayout mLayout;
    private TextView mActors;
    private TextView mDirectors;
    private Button mTraktIntent;
    private TextView mRating;
    private TextView mReleaseYear;
    private Button mTraktCommentsActivity;
    private Button mWatchlist;
    private Button mRelatedMovies;

    private Context getContext() { return this;}

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_movie);

        Initialize();
    }

    private void Initialize()
    {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        mLayout = (LinearLayout) findViewById(R.id.layoutShowMore);
        mProgressBar = (ProgressBar) findViewById(R.id.progressIndicator);
        mOverview = (TextView) findViewById(R.id.movieOverview);
        mTitle = (TextView) findViewById(R.id.movieTitle);
        mPoster = (ImageView) findViewById(R.id.moviePoster);
        mRuntime = (TextView) findViewById(R.id.movieRuntime);
        mGenres = (TextView) findViewById(R.id.movieGenres);
        mYoutubeIntent = (Button) findViewById(R.id.startYoutubeIntent);
        mImdbIntent = (Button) findViewById(R.id.startImdbIntent);
        mActors = (TextView) findViewById(R.id.movieActors);
        mDirectors = (TextView) findViewById(R.id.movieDirectors);
        mTraktIntent = (Button) findViewById(R.id.startTraktIntent);
        mRating = (TextView) findViewById(R.id.movieRating);
        mReleaseYear = (TextView) findViewById(R.id.movieReleaseYear);
        mTraktCommentsActivity = (Button) findViewById(R.id.startTraktComments);
        mWatchlist = (Button) findViewById(R.id.traktWatchlist);
        mRelatedMovies = (Button) findViewById(R.id.startTraktRelatedMovies);

        String movieId = intent.getStringExtra(TrendingMoviesFragment.EXTRA_MOVIEID);
        String moviePoster = intent.getStringExtra(TrendingMoviesFragment.EXTRA_MOVIEPOSTER);

        RestAdapter restAdapter = RetrofitUtil.RetrofitRestAdapterInstance();
        TraktService service = restAdapter.create(TraktService.class);

        service.getMovie(movieId, movieCallback);

        new PosterTask().execute(moviePoster);
    }

    Callback<TraktMovieDetailedData> movieCallback = new Callback<TraktMovieDetailedData>() {
        @Override
        public void success(final TraktMovieDetailedData movie, Response response)
        {
            if (movie != null)
            {
                setTitle(movie.getTitle());
                Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_layout);
                mLayout.startAnimation(fadeInAnimation);
                mLayout.setVisibility(View.VISIBLE);

                mOverview.setText(movie.getOverview());
                mTitle.setText(movie.getTitle());
                mRuntime.setText(String.format("%d min", movie.getRuntime()));
                mGenres.setText(StringUtil.JoinArrayToString(movie.getGenres()));
                mActors.setText(StringUtil.GetTraktPeople(movie.getPeople().getActors()));
                mDirectors.setText(StringUtil.GetTraktPeople(movie.getPeople().getDirectors()));
                mRating.setText(movie.getRating().getPercentage() + " %");
                mReleaseYear.setText(String.format("%d", movie.getReleaseYear()));

                mYoutubeIntent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(movie.getTrailer()));
                            startActivity(intent);
                        } catch (Exception ex) {
                            Toast.makeText(getContext(), "Couldn't open trailer", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                mImdbIntent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ExternalIntents.StartIMDBIntent(getContext(), movie.getImdbId());
                    }
                });

                mTraktIntent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            StartUriIntent(movie.getTraktUrl());
                        }
                        catch (Exception ex) {
                            Toast.makeText(getContext(), "Couldn't open IMDB", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                mTraktCommentsActivity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent = new Intent(getContext(), TraktCommentsActivity.class);
                        intent.putExtra(TraktCommentsActivity.EXTRA_TITLE, movie.getTitle());
                        intent.putExtra(TraktCommentsActivity.EXTRA_ImdbId, movie.getImdbId());
                        intent.putExtra(TraktCommentsActivity.EXTRA_TYPE, "movie");
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });

                mWatchlist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        try
                        {
                            TraktMovieData movieData = new TraktMovieData();
                            movieData.setTitle(movie.getTitle());
                            movieData.setImdbId(movie.getImdbId());
                            movieData.setImage(movie.getImage());
                            movieData.setOverview(movie.getOverview());

                            DbMovies db = new DbMovies(getContext());
                            db.AddMovieToWatchList(movieData);

                            Toast.makeText(getContext(), String.format("Added %s to your watchlist", movie.getTitle()), Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception ex)
                        {
                            Log.e(getContext().getClass().getName(), "Error adding movie to watchlist");
                        }
                    }
                });

                mRelatedMovies.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), RelatedMovieActivity.class);
                        intent.putExtra(EXTRA_MOVIEID, movie.getImdbId());
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
            }
        }

        @Override
        public void failure(RetrofitError retrofitError)
        {
            //Do something ?
        }
    };

    private class PosterTask extends AsyncTask<String, Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... strings)
        {
            return GetPoster(strings[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            if (bitmap != null)
                mPoster.setImageBitmap(bitmap);

            mProgressBar.setVisibility(View.GONE);
        }

        private Bitmap GetPoster(String posterUrl)
        {
            try
            {
                return PictureTask.getBitmapFromUrl(posterUrl);
            }
            catch (Exception ex)
            {
                Log.e(getClass().getName(), ex.getMessage());
            }

            return null;
        }
    }

    private void StartUriIntent(String uri)
    {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(uri));
        startActivity(i);
    }
}
