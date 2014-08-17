package is.presentation.presenter;

import java.util.List;

import is.datacontracts.ITrakt;
import is.datacontracts.trakt.TraktEpisodeData;
import is.datacontracts.trakt.TraktMovieData;
import is.presentation.view.ITraktTrendingView;
import is.system.TraktSystem;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TraktTrendingPresenter extends BasePresenter<ITraktTrendingView, ITrakt>
{
    public TraktTrendingPresenter(ITraktTrendingView view)
    {
        super(view, new TraktSystem());
    }

    public void GetTrendingMovies()
    {
        Callback<List<TraktMovieData>> callback = Callback();
        System.TraktService().getTrendingMovies(callback);
    }

    public void GetTrendingEpisodes()
    {
        Callback<List<TraktEpisodeData>> callback = Callback();
        System.TraktService().getTrendingShows(callback);
    }

    public <T> Callback<List<T>> Callback()
    {
        return new Callback<List<T>> ()
        {
            @Override
            public void success(List<T> movies, Response response)
            {
                if (movies == null)
                {
                    View.SetViewVisibility(android.view.View.GONE, android.view.View.VISIBLE);
                }
                else
                {
                    View.SetAdapter(movies);
                    View.SetViewVisibility(android.view.View.GONE, android.view.View.GONE);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError)
            {
                View.SetViewVisibility(android.view.View.GONE, android.view.View.VISIBLE);
            }
        };
    }
}
