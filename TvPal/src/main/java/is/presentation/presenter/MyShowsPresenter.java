package is.presentation.presenter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import is.handlers.database.DbEpisodes;
import is.handlers.database.IEpisodes;
import is.presentation.view.IMyShowsView;
import is.tvdb.TvdbSeriesUpdateAllWorker;
import is.tvdb.TvdbSeriesUpdateWorker;
import is.utilities.ConnectionListener;

public class MyShowsPresenter extends BasePresenter<IMyShowsView, IEpisodes>
{
    public MyShowsPresenter(Context ctx, IMyShowsView view)
    {
        super(view, new DbEpisodes(ctx));
    }

    public void UpdateAllShows()
    {
        boolean networkAvailable = ConnectionListener.isNetworkAvailable(View.GetContext());

        if(networkAvailable)
            new TvdbSeriesUpdateAllWorker(View.GetContext(), View.GetProgressBar()).execute();
        else
            Toast.makeText(View.GetContext(), "Connect to a network to update shows", Toast.LENGTH_SHORT).show();
    }

    public void UpdateShow(int position)
    {
        Cursor show = (Cursor) View.GetAdapter().getItem(position);

        boolean networkAvailable = ConnectionListener.isNetworkAvailable(View.GetContext());

        if(networkAvailable)
            new TvdbSeriesUpdateWorker(View.GetContext(), View.GetProgressBar()).execute(show.getInt(0));
        else
            Toast.makeText(View.GetContext(), "Connect to a network to update show", Toast.LENGTH_SHORT).show();
    }

    public Cursor GetCursorMyShows()
    {
        return System.GetCursorMyShows();
    }

    public void SetAllEpisodesAsSeen(int position)
    {
        Cursor cursor = (Cursor) View.GetAdapter().getItem(position);
        System.SetSeriesSeen(cursor.getInt(0));
    }

    public void RemoveShow(int selectedShow, String seriesTitle)
    {
        try
        {
            System.RemoveShow(selectedShow);
            View.UpdateCursor();
            Toast.makeText(View.GetContext(), String.format("Removed %s from your shows", seriesTitle), Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            Log.e(getClass().getName(), ex.getMessage());
        }
    }

    public void ReloadPosters()
    {

    }
}
