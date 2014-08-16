package is.webservices;

import is.contracts.datacontracts.cinema.CinemaResults;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface IApisService
{
    @GET("/{endpoint}")
    void getMovies(@Path("endpoint") String endpoint,
                   Callback<CinemaResults> movies);
}
