package is.handlers.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import is.contracts.datacontracts.EventData;
import is.tvpal.R;

public class EventAdapter extends BaseAdapter
{
    private Context mContext;
    private int mLayoutResourceId;
    private List<EventData> mEvents;
    private int mImageResourceId;

    public EventAdapter(Context context, int layoutResourceId, List<EventData> events, int imageResourceId)
    {
        this.mContext = context;
        this.mLayoutResourceId = layoutResourceId;
        this.mEvents = events;
        this.mImageResourceId = imageResourceId;
    }

    static class EventHolder
    {
        ImageView imgIcon;
        TextView title;
        TextView startTime;
        TextView duration;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        EventHolder holder;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);

            holder = new EventHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.title = (TextView) row.findViewById(R.id.title);
            holder.startTime = (TextView) row.findViewById(R.id.startTime);
            holder.duration = (TextView) row.findViewById(R.id.duration);

            row.setTag(holder);
        }
        else
        {
            holder = (EventHolder)row.getTag();
        }

        EventData schedule = getItem(position);

        holder.imgIcon.setImageResource(mImageResourceId);
        holder.title.setText(schedule.getTitle());
        holder.startTime.setText(String.format("Byrjar: %s",schedule.getStartTime()));
        holder.duration.setText(String.format("Lengd: %s", schedule.getDuration()));

        return row;
    }

    @Override
    public int getCount()
    {
        return (mEvents == null) ? 0 : mEvents.size();
    }

    @Override
    public EventData getItem(int position)
    {
        return mEvents.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return mEvents.indexOf(getItem(position));
    }
}
