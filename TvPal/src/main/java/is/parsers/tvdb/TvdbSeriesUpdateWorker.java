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
import is.webservices.ITvdbService;
import is.webservices.RetrofitUtil;
import retrofit.RestAdapter;

public class TvdbSeriesUpdateWorker extends AsyncTask<Integer, Void, Boolean>
{
    private Context mContext;
    private ProgressBar mProgressBar;

    public TvdbSeriesUpdateWorker(Context context, ProgressBar mProgressBar)
    {
        this.mContext = context;
        this.mProgressBar = mProgressBar;
    }

    @Override
    protected Boolean doInBackground(Integer... params)
    {
        try
        {
            return GetEpisodes(params[0]);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
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
            Toast.makeText(mContext, "Couldn't update show", Toast.LENGTH_SHORT).show();

        mProgressBar.setVisibility(View.GONE);
    }

    private Boolean GetEpisodes(int seriesId)
    {
        RestAdapter restAdapter = RetrofitUtil.RestAdapterXMLInstance();

        int lastLocalUpdate = new DbEpisodes(mContext).GetSeriesLastUpdate(seriesId);
        //int lastLocalUpdate = 1391882207; //just for testing

        ITvdbService service = restAdapter.create(ITvdbService.class);
        ShowData series = service.getSeries(seriesId);

        List<Episode>  episodesToUpdate = new ArrayList<Episode>();
        for(Episode e : series.getEpisodes())
        {
            if (e.getLastUpdated() > lastLocalUpdate)
                episodesToUpdate.add(e);
        }

        if (!episodesToUpdate.isEmpty())
            new DbEpisodes(mContext).UpdateSingleSeries(episodesToUpdate, series.getSeries().getLastUpdated(), seriesId);

        return true;
    }
}
