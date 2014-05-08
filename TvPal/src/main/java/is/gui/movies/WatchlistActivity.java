package is.gui.movies;

import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.slidinglayer.SlidingLayer;
import com.squareup.picasso.Picasso;

import is.gui.base.TraktThumbnailSize;
import is.gui.base.BaseFragmentActivity;
import is.handlers.adapters.WatchListAdapter;
import is.handlers.database.DbMovies;
import is.tvpal.R;
import is.utilities.StringUtil;

public class WatchlistActivity extends BaseFragmentActivity implements AdapterView.OnItemClickListener
{
    private WatchListAdapter mAdapter;
    private GridView mGridView;
    private SlidingLayer mSlidingLayer;
    private ImageView mWatchListPoster;
    private TextView mWatchlistDesc;
    private Button mCloseButton;
    private TextView mEmptyWatchList;

    private DbMovies mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        Initialize();
    }

    private void Initialize()
    {
        getActionBar().setDisplayHomeAsUpEnabled(true);

        AttachViews();
        AttachEventListeners();

        registerForContextMenu(mGridView);
        mDb = new DbMovies(this);

        if (!(mDb.GetWatchlistCursor().moveToFirst()) || mDb.GetWatchlistCursor().getCount() ==0)
            mEmptyWatchList.setVisibility(View.VISIBLE);

        mAdapter = new WatchListAdapter(this, mDb.GetWatchlistCursor(), 0);
        mGridView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.watchlist, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;

        switch (item.getItemId())
        {
            case R.id.remove_from_watchlist:
                RemoveMovieFromWatchlist(position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void RemoveMovieFromWatchlist(int position)
    {
        Cursor movie = (Cursor) mAdapter.getItem(position);
        new DbMovies(this).RemoveMovieFromWatchList(movie.getString(0));
        UpdateCursor();
    }

    private void UpdateCursor()
    {
        mAdapter.swapCursor(mDb.GetWatchlistCursor());
        mAdapter.notifyDataSetChanged();

        if (!(mDb.GetWatchlistCursor().moveToFirst()) || mDb.GetWatchlistCursor().getCount() ==0)
            mEmptyWatchList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
    {
        Cursor movie = (Cursor) mAdapter.getItem(position);

        if (!mSlidingLayer.isOpened())
        {
            mSlidingLayer.openLayer(true);

            final String posterUrl = StringUtil.formatTrendingPosterUrl(movie.getString(3), TraktThumbnailSize.Small);

            Picasso.with(this)
                    .load(posterUrl)
                    .into(mWatchListPoster);

            mWatchlistDesc.setText(movie.getString(2));

            mCloseButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mSlidingLayer.closeLayer(true);

                    RemoveMovieFromWatchlist(position);
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                if (mSlidingLayer.isOpened())
                {
                    mSlidingLayer.closeLayer(true);
                    return true;
                }
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    private void AttachViews()
    {
        mGridView = (GridView) findViewById(R.id.trendingTrakt);
        mSlidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer1);
        mWatchListPoster = (ImageView) findViewById(R.id.watchlistTitle);
        mWatchlistDesc = (TextView) findViewById(R.id.watchListDesc);
        mCloseButton = (Button) findViewById(R.id.closeSlider);
        mEmptyWatchList = (TextView) findViewById(R.id.emptyWatchList);
    }

    private void AttachEventListeners()
    {
        mGridView.setOnItemClickListener(this);
    }
}
