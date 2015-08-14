package iluxonchik.github.io.markitdown;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
    private static final String INTENT_TYPE_TEXT_PLAIN = "text/plain";

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

    @Override
    public void onAttach(Activity activity) {
        try {
            registerOnOptionSelectedListener((OnShareAsOptionSelectedListener)activity);

        } catch (ClassCastException e) {
            throw new ClassCastException("Activity that uses "+ this.getClass().getCanonicalName() + " must implement " +
                    OnShareAsOptionSelectedListener.class.getCanonicalName());
        }
        super.onAttach(activity);
    }

    public static void startShareIntent(Context context, String shareTextContent, String shareTextTitle) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, shareTextContent);
        intent.putExtra(Intent.EXTRA_TITLE, shareTextTitle);
        intent.setType(INTENT_TYPE_TEXT_PLAIN);
        intent = Intent.createChooser(intent, context.getString(R.string.share_note_action_text));
        context.startActivity(intent);
    }

}
