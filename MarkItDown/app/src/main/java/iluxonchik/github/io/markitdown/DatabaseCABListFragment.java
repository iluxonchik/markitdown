package iluxonchik.github.io.markitdown;

import android.app.Activity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * A list fragment that uses a database to fill the ListView and has a CAB.
 */
public abstract class DatabaseCABListFragment extends DatabaseListFragment {

    public interface OnCABStatusChangedListener {
        void onCABCreate();
        void onCABDestroy();
    }

    private OnCABStatusChangedListener onCABStatusChangedListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onCABStatusChangedListener = (OnCABStatusChangedListener)activity;

        } catch (ClassCastException e) {
            throw new ClassCastException("Activity that uses "+
                    this.getClass().getCanonicalName() + " must implement " +
                    OnCABStatusChangedListener.class.getCanonicalName());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        //listView.setSelector(R.drawable.list_selector);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            private boolean multipleItemsChecked = false;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // TODO: REFRACTOR nested if's
                if (multipleItemsChecked) {
                    if (listView.getCheckedItemCount() == 1) {
                        // Passing from multiple checked items to one
                        multipleItemsChecked = false;
                        handleFromMultipleCheckedItemsToSingle(mode, position, id, checked);
                    }
                } else {
                    if (listView.getCheckedItemCount() > 1) {
                        // Passing to multiple checked items
                        multipleItemsChecked = true;
                        handleFromSingleCheckedItemToMultiple(mode, position, id, checked);
                    }
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate menu
                inflateContextMenu(mode, menu);
                onCABStatusChangedListener.onCABCreate();
                handleOnCreateActionMode(mode, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return handleOnPrepareActionMode(mode, menu);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return handleOnActionItemClicked(mode, item);
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                onCABStatusChangedListener.onCABDestroy();
                handleOnDestroyActionMode(mode);
            }

        });
    }

    protected boolean handleOnPrepareActionMode(ActionMode mode, Menu menu) { return false; }

    protected abstract void inflateContextMenu(ActionMode mode, Menu menu);
    protected abstract void handleOnDestroyActionMode(ActionMode mode);
    protected abstract boolean handleOnActionItemClicked(ActionMode mode, MenuItem item);
    protected abstract void handleOnCreateActionMode(ActionMode mode, Menu menu);
    protected abstract void handleFromSingleCheckedItemToMultiple(ActionMode mode, int position,
                                                                  long id, boolean checked);
    protected abstract void handleFromMultipleCheckedItemsToSingle(ActionMode mode, int position,
                                                                   long id, boolean checked);
}
