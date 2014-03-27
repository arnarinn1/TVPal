package is.handlers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import is.contracts.datacontracts.EpisodeData;
import is.contracts.datacontracts.SeriesData;
import is.contracts.datacontracts.StatisticData;
import is.contracts.datacontracts.tvdb.Episode;
import is.contracts.datacontracts.tvdb.Series;
import is.utilities.DateUtil;

/**
 * A class to work with Episode Data
 * @author Arnar
 */
public class DbEpisodes extends DatabaseHandler
{
    public DbEpisodes(Context context)
    {
        super(context);
    }

    /*
        Cursors
    */

    public Cursor GetCursorOverview(int seriesId)
    {
        String selectQuery = String.format("select seriesId as _id, overview, name, network, genres, actors, imdbid " +
                "from series where seriesId = %d", seriesId);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        return cursor;
    }

    public Cursor GetCursorSeasons(int seriesId)
    {
        String selectQuery = "SELECT distinct season as _id, seriesId FROM episodes " +
                "WHERE seriesId = " + seriesId + " order by season desc";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        return cursor;
    }

    public Cursor GetCursorEpisodes(int seriesId, int seasonNumber)
    {
        String selectQuery = String.format("select episodeId as _id, seriesId, season, episode, episodeName, aired, seen " +
                "from episodes where seriesId = %d and season = %d" +
                " order by 0+episode", seriesId, seasonNumber);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        return cursor;
    }

    public Cursor GetCursorUpcoming()
    {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String selectQuery = String.format("select episodeId as _id, episodeName, aired, seriesId, season, episode " +
                "from episodes " +
                "where aired >= '%s' " +
                "order by aired limit 15", date);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        return cursor;
    }

    public Cursor GetCursorRecent()
    {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String selectQuery = String.format("select episodeId as _id, episodeName, aired, seriesId, season, episode " +
                "from episodes " +
                "where aired < '%s' " +
                "order by aired desc limit 15", date);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        return cursor;
    }

    public Cursor GetCursorMyShows()
    {
        String selectQuery = String.format("select seriesId as _id, name, genres " +
                "from series order by name");

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        return cursor;
    }

    public Cursor GetCursorEpisodesDetailed(int seriesId, int seasonNumber)
    {
        String selectQuery = String.format("select episodeId as _id, episode, episodeName, season, overview, aired, director, rating, seen, guestStars, seriesId " +
                "from episodes where seriesId = %d and season = %d" +
                " order by 0+episode", seriesId, seasonNumber);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        return cursor;
    }

    /*
        Insert/Update/Remove data
    */

    public void InsertFullSeriesInfo(List<Episode> episodes, Series series)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        if (db != null)
        {
            db.beginTransaction();

            try
            {
                AddSeries(series, db);

                AddEpisodes(db, episodes);

                db.setTransactionSuccessful();
            }
            catch (Exception ex)
            {
                Log.e(getClass().getName(), ex.getMessage());
            }
            finally
            {
                db.endTransaction();
            }
            db.close();
        }
    }

    public void AddSeries(Series series, SQLiteDatabase db)
    {
        try
        {
            ContentValues values = new ContentValues();
            values.put(KEY_S_SERIESID, series.getId());
            values.put(KEY_S_NAME, series.getSeriesName());
            values.put(KEY_S_NETWORK, series.getNetwork());
            values.put(KEY_S_OVERVIEW, series.getOverview());
            values.put(KEY_S_THUMBNAIL, series.getPosterByteStream());
            values.put(KEY_S_LASTUPDATED, series.getLastUpdated());
            values.put(KEY_S_GENRES, series.getGenre());
            values.put(KEY_S_ACTORS, series.getActors());
            values.put(KEY_S_IMDBID, series.getImdbId());

            db.insert(TABLE_SERIES, null, values);
        }
        catch (Exception ex)
        {
            Log.e(getClass().getName(), ex.getMessage());
        }
    }

    public void AddEpisodes(SQLiteDatabase db, List<Episode> episodes)
    {
        try
        {
            for(Episode episode : episodes)
            {
                ContentValues values = new ContentValues();
                values.put(KEY_E_EPISODEID, episode.getId());
                values.put(KEY_S_SERIESID, episode.getSeriesId());
                values.put(KEY_E_SEASON, episode.getSeasonNumber());
                values.put(KEY_E_EPISODE, episode.getEpisodeNumber());
                values.put(KEY_E_EPISODENAME, episode.getEpisodeName());
                values.put(KEY_E_AIRED, episode.getFirstAired());
                values.put(KEY_S_OVERVIEW, episode.getOverview());
                values.put(KEY_E_SEEN, 0);
                values.put(KEY_E_DIRECTOR, episode.getDirector());
                values.put(KEY_E_RATING, episode.getRating());
                values.put(KEY_E_GUESTSTARS, episode.getGuestStars());

                db.insert(TABLE_EPISODES, null, values);
            }
        }
        catch (Exception ex)
        {
            Log.e(getClass().getName(), ex.getMessage());
        }
    }

    public void AddEpisode(SQLiteDatabase db, Episode episode)
    {
        try
        {
            ContentValues values = new ContentValues();
            values.put(KEY_E_EPISODEID, episode.getId());
            values.put(KEY_S_SERIESID, episode.getSeriesId());
            values.put(KEY_E_SEASON, episode.getSeasonNumber());
            values.put(KEY_E_EPISODE, episode.getEpisodeNumber());
            values.put(KEY_E_EPISODENAME, episode.getEpisodeName());
            values.put(KEY_E_AIRED, episode.getFirstAired());
            values.put(KEY_S_OVERVIEW, episode.getOverview());
            values.put(KEY_E_SEEN, 0);
            values.put(KEY_E_DIRECTOR, episode.getDirector());
            values.put(KEY_E_RATING, episode.getRating());
            values.put(KEY_E_GUESTSTARS, episode.getGuestStars());

            db.insert(TABLE_EPISODES, null, values);
        }
        catch (Exception ex)
        {
            Log.e(getClass().getName(), ex.getMessage());
        }
    }

    public void UpdateEpisode(SQLiteDatabase db, Episode episode)
    {
        try
        {
            ContentValues values = new ContentValues();
            values.put(KEY_E_EPISODEID, episode.getId());
            values.put(KEY_S_SERIESID, episode.getSeriesId());
            values.put(KEY_E_SEASON, episode.getSeasonNumber());
            values.put(KEY_E_EPISODE, episode.getEpisodeNumber());
            values.put(KEY_E_EPISODENAME, episode.getEpisodeName());
            values.put(KEY_E_AIRED, episode.getFirstAired());
            values.put(KEY_S_OVERVIEW, episode.getOverview());
            values.put(KEY_E_DIRECTOR, episode.getDirector());
            values.put(KEY_E_RATING, episode.getRating());
            values.put(KEY_E_GUESTSTARS, episode.getGuestStars());

            db.update(TABLE_EPISODES, values, KEY_E_EPISODEID + " = " + episode.getId(), null);
        }
        catch (Exception ex)
        {
            Log.e(getClass().getName(), ex.getMessage());
        }
    }

    public void RemoveShow(int seriesId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try
        {
            db.delete(TABLE_SERIES, KEY_S_SERIESID + " = " + seriesId, null);
            db.delete(TABLE_EPISODES, KEY_E_SERIESID + " = " + seriesId, null);
            db.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            db.endTransaction();
        }

        db.close();
    }

    public void UpdateSingleSeries(List<Episode> episodes, int latestUpdate, int seriesId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try
        {
            for (Episode e : episodes)
            {
                if (!DoesEpisodeExist(db, e.getId()))
                {
                    AddEpisode(db, e);
                    Log.d(getClass().getName(), "Updating episode: " + e.getEpisodeName());
                }
                else
                {
                    UpdateEpisode(db, e);
                    Log.d(getClass().getName(), "Updating episode: " + e.getEpisodeName());
                }
            }

            //Finally update latest update column in seriesTable
            ContentValues values = new ContentValues();
            values.put(KEY_S_LASTUPDATED, latestUpdate);
            db.update(TABLE_SERIES, values, KEY_S_SERIESID + " = " + seriesId, null);

            db.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            Log.e(getClass().getName(), ex.getMessage());
        }
        finally
        {
            db.endTransaction();
        }

        db.close();
    }

    /*
        Other Episodes related methods
    */

    public Bitmap GetSeriesPoster(int seriesId, boolean lowRes)
    {
        String selectQuery = String.format("SELECT thumbnail from series where seriesId = %d", seriesId);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        byte[] posterByteStream = cursor.getBlob(0);
        Bitmap poster;

        if (lowRes)
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            poster = BitmapFactory.decodeByteArray(posterByteStream, 0, posterByteStream.length, options);
        }
        else
        {
            poster = BitmapFactory.decodeByteArray(posterByteStream, 0, posterByteStream.length);
        }

        db.close();
        return poster;
    }

    public void UpdateSeasonSeenStatus(int seriesId, int seasonNumber, int seenStatus)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            if (db !=null)
            {
                db.beginTransaction();
                db.execSQL(String.format("UPDATE episodes SET seen = %d WHERE seriesId = %d AND season = %d", seenStatus , seriesId, seasonNumber));
                db.setTransactionSuccessful();
            }
        }
        catch (Exception ex)
        {
            Log.e(getClass().getName(), ex.getMessage());
        }
        finally
        {
            db.endTransaction();
        }

        db.close();
    }

    public void SetSeriesSeen(int seriesId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            if (db !=null)
            {
                db.beginTransaction();
                db.execSQL(String.format("UPDATE %s SET seen = 1 WHERE seriesId = %d", TABLE_EPISODES, seriesId));
                db.setTransactionSuccessful();
            }
        }
        catch (Exception ex)
        {
            Log.e(getClass().getName(), ex.getMessage());
        }
        finally
        {
            db.endTransaction();
        }

        db.close();
    }

    public void UpdateEpisodeSeen(int episodeId)
    {
        int seen = 1;

        if (GetShowSeen(episodeId))
            seen = 0;

        ContentValues values = new ContentValues();

        values.put(KEY_E_SEEN, seen);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_EPISODES, values, KEY_E_EPISODEID + " = " + episodeId, null);
        db.close();
    }

    public boolean GetShowSeen(int episodeId)
    {
        String selectQuery = String.format("select * from episodes where episodeId = %d", episodeId);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        return cursor.getInt(7) == 1;
    }

    public int GetTotalSeasonCount(int seriesId, int season)
    {
        String selectQuery = String.format("select count(*) from episodes " +
                "where seriesId = %d and season = %d", seriesId, season);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public int GetTotalSeasonSeen(int seriesId, int season)
    {
        String selectQuery = String.format("select count(*) from %s " +
                "where seriesId = %d and season = %d and seen = 1", TABLE_EPISODES, seriesId, season);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public int GetSeriesLastUpdate(int seriesId)
    {
        String selectQuery = String.format("select seriesId as _id, lastupdated from series where seriesId = %d", seriesId);

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectQuery ,null);

        cursor.moveToFirst();
        return cursor.getInt(1);
    }

    public boolean DoesEpisodeExist(SQLiteDatabase db, int episodeId)
    {
        String selectQuery = String.format("select episodeId as _id from episodes where episodeId = %d", episodeId);
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount() == 1;
    }

    public List<Integer> GetAllSeriesIds()
    {
        List<Integer> seriesIds = new ArrayList<Integer>();

        String selectQuery = "select seriesId as _id from series";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            seriesIds.add(Integer.parseInt(cursor.getString(0)));
            cursor.moveToNext();
        }
        db.close();

        return seriesIds;
    }
}
