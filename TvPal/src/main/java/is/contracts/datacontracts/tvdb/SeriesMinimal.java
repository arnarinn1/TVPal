package is.contracts.datacontracts.tvdb;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Series", strict = false)
public class SeriesMinimal
{
    @Element(name = "seriesid")
    private int id;

    @Element(name = "Overview", required = false)
    private String overview;

    @Element(name = "SeriesName")
    private String seriesName;

    public int getId() { return id; }
    public String getOverview() { return overview; }
    public String getSeriesName() { return seriesName; }
}
