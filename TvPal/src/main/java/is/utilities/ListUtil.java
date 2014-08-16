package is.utilities;

import java.util.ArrayList;
import java.util.List;

import is.datacontracts.trakt.TraktMovieData;

/**
 * Created by Arnar on 11.5.2014.
 */
public class ListUtil
{
    public static ArrayList<TraktMovieData> FilterGenre(List<TraktMovieData> movies, String genre)
    {
        ArrayList<TraktMovieData> filteredMovies = new ArrayList<TraktMovieData>();

        for(TraktMovieData movie : movies)
        {
            if (movie.getGenres().contains(genre))
            {
                filteredMovies.add(movie);
            }
        }

        return filteredMovies;
    }
}
