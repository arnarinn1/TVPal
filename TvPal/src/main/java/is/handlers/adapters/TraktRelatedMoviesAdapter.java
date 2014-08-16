package is.handlers.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;
import is.datacontracts.trakt.TraktMovieDetailedData;
import is.gui.base.TraktThumbnailSize;
import is.tvpal.R;
import is.utilities.StringUtil;

public class TraktRelatedMoviesAdapter extends BaseAdapter
{
    private Context mContext;
    private int layoutResourceId;
    private List<TraktMovieDetailedData> mMovies;

    public TraktRelatedMoviesAdapter(Context context, int layoutResourceId, List<TraktMovieDetailedData> movies)
    {
        this.mContext = context;
        this.layoutResourceId = layoutResourceId;
        this.mMovies = movies;
    }

    static class RelatedMovieHolder
    {
        ImageView poster;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        final RelatedMovieHolder holder;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RelatedMovieHolder();

            holder.poster = (ImageView) row.findViewById(R.id.relatedMoviePoster);

            row.setTag(holder);
        }
        else
        {
            holder = (RelatedMovieHolder)row.getTag();
        }

        final TraktMovieDetailedData movie = getItem(position);

        Picasso.with(mContext)
                .load(StringUtil.formatTrendingPosterUrl(movie.getImage().getPoster(), TraktThumbnailSize.Medium))
                .into(holder.poster);

        return row;
    }

    @Override
    public int getCount()
    {
        return (mMovies == null) ? 0 : mMovies.size();
    }

    @Override
    public TraktMovieDetailedData getItem(int position)
    {
        return mMovies.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return mMovies.indexOf(getItem(position));
    }
}

