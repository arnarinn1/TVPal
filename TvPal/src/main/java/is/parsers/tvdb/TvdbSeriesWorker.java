package is.parsers.tvdb;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import is.contracts.datacontracts.tvdb.Series;
import is.contracts.datacontracts.tvdb.ShowData;
import is.handlers.database.DbEpisodes;
import is.utilities.PictureTask;
import is.webservices.RetrofitUtil;
import is.webservices.TvdbService;
import retrofit.RestAdapter;

public class TvdbSeriesWorker extends AsyncTask<Integer, Void, Boolean>
{
    private Context mContext;

    public TvdbSeriesWorker(Context context)
    {
        this.mContext = context;
    }

    @Override
    protected Boolean doInBackground(Integer... params)
    {
        try
        {
            return GetSeries(params[0]);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    private Boolean GetSeries(int seriesId)
    {
        RestAdapter restAdapter = RetrofitUtil.RestAdapterXMLInstance();

        TvdbService service = restAdapter.create(TvdbService.class);
        ShowData series = service.getSeries(seriesId);

        try
        {
            String posterUrl = String.format("http://thetvdb.com/banners/%s", series.getSeries().getPoster());
            PictureTask pic = new PictureTask();
            byte[] posterByteStream = pic.getByteStreamFromUrl(posterUrl);
            series.getSeries().setPosterByteStream(posterByteStream);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }

        //new DbEpisodes(mContext).InsertFullSeriesInfo(series.getEpisodes(), series.getSeries());

        return true;
    }

    @Override
    protected void onPostExecute(Boolean successful)
    {
        if (successful)
            Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(mContext, "Failure", Toast.LENGTH_SHORT).show();
    }
}

