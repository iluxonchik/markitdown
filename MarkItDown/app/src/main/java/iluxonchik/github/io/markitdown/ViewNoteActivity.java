package iluxonchik.github.io.markitdown;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

public class ViewNoteActivity extends Activity {

    private final String VIEW_NOTE_ACTIVITY_LOGTAG = "ViewNoteActivity";

    public static  final String EXTRA_NOTE_ID = "NoteId";
    private final int NULL_NOTE = -1;
    private int noteId;
    private BroadcastReceiver receiver;
    private MarkItDownDbHelper dbHelper;
    private SQLiteDatabase readableDb;
    private Cursor cursor;

    private final int CURSOR_TITLE_POS = 0;
    private final int CURSOR_HTMLTEXT_POS = 1;
    private final String WEBVIEW_MIME = "text/html";
    private final String WEBVIEW_ENCODING = "utf-8";

    private boolean cameFromPausedState = false;

    private class EditCheckTask extends AsyncTask<Integer, Void, Boolean> {
        private final int CURSOR_EDITED_POS = 0;
        private final int TRUE = 1;

        @Override
        protected Boolean doInBackground(Integer... params) {
            Cursor cursor = readableDb.query(MarkItDownDbContract.Notes.TABLE_NAME,
                    new String[]{MarkItDownDbContract.Notes.COLUMN_NAME_EDITED}, "_id = ?",
                    new String[]{Integer.toString(params[0])}, null, null, null, null);

            if (!cursor.moveToFirst()) {
                Log.d(VIEW_NOTE_ACTIVITY_LOGTAG, "EiditCheckTask: cursor empty");
                return false;
            }

            return (cursor.getInt(CURSOR_EDITED_POS) == TRUE);
        }

        @Override
        protected void onPostExecute(Boolean noteEdited) {
            if (noteEdited) {
                Log.d(VIEW_NOTE_ACTIVITY_LOGTAG, "EiditCheckTask: note contents updated");
                updateNoteCursor();
                showNote();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        noteId = getIntent().getIntExtra(EXTRA_NOTE_ID, NULL_NOTE);

        dbHelper = new MarkItDownDbHelper(this);
        readableDb = dbHelper.getReadableDatabase();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // HTML note ready, query db and display results
                Log.d(VIEW_NOTE_ACTIVITY_LOGTAG, "Received broadcast");
                ViewNoteActivity.this.populateWebViewWithNote((WebView) findViewById(R.id.note_content), getCursor());
            }
        };

        showNote();
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(MarkdownToHTMLService.ACTION_COMPLETE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        if (cameFromPausedState)
            new EditCheckTask().execute(noteId);
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        cameFromPausedState = true;
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit_note) {
            Intent intent = new Intent(this, EditNoteActivity.class);
            intent.putExtra(EditNoteActivity.NOTE_ID_ARG, noteId);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Cursor getCursor() {
        if (cursor == null) {
            updateNoteCursor();
        }
        return cursor;
    }

    private Cursor getNewNoteCursor() {
        return readableDb.query(MarkItDownDbContract.Notes.TABLE_NAME,
                new String[]{MarkItDownDbContract.Notes.COLUMN_NAME_TITLE,
                        MarkItDownDbContract.Notes.COLUMN_NAME_TEXT_HTML},
                "_id = ?", new String[]{Integer.toString(noteId)}, null, null, null, null);
    }

    private void updateNoteCursor() {
        cursor = getNewNoteCursor();
    }

    private void showNote() {
        startMarkdownToHTMLService();
        populateTextViewWithTitle((TextView) findViewById(R.id.note_title), getCursor());
    }

    private void startMarkdownToHTMLService() {
        Intent intent = new Intent(this, MarkdownToHTMLService.class);
        intent.putExtra(MarkdownToHTMLService.EXTRA_NOTE_ID, noteId);
        startService(intent);
    }

    private void populateWebViewWithNote(WebView webView, Cursor cursor) {

        if (!cursor.moveToFirst()) {
            Log.d(VIEW_NOTE_ACTIVITY_LOGTAG, "populateWebViewWithNote(): Cursor is empty.");
            return;
        }

        webView.loadData(cursor.getString(CURSOR_HTMLTEXT_POS), WEBVIEW_MIME, WEBVIEW_ENCODING);
    }

    private void populateTextViewWithTitle(TextView textView, Cursor cursor) {

        if (!cursor.moveToFirst()) {
            Log.d(VIEW_NOTE_ACTIVITY_LOGTAG, "populateTextViewWithTitle(): Cursor is empty.");
            return;
        }
        textView.setText(cursor.getString(CURSOR_TITLE_POS));
    }

}
