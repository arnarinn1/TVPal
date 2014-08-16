package is.gui.events;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.slidinglayer.SlidingLayer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import is.contracts.datacontracts.EventData;
import is.gui.base.BaseScheduleFragment;
import is.handlers.adapters.EventAdapter;
import is.tvpal.R;

public class ScheduleFragment extends BaseScheduleFragment implements AdapterView.OnItemClickListener
{
    public static final String EXTRA_SCHEDULE_DAY = "is.activites.scheduleActivites.SCHEDULE_DAY";
    public static final String EXTRA_IMG_RESOURCE = "is.activites.scheduleActivites.IMGRESOURCE";
    public static final String EXTRA_TAB_TITLE = "is.activites.scheduleActivites.TAB_TITLE";

    private EventAdapter mAdapter;
    private Context mContext;
    private SlidingLayer mSlidingLayer;
    private ScrollView mDetailedScrollView;
    private TextView mTitle;
    private TextView mEventStartTime;
    private TextView mEventDuration;
    private TextView mEventDescription;
    private CheckBox mCheckboxNotification;

    public ScheduleFragment() {}

    public static ScheduleFragment newInstance()
    {
        ScheduleFragment fragment = new ScheduleFragment();

        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mContext = activity.getContext();

        Bundle args = getArguments();
        ArrayList<EventData> _todaySchedule = (ArrayList<EventData>)args.getSerializable(EXTRA_SCHEDULE_DAY);
        int imageResourceId = args.getInt(EXTRA_IMG_RESOURCE);

        if (_todaySchedule != null && _todaySchedule.size() > 0)
        {
            AttachViews();

            mAdapter = new EventAdapter(mContext, R.layout.listview_event, _todaySchedule, imageResourceId);

            ((ListView) getView().findViewById(R.id.schedules)).setAdapter(mAdapter);
            ((ListView) getView().findViewById(R.id.schedules)).setOnItemClickListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        final EventData event = mAdapter.getItem(position);

        mCheckboxNotification.setChecked(false);

        if (!mSlidingLayer.isOpened())
        {
            mSlidingLayer.openLayer(true);

            mTitle.setText(event.getTitle());
            mEventDescription.setText(event.getDescription());

            if (!event.getStartTime().isEmpty())
            {
                mEventStartTime.setText(String.format("%s: %s"
                        , getResources().getString(R.string.starting_time)
                        , event.getStartTime()));
            }

            if (!event.getDuration().isEmpty())
            {
                mEventDuration.setText(String.format("%s: %s"
                        , getResources().getString(R.string.duration)
                        , event.getDuration()));
            }

            mCheckboxNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    GetReminder(event);
                }
            });
        }
    }

    private void AttachViews()
    {
        mSlidingLayer = (SlidingLayer) getActivity().findViewById(R.id.sliderDetailedInfo);
        mDetailedScrollView = (ScrollView) getActivity().findViewById(R.id.detailedLayout);

        if (mDetailedScrollView != null)
        {
            mTitle = (TextView) mDetailedScrollView.findViewById(R.id.title);
            mEventStartTime = (TextView) mDetailedScrollView.findViewById(R.id.event_starting);
            mEventDuration = (TextView) mDetailedScrollView.findViewById(R.id.event_duration);
            mEventDescription = (TextView) mDetailedScrollView.findViewById(R.id.event_description);
            mCheckboxNotification = (CheckBox) mDetailedScrollView.findViewById(R.id.getReminder);
        }
    }

    public void GetReminder(EventData mEvent)
    {
        if (!mEvent.getStartTime().isEmpty() && !mEvent.getEventDate().isEmpty())
        {
            String year = mEvent.getEventDate().substring(2,4);
            String month = mEvent.getEventDate().substring(5,7);
            String day = mEvent.getEventDate().substring(8);
            int hour = Integer.parseInt(mEvent.getStartTime().substring(0,2));
            int minute = Integer.parseInt(mEvent.getStartTime().substring(3));

            Date notificationDate = FormatNotificationDate(String.format("%s/%s/%s", month, day, year));

            if (notificationDate != null)
            {
                String[] showInfo = { mEvent.getTitle(), mEvent.getStartTime()};

                Calendar alarmDate = Calendar.getInstance();
                alarmDate.setTime(notificationDate);
                alarmDate.set(Calendar.HOUR_OF_DAY, hour);
                alarmDate.set(Calendar.MINUTE, minute-15);

                Calendar dateNow = Calendar.getInstance();
                dateNow.setTime(new Date());

                // Ask our service to set an alarm for that date, this activity talks to the client that talks to the service
                if(alarmDate.before(dateNow))
                {
                    Toast.makeText(mContext, "Dagskrárliður hefur nú þegar verið sýndur", Toast.LENGTH_SHORT).show();
                }
                else if (alarmDate.after(dateNow))
                {
                    activity.getScheduleClient().setAlarmForNotification(alarmDate, showInfo);
                    // Notify the user what they just did
                    Toast.makeText(mContext, "Áminning sett: " + formatDate(alarmDate.getTime()), Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(mContext, "Villa kom upp við skráningu", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private String formatDate(Date date)
    {
        return new SimpleDateFormat("yyyy-MM-dd kk:mm").format(date);
    }

    private Date FormatNotificationDate(String date)
    {
        try
        {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
            return dateFormat.parse(date);
        }
        catch(ParseException e)
        {
            Toast.makeText(mContext, "Ekki gekk að vista áminningu", Toast.LENGTH_SHORT).show();
        }

        return null;
    }
}