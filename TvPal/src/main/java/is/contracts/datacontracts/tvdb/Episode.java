package is.contracts.datacontracts.tvdb;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import is.utilities.StringUtil;

@Root(name = "Episode", strict = false)
public class Episode
{
    @Element(name = "id")
    private int id;

    @Element(name = "seriesid")
    private int seriesId;

    @Element(name = "SeasonNumber", required = false)
    private int seasonNumber;

    @Element(name = "EpisodeNumber", required = false)
    private int episodeNumber;

    @Element(name = "EpisodeName", required = false)
    private String episodeName;

    @Element(name = "FirstAired", required = false)
    private String firstAired;

    @Element(name = "Overview", required = false)
    private String overview;

    @Element(name = "Rating", required = false)
    private String rating;

    @Element(name = "Director", required = false)
    private String director;

    @Element(name = "GuestStars", required = false)
    private String guestStars;

    @Element(name = "filename", required = false)
    private String image;

    public int getId() { return id; }
    public int getSeriesId() { return seriesId; }
    public int getSeasonNumber() { return seasonNumber; }
    public int getEpisodeNumber() { return episodeNumber; }
    public String getEpisodeName() { return episodeName; }
    public String getFirstAired() { return firstAired; }
    public String getOverview() { return overview; }
    public String getRating() { return rating; }
    public String getDirector() {return director; }
    public String getGuestStars() { return guestStars == null ? null : StringUtil.ArrayToString(guestStars); }
    public String getImage() {return image; }

    @Override
    public String toString()
    {
        return String.format("%sx%s - %s", seasonNumber, episodeNumber, episodeName);
    }
}
