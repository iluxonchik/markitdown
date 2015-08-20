package iluxonchik.github.io.markitdown;

import android.app.ListFragment;
import android.database.sqlite.SQLiteDatabase;

/**
 * A ListFragment that uses a database to fill the ListView.
 */
public abstract class DatabaseListFragment extends ListFragment{

    protected MarkItDownDbHelper dbHelper;
    protected SQLiteDatabase readableDb;

    protected void openDatabase() {
        dbHelper = new MarkItDownDbHelper(getActivity());
        readableDb = dbHelper.getReadableDatabase();
    }

    protected void closeDatabase() {
        if (readableDb != null) {
            readableDb.close();
        }

        if (dbHelper != null) {
            dbHelper.close();
        }
    }

}
