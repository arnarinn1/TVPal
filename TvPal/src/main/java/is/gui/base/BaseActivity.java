package is.gui.base;

import android.app.Activity;
import android.view.MenuItem;

import is.tvpal.R;

/**
 * Created by Arnar on 21.11.2013.
 */
public class BaseActivity extends Activity
{
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
