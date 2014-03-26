package is.gui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;

import is.gui.base.BaseNavDrawer;
import is.tvpal.R;

public class MainActivity extends BaseNavDrawer
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Initialize();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void Initialize()
    {
        InitializeNavDrawer();
    }
}
