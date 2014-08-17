package is.presentation.view;

import android.widget.ProgressBar;

import is.handlers.adapters.MyShowsAdapter;

public interface IMyShowsView extends IView
{
    public void UpdateCursor();
    public ProgressBar GetProgressBar();
    public MyShowsAdapter GetAdapter();
}
