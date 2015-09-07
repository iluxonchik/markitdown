package iluxonchik.github.io.markitdown.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import iluxonchik.github.io.markitdown.Defaults;
import iluxonchik.github.io.markitdown.NotebooksFragment;
import iluxonchik.github.io.markitdown.R;
import io.github.iluxonchik.colorpicker.ColorPickerDialog;

/**
 * New notebook dialog
 */
public class NewNotebookDialogFragment extends DialogFragment {
    private PositiveNegativeListener listener;
    private int selectedColor = Defaults.Colors.DEFAULT_COLOR;
    private String notebookTitle = null;

    public NewNotebookDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Resources res = getResources();
        final LayoutInflater factory = LayoutInflater.from(getActivity());
        final View dialogView = factory.inflate(R.layout.dialog_create_notebook, null);
        final ImageView colorImageView = (ImageView) dialogView.findViewById(R.id.notebook_color);
        final EditText notebookNameEditText = (EditText) dialogView.findViewById(R.id.note_title);
        setImageViewColor(colorImageView, selectedColor);
        colorImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder();
                builder.maxSelectedColors(1)
                        .colors(Defaults.Colors.COLOR_ARRAY)
                        .selectedColors(new int[]{selectedColor})
                        .setOnOkCancelPressListener(new ColorPickerDialog.OnOkCancelPressListener() {
                            @Override
                            public void onColorPickerDialogOkPressed(int[] selectedColors) {
                                if (selectedColors.length == 0) {
                                    // If no colors are selected, reset the notebook color to def
                                    selectedColor = Defaults.Colors.DEFAULT_COLOR;
                                } else {
                                    selectedColor = selectedColors[0];
                                }
                                setImageViewColor(colorImageView, selectedColor);
                            }

                            @Override
                            public void onColorPickerDialogCancelPressed(int[] selectedColors) {

                            }
                        });
                builder.build().show(getFragmentManager(), null);
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(factory.getContext());
        AlertDialog dialog = builder.setView(dialogView)
                .setTitle(R.string.dialog_new_notebook_title)
                .setPositiveButton(res.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notebookTitle = notebookNameEditText.getText().toString();
                        listener.onDialogPositiveClick(NewNotebookDialogFragment.this,
                                createNewNotebookBundle());
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notebookTitle = notebookNameEditText.getText().toString();
                        listener.onDialogPositiveClick(NewNotebookDialogFragment.this,
                                createNewNotebookBundle());
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

    private Bundle createNewNotebookBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(NotebooksFragment.EXTRA_NOTEBOOK_COLOR, selectedColor);
        bundle.putString(NotebooksFragment.EXTRA_NOTEBOOK_TITLE, notebookTitle);
        return bundle;
    }
}
