package is.parsers.tvdb;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import is.contracts.datacontracts.tvdb.ShowData;
import is.handlers.database.DbEpisodes;
import is.utilities.PictureTask;
import is.webservices.ITvdbService;
import is.webservices.RetrofitUtil;
import retrofit.RestAdapter;

public class TvdbSeriesWorker extends AsyncTask<Integer, Void, Boolean>
{
    private Context mContext;
    private String mSeriesTitle;

    public TvdbSeriesWorker(Context context, String title)
    {
        this.mContext = context;
        this.mSeriesTitle = title;
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

        ITvdbService service = restAdapter.create(ITvdbService.class);
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

        //TODO: Refactor InsertFullSeriesInfo to throw exceptions not catch them
        new DbEpisodes(mContext).InsertFullSeriesInfo(series.getEpisodes(), series.getSeries());

        return true;
    }

    @Override
    protected void onPreExecute()
    {
        Toast.makeText(mContext, String.format("%s will be added to your shows", mSeriesTitle),Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(Boolean successful)
    {
        if (successful)
            Toast.makeText(mContext, String.format("Added %s to your shows", mSeriesTitle), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(mContext, "Whoops, Something went wrong", Toast.LENGTH_SHORT).show();
    }
}

