package iluxonchik.github.io.markitdown;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MarkItDownDbHelper extends SQLiteOpenHelper {

    public MarkItDownDbHelper(Context context) {
        super(context, MarkItDownDbContract.DB_NAME, null, MarkItDownDbContract.DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MarkItDownDbContract.Colors.CREATE_TABLE);
        db.execSQL(MarkItDownDbContract.Notebooks.CREATE_TABLE);
        db.execSQL(MarkItDownDbContract.Notes.CREATE_TABLE);
        db.execSQL(MarkItDownDbContract.Tags.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Empty, since there are no available upgrades
    }
}
