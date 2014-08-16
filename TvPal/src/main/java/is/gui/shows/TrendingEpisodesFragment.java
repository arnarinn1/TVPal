package is.gui.shows;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import is.gui.base.BaseFragment;
import is.datacontracts.trakt.TraktEpisodeData;
import is.handlers.adapters.TraktEpisodeAdapter;
import is.tvpal.R;
import is.webservices.ITraktService;
import is.webservices.RetrofitUtil;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A class which displays Trending Shows from Trakt Web Service
 * @author Arnar
 * @see android.support.v4.app.Fragment
 */
public class TrendingEpisodesFragment extends BaseFragment
{
    private Context mContext;
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private TextView mNoResults;

    public TrendingEpisodesFragment() {}

    public static TrendingEpisodesFragment newInstance()
    {
        TrendingEpisodesFragment fragment = new TrendingEpisodesFragment();

        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mContext = activity.getContext();
        mProgressBar = (ProgressBar) getView().findViewById(R.id.progressIndicator);
        mNoResults = (TextView) getView().findViewById(R.id.traktNoResults);
        mGridView = (GridView) getView().findViewById(R.id.trendingTrakt);

        RestAdapter restAdapter = RetrofitUtil.TraktRestAdapterInstance();
        ITraktService service = restAdapter.create(ITraktService.class);

        service.getTrendingShows(trendingShowsCallback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_trakt_trending, container, false);
    }

    Callback<List<TraktEpisodeData>> trendingShowsCallback = new Callback<List<TraktEpisodeData>>()
    {
        @Override
        public void success(List<TraktEpisodeData> shows, Response response)
        {
            if (shows == null)
            {
                SetViewVisibility(View.GONE, View.VISIBLE);
            }
            else
            {
                mGridView.setAdapter(new TraktEpisodeAdapter(mContext, R.layout.listview_trakt_episodes, shows));
                SetViewVisibility(View.GONE, View.GONE);
            }
        }

        @Override
        public void failure(RetrofitError retrofitError)
        {
            SetViewVisibility(View.GONE, View.VISIBLE);
        }
    };

    private void SetViewVisibility(int progressBarVisibility, int noResultsVisibility)
    {
        mProgressBar.setVisibility(progressBarVisibility);
        mNoResults.setVisibility(noResultsVisibility);
    }
}
