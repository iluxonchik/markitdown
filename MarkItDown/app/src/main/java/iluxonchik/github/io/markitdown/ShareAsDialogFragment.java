package iluxonchik.github.io.markitdown;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.app.DialogFragment;

import java.util.BitSet;

public class ShareAsDialogFragment extends DialogFragment{

    private OnShareAsOptionSelectedListener listener;

    public interface OnShareAsOptionSelectedListener {
        public void onHTMLClicked(DialogFragment dialog);
        public void onMarkdownClicked(DialogFragment dialog);
    }

    public void registerOnOptionSelectedListener(OnShareAsOptionSelectedListener listener) {
        this.listener = listener;
    }

    public void removeOnOptionSelectedListener() {
        listener = null;
    }

    public ShareAsDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.action_shareAs);
        alertDialogBuilder.setItems(R.array.shareAs_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // HTML
                        listener.onHTMLClicked(ShareAsDialogFragment.this);
                        break;
                    case 1:
                        // Markdown
                        listener.onMarkdownClicked(ShareAsDialogFragment.this);
                        break;
                }
            }
        });
        return alertDialogBuilder.create();
    }

}
