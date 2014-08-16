package is.webservices;

import is.datacontracts.tvdb.SeriesSearch;
import is.datacontracts.tvdb.ShowData;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface ITvdbService
{
    @GET("/{apiKey}/series/{seriesId}/{type}")
    ShowData getSeries(@Path("seriesId") int seriesId);

    @GET("/GetSeries.php")
    void getSearchSeries(@Query("seriesname") String seriesname,
                         Callback<SeriesSearch> callback);
}