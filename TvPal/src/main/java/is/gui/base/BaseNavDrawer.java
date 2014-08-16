package is.gui.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import is.gui.movies.CinemaActivity;
import is.gui.movies.SearchMoviesActivity;
import is.gui.movies.WatchlistActivity;
import is.gui.events.EventActivity;
import is.gui.shows.MyShowsActivity;
import is.gui.shows.SearchShowsActivity;
import is.gui.shows.UpcomingRecentActivity;
import is.datacontracts.DrawerListData;
import is.datacontracts.DrawerListHeader;
import is.datacontracts.IDrawerItem;
import is.handlers.adapters.DrawerListAdapter;
import is.tvpal.R;

public class BaseNavDrawer extends Activity implements ListView.OnItemClickListener
{
    public static final String EXTRA_STOD2 = "is.activites.STOD2";
    public static final String EXTRA_TITLE = "is.activites.TITLE";
    public static final String EXTRA_IMAGE_RESOURCE = "is.activites.IMAGE_RESOURCE";

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void InitializeNavDrawer()
    {
        ListView mDrawerList = (ListView) findViewById(R.id.drawer);

        List<IDrawerItem> items = new ArrayList<IDrawerItem>();
        items.add(new DrawerListHeader(getResources().getString(R.string.schedule_header)));
        items.add(new DrawerListData(getResources().getString(R.string.ruv), R.drawable.ruv_hvitur_64 , false));
        items.add(new DrawerListData(getResources().getString(R.string.stod_2), R.drawable.stod2_hvitur_64, false));
        items.add(new DrawerListData(getResources().getString(R.string.stod_2_sport), R.drawable.stod2sport_hvitur_64, false));
        items.add(new DrawerListData(getResources().getString(R.string.stod_2_sport2), R.drawable.stod2sport2_hvitur_64, false));
        items.add(new DrawerListData(getResources().getString(R.string.stod_2_bio), R.drawable.stod2bio_hvitur_64, false));
        items.add(new DrawerListData(getResources().getString(R.string.stod_2_gull), R.drawable.stod2gull_hvitur_64, false));
        items.add(new DrawerListData(getResources().getString(R.string.stod_3), R.drawable.stod3_hvitur_64, false));
        items.add(new DrawerListData(getResources().getString(R.string.skjar_einn), R.drawable.skjareinn_hvitur_64, true));
        items.add(new DrawerListHeader(getResources().getString(R.string.shows_header)));
        items.add(new DrawerListData(getResources().getString(R.string.search_show), R.drawable.m_glass_hvitur_64, false));
        items.add(new DrawerListData(getResources().getString(R.string.my_shows), R.drawable.eye_64_hvitur, false));
        items.add(new DrawerListData(getString(R.string.upcoming_shows), R.drawable.calendar_hvitur_64, true));
        items.add(new DrawerListHeader(getString(R.string.movieText)));
        items.add(new DrawerListData(getString(R.string.cinemaSchedules), R.drawable.cinema_hvitur_64, false));
        items.add(new DrawerListData(getString(R.string.trakt_search_movies), R.drawable.m_glass_hvitur_64, false));
        items.add(new DrawerListData(getString(R.string.watchlist), R.drawable.watchlist_hvitur_64, true));

        mDrawerList.setAdapter(new DrawerListAdapter(this, items));
        mDrawerList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        StartActivity(position);
    }

    private void StartActivity(int position)
    {
        try
        {
            Intent intent = null;

            switch (position)
            {
                case 1:
                    intent = new Intent(this, EventActivity.class);
                    intent.putExtra(EXTRA_STOD2, "ruv");
                    intent.putExtra(EXTRA_TITLE, getResources().getString(R.string.ruv));
                    intent.putExtra(EXTRA_IMAGE_RESOURCE, R.drawable.ruv_svartur_64);
                    break;
                case 2:
                    intent = new Intent(this, EventActivity.class);
                    intent.putExtra(EXTRA_STOD2, "stod2");
                    intent.putExtra(EXTRA_TITLE, getResources().getString(R.string.stod_2));
                    intent.putExtra(EXTRA_IMAGE_RESOURCE, R.drawable.stod2_64);
                    break;
                case 3:
                    intent = new Intent(this, EventActivity.class);
                    intent.putExtra(EXTRA_STOD2, "sport");
                    intent.putExtra(EXTRA_TITLE, getResources().getString(R.string.stod_2_sport));
                    intent.putExtra(EXTRA_IMAGE_RESOURCE, R.drawable.stod2sport_64);
                    break;
                case 4:
                    intent = new Intent(this, EventActivity.class);
                    intent.putExtra(EXTRA_STOD2, "sport2");
                    intent.putExtra(EXTRA_TITLE, getResources().getString(R.string.stod_2_sport2));
                    intent.putExtra(EXTRA_IMAGE_RESOURCE, R.drawable.stod2sport2_64);
                    break;
                case 5:
                    intent = new Intent(this, EventActivity.class);
                    intent.putExtra(EXTRA_STOD2, "bio");
                    intent.putExtra(EXTRA_TITLE, getResources().getString(R.string.stod_2_bio));
                    intent.putExtra(EXTRA_IMAGE_RESOURCE, R.drawable.stod2bio_64);
                    break;
                case 6:
                    intent = new Intent(this, EventActivity.class);
                    intent.putExtra(EXTRA_STOD2, "gull");
                    intent.putExtra(EXTRA_TITLE, getResources().getString(R.string.stod_2_gull));
                    intent.putExtra(EXTRA_IMAGE_RESOURCE, R.drawable.stod2gull_64);
                    break;
                case 7:
                    intent = new Intent(this, EventActivity.class);
                    intent.putExtra(EXTRA_STOD2, "stod3");
                    intent.putExtra(EXTRA_TITLE, getResources().getString(R.string.stod_3));
                    intent.putExtra(EXTRA_IMAGE_RESOURCE, R.drawable.stod3_64);
                    break;
                case 8:
                    intent = new Intent(this, EventActivity.class);
                    intent.putExtra(EXTRA_STOD2, "skjarinn");
                    intent.putExtra(EXTRA_TITLE, getResources().getString(R.string.skjarinn));
                    intent.putExtra(EXTRA_IMAGE_RESOURCE, R.drawable.skjareinn_64);
                    break;
                case 10:
                    intent = new Intent(this, SearchShowsActivity.class);
                    break;
                case 11:
                    intent = new Intent(this, MyShowsActivity.class);
                    break;
                case 12:
                    intent = new Intent(this, UpcomingRecentActivity.class);
                    break;
                case 14:
                    intent = new Intent(this, CinemaActivity.class);
                    break;
                case 15:
                    intent = new Intent(this, SearchMoviesActivity.class);
                    break;
                case 16:
                    intent = new Intent(this, WatchlistActivity.class);
                    break;
            }

            startActivity(intent);
            overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
        }
        catch (Exception ex)
        {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
