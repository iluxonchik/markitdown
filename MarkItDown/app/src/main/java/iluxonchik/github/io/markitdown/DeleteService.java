package iluxonchik.github.io.markitdown;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This IntentService handles the deletion actions.
 */
public class DeleteService extends IntentService {

    private static final String ACTION_DELETE_NOTE = "iluxonchik.github.io.markitdown.action.DLETE_NOTE";
    private static final String ACTION_DELETE_NOTEBOOK = "iluxonchik.github.io.markitdown.action.DELETE_NOTEBOOK";

    private static final String EXTRA_NOTE_ID = "iluxonchik.github.io.markitdown.extra.NOTE_ID";
    private static final String EXTRA_NOTEBOOK_ID = "iluxonchik.github.io.markitdown.extra.NOTEBOOK_ID";

    private static final int DEFAULT_ID = -1;
    private final String DELETE_NOTE_SERVICE_LOGTAG = "DeleteService";

    private MarkItDownDbHelper dbHelper;
    private SQLiteDatabase writableDb;

    /**
     * Starts this service to perform action DELETE_NOTE with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    public static void startActionDeleteNote(Context context, int noteId) {
        Intent intent = new Intent(context, DeleteService.class);
        intent.setAction(ACTION_DELETE_NOTE);
        intent.putExtra(EXTRA_NOTE_ID, noteId);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action DELETE_NOTEBOOK with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, int notebookId) {
        Intent intent = new Intent(context, DeleteService.class);
        intent.setAction(ACTION_DELETE_NOTEBOOK);
        intent.putExtra(EXTRA_NOTEBOOK_ID, notebookId);
        context.startService(intent);
    }

    public DeleteService() {
        super("DeleteService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DELETE_NOTE.equals(action)) {
                final int noteId = intent.getIntExtra(EXTRA_NOTE_ID, DEFAULT_ID);
                handleActionDeleteNote(noteId);
            } else if (ACTION_DELETE_NOTEBOOK.equals(action)) {
                final int notebookId = intent.getIntExtra(EXTRA_NOTEBOOK_ID, DEFAULT_ID);
                handleActionDeleteNotebook(notebookId);
            }
        }
    }

    /**
     * Handle action DeleteNote in the provided background thread with the provided
     * parameters.
     */
    private void handleActionDeleteNote(int noteId) {
        if (noteId == DEFAULT_ID) {
            // Something went wrong...
            Log.d(DELETE_NOTE_SERVICE_LOGTAG, "noteId = -1");
            return;
        }
        dbHelper = new MarkItDownDbHelper(this);
        writableDb = dbHelper.getWritableDatabase();
        // Remove note references from "Notes" table
        deleteFromNotesTable(noteId);
    }

    /**
     * Handle action DeleteNotebook in the provided background thread with the provided
     * parameters.
     */
    private void handleActionDeleteNotebook(int notebookId) {
        // TODO: Handle action DeleteNote
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        // Cleanup
        if (writableDb != null) {
            writableDb.close();
        }

        if(dbHelper != null) {
            dbHelper.close();
        }
    }

    private void deleteFromNotesTable(int noteId) {
        writableDb.delete(MarkItDownDbContract.Notes.TABLE_NAME, "_id = ?", new String[]{Integer.toString(noteId)});
    }
}
