package iluxonchik.github.io.markitdown;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * New notebook dialog
 */
public class NewNotebookDialogFragment extends DialogFragment {
    private PositiveNegativeListener listener;
    private int selectedColor = Color.TRANSPARENT;

    public NewNotebookDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Resources res = getResources();
        final LayoutInflater factory = LayoutInflater.from(getActivity());
        final View dialogView = factory.inflate(R.layout.dialog_create_notebook, null);
        ImageView colorImageView = (ImageView) dialogView.findViewById(R.id.notebookColor);
        setImageViewColor(colorImageView, Color.BLACK);
        colorImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                  * 1. Show color picker, read color
                  * 2. Change rectangle's color.
                  * // store color in a var and put in Bundle later
                 */

                Toast.makeText(getActivity(), "ColorPickerDialog", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(factory.getContext());
        AlertDialog dialog = builder.setView(dialogView)
                .setTitle(R.string.dialog_new_notebook_title)
                .setPositiveButton(res.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO
                        listener.onDialogPositiveClick(NewNotebookDialogFragment.this, null);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO
                        listener.onDialogPositiveClick(NewNotebookDialogFragment.this, null);
                    }
                }).create();

        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Fragment frag = getTargetFragment();

        if(! (frag instanceof PositiveNegativeListener)) {
            throw new ClassCastException("Starting activity or fragment must implement " +
            PositiveNegativeListener.class.getCanonicalName());
        }

        setOnDialogClickListener((PositiveNegativeListener) frag);

    }

    private void setImageViewColor(ImageView imageView, int color) {
        GradientDrawable shape = (GradientDrawable)imageView.getBackground();
        shape.setColor(color);
    }

    private void setOnDialogClickListener(PositiveNegativeListener listener) {
        this.listener = listener;
    }
}
