package iluxonchik.github.io.markitdown;


import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

public class ViewNoteActivity extends AppCompatActivity implements ShareAsDialogFragment.OnShareAsOptionSelectedListener {

    private final String VIEW_NOTE_ACTIVITY_LOGTAG = "ViewNoteActivity";
    private final String SHARE_DIALOG_TAG = "ShareDialog";

    public static  final String EXTRA_NOTE_ID = "NoteId";
    private final int NULL_NOTE = -1;
    private int noteId;
    private BroadcastReceiver receiver;
    private MarkItDownDbHelper dbHelper;
    private SQLiteDatabase readableDb;
    private Cursor cursor;
    private String noteTitle;
    private String noteContent;

    private final int CURSOR_TITLE_POS = 0;
    private final int CURSOR_HTMLTEXT_POS = 1;
    private final int CURSOR_MDTEXT_POS = 0;
    private final String WEBVIEW_MIME = "text/html";
    private final String WEBVIEW_ENCODING = "utf-8";

    private final String NOTE_TITLE = "noteTile";
    private final String NOTE_CONTENT = "noteContent";
    private final String CAME_FROM_PAUSED_STATE = "cameFromPausedState";

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


        // Set the Toolbar to be the ActionBar
        Toolbar toolbar = (Toolbar)findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);

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

        if (savedInstanceState != null) {
            restoreNoteFromSavedInstanceState(savedInstanceState);
        } else {
             showNote();
        }
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.view_note_activity_title));
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NOTE_TITLE, noteTitle);
        outState.putString(NOTE_CONTENT, noteContent);
        outState.putBoolean(CAME_FROM_PAUSED_STATE, cameFromPausedState);
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

        switch (id) {
            case R.id.action_edit_note:
                editNote();
                return true;

            case R.id.action_share_note:
                shareNote();
                return true;

            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void editNote() {
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(EditNoteActivity.NOTE_ID_ARG, noteId);
        startActivity(intent);
    }

    private void shareNote() {
        showShareAsDialog();
    }

    private void showShareAsDialog() {
        ShareAsDialogFragment shareAsDialogFragment = new ShareAsDialogFragment();
        shareAsDialogFragment.show(getFragmentManager(), SHARE_DIALOG_TAG);
    }


    @Override
    public void onHTMLClicked(DialogFragment dialog) {
        //Log.d(VIEW_NOTE_ACTIVITY_LOGTAG, "onHTML called");
        ShareAsDialogFragment.startShareIntent(this, noteContent, noteTitle);
    }

    @Override
    public void onMarkdownClicked(DialogFragment dialog) {
        // Get Markdown from db and start an intent with it
        Cursor cursor = readableDb.query(MarkItDownDbContract.Notes.TABLE_NAME, new String[]{MarkItDownDbContract.Notes.COLUMN_NAME_TEXT_MARKDOWN},
                "_id = ?", new String [] { Integer.toString(noteId)},null, null, null, null);
        if (cursor.moveToFirst()) {
            ShareAsDialogFragment.startShareIntent(this, cursor.getString(CURSOR_MDTEXT_POS), noteTitle);

        } else {
            // TODO: raise an exception (?)
            Log.d(VIEW_NOTE_ACTIVITY_LOGTAG, "onMarkdownClicked(): cursor empty!");
        }
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

    private void restoreNoteFromSavedInstanceState(Bundle savedInstanceState) {

        Log.d(VIEW_NOTE_ACTIVITY_LOGTAG, "Restoring note from savedInstanceState");

        noteContent = savedInstanceState.getString(NOTE_CONTENT);
        noteTitle = savedInstanceState.getString(NOTE_TITLE);
        cameFromPausedState = savedInstanceState.getBoolean(CAME_FROM_PAUSED_STATE);
        ((TextView)findViewById(R.id.note_title)).setText(noteTitle);
        ((WebView)findViewById(R.id.note_content)).loadData(noteContent, WEBVIEW_MIME, WEBVIEW_ENCODING);
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

        noteContent = cursor.getString(CURSOR_HTMLTEXT_POS);
        webView.loadData(noteContent, WEBVIEW_MIME, WEBVIEW_ENCODING);
    }

    private void populateTextViewWithTitle(TextView textView, Cursor cursor) {

        if (!cursor.moveToFirst()) {
            Log.d(VIEW_NOTE_ACTIVITY_LOGTAG, "populateTextViewWithTitle(): Cursor is empty.");
            return;
        }
        noteTitle = cursor.getString(CURSOR_TITLE_POS);
        textView.setText(noteTitle);
    }

}
