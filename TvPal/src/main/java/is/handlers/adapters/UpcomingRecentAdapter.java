package is.handlers.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.CursorAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import is.datacontracts.tvdb.Episode;
import is.handlers.database.DbEpisodes;
import is.utilities.DateUtil;
import is.tvpal.R;

/**
 * Created by Arnar on 17.10.2013.
 * An adapter to show recent and upcoming shows.
 * It extends CursorAdapter
 * @see android.support.v4.widget.CursorAdapter
 */

public class UpcomingRecentAdapter extends CursorAdapter
{
    private static final int LAYOUT = R.layout.listview_activity;

    private LayoutInflater mLayoutInflater;

    public UpcomingRecentAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder
    {
        ImageView episodeImage;
        TextView episodeName;
        TextView episodeNumber;
        TextView episodeAired;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (!mDataValid)
        {
            throw new IllegalStateException("Only call when cursor is valid");
        }
        if (!mCursor.moveToPosition(position))
        {
            throw new IllegalStateException("Failed to move cursor to position  " + position);
        }

        final ViewHolder holder;

        if (convertView == null)
        {
            convertView = newView(mContext, mCursor, parent);

            holder = new ViewHolder();

            holder.episodeImage = (ImageView) convertView.findViewById(R.id.activityImg);
            holder.episodeName = (TextView) convertView.findViewById(R.id.activityTitle);
            holder.episodeAired = (TextView) convertView.findViewById(R.id.activityAirDate);
            holder.episodeNumber = (TextView) convertView.findViewById(R.id.activityEpisode);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.episodeName.setText(mCursor.getString(Episodes.EpisodeName));
        holder.episodeNumber.setText(String.format("%dx%d", mCursor.getInt(Episodes.Season), mCursor.getInt(Episodes.Episode)));
        holder.episodeAired.setText(DateUtil.FormatDateEpisode(mCursor.getString(Episodes.Aired)));

        Picasso.with(mContext).load(mCursor.getString(Episodes.Thumbnail)).into(holder.episodeImage);

        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {}

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return mLayoutInflater.inflate(LAYOUT, parent, false);
    }

    private interface Episodes
    {
        int EpisodeId = 0;
        int EpisodeName = 1;
        int Aired = 2;
        int SeriesId = 3;
        int Season = 4;
        int Episode = 5;
        int Thumbnail = 6;
    }
}
