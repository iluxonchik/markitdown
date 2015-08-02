package iluxonchik.github.io.markitdown;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.security.PublicKey;
import java.util.ArrayList;

/**
 * Created by ILUXONCHIK on 01/08/2015.
 */
public class SaveNoteDialogFragment extends DialogFragment {

    public SaveNoteDialogFragment() {
        listeners = new ArrayList<SaveNoteDialogListener>();
    }

    public interface SaveNoteDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    private ArrayList<SaveNoteDialogListener> listeners;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.dialog_save_note_question)
                .setTitle(R.string.dialog_save_note_title)
        .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (SaveNoteDialogListener listener : listeners) {
                    listener.onDialogPositiveClick(SaveNoteDialogFragment.this);
                }
            }
        })
        .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (SaveNoteDialogListener listener : listeners) {
                    listener.onDialogNegativeClick(SaveNoteDialogFragment.this);
                }
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void addOnDialogClickListener(SaveNoteDialogListener listener) {
        listeners.add(listener);
    }

}
