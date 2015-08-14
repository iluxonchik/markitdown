package iluxonchik.github.io.markitdown;

import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

public class NotesFragment extends ListFragment implements ShareAsDialogFragment.OnShareAsOptionSelectedListener{

    private MarkItDownDbHelper dbHelper;
    private SQLiteDatabase readableDb;

    private final int CURSOR_TITLE_POS = 0;
    private final int CURSOR_CONTENT_POS = 1;

    public NotesFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new MarkItDownDbHelper(getActivity());
        readableDb = dbHelper.getReadableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Cursor cursor = readableDb.query(MarkItDownDbContract.Notes.TABLE_NAME,
                new String[]{MarkItDownDbContract.Notes._ID, MarkItDownDbContract.Notes.COLUMN_NAME_TITLE,
                        MarkItDownDbContract.Notes.COLUMN_NAME_DATE_SAVED},
                null, null, null, null, null);

        TaggableCursorAdapter cursorAdapter = new TaggableCursorAdapter(inflater.getContext(), cursor, 0);
        setListAdapter(cursorAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
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
                Log.d("CAT", "onCheckedStateChanged");
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
                Log.d("CAT", "onCreateActionMode");
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.menu_notes_context, menu);
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
                        startActivity(intent);
                        break;
                    case (R.id.share_note):
                        ShareAsDialogFragment shareAsDialogFragment = new ShareAsDialogFragment();
                        shareAsDialogFragment.show(getFragmentManager(), null);
                        break;
                    case (R.id.edit_note):
                        intent = new Intent(getActivity(), EditNoteActivity.class);
                        intent.putExtra(EditNoteActivity.NOTE_ID_ARG, getSingleSelectedItemId());
                        startActivity(intent);
                        break;
                    case (R.id.delete_note):

                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

        });
    }

    private int getSingleSelectedItemId() {
        long[] checkedItemIds = getListView().getCheckedItemIds();
        return (int)checkedItemIds[0];
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

    private Cursor getNoteContentsCursor(String contentColumnName) {
        return readableDb.query(MarkItDownDbContract.Notes.TABLE_NAME,
                new String[]{MarkItDownDbContract.Notes.COLUMN_NAME_TITLE,
                        contentColumnName}, "_id = ?",
                new String[]{Integer.toString(getSingleSelectedItemId())}, null, null, null, null);
    }
}
