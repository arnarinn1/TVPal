package is.handlers.database;

import android.database.Cursor;

public interface IEpisodes
{
    public Cursor GetCursorMyShows();
    public void SetSeriesSeen(int seriesId);
    public void RemoveShow(int seriesId);
}
