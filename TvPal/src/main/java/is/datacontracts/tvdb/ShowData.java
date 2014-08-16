package is.datacontracts.tvdb;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "Data", strict = false)
public class ShowData
{
    @Element(name = "Series")
    private Series series;

    @ElementList(name = "Episode", inline = true)
    private List<Episode> episodes;

    public Series getSeries() { return series; }
    public List<Episode> getEpisodes() { return episodes; }
}
