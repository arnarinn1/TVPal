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

import butterknife.ButterKnife;
import butterknife.InjectView;
import is.gui.base.BaseFragment;
import is.datacontracts.trakt.TraktEpisodeData;
import is.handlers.adapters.TraktEpisodeAdapter;
import is.presentation.presenter.TraktTrendingPresenter;
import is.presentation.view.ITraktTrendingView;
import is.tvpal.R;

/**
 * A class which displays Trending Shows from Trakt Web Service
 * @author Arnar
 * @see android.support.v4.app.Fragment
 */
public class TrendingEpisodesFragment extends BaseFragment implements ITraktTrendingView
{
    @InjectView(R.id.trendingTrakt)     GridView mGridView;
    @InjectView(R.id.progressIndicator) ProgressBar mProgressBar;
    @InjectView(R.id.traktNoResults)    TextView mNoResults;

    private TraktTrendingPresenter mPresenter;

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

        mPresenter.GetTrendingEpisodes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mPresenter = new TraktTrendingPresenter(this);

        View rootView = inflater.inflate(R.layout.fragment_trakt_trending, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void SetViewVisibility(int progressBarVisibility, int noResultsVisibility)
    {
        mProgressBar.setVisibility(progressBarVisibility);
        mNoResults.setVisibility(noResultsVisibility);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void SetAdapter(List<T> episodes)
    {
        TraktEpisodeAdapter mAdapter = new TraktEpisodeAdapter(activity.getContext(), R.layout.listview_trakt_episodes, (List<TraktEpisodeData>) episodes);
        mGridView.setAdapter(mAdapter);
    }

    @Override
    public Context GetContext()
    {
        return activity.getContext();
    }
}