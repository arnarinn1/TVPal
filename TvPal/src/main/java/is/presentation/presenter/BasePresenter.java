package is.presentation.presenter;

/**
 * A generic Presenter class
 */
public class BasePresenter<TView extends IView, TSystem>
{
    TView View;
    TSystem System;

    public BasePresenter(TView view, TSystem system)
    {
        if(view == null) throw new NullPointerException();
        if(system == null) throw new NullPointerException();

        this.View = view;
        this.System = system;
    }
}
