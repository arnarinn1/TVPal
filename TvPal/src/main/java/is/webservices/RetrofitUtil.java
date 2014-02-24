package is.webservices;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.ApacheClient;

/**
 * Created by Arnar on 24.2.2014.
 */
public class RetrofitUtil
{
    public static final String ApiUrl = "http://api.trakt.tv";
    public static final String ApiKey = "f0e3af66061e47b3243e25ed7b6443ca";

    public static RestAdapter RetrofitRestAdapterInstance()
    {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request)
            {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("Accept", "application/json");
                request.addPathParam("apiKey", ApiKey);
            }
        };

        return new
                RestAdapter.Builder()
                .setEndpoint(ApiUrl)
                .setClient(new ApacheClient())
                .setRequestInterceptor(requestInterceptor)
                .build();
    }
}
