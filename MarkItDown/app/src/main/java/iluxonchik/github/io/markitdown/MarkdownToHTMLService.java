package iluxonchik.github.io.markitdown;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MarkdownToHTMLService extends IntentService {

    private static final String ACTION_COMPLETE = "iluxonchik.github.io.markitdown.action.COMPLETE";
    private static final String EXTRA_NOTE_ID = "iluxonchik.github.io.markitdown.extra.NOTE_ID";
    private final int NULL_NOTE = -1;
    private final int EDITED_POS = 0;
    private final int FALSE = 0;


    private int noteId;
    private MarkItDownDbHelper dbHelper;
    private SQLiteDatabase readableDb;

    public MarkdownToHTMLService() {
        super("MarkdownToHTMLService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) return;
        noteId = intent.getIntExtra(EXTRA_NOTE_ID, -1);
        if (noteId == NULL_NOTE) return;
        dbHelper = new MarkItDownDbHelper(this);
        readableDb = dbHelper.getReadableDatabase();

        Cursor cursor = readableDb.query(MarkItDownDbContract.Notes.TABLE_NAME,
                new String[]{ MarkItDownDbContract.Notes.COLUMN_NAME_EDITED}, "_id = ?",
                new String[]{Integer.toString(noteId)}, null, null, null, null);

        if (cursor.moveToFirst() == false) {
            cleanup();
            return;
        }

        int isNoteEdited = cursor.getInt(EDITED_POS);

        if (isNoteEdited == FALSE) {
            // TODO: broadcast that note is updated
        }

        // TODO: convert md to html, store html in db, set edited column val to 0, broadcast that
        // note is updated.





    }

    private void cleanup() {
        // TODO
    }
}
