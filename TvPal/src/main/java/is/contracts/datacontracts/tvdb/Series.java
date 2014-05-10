package is.contracts.datacontracts.tvdb;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import is.utilities.StringUtil;

@Root(name = "Series", strict = false)
public class Series
{
    @Element(name = "id")
    private int id;

    @Element(name = "Actors", required = false)
    private String actors;

    @Element(name = "SeriesName")
    private String seriesName;

    @Element(name = "Overview", required = false)
    private String overview;

    @Element(name = "Network", required = false)
    private String network;

    @Element(name = "lastupdated")
    private int lastUpdated;

    @Element(name = "Genre", required = false)
    private String genre;

    @Element(name = "IMDB_ID", required = false)
    private String imdbId;

    @Element(name = "poster", required = false)
    private String poster;

    private byte[] posterByteStream;

    public int getId() { return id; }
    public String getActors() { return StringUtil.ArrayToString(actors); }
    public String getSeriesName() { return seriesName; }
    public String getOverview() { return overview; }
    public String getNetwork() { return network;}
    public int getLastUpdated() { return lastUpdated; }
    public String getGenre() { return StringUtil.ArrayToString(genre); }
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
