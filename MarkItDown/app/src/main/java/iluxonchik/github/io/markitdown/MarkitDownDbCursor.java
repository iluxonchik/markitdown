package iluxonchik.github.io.markitdown;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

// TODO: move the rest of the cursors here

/**
 * Contains cursor getters, so that those can be accessible from any part of the app.
 * This class should never be instantiated directly
 */
public final class MarkitDownDbCursor {

    private MarkitDownDbCursor() { }

    /**
     * Cursors related to the Notebooks table.
     */
    public static class Notebooks {

        public static Cursor createNotebookListCursor(SQLiteDatabase readableDb) {
            return readableDb.query(MarkItDownDbContract.Notebooks.TABLE_NAME,
                    new String[] {MarkItDownDbContract.Notebooks._ID,
                            MarkItDownDbContract.Notebooks.COLUMN_NAME_TITLE,
                            MarkItDownDbContract.Notebooks.COLUMN_NAME_COLOR},
                    null, null, null, null, null);
        }
    }
}
