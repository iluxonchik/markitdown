package iluxonchik.github.io.markitdown;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.webkit.WebView;

public class MarkdownToHTMLService extends IntentService {

    private static final String ACTION_COMPLETE = "iluxonchik.github.io.markitdown.action.COMPLETE";
    public static final String EXTRA_NOTE_ID = "iluxonchik.github.io.markitdown.extra.NOTE_ID";
    private final int NULL_NOTE = -1;
    private final int EDITED_POS = 0;
    private final int FALSE = 0;
    private final int MD_POS = 0;
    private final String LIMIT_1 = "1";

    private final String MD_TO_HTML_SERVICE_LOGTAG = "MarkdownToHTMLService";


    private int noteId;
    private MarkItDownDbHelper dbHelper;
    private SQLiteDatabase readableDb;
    private Cursor cursor;

    public MarkdownToHTMLService() {
        super("MarkdownToHTMLService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(MD_TO_HTML_SERVICE_LOGTAG, "Starting service...");

        if (intent == null) {
            Log.d(MD_TO_HTML_SERVICE_LOGTAG, "Intent null");
            return;
        }
        noteId = intent.getIntExtra(EXTRA_NOTE_ID, NULL_NOTE);

        if (noteId == NULL_NOTE) {
            Log.d(MD_TO_HTML_SERVICE_LOGTAG, "Note ID == -1");
            return;
        }

        dbHelper = new MarkItDownDbHelper(this);
        readableDb = dbHelper.getReadableDatabase();

        cursor = readableDb.query(MarkItDownDbContract.Notes.TABLE_NAME,
                new String[]{MarkItDownDbContract.Notes.COLUMN_NAME_EDITED}, "_id = ?",
                new String[]{Integer.toString(noteId)}, null, null, null, LIMIT_1);

        if (cursor.moveToFirst() == false) {
            Log.d(MD_TO_HTML_SERVICE_LOGTAG, "Cursor empty.");
            return;
        }

        int isNoteEdited = cursor.getInt(EDITED_POS);
        if (isNoteEdited == FALSE) {
            // TODO: broadcast that note is updated
            Log.d(MD_TO_HTML_SERVICE_LOGTAG, "Note has an updated HTML");
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_COMPLETE));
            return;
        }

        // TODO: convert md to html, store html in db, set edited column val to 0, broadcast that
        // note is updated.

        // Get markdown text
        cursor = readableDb.query(MarkItDownDbContract.Notes.TABLE_NAME,
                new String[]{MarkItDownDbContract.Notes.COLUMN_NAME_TEXT_MARKDOWN}, "_id = ?",
                new String[]{Integer.toString(noteId)}, null, null, null, LIMIT_1);

        if (cursor.moveToFirst() == false) {
            Log.d(MD_TO_HTML_SERVICE_LOGTAG, "Cursor empty.");
            return;
        }

        // Convert markdown to HTML
        MarkdownProcessor txtmark = new TxtmarkProcessor();
        String html = txtmark.mdToHTML(cursor.getString(MD_POS));
        Log.d(MD_TO_HTML_SERVICE_LOGTAG, "HTML result: \n" + html);

        // Put HTML in DB
        SQLiteDatabase writableDb = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MarkItDownDbContract.Notes.COLUMN_NAME_TEXT_HTML, html);
        contentValues.put(MarkItDownDbContract.Notes.COLUMN_NAME_EDITED, FALSE);

        writableDb.update(MarkItDownDbContract.Notes.TABLE_NAME, contentValues, "_id = ?",
                new String[]{Integer.toString(noteId)});

        Log.d(MD_TO_HTML_SERVICE_LOGTAG, "Updated db");

        writableDb.close();

        // Broadcast
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_COMPLETE));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (cursor != null) {
            Log.d(MD_TO_HTML_SERVICE_LOGTAG, "Closing cursor");
            cursor.close();
        }

        if (readableDb != null) {
            Log.d(MD_TO_HTML_SERVICE_LOGTAG, "Closing readableDb");
            readableDb.close();
        }

        if (dbHelper != null) {
            Log.d(MD_TO_HTML_SERVICE_LOGTAG, "Closing dbHelper");
            dbHelper.close();
        }
    }

}
