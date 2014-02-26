package is.webservices;

import is.contracts.datacontracts.tvdb.ShowData;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Arnar on 25.2.2014.
 */
public interface TvdbService
{
    @GET("/{apiKey}/series/{seriesId}/{type}")
    ShowData getSeries(@Path("seriesId") int seriesId);
}