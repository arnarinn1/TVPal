package is.handlers.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import is.contracts.datacontracts.trakt.TraktMovieDetailedData;
import is.tvpal.R;
import is.utilities.PictureTask;
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
        TextView title;
        TextView overview;
        ImageView poster;
        TextView runtime;
        TextView rating;
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

            holder.title = (TextView) row.findViewById(R.id.relatedMovieTitle);
            holder.overview = (TextView) row.findViewById(R.id.relatedMovieOverview);
            holder.poster = (ImageView) row.findViewById(R.id.relatedMoviePoster);
            holder.rating = (TextView) row.findViewById(R.id.relatedMovieRating);
            holder.runtime = (TextView) row.findViewById(R.id.relatedMovieRuntime);

            row.setTag(holder);
        }
        else
        {
            holder = (RelatedMovieHolder)row.getTag();
        }

        final TraktMovieDetailedData movie = getItem(position);

        holder.overview.setText(String.format("%s (%s)", movie.getOverview(), movie.getReleaseYear()));
        holder.title.setText(movie.getTitle());

        holder.rating.setText(movie.getRating().getPercentage() + "%");
        holder.runtime.setText(movie.getRuntime() + " min");

        Picasso.with(mContext)
                .load(movie.getImage()
                .getPoster())
                .skipMemoryCache()
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

