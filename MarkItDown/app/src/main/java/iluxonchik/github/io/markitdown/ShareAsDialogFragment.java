package iluxonchik.github.io.markitdown;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.app.DialogFragment;

import java.util.BitSet;

public class ShareAsDialogFragment extends DialogFragment {


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
                        // TODO: start share action with HTML
                        break;
                    case 1:
                        // Markdown
                        // TODO: start share action with Markdown
                        break;
                }
            }
        });
        return alertDialogBuilder.create();
    }


}
