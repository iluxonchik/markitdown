package iluxonchik.github.io.markitdown;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
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
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import iluxonchik.github.io.markitdown.database.MarkItDownDbContract;
import iluxonchik.github.io.markitdown.database.MarkitDownDbCursor;
import iluxonchik.github.io.markitdown.dialog.NewNotebookDialogFragment;
import iluxonchik.github.io.markitdown.dialog.PositiveNegativeListener;
import iluxonchik.github.io.markitdown.services.DeleteService;

public class NotebooksFragment extends DatabaseCABListFragment implements PositiveNegativeListener {

    public static final String EXTRA_NOTEBOOK_TITLE = "notebookName";
    public static final String EXTRA_NOTEBOOK_COLOR = "notebookColor";

    private NotebooksListCursorAdapter cursorAdapter;
    private FragmentCommunicationContract.OnMessageSendingNeeded listenerActivity;

    private FloatingActionButton newNotebookFAB;

    public NotebooksFragment() {

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshNotebookListCursor();
        }
    };

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        try {
            this.listenerActivity =
                    (FragmentCommunicationContract.OnMessageSendingNeeded)context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity that uses "+
                    this.getClass().getCanonicalName() + " must implement " +
                    FragmentCommunicationContract.
                            OnMessageSendingNeeded.class.getCanonicalName());
        }
    }

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

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openNotebook(view, id);
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        Cursor cursor = MarkitDownDbCursor.Notebooks.createNotebookListCursor(readableDb);
        cursorAdapter = new NotebooksListCursorAdapter(getActivity(), cursor, 0);
        setListAdapter(cursorAdapter);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(DeleteService.ACTION_DELETE_NOTEBOOK));
    }

    @Override
    public void onDestroy() {
        closeDatabase();
        super.onDestroy();
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Bundle args) {
        // Add notebook to db
        newCreateNotebookTask().execute(args);
        
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, Bundle args) {

    }


    private void refreshNotebookListCursor() {
        if (cursorAdapter != null) {
            cursorAdapter.swapCursor(MarkitDownDbCursor.Notebooks
                    .createNotebookListCursor(readableDb));
        }
    }

    private AsyncTask<Bundle, Void, Void> newCreateNotebookTask() {
        return new AsyncTask<Bundle, Void, Void>() {

            @Override
            protected Void doInBackground(Bundle... params) {
                String title = params[0].getString(NotebooksFragment.EXTRA_NOTEBOOK_TITLE);
                int color = params[0].getInt(NotebooksFragment.EXTRA_NOTEBOOK_COLOR);

                SQLiteDatabase writableDb = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MarkItDownDbContract.Notebooks.COLUMN_NAME_TITLE, title);
                contentValues.put(MarkItDownDbContract.Notebooks.COLUMN_NAME_COLOR, color);
                writableDb.insert(MarkItDownDbContract.Notebooks.TABLE_NAME, null, contentValues);

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                // Swap cursor adapter
                refreshNotebookListCursor();
            }
        };
    }

    private void invertMenuOptions(Menu menu) {
        final int OPEN_POS = 0;
        menu.getItem(OPEN_POS).setEnabled(!menu.getItem(OPEN_POS).isEnabled());
    }

    // Overrides from parent class
    @Override
    protected void handleOnDestroyActionMode(ActionMode mode) {
        newNotebookFAB.show();
    }

    @Override
    protected boolean handleOnActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.action_open_notebook):
                    openNotebook(getListView(), getSingleSelectedItemId());
                    mode.finish();
                break;
            case (R.id.action_delete_notebook):
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSelectedNotebooks();
                    }
                });
                builder.setNegativeButton(android.R.string.no, null);
                setAlertDialogText(builder);
                builder.show();
                break;

        }

        return false;
    }

    private void deleteSelectedNotebooks() {
        long[] selectedNotebooksIds = getSelectedItemIds();
        for (long id : selectedNotebooksIds) {
            DeleteService.startActionDeleteNotebook(getActivity(), (int)id);
        }

    }


    @Override
    protected void handleOnCreateActionMode(ActionMode mode, Menu menu) {
        newNotebookFAB.hide();
    }

    @Override
    protected void handleFromSingleCheckedItemToMultiple(ActionMode mode, int position, long id, boolean checked) {
        invertMenuOptions(mode.getMenu());
    }

    @Override
    protected void handleFromMultipleCheckedItemsToSingle(ActionMode mode, int position, long id, boolean checked) {
        invertMenuOptions(mode.getMenu());
    }

    @Override
    protected void inflateContextMenu(ActionMode mode, Menu menu) {
        MenuInflater menuInflater = mode.getMenuInflater();
        menuInflater.inflate(R.menu.menu_notebooks_context, menu);
    }

    private void openNotebook(View view, long id) {
        Bundle bundle = new Bundle();
        TextView notebookTitle = (TextView) view.findViewById(R.id.notebook_title);
        bundle.putString(FragmentCommunicationContract.
                SetTitleBarTitle.ARG_FRAGMENT_TITLE, notebookTitle.getText()
                .toString());
        bundle.putString(FragmentCommunicationContract.
                        StartNotesFromNotebooks.ARG_CUSTOM_SELECTION,
                MarkItDownDbContract.Notes.COLUMN_NAME_NOTEBOOK + "=?");
        bundle.putStringArray(FragmentCommunicationContract.
                        StartNotesFromNotebooks.ARG_CUSTOM_SELECTION_ARGS,
                new String[]{Long.toString(id)});

        listenerActivity.onMessageSent(
                FragmentCommunicationContract.Message.MESSAGE_START_NOTES_FROM_NOTEBOOKS,
                bundle
        );
    }

    public void setAlertDialogText(AlertDialog.Builder builder) {
        int titleResId;
        int messageResId;
        int numSelectedNotes = getListView().getCheckedItemCount();

        if (numSelectedNotes > 1) {
            titleResId = R.string.dialog_delete_multiple_notebooks_question;
            messageResId = R.string.dialog_delete_multiple_notebooks_text;
        } else {
            titleResId = R.string.dialog_delete_single_notebook_question;
            messageResId = R.string.dialog_delete_single_notebook_text;
        }

        builder.setTitle(titleResId).setMessage(messageResId);
    }
}
