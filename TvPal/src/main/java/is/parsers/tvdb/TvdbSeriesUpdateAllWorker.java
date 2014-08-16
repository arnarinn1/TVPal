package is.parsers.tvdb;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import is.contracts.datacontracts.tvdb.Episode;
import is.contracts.datacontracts.tvdb.ShowData;
import is.handlers.database.DbEpisodes;
import is.utilities.ConnectionListener;
import is.webservices.ITvdbService;
import is.webservices.RetrofitUtil;
import retrofit.RestAdapter;

/**
 * Created by Arnar on 27.3.2014.
 */
public class TvdbSeriesUpdateAllWorker extends AsyncTask<Integer, Void, Boolean>
{
    private Context mContext;
    private ProgressBar mProgressBar;

    public TvdbSeriesUpdateAllWorker(Context context, ProgressBar mProgressBar)
    {
        this.mContext = context;
        this.mProgressBar = mProgressBar;
    }

    @Override
    protected Boolean doInBackground(Integer... params)
    {
        try
        {
            return GetEpisodes();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPreExecute()
    {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Boolean status)
    {
        if (!status)
            Toast.makeText(mContext, "Couldn't update shows", Toast.LENGTH_SHORT).show();

        mProgressBar.setVisibility(View.GONE);
    }

    private Boolean GetEpisodes()
    {
        RestAdapter restAdapter = RetrofitUtil.RestAdapterXMLInstance();
        ITvdbService service = restAdapter.create(ITvdbService.class);

        DbEpisodes db = new DbEpisodes(mContext);
        ConnectionListener network = new ConnectionListener(mContext);
        List<Integer> seriesIds = db.GetAllSeriesIds();

        for(Integer seriesId : seriesIds)
        {
            if (network.isNetworkAvailable())
            {
                int lastLocalUpdate = db.GetSeriesLastUpdate(seriesId);
                //int lastLocalUpdate = 1396979565; //just for testing

                ShowData series = service.getSeries(seriesId);

                List<Episode>  episodesToUpdate = new ArrayList<Episode>();
                for(Episode e : series.getEpisodes())
                {
                    if (e.getLastUpdated() > lastLocalUpdate)
                        episodesToUpdate.add(e);
                }

                if (!episodesToUpdate.isEmpty())
                    db.UpdateSingleSeries(episodesToUpdate, series.getSeries().getLastUpdated(), seriesId);
            }
        }

        return true;
    }
}
