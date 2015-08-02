package iluxonchik.github.io.markitdown;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.zip.Inflater;

public class NotesFragment extends ListFragment {

    private MarkItDownDbHelper dbHelper;
    private SQLiteDatabase readableDb;

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

        //notesList.setAdapter(adapter);
        setListAdapter(cursorAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Integer note_id = (Integer)v.getTag();
        // TODO: send to ViewNote activity, for now just takes to edit note
        Intent intent = new Intent(getActivity(), EditNoteActivity.class);
        intent.putExtra(EditNoteActivity.NOTE_ID_ARG, note_id);
        startActivity(intent);

        super.onListItemClick(l, v, position, id);
    }

}
