package is.webservices;

import java.util.List;

import is.contracts.datacontracts.EventData;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Arnar on 1.8.2014.
 */
public interface IScheduleService
{
    @GET("/schedules/{station}")
    void getSchedules(@Path("station") String station,
                                       Callback<List<EventData>> callback);
}
