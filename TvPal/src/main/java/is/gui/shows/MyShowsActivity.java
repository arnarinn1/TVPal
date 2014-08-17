package is.gui.shows;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.GridView;
import android.widget.ProgressBar;

import is.gui.base.BaseActivity;
import is.gui.dialogs.RemoveShowDialog;
import is.handlers.adapters.MyShowsAdapter;
import is.presentation.view.IMyShowsView;
import is.presentation.presenter.MyShowsPresenter;
import is.tvpal.R;

/**
 * Displays all series that a user has added to his shows
 * @author Arnar
 */

public class MyShowsActivity extends BaseActivity implements AdapterView.OnItemClickListener,
                                                             RemoveShowDialog.OnRemoveShowListener,
                                                             IMyShowsView
{
    public static final String EXTRA_SERIESID = "is.activities.showActivities.SERIESID";
    public static final String EXTRA_NAME = "is.actvities.showActivities.SERIESNAME";

    private MyShowsAdapter mAdapter;
    private ProgressBar mProgressBar;

    private MyShowsPresenter mPresenter;

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
        mPresenter = new MyShowsPresenter(this, this);

        mProgressBar = (ProgressBar) findViewById(R.id.progressUpdateShows);
        GridView mGridView = (GridView) findViewById(R.id.myshows_series);
        mGridView.setOnItemClickListener(this);

        mAdapter = new MyShowsAdapter(this, mPresenter.GetCursorMyShows(), 0);
        mGridView.setAdapter(mAdapter);

        registerForContextMenu(mGridView);

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void UpdateCursor()
    {
        mAdapter.swapCursor(mPresenter.GetCursorMyShows());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public ProgressBar GetProgressBar()
    {
        return mProgressBar;
    }

    @Override
    public MyShowsAdapter GetAdapter()
    {
        return mAdapter;
    }

    @Override
    public Context GetContext()
    {
        return this;
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
                mPresenter.UpdateShow(position);
                return true;
            case R.id.seenAllEpisodes:
                mPresenter.SetAllEpisodesAsSeen(position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
                mPresenter.UpdateAllShows();
                return true;
            case R.id.reloadPosters:
                mPresenter.ReloadPosters();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        mPresenter.RemoveShow(seriesId, seriesTitle);
    }
}