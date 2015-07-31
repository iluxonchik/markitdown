package iluxonchik.github.io.markitdown;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditNoteFragment extends Fragment {

    public static final String NOTE_ID_ARG = "noteId";
    private static final String NOTE_TITLE = "noteTitle";
    private static final String NOTE_CONTENT = "noteContent";

    private int noteId;
    private MarkItDownDbHelper dbHelper;
    private SQLiteDatabase readableDb;
    private EditText noteTitle;
    private EditText noteContent;

    public EditNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new MarkItDownDbHelper(getActivity());
        readableDb = dbHelper.getReadableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_edit_note, container, false);
        
        noteTitle = (EditText)fragmentView.findViewById(R.id.note_title);
        noteContent = (EditText)fragmentView.findViewById(R.id.note_content);

        if (savedInstanceState == null) {
            // No savedInstanceState, run db query and populate from the resulting cursor
            populateContents(getNotesCursor(getArguments()), true);
        } else {
            populateContents(savedInstanceState);
        }

        return fragmentView;
    }

    private void populateContents(Bundle savedInstanceState) {
        /*
            Populate contents from savedInstanceState
        */
        noteTitle.setText(savedInstanceState.getString(NOTE_TITLE));
        noteContent.setText(savedInstanceState.getString(NOTE_CONTENT));
    }

    private void populateContents(Cursor notesCursor, boolean closeCursor) {
        /*
            Populate "Title" and "Text Content" fields with text
        */
        final int TITLE_POS = 0;
        final int CONTENT_POS = 1;
        final int TAGS_POS = 2;

        if (notesCursor == null) {
            return;
        }

        if (notesCursor.moveToFirst()) {
            noteTitle.setText(notesCursor.getString(TITLE_POS));
            noteContent.setText(notesCursor.getString(CONTENT_POS));
            // TODO: treat TAGS
        }

        if(closeCursor) {
            notesCursor.close();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(NOTE_TITLE, noteTitle.getText().toString());
        savedInstanceState.putString(NOTE_CONTENT, noteContent.getText().toString());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        readableDb.close();
        dbHelper.close();
    }


    private Cursor getNotesCursor(Bundle args) {
        /* Get the id of the note to be edited. If it's a new note, then its id will be set to -1.
           Whoever starts this fragment is responsible for calling .setArguments() to pass the note
           id to edit.
        */

        if (args == null) {
            // If the fragment hasn't been passed any values, treat as a new note
            noteId = -1;
            return null;
        }

        noteId = args.getInt(NOTE_ID_ARG, -1);
        if (noteId == -1) {
            return null;
        } else {

            return readableDb.query(MarkItDownDbContract.Notes.TABLE_NAME,
                    new String[]{ MarkItDownDbContract.Notes.COLUMN_NAME_TITLE, MarkItDownDbContract.Notes.COLUMN_NAME_TEXT_MARKDOWN,
                    MarkItDownDbContract.Notes.COLUMN_NAME_TAGS}, "_id = ?", new String[]{ Integer.toString(noteId) },
                    null, null, null, Integer.toString(1));
        }
    }

    private void saveCurrentNote() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues noteValues = new ContentValues();
        noteValues.put(MarkItDownDbContract.Notes.COLUMN_NAME_TITLE, noteTitle.getText().toString());
        noteValues.put(MarkItDownDbContract.Notes.COLUMN_NAME_TEXT_MARKDOWN, noteContent.getText().toString());
        noteValues.put(MarkItDownDbContract.Notes.COLUMN_NAME_EDITED, 1);
        if (noteId == -1) {
            // Create new note
            db.insert(MarkItDownDbContract.Notes.TABLE_NAME, null, noteValues);
        } else {
            // Update existing note
            db.update(MarkItDownDbContract.Notes.TABLE_NAME, noteValues,
                    "_id = ?", new String[]{ Integer.toString(noteId) });
        }
        db.close();

        Toast.makeText(getActivity(), R.string.note_saved_msg , Toast.LENGTH_SHORT).show();
    }
}

/*
    --EXAMPLE--
    Starting this fragment to edit note with _id = 1:

                fragment = new EditNoteFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(EditNoteFragment.NOTE_ID_ARG, 1);
                fragment.setArguments(bundle);
 */