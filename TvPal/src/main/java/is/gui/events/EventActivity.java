package is.gui.events;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.slidinglayer.SlidingLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import is.gui.MainActivity;
import is.contracts.datacontracts.EventData;
import is.gui.base.BaseNavDrawer;
import is.utilities.ConnectionListener;
import is.utilities.DateUtil;
import is.tvpal.R;
import is.webservices.IScheduleService;
import is.webservices.RetrofitUtil;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class EventActivity extends BaseScheduleActivity
{
    @InjectView(R.id.progressSchedules) ProgressBar mProgressBar;
    @InjectView(R.id.noSchedules) TextView mNoResults;
    @InjectView(R.id.tabs) PagerSlidingTabStrip mTabStrip;
    @InjectView(R.id.sliderDetailedInfo) SlidingLayer mSlidingLayer;

    private Map<String, ArrayList<EventData>> mEventsByDate;

    private static int mNumberOfTabs;
    private String mToday;
    private static int mImageResource;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tab_strip_schedules);
        ButterKnife.inject(this);
        Initialize();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void Initialize()
    {
        RestAdapter restAdapter = RetrofitUtil.ScheduleRestAdapterInstance();
        IScheduleService scheduleClient = restAdapter.create(IScheduleService.class);

        mEventsByDate = new HashMap<String, ArrayList<EventData>>();

        Intent intent = getIntent();
        mImageResource = intent.getIntExtra(BaseNavDrawer.EXTRA_IMAGE_RESOURCE, R.drawable.stod2_64);

        setTitle(intent.getStringExtra(MainActivity.EXTRA_TITLE));

        getActionBar().setDisplayHomeAsUpEnabled(true);

        scheduleClient.getSchedules(intent.getStringExtra(MainActivity.EXTRA_STOD2), eventCallback);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void CreateTabViews()
    {
        SchedulePagerAdapter mScheduleAdapter = new SchedulePagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.pagerSchedules);
        mViewPager.setAdapter(mScheduleAdapter);
        mTabStrip.setViewPager(mViewPager);
    }

    private void SetNumberOfTabs(List<EventData> events)
    {
        HashSet<String> tabs = new HashSet<String>();

        for(EventData event : events)
        {
            if (!tabs.contains(event.getEventDate()))
                tabs.add(event.getEventDate());
        }

        mNumberOfTabs = tabs.size();
    }

    private void PopulateEventsByDate(List<EventData> events)
    {
        for (EventData event : events)
        {
            if (!mEventsByDate.containsKey(event.getEventDate()))
            {
                mEventsByDate.put(event.getEventDate(), new ArrayList<EventData>());
                mEventsByDate.get(event.getEventDate()).add(event);
            }
            else
            {
                mEventsByDate.get(event.getEventDate()).add(event);
            }
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

    public class SchedulePagerAdapter extends FragmentStatePagerAdapter
    {
        public SchedulePagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            Fragment fragment = ScheduleFragment.newInstance();
            Bundle args = new Bundle();

            String date;

            if (position == 0)
                date = mToday;
            else
                date = DateUtil.AddDaysToDate(mToday, position);

            String tabTitle = DateUtil.GetDateFormatForTabs(getContext(), date);

            args.putString(ScheduleFragment.EXTRA_TAB_TITLE, tabTitle);
            args.putInt(ScheduleFragment.EXTRA_IMG_RESOURCE, mImageResource);
            args.putSerializable(ScheduleFragment.EXTRA_SCHEDULE_DAY, mEventsByDate.get(date));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount()
        {
            return mNumberOfTabs;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            Fragment fm = getItem(position);
            Bundle args = fm.getArguments();
            return args.getString(ScheduleFragment.EXTRA_TAB_TITLE);
        }
    }

    Callback<List<EventData>> eventCallback = new Callback<List<EventData>>()
    {
        @Override
        public void success(List<EventData> events, Response response)
        {
            mProgressBar.setVisibility(View.GONE);
            mToday = events.get(0).getEventDate();
            SetNumberOfTabs(events);
            PopulateEventsByDate(events);
            CreateTabViews();
        }

        @Override
        public void failure(RetrofitError retrofitError)
        {
            mNoResults.setVisibility(View.VISIBLE);

            boolean networkStatus = new ConnectionListener(getContext()).isNetworkAvailable();

            if (!networkStatus)
                Toast.makeText(getContext(), "Turn on network", Toast.LENGTH_SHORT).show();
        }
    };
}