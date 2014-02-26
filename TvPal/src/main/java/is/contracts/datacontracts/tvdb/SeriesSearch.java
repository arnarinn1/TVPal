package is.contracts.datacontracts.tvdb;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "Data", strict = false)
public class SeriesSearch
{
    @ElementList(name = "Series", inline = true, required = false)
    private List<SeriesMinimal> series;

    public List<SeriesMinimal> getSeries() { return series; }
}
