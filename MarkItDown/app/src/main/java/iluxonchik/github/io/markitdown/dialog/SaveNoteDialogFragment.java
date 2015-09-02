package iluxonchik.github.io.markitdown.dialog;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import iluxonchik.github.io.markitdown.R;

/**
 * A dialog that asks whetehr the user wants to save the edited note.
 */
public class SaveNoteDialogFragment extends DialogFragment {

    public SaveNoteDialogFragment() {

    }
    private PositiveNegativeListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.dialog_save_note_question)
                .setTitle(R.string.dialog_save_note_title)
        .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onDialogPositiveClick(SaveNoteDialogFragment.this, null);

            }
        })
        .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onDialogNegativeClick(SaveNoteDialogFragment.this, null);
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(! (activity instanceof PositiveNegativeListener)) {
            throw new ClassCastException("Starting activity must implement " +
                    PositiveNegativeListener.class.getCanonicalName());
        }

        setOnDialogClickListener((PositiveNegativeListener) activity);
    }

    private void setOnDialogClickListener(PositiveNegativeListener listener) {
        this.listener = listener;
    }

}
