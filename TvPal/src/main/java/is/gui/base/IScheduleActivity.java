package is.gui.base;

import android.content.Context;

import is.gui.reminders.ScheduleClient;

/**
 * Created by Arnar on 9.2.2014.
 */
public interface IScheduleActivity
{
    public Context getContext();
    public ScheduleClient getScheduleClient();
}
