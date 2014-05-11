package is.webservices;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.ApacheClient;

/**
 * Created by Arnar on 24.2.2014.
 */
public class RetrofitUtil
{
    private static final String TraktApiUrl = "http://api.trakt.tv";
    private static final String TraktApiKey = "f0e3af66061e47b3243e25ed7b6443ca";

    private static final String TvdbApiUrl = "http://thetvdb.com/api";
    private static final String TvdbApiKey = "9A96DA217CEB03E7";

    private static final String ApisUrl = "http://apis.is";

    public static RestAdapter RetrofitRestAdapterInstance()
    {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request)
            {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("Accept", "application/json");
                request.addPathParam("apiKey", TraktApiKey);
            }
        };

        return new
                RestAdapter.Builder()
                .setEndpoint(TraktApiUrl)
                .setClient(new ApacheClient())
                .setRequestInterceptor(requestInterceptor)
                .build();
    }

    public static RestAdapter RestAdapterXMLInstance()
    {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request)
            {
                request.addPathParam("apiKey", TvdbApiKey);
                request.addPathParam("type", "all/en.xml");
            }
        };

        return new RestAdapter.Builder()
                .setEndpoint(TvdbApiUrl)
                .setConverter(new SimpleXMLConverter())
                .setRequestInterceptor(requestInterceptor)
                .setClient(new ApacheClient())
                .build();
    }

    public static RestAdapter RetrofitApisInstance()
    {
        return new RestAdapter.Builder()
                .setEndpoint(ApisUrl)
                .build();
    }
}
