package iluxonchik.github.io.markitdown;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class NotesListCursorAdapter extends CursorAdapter {



    private final int ID_POS = 0;
    private final int TITLE_POS = 1;
    private final int DATE_POS = 2;
    private final String DATE_FORMAT = "dd-MM-yyyy HH:mm"; // TODO: allow user to personalize (store in preferences (?))

    public NotesListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return  LayoutInflater.from(context).inflate(R.layout.notes_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Set title
        TextView noteTitle = (TextView) view.findViewById(R.id.item_title);
        noteTitle.setText(cursor.getString(TITLE_POS));

        noteTitle = (TextView) view.findViewById(R.id.item_date);

        // Get date in millis from cursor, convert it to String and set date in list item
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        calendar.setTimeInMillis(cursor.getLong(DATE_POS));
        noteTitle.setText(formatter.format(calendar.getTime()));

        // TODO: is this tag even necessary?
        view.setTag(new Integer(cursor.getInt(ID_POS)));
    }
}
