package is.presentation.view;

import java.util.List;

public interface ITraktTrendingView extends IView
{
    public void SetViewVisibility(int progressBarVisibility, int noResultsVisibility);
    public <T> void SetAdapter(List<T> movies);
}
