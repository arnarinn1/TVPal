package is.handlers.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Class to create/ update Database Schema
 * @author Arnar
 */

public class DatabaseHandler extends SQLiteOpenHelper
{
    //Database & table info
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "shows";
    protected static final String TABLE_SERIES = "series";
    protected static final String TABLE_EPISODES = "episodes";
    protected static final String TABLE_MOVIES = "movies";

    // Columns in series
    protected static final String KEY_S_SERIESID = "seriesId";
    protected static final String KEY_S_NAME = "name";
    protected static final String KEY_S_OVERVIEW = "overview";
    protected static final String KEY_S_NETWORK = "network";
    protected static final String KEY_S_THUMBNAIL = "thumbnail";
    protected static final String KEY_S_LASTUPDATED = "lastupdated";
    protected static final String KEY_S_GENRES = "genres";
    protected static final String KEY_S_IMDBID = "imdbid";
    protected static final String KEY_S_ACTORS = "actors";

    //Columns in episodes
    protected static final String KEY_E_EPISODEID = "episodeId";
    protected static final String KEY_E_SERIESID = "seriesId";
    protected static final String KEY_E_SEASON = "season";
    protected static final String KEY_E_EPISODE = "episode";
    protected static final String KEY_E_EPISODENAME = "episodeName";
    protected static final String KEY_E_OVERVIEW = "overview";
    protected static final String KEY_E_AIRED = "aired";
    protected static final String KEY_E_SEEN = "seen";
    protected static final String KEY_E_DIRECTOR = "director";
    protected static final String KEY_E_RATING = "rating";
    protected static final String KEY_E_GUESTSTARS = "guestStars";

    //Columns in movies
    protected static final String KEY_M_TITLE = "title";
    protected static final String KEY_M_IMDBID = "imdb_id";
    protected static final String KEY_M_OVERVIEW = "overview";
    protected static final String KEY_M_IMAGEURL = "image_url";
    protected static final String KEY_M_TIMESTAMP = "timestamp";

    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_SERIES_TABLE =
                "CREATE TABLE " + TABLE_SERIES + "("
                        + KEY_S_SERIESID + " INTEGER PRIMARY KEY,"
                        + KEY_S_NAME + " TEXT,"
                        + KEY_S_OVERVIEW + " TEXT,"
                        + KEY_S_NETWORK + " TEXT,"
                        + KEY_S_THUMBNAIL + " BLOB,"
                        + KEY_S_LASTUPDATED + " INTEGER, "
                        + KEY_S_GENRES + " TEXT,"
                        + KEY_S_ACTORS + " TEXT,"
                        + KEY_S_IMDBID + " TEXT"
                        + ")";

        String CREATE_EPISODE_TABLE =
                "CREATE TABLE " + TABLE_EPISODES + "("
                        + KEY_E_EPISODEID + " INTEGER PRIMARY KEY,"
                        + KEY_E_SERIESID + " INTEGER,"
                        + KEY_E_SEASON + " INTEGER,"
                        + KEY_E_EPISODE + " INTEGER,"
                        + KEY_E_EPISODENAME  + " TEXT,"
                        + KEY_E_AIRED + " TEXT,"
                        + KEY_E_OVERVIEW + " TEXT,"
                        + KEY_E_SEEN + " INTEGER,"
                        + KEY_E_DIRECTOR + " TEXT,"
                        + KEY_E_RATING + " TEXT, "
                        + KEY_E_GUESTSTARS + " TEXT"
                        + ")";

        String CREATE_MOVIES_TABLE =
                "CREATE TABLE " + TABLE_MOVIES + "("
                        + KEY_M_IMDBID + " TEXT PRIMARY KEY,"
                        + KEY_M_TITLE + " TEXT,"
                        + KEY_M_OVERVIEW + " TEXT,"
                        + KEY_M_IMAGEURL + " TEXT,"
                        + KEY_M_TIMESTAMP + " TEXT"
                        + ")";

        String indexEpisodeId = "CREATE UNIQUE INDEX episodeId ON episodes(episodeId ASC)";

        db.execSQL(CREATE_SERIES_TABLE);
        db.execSQL(CREATE_EPISODE_TABLE);
        db.execSQL(CREATE_MOVIES_TABLE);
        db.execSQL(indexEpisodeId);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        switch(oldVersion)
        {
            case 1:
                UpgradeToTwo(db);
                break;
        }
    }

    private void UpgradeToTwo(SQLiteDatabase db)
    {
        String CREATE_SERIES_TEMP_TABLE =
                "CREATE TABLE SERIES_TEMP("
                        + KEY_S_SERIESID + " INTEGER PRIMARY KEY,"
                        + KEY_S_NAME + " TEXT,"
                        + KEY_S_OVERVIEW + " TEXT,"
                        + KEY_S_NETWORK + " TEXT,"
                        + KEY_S_LASTUPDATED + " INTEGER, "
                        + KEY_S_GENRES + " TEXT,"
                        + KEY_S_ACTORS + " TEXT,"
                        + KEY_S_IMDBID + " TEXT"
                        + ")";

        String CREATE_SERIES_TABLE =
                "CREATE TABLE " + TABLE_SERIES + "("
                        + KEY_S_SERIESID + " INTEGER PRIMARY KEY,"
                        + KEY_S_NAME + " TEXT,"
                        + KEY_S_OVERVIEW + " TEXT,"
                        + KEY_S_NETWORK + " TEXT,"
                        + KEY_S_THUMBNAIL + " TEXT,"
                        + KEY_S_LASTUPDATED + " INTEGER, "
                        + KEY_S_GENRES + " TEXT,"
                        + KEY_S_ACTORS + " TEXT,"
                        + KEY_S_IMDBID + " TEXT"
                        + ")";

        db.beginTransaction();

        try
        {
            //Create Temporary table to store series data
            db.execSQL(CREATE_SERIES_TEMP_TABLE);
            //Insert data from Series into Series_Temp
            db.execSQL(String.format("insert into SERIES_TEMP(%s,%s,%s,%s,%s,%s,%s,%s) select %s,%s,%s,%s,%s,%s,%s,%s from series",
                    KEY_S_SERIESID,KEY_S_NAME, KEY_S_OVERVIEW, KEY_S_NETWORK, KEY_S_LASTUPDATED, KEY_S_GENRES, KEY_S_ACTORS,
                    KEY_S_IMDBID, KEY_S_SERIESID,KEY_S_NAME, KEY_S_OVERVIEW, KEY_S_NETWORK, KEY_S_LASTUPDATED, KEY_S_GENRES,
                    KEY_S_ACTORS, KEY_S_IMDBID));
            //Drop Series table to get rid of blob column and recreate Series table
            db.execSQL("drop table " + TABLE_SERIES);
            db.execSQL(CREATE_SERIES_TABLE);

            //Insert into Series table from temp table
            db.execSQL(String.format("insert into series(%s,%s,%s,%s,%s,%s,%s,%s) select %s,%s,%s,%s,%s,%s,%s,%s from SERIES_TEMP",
                    KEY_S_SERIESID,KEY_S_NAME, KEY_S_OVERVIEW, KEY_S_NETWORK, KEY_S_LASTUPDATED, KEY_S_GENRES, KEY_S_ACTORS,
                    KEY_S_IMDBID, KEY_S_SERIESID,KEY_S_NAME, KEY_S_OVERVIEW, KEY_S_NETWORK, KEY_S_LASTUPDATED, KEY_S_GENRES,
                    KEY_S_ACTORS, KEY_S_IMDBID));

            //Drop Series_Temp table again and commit the transaction
            db.execSQL("drop table SERIES_TEMP");

            db.setTransactionSuccessful();
        }
        catch(Exception ex)
        {
            Log.e(getClass().getName(), ex.getMessage());
        }
        finally
        {
            db.endTransaction();
        }
    }
}