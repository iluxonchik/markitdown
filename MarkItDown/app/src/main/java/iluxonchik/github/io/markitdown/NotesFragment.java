package iluxonchik.github.io.markitdown;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import iluxonchik.github.io.markitdown.dialog.NotebooksListDialogFragment;
import iluxonchik.github.io.markitdown.dialog.ShareAsDialogFragment;

public class NotesFragment extends DatabaseListFragment implements ShareAsDialogFragment.OnShareAsOptionSelectedListener{

    public interface OnCABStatusChangedListener {
        void onCABCreate();
        void onCABDestroy();
    }

    private NotesListCursorAdapter cursorAdapter;
    private FloatingActionButton newNoteFAB;
    private OnCABStatusChangedListener onCABStatusChangedListener;

    private final int CURSOR_TITLE_POS = 0;
    private final int CURSOR_CONTENT_POS = 1;

    private final String NOTESFRAGMENT_LOGCAT_TAG = "NotesFragment";

    public NotesFragment() {

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Broadcast from DeleteService
            cursorAdapter.swapCursor(createNotesListCursor());
            Log.d(NOTESFRAGMENT_LOGCAT_TAG, "Note deleted!");
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items_list, container, false);

        newNoteFAB = (FloatingActionButton) view.findViewById(R.id.addItemFAB);
        newNoteFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditNoteActivity.class);
                i.putExtra(EditNoteActivity.NOTE_ID_ARG, -1);
                startActivity(i);
            }
        });

        return view;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        //listView.setSelector(R.drawable.list_selector);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            private final int VIEW_MENU_POS = 0;
            private final int EDIT_MENU_POS = 1;
            private final int SHARE_MENU_POS = 2;

            private boolean multipleItemsChecked = false;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                Log.d("CAT", "onCheckedStateChanged. Number of checked items: " + listView.getCheckedItemCount());

                // TODO: REFRACTOR nested if's
                if (multipleItemsChecked) {
                    if (listView.getCheckedItemCount() == 1) {
                        Log.d("CAT", "Checked one item");
                        // Passing from multiple checked items to one
                        multipleItemsChecked = false;
                        invertMenuOptions(mode.getMenu());
                    }
                } else {
                    if (listView.getCheckedItemCount() > 1) {
                        Log.d("CAT", "Checked more than one item");
                        multipleItemsChecked = true;
                        invertMenuOptions(mode.getMenu());
                    }
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate menu
                // TODO: tell activity to disable drawer layout
                Log.d("CAT", "onCreateActionMode");
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.menu_notes_context, menu);
                newNoteFAB.hide();
                onCABStatusChangedListener.onCABCreate();
                return true;
            }

            private void invertMenuOptions(Menu menu) {
                /*
                    Inverts the View, Edit and Share options availability in menu.
                */
                menu.getItem(VIEW_MENU_POS).setEnabled(!menu.getItem(VIEW_MENU_POS).isEnabled());
                menu.getItem(EDIT_MENU_POS).setEnabled(!menu.getItem(EDIT_MENU_POS).isEnabled());
                menu.getItem(SHARE_MENU_POS).setEnabled(!menu.getItem(SHARE_MENU_POS).isEnabled());

            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case (R.id.view_note):
                        intent = new Intent(getActivity(), ViewNoteActivity.class);
                        intent.putExtra(ViewNoteActivity.EXTRA_NOTE_ID, getSingleSelectedItemId());
                        mode.finish();
                        startActivity(intent);
                        break;
                    case (R.id.share_note):
                        ShareAsDialogFragment shareAsDialogFragment = new ShareAsDialogFragment();
                        shareAsDialogFragment.show(getFragmentManager(), null);
                        break;
                    case (R.id.add_to_notebook):
                        NotebooksListDialogFragment dialog = new NotebooksListDialogFragment();
                        dialog.setOnNotebookSelectedListener(
                                new NotebooksListDialogFragment.OnNotebookSelectedListener() {
                            @Override
                            public void onNotebookSelected(long id) {
                                // TODO: add selected notes to selected notebook
                            }
                        });
                        dialog.show(getFragmentManager(), null);
                        break;
                    case (R.id.edit_note):
                        intent = new Intent(getActivity(), EditNoteActivity.class);
                        intent.putExtra(EditNoteActivity.NOTE_ID_ARG, getSingleSelectedItemId());
                        mode.finish();
                        startActivity(intent);
                        break;
                    case (R.id.delete_note):
                        handleNoteDeletion();
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // TODO: tell activity to enable drawer layout
                newNoteFAB.show();
                onCABStatusChangedListener.onCABDestroy();
            }

        });


    }

    @Override
    public void onDestroy() {
        closeDatabase();
        super.onDestroy();
    }

    private void handleNoteDeletion() {
        /*
          Show a dialog asking if the user is sure about note(s) deletion, in case he is,
          remove the selected note(s).
         */

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteSelectedNotes();
            }
        });
        builder.setNegativeButton(R.string.dialog_no, null);
        setAlertDialogText(builder);

        builder.create().show();

    }

    private void deleteSelectedNotes() {
        long[] checkedItemIds = getListView().getCheckedItemIds();
        for (long id : checkedItemIds)
        {
            DeleteService.startActionDeleteNote(getActivity(), (int)id);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Cursor cursor = createNotesListCursor();
        cursorAdapter = new NotesListCursorAdapter(getActivity(), cursor, 0);
        setListAdapter(cursorAdapter);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(DeleteService.ACTION_DELETE_NOTE));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onAttach(Activity activity) {
        try {
            onCABStatusChangedListener = (OnCABStatusChangedListener)activity;

        } catch (ClassCastException e) {
            throw new ClassCastException("Activity that uses "+ this.getClass().getCanonicalName() + " must implement " +
                    OnCABStatusChangedListener.class.getCanonicalName());
        }
        super.onAttach(activity);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //Integer note_id = (Integer)v.getTag();
        Integer note_id = (int)id;
        Intent intent = new Intent(getActivity(), ViewNoteActivity.class);
        intent.putExtra(ViewNoteActivity.EXTRA_NOTE_ID, note_id);
        startActivity(intent);

        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onHTMLClicked(DialogFragment dialog) {
        // Share note as HTML
        Cursor cursor = getNoteContentsCursor(MarkItDownDbContract.Notes.COLUMN_NAME_TEXT_HTML);

        if (cursor.moveToFirst()) {
            startShareIntent(cursor);
        }
    }

    @Override
    public void onMarkdownClicked(DialogFragment dialog) {
        // Share note as Markdown
        Cursor cursor = getNoteContentsCursor(MarkItDownDbContract.Notes.COLUMN_NAME_TEXT_MARKDOWN);

        if (cursor.moveToFirst()) {
            startShareIntent(cursor);
        }

    }

    private void startShareIntent(Cursor cursor) {
        ShareAsDialogFragment.startShareIntent(getActivity(),cursor.getString(CURSOR_CONTENT_POS),
                cursor.getString(CURSOR_CONTENT_POS));
    }

    /**
     * Get the cursor with the requested note's content.
     *
     * @param contentColumnName name of the content's column (HTML or Markdown text)
     * @return cursor with the requested contents
     */
    private Cursor getNoteContentsCursor(String contentColumnName) {
        return readableDb.query(MarkItDownDbContract.Notes.TABLE_NAME,
                new String[]{MarkItDownDbContract.Notes.COLUMN_NAME_TITLE,
                        contentColumnName}, "_id = ?",
                new String[]{Integer.toString(getSingleSelectedItemId())}, null, null, null, null);
    }

    public Cursor createNotesListCursor() {
        return  readableDb.query(MarkItDownDbContract.Notes.TABLE_NAME,
                new String[]{MarkItDownDbContract.Notes._ID, MarkItDownDbContract.Notes.COLUMN_NAME_TITLE,
                        MarkItDownDbContract.Notes.COLUMN_NAME_DATE_SAVED},
                null, null, null, null, null);

    }

    public void setAlertDialogText(AlertDialog.Builder builder) {
        int titleResId;
        int messageResId;
        int numSelectedNotes = getListView().getCheckedItemCount();

        if (numSelectedNotes > 1) {
            // More than one note selected for deletion
            titleResId = R.string.dialog_delete_multiple_notes_question;
            messageResId = R.string.dialog_delete_single_note_text;
        } else {
            // Single note for deletion selected
            titleResId = R.string.dialog_delete_single_note_question;
            messageResId = R.string.dialog_delete_single_note_text;
        }

        builder.setTitle(getResources().getString(titleResId));
        builder.setMessage(getResources().getString(messageResId) + " " + getResources().getString(R.string.dialog_action_cannot_be_undone));
    }
}
