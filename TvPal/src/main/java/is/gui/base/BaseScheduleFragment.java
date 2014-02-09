package is.gui.base;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by Arnar on 9.2.2014.
 */
public class BaseScheduleFragment extends Fragment
{
    protected IScheduleActivity activity;

    @Override
    public void onAttach (Activity activity)
    {
        super.onAttach(activity);
        try
        {
            this.activity = (IScheduleActivity) activity;
        }
        catch (ClassCastException e)
        {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
