package is.gui.shows;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import is.gui.base.BaseFragment;
import is.contracts.datacontracts.EpisodeData;
import is.handlers.database.DbEpisodes;
import is.tvpal.R;

/**
 * A fragment that shows a detailed information for a show
 * @author Arnar
 * @see Fragment
 */
public class EpisodeFragment extends BaseFragment
{
    public static final String EPISODE_FRAGMENT = "is.activites.showActivities.episode_fragment";

    private ImageView poster;
    private DbEpisodes db;

    public EpisodeFragment() {}

    public static EpisodeFragment newInstance(EpisodeData episode)
    {
        EpisodeFragment fragment = new EpisodeFragment();

        Bundle args = new Bundle();
        args.putSerializable(EPISODE_FRAGMENT, episode);

        fragment.setArguments(args);
        fragment.setRetainInstance(true);

        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        db = new DbEpisodes(activity.getContext());

        Bundle args = getArguments();
        final EpisodeData episode = (EpisodeData) args.getSerializable(EPISODE_FRAGMENT);

        if (getView() != null)
        {
            CheckBox episodeSeenCbx = (CheckBox) getView().findViewById(R.id.episodeSeen);

            episodeSeenCbx.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    db.UpdateEpisodeSeen(episode.getEpisodeId());

                    int seen = 0;
                    if(episode.getSeen() == 0)
                        seen = 1;

                    episode.setSeen(seen);
                }
            });

            final String guestStars = episode.getGuestStars().equals("") ? "No Guest Stars" : episode.getGuestStars();

            episodeSeenCbx.setChecked(episode.getSeen() == 1);

            ((TextView) getView().findViewById(R.id.episodeTitle)).setText(episode.getEpisodeName());
            ((TextView) getView().findViewById(R.id.episodeAired)).setText(String.format("Aired: %s", episode.getAired()));
            ((TextView) getView().findViewById(R.id.episodeSeason)).setText(String.format("Season: %s", episode.getSeasonNumber()));
            ((TextView) getView().findViewById(R.id.episodeOverview)).setText(episode.getOverview());
            ((TextView) getView().findViewById(R.id.episodeDirector)).setText(episode.getDirector());
            ((TextView) getView().findViewById(R.id.episodeRating)).setText(episode.getRating());
            ((TextView) getView().findViewById(R.id.episodeGuestStars)).setText(guestStars);
            poster = (ImageView) getView().findViewById(R.id.episodePicture);

            String imageUrl = String.format("http://thetvdb.com/banners/episodes/%s/%s.jpg", episode.getSeriesId(), episode.getEpisodeId());

            Picasso.with(activity.getContext())
                    .load(imageUrl)
                    .skipMemoryCache()
                    .placeholder(R.drawable.default_episode_background)
                    .into(poster, imageCallback);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.activity_episode, container, false);
    }

    private Callback imageCallback = new Callback()
    {
        @Override
        public void onSuccess()
        {
            SetProgressBarVisibility();
        }

        @Override
        public void onError()
        {
            SetProgressBarVisibility();
        }
    };

    private void SetProgressBarVisibility()
    {
        if (getView() != null)
            (getView().findViewById(R.id.progressDownloadingPicture)).setVisibility(View.INVISIBLE);
    }
}