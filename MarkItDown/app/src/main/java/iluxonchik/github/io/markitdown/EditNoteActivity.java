package iluxonchik.github.io.markitdown;


import android.app.DialogFragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditNoteActivity extends AppCompatActivity implements PositiveNegativeListener {

    public static final String NOTE_ID_ARG = "noteId";
    private final String NOTE_TITLE = "noteTitle";
    private final String NOTE_CONTENT = "noteContent";
    private final String TEXT_CHANGED = "textChanged";

    private final String EDIT_NOTE_ACTIVITY_LOGTAG = "EditNoteActivity";

    private int noteId;
    private MarkItDownDbHelper dbHelper;
    private SQLiteDatabase readableDb;
    private EditText noteTitle;
    private EditText noteContent;
    private boolean textChanged; // indicates whether the title or content has changed
    private boolean dialogShown;

    public EditNoteActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        dbHelper = new MarkItDownDbHelper(this);
        readableDb = dbHelper.getReadableDatabase();
        textChanged = false;


        // Set the Toolbar to be the ActionBar
        Toolbar toolbar = (Toolbar)findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        noteTitle = (EditText)findViewById(R.id.note_title);
        noteContent = (EditText)findViewById(R.id.note_content);

        if (savedInstanceState == null) {
            // No savedInstanceState, run db query and populate from the resulting cursor
            populateContents(getNotesCursor(getIntent().getExtras()), true);
        } else {
            textChanged = savedInstanceState.getBoolean(TEXT_CHANGED);
            populateContents(savedInstanceState);
        }

        addTextChangedListener();

    }

    private void addTextChangedListener() {
        // TODO: this code ain't good :\
        /*
        * Adds TextChanged listener to note title and content, only if necessary (i.e. if
        * textChanged = false, since their sole purpose is to set that var to true if some part
        * of the title or content gets changed.
        *
        */

        if (!textChanged) {
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d(EDIT_NOTE_ACTIVITY_LOGTAG, "Key pressed, removing listener.");
                    textChanged = true;
                    Log.d(EDIT_NOTE_ACTIVITY_LOGTAG, "afterTextChanged(): textChanged = " + Boolean.toString(textChanged));
                    noteTitle.removeTextChangedListener(this);
                    noteContent.removeTextChangedListener(this);
                }
            };

            noteTitle.addTextChangedListener(textWatcher);
            noteContent.addTextChangedListener(textWatcher);
        }
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
            textChanged = false;
            // TODO: treat TAGS
        }

        if(closeCursor) {
            notesCursor.close();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(EDIT_NOTE_ACTIVITY_LOGTAG, "onBackPressed(): textChanged = " + Boolean.toString(textChanged));
        if (textChanged) {
            showSaveNoteDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (textChanged) {
                    showSaveNoteDialog();
                } else {
                    onBackPressed();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(NOTE_TITLE, noteTitle.getText().toString());
        savedInstanceState.putString(NOTE_CONTENT, noteContent.getText().toString());
        savedInstanceState.putBoolean(TEXT_CHANGED, textChanged);
    }

    @Override
    public void onStop() {
        readableDb.close();
        dbHelper.close();
        super.onStop();
    }

    public void showSaveNoteDialog() {
        SaveNoteDialogFragment dialog = new SaveNoteDialogFragment();
        dialog.show(getFragmentManager(), null);
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
        noteValues.put(MarkItDownDbContract.Notes.COLUMN_NAME_DATE_SAVED, Calendar.getInstance().getTimeInMillis());
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

        Toast.makeText(this, R.string.note_saved_msg , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Bundle bundle) {
        saveCurrentNote();
        super.onBackPressed();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, Bundle bundle) {
        // Don't save note
        super.onBackPressed();
    }

}
