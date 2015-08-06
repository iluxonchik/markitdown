package iluxonchik.github.io.markitdown;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ViewNoteActivity extends Activity {

    private final String VIEW_NOTE_ACTIVITY_LOGTAG = "ViewNoteActivity";

    public static  final String EXTRA_NOTE_ID = "NoteId";
    private final int NULL_NOTE = -1;
    private int noteId;
    private BroadcastReceiver receiver;
    private MarkItDownDbHelper dbHelper;
    private SQLiteDatabase readableDb;

    private final int CURSORT_TITLE_POS = 0;
    private final int CURSOR_HTMLTEXT_POS = 1;
    private final String WEBVIEW_MIME = "text/html";
    private final String WEBVIEW_ENCODING = "utf-8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        noteId = getIntent().getIntExtra(EXTRA_NOTE_ID, NULL_NOTE);

        Intent intent = new Intent(this, MarkdownToHTMLService.class);
        intent.putExtra(MarkdownToHTMLService.EXTRA_NOTE_ID, noteId);

        dbHelper = new MarkItDownDbHelper(this);
        readableDb = dbHelper.getReadableDatabase();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // HTML note ready, query db and display results
                Log.d(VIEW_NOTE_ACTIVITY_LOGTAG, "Received broadcast");
                ViewNoteActivity.this.populateWebViewWithNote((WebView) findViewById(R.id.note_content), getNoteCursor());
            }
        };

        startService(intent);

       // WebView webView = (WebView) findViewById(R.id.note_content);
       // webView.loadData(txtmark.mdToHTML(md), "text/html", "UTF-8");
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(MarkdownToHTMLService.ACTION_COMPLETE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Cursor getNoteCursor() {
        return readableDb.query(MarkItDownDbContract.Notes.TABLE_NAME,
                new String[]{MarkItDownDbContract.Notes.COLUMN_NAME_TITLE,
                        MarkItDownDbContract.Notes.COLUMN_NAME_TEXT_HTML},
                "_id = ?", new String[]{Integer.toString(noteId)}, null, null, null, null);
    }

    private void populateWebViewWithNote(WebView webView, Cursor cursor) {

        if (!cursor.moveToFirst()) {
            Log.d(VIEW_NOTE_ACTIVITY_LOGTAG, "populateWebViewWithNote(): Cursor is empty.");
            return;
        }

        webView.loadData(cursor.getString(CURSOR_HTMLTEXT_POS), WEBVIEW_MIME, WEBVIEW_ENCODING);
    }

}
