package is.gui.schedules;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.slidinglayer.SlidingLayer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import is.gui.base.BaseFragmentActivity;
import is.contracts.datacontracts.EventData;
import is.parsers.cache.SchedulesCache;
import is.parsers.schedules.RuvScheduleParser;
import is.utilities.ConnectionListener;
import is.utilities.DateUtil;
import is.tvpal.R;

/**
 * Created by Arnar
 *
 * This class handles the activity to show Rúv events.
 * It extends FragmentActivity, it shows ListActivity for each Fragment
 * It implements ActionBar.TabListener to handle swipe between views.
 * @author Arnar
 * @see import android.support.v4.app.FragmentActivity;
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DisplayRuvActivity extends BaseFragmentActivity
{
    public static final String RuvUrl = "http://muninn.ruv.is/files/xml/ruv/";

    private List<EventData> _events;
    private String _workingDate;
    private ViewPager mViewPager;
    private ProgressBar mProgressBar;
    private TextView mNoResults;
    private PagerSlidingTabStrip mTabStrip;
    private SlidingLayer mSlidingLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_strip_schedules);

        Initialize();
    }

    private void Initialize()
    {
        _workingDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        mProgressBar = (ProgressBar) findViewById(R.id.progressSchedules);
        mNoResults = (TextView) findViewById(R.id.noSchedules);
        mTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mSlidingLayer = (SlidingLayer) findViewById(R.id.sliderDetailedInfo);
        String ruvUrl = String.format("%s%s", RuvUrl, DateUtil.GetCorrectRuvUrlFormat(5));
        new DownloadRuvSchedules(this).execute(ruvUrl);

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void CreateTabViews()
    {
        SchedulePagerAdapter mScheduleAdapter = new SchedulePagerAdapter(getSupportFragmentManager(), this);

        mViewPager = (ViewPager) findViewById(R.id.pagerSchedules);
        mViewPager.setAdapter(mScheduleAdapter);
        mTabStrip.setViewPager(mViewPager);
    }

    public class SchedulePagerAdapter extends FragmentStatePagerAdapter
    {
        private Context cxt;

        public SchedulePagerAdapter(FragmentManager fm, Context cxt)
        {
            super(fm);
            this.cxt = cxt;
        }

        @Override
        public Fragment getItem(int position)
        {
            Fragment fragment = ScheduleFragment.newInstance();
            Bundle args = new Bundle();

            String date;

            if (position == 0)
                date = _workingDate;
            else
                date = DateUtil.AddDaysToDate(_workingDate, position);

            ArrayList<EventData> _todaySchedule = new ArrayList<EventData>();

            for (EventData e : _events)
            {
                if (e.getEventDate().equalsIgnoreCase(date))
                    _todaySchedule.add(e);
            }

            args.putInt(ScheduleFragment.EXTRA_IMG_RESOURCE, R.drawable.ruv_svartur_64);
            args.putSerializable(ScheduleFragment.EXTRA_SCHEDULE_DAY, _todaySchedule);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount()
        {
            //TODO: Hardcoded length for now
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            //TODO: Find better way to set tab titles, this is very gay
            if (position == 0)
                return DateUtil.GetDateFormatForTabs(cxt, _workingDate);
            else
            {
                String date = DateUtil.AddDaysToDate(_workingDate, position);
                return DateUtil.GetDateFormatForTabs(cxt, date);
            }
        }
    }

    private class DownloadRuvSchedules extends AsyncTask<String, Void, Boolean>
    {
        private Context mContext;

        public DownloadRuvSchedules(Context context)
        {
            this.mContext = context;
        }

        @Override
        protected Boolean doInBackground(String... urls)
        {
            try
            {
                return GetSchedules(urls[0]);
            }
            catch (IOException e)
            {
                Log.e(getClass().getName(), e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPreExecute()
        {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean successful)
        {
            if (successful)
            {
                CreateTabViews();
            }
            else
            {
                mNoResults.setVisibility(View.VISIBLE);
                boolean networkStatus = new ConnectionListener(mContext).isNetworkAvailable();

                if (!networkStatus)
                    Toast.makeText(mContext, "Turn on network", Toast.LENGTH_SHORT).show();
            }

            mProgressBar.setVisibility(View.GONE);
        }

        private boolean GetSchedules(String myurl) throws IOException
        {
            try
            {
                String ruvSchedules = new SchedulesCache(mContext).GetSchedules(myurl, SchedulesCache.RuvSchedules,
                                                         SchedulesCache.RuvLatestUpdate);
                RuvScheduleParser parser = new RuvScheduleParser(ruvSchedules);

                if (ruvSchedules == null)
                    return false;

                _events = parser.GetSchedules();

                _workingDate = _events.get(0).getEventDate();

                return true;
            }
            catch (Exception ex)
            {
                Log.e(getClass().getName(), ex.getMessage());
            }

            return false;
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
}