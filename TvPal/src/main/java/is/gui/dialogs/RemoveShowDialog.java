package is.gui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class RemoveShowDialog extends DialogFragment
{
    public static final String EXTRA_POSITION = "is.gui.dialogs.POSITION";
    public static final String EXTRA_TITLE = "is.gui.dialogs.TITLE";
    public static final int HOLO_DARK_THEME = 2;

    public static RemoveShowDialog newInstance(int position, String seriesTitle)
    {
        RemoveShowDialog f = new RemoveShowDialog();
        Bundle args = new Bundle();
        args.putInt(EXTRA_POSITION, position);
        args.putString(EXTRA_TITLE, seriesTitle);
        f.setArguments(args);
        return f;
    }

    private OnRemoveShowListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try
        {
            mListener = (OnRemoveShowListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement RemoveShowListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Bundle args = getArguments();
        final int position = args.getInt(EXTRA_TITLE);
        final String seriesTitle = args.getString(EXTRA_TITLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), HOLO_DARK_THEME);
        builder
                .setMessage(String.format("Are you sure you want to remove %s from your shows ?", seriesTitle))
                .setTitle("Remove Show")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        mListener.onRemoveShow(position);
                        dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dismiss();
                    }
                });

        return builder.create();
    }

    public interface OnRemoveShowListener
    {
        public void onRemoveShow(int position);
    }
}
