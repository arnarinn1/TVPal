package is.gui.movies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;

import is.contracts.datacontracts.trakt.TraktMovieData;
import is.gui.base.BaseActivity;
import is.contracts.datacontracts.trakt.TraktComment;
import is.handlers.adapters.TraktCommentAdapter;
import is.handlers.adapters.TraktMoviesAdapter;
import is.parsers.trakt.TraktParser;
import is.tvpal.R;
import is.webservices.RetrofitUtil;
import is.webservices.TraktService;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Arnar on 5.12.2013.
 */
public class TraktCommentsActivity extends BaseActivity
{
    public static final String EXTRA_TITLE = "is.activites.movieActivities.EXTRA_MOVIE";
    public static final String EXTRA_ImdbId = "is.activites.movieActivities.EXTRA_MOVIEID";
    public static final String EXTRA_TYPE = "is.activites.movieActivities.EXTRA_TYPE";

    private ListView mListView;
    private ProgressBar mProgressBar;
    private TextView mNoResults;
    private TextView mMovieTitle;

    private String mTitle;
    private Context getContext() { return this; }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trakt_comments);

        Initialize();
    }

    private void Initialize()
    {
        mListView = (ListView) findViewById(R.id.trakt_comments);
        mProgressBar = (ProgressBar) findViewById(R.id.progressIndicator);
        mNoResults = (TextView) findViewById(R.id.noResults);
        mMovieTitle = (TextView) findViewById(R.id.comment_movieid);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        mTitle = intent.getStringExtra(EXTRA_TITLE);
        String imdbId = intent.getStringExtra(EXTRA_ImdbId);
        String type = intent.getStringExtra(EXTRA_TYPE);

        RestAdapter restAdapter = RetrofitUtil.RetrofitRestAdapterInstance();
        TraktService service = restAdapter.create(TraktService.class);

        service.getComments(type, imdbId, commentsCallback);
    }

    Callback<List<TraktComment>> commentsCallback = new Callback<List<TraktComment>>()
    {
        @Override
        public void success(List<TraktComment> comments, Response response)
        {
            if(comments == null || comments.size() == 0)
            {
                mProgressBar.setVisibility(View.INVISIBLE);
                mNoResults.setVisibility(View.VISIBLE);
            }
            else
            {
                mMovieTitle.setText(mTitle);
                mListView.setAdapter(new TraktCommentAdapter(getContext(), R.layout.listview_trakt_comments, comments));
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void failure(RetrofitError retrofitError)
        {
            mProgressBar.setVisibility(View.INVISIBLE);
            mNoResults.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
