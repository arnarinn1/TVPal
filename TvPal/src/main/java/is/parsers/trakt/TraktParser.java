package is.parsers.trakt;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import is.contracts.datacontracts.trakt.TraktComment;
import is.contracts.datacontracts.trakt.TraktMovieDetailedData;
import is.contracts.datacontracts.trakt.TraktEpisodeData;
import is.contracts.datacontracts.trakt.TraktMovieData;
import is.webservices.RestClient;

/**
 * Created by Arnar on 16.11.2013.
 */
public class TraktParser
{
    private static final String TraktSearchUrl = "http://api.trakt.tv/search/movies.json/f0e3af66061e47b3243e25ed7b6443ca/";
    private static final String TraktSummaryUrl = "http://api.trakt.tv/movie/summary.json/f0e3af66061e47b3243e25ed7b6443ca/";

    public List<TraktMovieData> SearchMovie(String movie)
    {
        try
        {
            RestClient client = new RestClient();
            String json = client.Get(String.format("%s%s", TraktSearchUrl, movie));

            Type listType = new TypeToken<ArrayList<TraktMovieData>>() {}.getType();

            return new Gson().fromJson(json, listType);
        }
        catch (Exception ex)
        {
            Log.e(getClass().getName(), "Error searching for movies");
        }

        return null;
    }

    public TraktMovieDetailedData GetMovieDetailed(String movieId)
    {
        try
        {
            RestClient client = new RestClient();
            String json = client.Get(String.format("%s%s", TraktSummaryUrl, movieId));

            return new Gson().fromJson(json, TraktMovieDetailedData.class);
        }
        catch (Exception ex)
        {
            Log.e(getClass().getName(), "Error getting detailed movie");
        }

        return null;
    }
}
