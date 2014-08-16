package is.handlers.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import is.datacontracts.cinema.CinemaMovie;
import is.tvpal.R;

/**
 * Created by Arnar
 *
 * This class implements an Adapter that can be used in both ListView
 * (by implementing the specialized ListAdapter interface}
 * and Spinner (by implementing the specialized SpinnerAdapter interface.
 * It extends BaseAdapter.
 *
 * @see android.widget.BaseAdapter
 */
public class CinemaAdapter extends BaseAdapter
{
    private Context context;
    private int layoutResourceId;
    private List<CinemaMovie> movies;

    public CinemaAdapter(Context context, int layoutResourceId, List<CinemaMovie> movies)
    {
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.movies = movies;
    }

    static class CinemaHolder
    {
        TextView title;
        ImageView image;
        TextView imdb;
        TextView restricted;
        int position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        final CinemaHolder holder;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new CinemaHolder();
            holder.title = (TextView) row.findViewById(R.id.cinemaTitle);
            holder.image = (ImageView) row.findViewById(R.id.cinemaPicture);
            holder.imdb = (TextView) row.findViewById(R.id.cinemaImdbScore);
            holder.restricted = (TextView) row.findViewById(R.id.cinemaRestricted);

            row.setTag(holder);
        }
        else
        {
            holder = (CinemaHolder)row.getTag();
        }

        final CinemaMovie movie = getItem(position);

        holder.position = position;
        holder.title.setText(movie.getTitle());
        holder.imdb.setText(movie.getImdb());
        holder.restricted.setText(movie.getRestricted());

        Picasso.with(context)
               .load(movie.getImageUrl())
               .resizeDimen(R.dimen.poster_width, R.dimen.poster_height)
               .into(holder.image);

        return row;
    }

    @Override
    public int getCount()
    {
        return (movies == null) ? 0 : movies.size();
    }

    @Override
    public CinemaMovie getItem(int position)
    {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return movies.indexOf(getItem(position));
    }
}
