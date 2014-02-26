package is.gui.shows;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import is.contracts.datacontracts.tvdb.SeriesSearch;
import is.gui.base.BaseFragment;
import is.handlers.adapters.SearchShowAdapter;
import is.tvpal.R;
import is.webservices.RetrofitUtil;
import is.webservices.TvdbService;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * An activity to search for episodes and add them to "MyShows"
 * @author Arnar
 * @see is.gui.shows.MyShowsActivity
 */

public class SearchShowFragment extends BaseFragment
{
    private ListView mListView;
    private EditText mEditSearch;
    private Context mContext;
    private ProgressBar mProgressBar;

    private TvdbService mService;

    public SearchShowFragment() {}

    public static SearchShowFragment newInstance()
    {
        SearchShowFragment fragment = new SearchShowFragment();

        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mContext = activity.getContext();
        mEditSearch = (EditText) getView().findViewById(R.id.searchShow);
        mProgressBar = (ProgressBar) getView().findViewById(R.id.progressIndicator);
        mListView = (ListView) getView().findViewById(R.id.lvId);

        RestAdapter restAdapter = RetrofitUtil.RestAdapterXMLInstance();
        mService = restAdapter.create(TvdbService.class);

        InitializeEditTextSearch();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_search_shows, container, false);
    }

    private void InitializeEditTextSearch()
    {
        mEditSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();

                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); //Close the keyboard

                    return true;
                }
                return false;
            }
        });
    }

    private void performSearch()
    {
        mProgressBar.setVisibility(View.VISIBLE);
        mListView.setAdapter(null);

        String userEntry = mEditSearch.getText().toString();
        mService.getSearchSeries(userEntry, searchSeriesCallback);
    }

    Callback<SeriesSearch> searchSeriesCallback = new Callback<SeriesSearch>() {
        @Override
        public void success(SeriesSearch series, Response response)
        {
            if (series.getSeries() == null)
                Toast.makeText(mContext, "Found no shows", Toast.LENGTH_SHORT).show();
            else
            {
                SearchShowAdapter mAdapter = new SearchShowAdapter(mContext, R.layout.listview_search_show, series.getSeries());
                mListView.setAdapter(mAdapter);
            }

            mProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void failure(RetrofitError retrofitError)
        {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    };
}