package is.system;

import is.datacontracts.ITrakt;
import is.webservices.RetrofitUtil;
import retrofit.RestAdapter;

/**
 * Created by Arnar on 17.8.2014.
 */
public class TraktSystem implements ITrakt
{
    is.webservices.ITraktService service;

    @Override
    public is.webservices.ITraktService TraktService()
    {
        if (service != null)
            return service;

        RestAdapter restAdapter = RetrofitUtil.TraktRestAdapterInstance();
        return service = restAdapter.create(is.webservices.ITraktService.class);
    }
}
