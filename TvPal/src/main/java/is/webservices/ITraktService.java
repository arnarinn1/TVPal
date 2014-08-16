package is.webservices;

import java.util.List;

import is.contracts.datacontracts.trakt.TraktComment;
import is.contracts.datacontracts.trakt.TraktEpisodeData;
import is.contracts.datacontracts.trakt.TraktMovieData;
import is.contracts.datacontracts.trakt.TraktMovieDetailedData;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface ITraktService
{
    @GET("/shows/trending.json/{apiKey}")
    void getTrendingShows(Callback<List<TraktEpisodeData>> callback);

    @GET("/movies/trending.json/{apiKey}")
    void getTrendingMovies(Callback<List<TraktMovieData>> callback);

    @GET("/movie/related.json/{apiKey}/{imdbId}")
    void getRelatedMovies(@Path("imdbId") String imdbId,
                          Callback<List<TraktMovieDetailedData>> callback);

    @GET("/{type}/comments.json/{apiKey}/{imdbId}")
    void getComments(@Path("type") String type,
                     @Path("imdbId") String imdbId,
                     Callback<List<TraktComment>> callback);

    @GET("/search/movies.json/{apiKey}/{movie}")
    void getSearchMovie(@Path("movie") String movie,
                        Callback<List<TraktMovieData>> callback);

    @GET("/movie/summary.json/{apiKey}/{imdbId}")
    void getMovie(@Path("imdbId") String imdbId,
                  Callback<TraktMovieDetailedData> callback);
}
