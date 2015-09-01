package iluxonchik.github.io.markitdown;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


public class NotebooksListCursorAdapter extends CursorAdapter {

    private final int NOTEBOOK_NAME_POS = 1;
    private final int NOTEBOOK_COLOR_POS = 2;

    public NotebooksListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.notebooks_list_item, parent, false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView notebookName = (TextView) view.findViewById(R.id.notebook_title);
        View colorTag = view.findViewById(R.id.notebook_color_tag);

        String name = cursor.getString(NOTEBOOK_NAME_POS);
        int color = cursor.getInt(NOTEBOOK_COLOR_POS);

        notebookName.setText(name);
        colorTag.setBackgroundColor(color);


    }
}
