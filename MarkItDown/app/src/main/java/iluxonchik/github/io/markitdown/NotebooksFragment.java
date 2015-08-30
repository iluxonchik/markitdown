package iluxonchik.github.io.markitdown;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import javax.xml.transform.Result;

public class NotebooksFragment extends DatabaseListFragment implements PositiveNegativeListener{

    public static final String EXTRA_NOTEBOOK_NAME = "notebookName";
    public static final String EXTRA_NOTEBOOK_COLOR = "notebookColor";

    private AsyncTask<Bundle, Void, Void>  createNotebookTask = new AsyncTask<Bundle, Void, Void>() {

        @Override
        protected Void doInBackground(Bundle... params) {
            String name = params[0].getString(NotebooksFragment.EXTRA_NOTEBOOK_NAME);
            int color = params[0].getInt(NotebooksFragment.EXTRA_NOTEBOOK_COLOR);

            SQLiteDatabase writableDb = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MarkItDownDbContract.Notebooks.COLUMN_NAME_TITLE, name);
            contentValues.put(MarkItDownDbContract.Notebooks.COLUMN_NAME_COLOR, color);
            writableDb.insert(MarkItDownDbContract.Notebooks.TABLE_NAME, null, contentValues);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            // Swap cursor adapter
        }
    };

    private FloatingActionButton newNotebookFAB;

    public NotebooksFragment() {

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: swap cursor
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openDatabase();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_items_list, container, false);

        newNotebookFAB = (FloatingActionButton)v.findViewById(R.id.addItemFAB);
        newNotebookFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: show create notebook dialog
                DialogFragment newNotebookDialog = new NewNotebookDialogFragment();
                newNotebookDialog.setTargetFragment(NotebooksFragment.this, 0);
                newNotebookDialog.show(getFragmentManager(), null);

            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceSate) {
        super.onActivityCreated(savedInstanceSate);
        final ListView listView = getListView();

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(DeleteService.ACTION_DELETE_NOTEBOOK));
    }

    @Override
    public void onDestroy() {
        closeDatabase();
        super.onDestroy();
    }

    private Cursor createNotebooksListCursor() {
        return readableDb.query(MarkItDownDbContract.Notebooks.TABLE_NAME,
                new String[] {MarkItDownDbContract.Notebooks.COLUMN_NAME_TITLE, MarkItDownDbContract.Notebooks.COLUMN_NAME_COLOR},
                null, null, null, null, null);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Bundle args) {
        // Add notebook to db
        createNotebookTask.execute(args);
        
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, Bundle args) {

    }
}
