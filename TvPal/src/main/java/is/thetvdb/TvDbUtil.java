package is.thetvdb;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.io.IOException;
import java.net.PortUnreachableException;
import java.util.List;
import is.contracts.datacontracts.EpisodeData;
import is.contracts.datacontracts.SeriesData;
import is.handlers.database.DatabaseHandler;
import is.handlers.database.DbEpisodes;
import is.parsers.tvdb.TvDbEpisodeParser;
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

    public void UpdateSeries(int seriesId, ProgressBar progressBar)
    {
        DbEpisodes db = new DbEpisodes(context);
        int lastUpdate = Integer.parseInt(db.GetSeriesLastUpdate(seriesId));
        new UpdateSingleSeriesTask(context, lastUpdate, seriesId, progressBar).execute(String.format("%s%s/all/en.xml", ApiUrl, seriesId));
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
     * A class to update single series
     * It creates a new thread to handle network communications
     * @author Arnar
     * @see android.os.AsyncTask
     */
    private class UpdateSingleSeriesTask extends AsyncTask<String, Void, String>
    {
        private Context context;
        private int lastUpdate;
        private int seriesId;
        private ProgressBar progressBar;

        /**
         * @param context The current application context
         * @param lastUpdate The latest time the series was updated, stored as unix time
         * @param seriesId The id of the series to be updated
         */
        public UpdateSingleSeriesTask(Context context, int lastUpdate, int seriesId, ProgressBar progressBar)
        {
            this.context = context;
            this.lastUpdate = lastUpdate;
            this.seriesId = seriesId;
            this.progressBar = progressBar;
        }

        @Override
        protected String doInBackground(String... urls)
        {
            try
            {
                return UpdateEpisodesOfSeries(urls[0]);
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

        private String UpdateEpisodesOfSeries(String myurl) throws IOException
        {
            try
            {
                ConnectionListener network = new ConnectionListener(context);
                DbEpisodes db = new DbEpisodes(context);

                if (network.isNetworkAvailable())
                {
                    TvDbUpdateParser parser = new TvDbUpdateParser(myurl, lastUpdate);

                    List<EpisodeData> episodes = parser.GetEpisodes();

                    db.UpdateSingleSeries(episodes, parser.getLatestSeriesUpdate(), seriesId);

                    return Integer.toString(episodes.size());
                }
            }
            catch (Exception ex)
            {
                Log.e(getClass().getName(), ex.getMessage());
            }

            return "error";
        }
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

                        db.UpdateSingleSeries(episodes, parser.getLatestSeriesUpdate(), seriesId);
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
