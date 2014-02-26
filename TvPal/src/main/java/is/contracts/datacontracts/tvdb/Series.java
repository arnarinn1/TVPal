package is.contracts.datacontracts.tvdb;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import is.utilities.StringUtil;

@Root(name = "Series", strict = false)
public class Series
{
    @Element(name = "id")
    private int id;

    @Element(name = "Actors")
    private String actors;

    @Element(name = "SeriesName")
    private String seriesName;

    @Element(name = "Overview")
    private String overview;

    @Element(name = "Network")
    private String network;

    @Element(name = "lastupdated")
    private int lastUpdated;

    @Element(name = "Genre")
    private String genre;

    @Element(name = "IMDB_ID")
    private String imdbId;

    @Element(name = "poster")
    private String poster;

    private byte[] posterByteStream;

    public int getId() { return id; }
    public String getActors() { return StringUtil.ArrayToString(actors); }
    public String getSeriesName() { return seriesName; }
    public String getOverview() { return overview; }
    public String getNetwork() { return network;}
    public int getLastUpdated() { return lastUpdated; }
    public String getGenre() { return genre; }
    public String getImdbId() { return imdbId; }
    public String getPoster() { return poster; }

    public byte[] getPosterByteStream() { return posterByteStream; }
    public void setPosterByteStream(byte[] posterByteStream) { this.posterByteStream = posterByteStream; }

    @Override
    public String toString()
    {
        return seriesName;
    }
}
