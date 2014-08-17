package is.presentation.view;

import android.widget.ProgressBar;

import is.handlers.adapters.MyShowsAdapter;
import is.presentation.presenter.IView;

public interface IMyShowsView extends IView
{
    public void UpdateCursor();
    public ProgressBar GetProgressBar();
    public MyShowsAdapter GetAdapter();
}
