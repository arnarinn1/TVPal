package is.gui.events;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import is.gui.base.IScheduleActivity;
import is.gui.reminders.ScheduleClient;

/**
 * Created by Arnar on 9.2.2014.
 */
public class BaseScheduleActivity extends FragmentActivity implements IScheduleActivity
{
    protected ScheduleClient mScheduleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mScheduleClient = new ScheduleClient(this);
        mScheduleClient.doBindService();
    }

    @Override
    protected void onStop()
    {
        // When our activity is stopped ensure we also stop the connection to the service
        // this stops us leaking our activity into the system *bad*
        if(mScheduleClient != null)
            mScheduleClient.doUnbindService();
        super.onStop();
    }

    @Override
    public Context getContext()
    {
        return this;
    }

    @Override
    public ScheduleClient getScheduleClient()
    {
        return mScheduleClient;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
