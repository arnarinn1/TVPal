package is.webservices;

import is.contracts.datacontracts.tvdb.SeriesSearch;
import is.contracts.datacontracts.tvdb.ShowData;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface TvdbService
{
    @GET("/{apiKey}/series/{seriesId}/{type}")
    ShowData getSeries(@Path("seriesId") int seriesId);

    @GET("/GetSeries.php")
    void getSearchSeries(@Query("seriesname") String seriesname,
                         Callback<SeriesSearch> callback);
}