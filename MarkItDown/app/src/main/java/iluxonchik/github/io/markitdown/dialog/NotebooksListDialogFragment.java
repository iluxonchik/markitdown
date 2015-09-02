package iluxonchik.github.io.markitdown.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import iluxonchik.github.io.markitdown.MarkItDownDbHelper;
import iluxonchik.github.io.markitdown.MarkitDownDbCursor;
import iluxonchik.github.io.markitdown.NotebooksListCursorAdapter;
import iluxonchik.github.io.markitdown.R;

/**
 * Dialog fragment which is shown when the user tries to add a note to a notebook.
 */
public class NotebooksListDialogFragment extends DialogFragment {

    /**
     * Interface must be implemented by the listener that wants to know the clicked dialog.
     */
    public interface OnNotebookSelectedListener {
        void onNotebookSelected(long id);
    }

    private OnNotebookSelectedListener listener;
    private SQLiteDatabase readableDb;
    private MarkItDownDbHelper dbHelper;

    public NotebooksListDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dbHelper = new MarkItDownDbHelper(getActivity());
        readableDb = dbHelper.getReadableDatabase();

        Resources res = getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        ListView notebooksList = new ListView(getActivity());
        NotebooksListCursorAdapter adapter = new NotebooksListCursorAdapter(getActivity(),
                MarkitDownDbCursor.Notebooks.createNotebookListCursor(readableDb), 0);
        notebooksList.setAdapter(adapter);
        notebooksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.onNotebookSelected(id);
                }
            }
        });
        builder.setNegativeButton(res.getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setView(notebooksList);

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Fragment frag = getTargetFragment();

        if (frag != null) {
            if (!(frag instanceof OnNotebookSelectedListener)) {
                throw new ClassCastException("Starting activity or fragment must implement " +
                        OnNotebookSelectedListener.class.getCanonicalName());
            }
        }

        setOnNotebookSelectedListener((OnNotebookSelectedListener) frag);

    }

    private void setImageViewColor(ImageView imageView, int color) {
        GradientDrawable shape = (GradientDrawable)imageView.getBackground();
        shape.setColor(color);
    }

    public void setOnNotebookSelectedListener(OnNotebookSelectedListener listener) {
        this.listener = listener;
    }

}