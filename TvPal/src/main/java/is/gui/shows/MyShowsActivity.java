package is.gui.shows;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import is.gui.base.BaseActivity;
import is.gui.dialogs.RemoveShowDialog;
import is.handlers.database.DbEpisodes;
import is.handlers.adapters.MyShowsAdapter;
import is.tvdb.TvdbSeriesUpdateAllWorker;
import is.tvdb.TvdbSeriesUpdateWorker;
import is.tvpal.R;
import is.utilities.ConnectionListener;

/**
 * Displays all series that a user has added to his shows
 * @author Arnar
 */

public class MyShowsActivity extends BaseActivity implements AdapterView.OnItemClickListener, RemoveShowDialog.OnRemoveShowListener
{
    public static final String EXTRA_SERIESID = "is.activities.showActivities.SERIESID";
    public static final String EXTRA_NAME = "is.actvities.showActivities.SERIESNAME";

    private DbEpisodes mDB;
    private GridView mGridView;
    private MyShowsAdapter mAdapter;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myshows);

        Initialize();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void Initialize()
    {
        mDB = new DbEpisodes(this);

        mProgressBar = (ProgressBar) findViewById(R.id.progressUpdateShows);
        mGridView = (GridView) findViewById(R.id.myshows_series);
        mGridView.setOnItemClickListener(this);

        mAdapter = new MyShowsAdapter(this, mDB.GetCursorMyShows(), 0);
        mGridView.setAdapter(mAdapter);

        registerForContextMenu(mGridView);

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void UpdateCursor()
    {
        mAdapter.swapCursor(mDB.GetCursorMyShows());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_shows_context_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;

        Cursor show = (Cursor) mAdapter.getItem(position);

        switch (item.getItemId())
        {
            case R.id.removeShow:
                RemoveShowDialog(show.getInt(0), show.getString(1));
                return true;
            case R.id.updateShow:
                UpdateShow(position);
                return true;
            case R.id.seenAllEpisodes:
                SeenAllEpisodes(position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void RemoveShow(int selectedShow, String seriesTitle)
    {
        try
        {
            mDB.RemoveShow(selectedShow);
            UpdateCursor();
            Toast.makeText(this, String.format("Removed %s from your shows", seriesTitle), Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            Log.e(getClass().getName(), ex.getMessage());
        }
    }

    private void UpdateShow(int position)
    {
        Cursor show = (Cursor) mAdapter.getItem(position);

        boolean networkAvailable = ConnectionListener.isNetworkAvailable(this);

        if(networkAvailable)
            new TvdbSeriesUpdateWorker(this, mProgressBar).execute(show.getInt(0));
        else
            Toast.makeText(this, "Connect to a network to update show", Toast.LENGTH_SHORT).show();
    }

    private void SeenAllEpisodes(int position)
    {
        Cursor cursor = (Cursor) mAdapter.getItem(position);

        DbEpisodes db = new DbEpisodes(this);
        db.SetSeriesSeen(cursor.getInt(0));
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        Cursor show = (Cursor) mAdapter.getItem(position);

        Intent intent = new Intent(this, SeriesActivity.class);
        intent.putExtra(EXTRA_SERIESID, show.getInt(0));
        intent.putExtra(EXTRA_NAME, show.getString(1));
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            case R.id.updateAllShows:
                UpdateAllShows();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void UpdateAllShows()
    {
        boolean networkAvailable = ConnectionListener.isNetworkAvailable(this);

        if(networkAvailable)
            new TvdbSeriesUpdateAllWorker(this, mProgressBar).execute();
        else
            Toast.makeText(this, "Connect to a network to update shows", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_shows_menu, menu);
        return true;
    }

    private void RemoveShowDialog(int position, String seriesTitle)
    {
        RemoveShowDialog removeShowDialog = RemoveShowDialog.newInstance(position, seriesTitle);
        removeShowDialog.show(getFragmentManager(), "tag_removeshow");
    }

    @Override
    public void onRemoveShow(int seriesId, String seriesTitle)
    {
        RemoveShow(seriesId, seriesTitle);
    }
}