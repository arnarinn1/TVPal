package is.thetvdb;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import is.contracts.datacontracts.EpisodeData;
import is.handlers.database.DbEpisodes;
import is.parsers.tvdb.TvDbUpdateParser;
import is.utilities.ConnectionListener;

/**
 * A class to handle Network operations for thetvdb api.
 * @author Arnar
 */
public class TvDbUtil
{
    public static String ApiUrl = "http://thetvdb.com/api/9A96DA217CEB03E7/series/";

    private Context context;

    public TvDbUtil(Context context)
    {
        this.context = context;
    }

    public void UpdateAllSeries(ProgressBar progressBar)
    {
        new UpdateAllSeriesTask(context, progressBar).execute();
    }

    public void SetAllEpisodesOfSeriesSeen(int seriesId)
    {
        DbEpisodes db = new DbEpisodes(context);
        db.SetSeriesSeen(seriesId);
    }

    /**
     * A class to update episodes of all Series
     * @author Arnar
     * @see android.os.AsyncTask
     */
    private class UpdateAllSeriesTask extends AsyncTask<String, Void, String>
    {
        private Context context;
        private ProgressBar progressBar;

        /**
         * @param context The current application context
         */
        public UpdateAllSeriesTask(Context context, ProgressBar progressBar)
        {
            this.context = context;
            this.progressBar = progressBar;
        }

        @Override
        protected String doInBackground(String... urls)
        {
            try
            {
                return UpdateEpisodesOfSeries();
            }
            catch (IOException e)
            {
                return "Unable to retrieve web page. URL may be invalid";
            }
        }

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (result.equalsIgnoreCase("error"))
                Toast.makeText(context, "Whoops, something went wrong...", Toast.LENGTH_SHORT).show();

            progressBar.setVisibility(View.INVISIBLE);
        }

        private String UpdateEpisodesOfSeries() throws IOException
        {
            try
            {
                DbEpisodes db = new DbEpisodes(context);
                ConnectionListener network = new ConnectionListener(context);
                List<Integer> seriesIds = db.GetAllSeriesIds();

                for(Integer seriesId : seriesIds)
                {
                    if (network.isNetworkAvailable())
                    {
                        String apiUrl = String.format("%s%s/all/en.xml", ApiUrl, seriesId);
                        String latestUpdate = db.GetSeriesLastUpdate(seriesId);

                        TvDbUpdateParser parser = new TvDbUpdateParser(apiUrl, Integer.parseInt(latestUpdate));

                        List<EpisodeData> episodes = parser.GetEpisodes();

                        //db.UpdateSingleSeries(episodes, parser.getLatestSeriesUpdate(), seriesId);
                    }
                }

                db.close();
                return Integer.toString(seriesIds.size());
            }
            catch (Exception ex)
            {
                Log.e(getClass().getName(), ex.getMessage());
            }

            return "error";
        }
    }
}
