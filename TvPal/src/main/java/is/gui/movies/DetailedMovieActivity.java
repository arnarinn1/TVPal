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

import butterknife.ButterKnife;
import butterknife.InjectView;
import is.gui.base.BaseActivity;
import is.contracts.datacontracts.trakt.TraktMovieData;
import is.contracts.datacontracts.trakt.TraktMovieDetailedData;
import is.handlers.database.DbMovies;
import is.tvpal.R;
import is.utilities.ExternalIntents;
import is.utilities.PictureTask;
import is.utilities.StringUtil;
import is.webservices.ITraktService;
import is.webservices.RetrofitUtil;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DetailedMovieActivity extends BaseActivity
{
    public static final String EXTRA_MOVIEID = "is.activites.movieActivities.EXTRA_MOVIEID";

    @InjectView(R.id.progressIndicator)       ProgressBar mProgressBar;
    @InjectView(R.id.movieOverview)           TextView mOverview;
    @InjectView(R.id.movieTitle)              TextView mTitle;
    @InjectView(R.id.moviePoster)             ImageView mPoster;
    @InjectView(R.id.movieRuntime)            TextView mRuntime;
    @InjectView(R.id.movieGenres)             TextView mGenres;
    @InjectView(R.id.startYoutubeIntent)      Button mYoutubeIntent;
    @InjectView(R.id.startImdbIntent)         Button mImdbIntent;
    @InjectView(R.id.layoutShowMore)          LinearLayout mLayout;
    @InjectView(R.id.movieActors)             TextView mActors;
    @InjectView(R.id.movieDirectors)          TextView mDirectors;
    @InjectView(R.id.startTraktIntent)        Button mTraktIntent;
    @InjectView(R.id.movieRating)             TextView mRating;
    @InjectView(R.id.movieReleaseYear)        TextView mReleaseYear;
    @InjectView(R.id.startTraktComments)      Button mTraktCommentsActivity;
    @InjectView(R.id.traktWatchlist)          Button mWatchlist;
    @InjectView(R.id.startTraktRelatedMovies) Button mRelatedMovies;

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
        ButterKnife.inject(this);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        String movieId = intent.getStringExtra(TrendingMoviesFragment.EXTRA_MOVIEID);
        String moviePoster = intent.getStringExtra(TrendingMoviesFragment.EXTRA_MOVIEPOSTER);

        RestAdapter restAdapter = RetrofitUtil.TraktRestAdapterInstance();
        ITraktService service = restAdapter.create(ITraktService.class);

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
                mActors.setText(StringUtil.GetTraktActorsAndCharacters(movie.getPeople().getActors()));
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
