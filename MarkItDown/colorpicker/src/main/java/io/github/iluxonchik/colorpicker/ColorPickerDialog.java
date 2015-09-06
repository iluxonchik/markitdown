package io.github.iluxonchik.colorpicker;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import java.util.HashMap;

/**
 * A dialog which takes in a input an array of colors and creates a palette allowing the user to
 * select one or more color swatches. There are two versions of the dialog: material and default.
 */
public class ColorPickerDialog extends DialogFragment implements ColorPickerSwatch.OnColorSelectedListener {

    /**
     * Builder for {@link #ColorPickerDialog}.
     */
    public static class Builder {

        private final String NON_POSITIVE_NUM_MSG = "Argument must be greater than zero";

        // Optional parameters, initialized to default values
        private int titleResId = R.string.dialog_title;
        private int[] colors = new int[] {-13388315, -5609780, -6697984, -17613, -48060, -16737844
                -6736948, -10053376, -30720, -3407872, -1118482, -3355444, -7829368};
        private String[] colorContentDescriptions = null;
        private int[] selectedColors = new int[0];
        private int numColumns = 4;
        private int swatchSize = ColorPickerDialog.SIZE_SMALL;
        private int maxSelectedColors = 1;
        private boolean useMaterial = false;
        private boolean useDefaultColorContentDescriptions = true;
        private OnOkCancelPressListener listener;

        public Builder() { }

        /**
         * Set the title using the given resource id. Default value is "Select a Color".
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder titleResId(int value) {this.titleResId = value; return this; }

        /**
         * Set the color array to be displayed in the palette.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder colors(int[] value) {this.colors = value; return this; }

        /**
         * Set which colors are selected in a palette. Assumes that there are no duplicate colors.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder selectedColors(int[] value) {
            this.selectedColors = value;
            return this;
        }

        /**
         * Sets the number of columns in the palette. Default value is 4.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder numColumns(int value) { this.numColumns = value; return this; }

        /**
         *  Sets the size of the color swatch. Size should be a pre-defined size (SIZE_LARGE
         * or SIZE_SMALL) from ColorPickerDialogFragment.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder swatchSize(int value) {
            positiveIntegerCheck(value, NON_POSITIVE_NUM_MSG);
            this.swatchSize = value;
            return this;
        }

        /**
         * Sets the maximum possible number of selected colors in the palette. The user can select
         * less, but no more than the specified value. If the user tries to select more colors than
         * this value, a color will be de-selected before selecting the one pressed by the user,
         * according to the following rules:
         *
         * 1. If it's the first color to be selected since the dialog instantiation, the first color
         * in the selected colors array will be de-selected.
         * 2. If it's not the first color to be selected sine the dialog instantiation, the last
         * selected color will be de-selected.
         *
         * If no parameter is specified, the default value 1 is used, which represents a dialog,
         * where the user can select a single color. The parameter must be greater than zero.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder maxSelectedColors(int value) {
            positiveIntegerCheck(value, NON_POSITIVE_NUM_MSG);
            this.maxSelectedColors = value;
            return this;
        }

        /**
         * Sets whether do or not force the material dialog design. If this value is set to true,
         * android.support.v7.app.AlertDialog is used, otherwise android.app.AlertDialog is.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder useMaterial(boolean value) { this.useMaterial = value; return this; }

        /**
         * Sets the content descriptions for the color swatches. Each item at the index i will be
         * attributed to the swatch with color at the portion i in the provided colors array.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder colorContentDescriptions(String[] value) {
            this.colorContentDescriptions = value;
            return this;
        }

        /**
         * Sets weather to use the default color content descriptions. The default description
         * consists of the following string: "Color [index]", where [index] is the accessibility
         * index of the color swatch.
         *
         * Default content descriptions can be mixed with custom ones
         * specified using the {@link #colorContentDescriptions(String[])} colorContentDescriptions}
         * method. For examples, if the colors array has 10 colors, and the colorContentDescriptions
         * array has 7 elements in it, then colors in the colors array with indices 0 through 6 will
         * have the corresponding descriptions from the colorContentDescriptions array and colors
         * with indices 7 though 9 will have the default ones.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder useDefaultColorContentDescriptions(boolean useDefaultColorContentDescriptions) {
            this.useDefaultColorContentDescriptions = useDefaultColorContentDescriptions;
            return this;
        }

        /**
         * Sets the listener that gets called when the "Ok" or the "CANCEL" button gets pressed on
         * the color picker dialog.
         *
         * You'll have to override two methods:
         *<<code>public void onColorPickerDialogOkPressed(int[] selectedColors)</code> and
         <code>public void onColorPickerDialogOkPressed(int[] selectedColors)</code>
         both of them receive an array of the selected colors in the
         {@link #ColorPickerDialog}, at the time "Ok" or "CANCEL" was pressed.
         *
         * @param listener an instance of the listener to assign
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setOnOkCancelPressListener(OnOkCancelPressListener listener) {
            this.listener = listener; return this;}

        /**
         * Builds the ColorPickerDialog.
         *
         * @return color picker dialog
         */
        public ColorPickerDialog build() {return ColorPickerDialog.newInstance(this);}

        private void positiveIntegerCheck(int number, String message) {
            if (number < 1) {
                throw new IllegalArgumentException(message);
            }
        }

    }

    public interface OnOkCancelPressListener {
        void onColorPickerDialogOkPressed(int[] selectedColors);
        void onColorPickerDialogCancelPressed(int[] selectedColors);
    }

    public static final int SIZE_LARGE = 1;
    public static final int SIZE_SMALL = 2;

    protected static final String KEY_TITLE_ID = "title_id";
    protected static final String KEY_COLORS = "colors";
    protected static final String KEY_COLOR_CONTENT_DESCRIPTIONS = "color_content_descriptions";
    protected static final String KEY_SELECTED_COLORS = "selected_color";
    protected static final String KEY_NUM_COLUMNS = "columns";
    protected static final String KEY_SWATCH_SIZE = "size";
    protected static final String KEY_INDEX_OF_COLOR = "indexOfColor";
    protected static final String KEY_LAST_SELECTED_COLOR_INDEX = "lastSelectedColorIndex";
    protected static final String KEY_NUM_SELECTED_COLORS = "numSelectedColors";
    protected static final String KEY_MAX_SELECTED_COLORS = "maxSelectedColors";
    protected static final String KEY_USE_DEFAULT_DESC = "useDefaultColorContentDescriptions";
    protected static final String KEY_USE_MATERIAL = "useMaterialDialog";



    protected String LOG_TAG = "io.github.iluxonchik.ColorPickerDialog";

    protected int titleResId = R.string.dialog_title;
    protected int[] colors = null; // colors to show in palette
    protected String[] colorContentDescriptions = null;
    protected int[] selectedColors;
    protected int numColumns; // number of columns in palette
    protected int swatchSize; // used for circle height/width
    protected boolean[] colorSelected;
    protected HashMap<Integer, Integer> indexOfColor;
    protected int numSelectedColors;
    protected int maxSelectedColors = Integer.MAX_VALUE;
    protected int lastSelectedColorIndex; // index of the last selected color
    protected boolean useMaterialDialog;
    protected boolean useDefaultColorContentDescriptions = true;

    protected boolean showOkCancelButtons = true; // TODO: do something with this later?

    private ColorPickerPalette palette;
    private ProgressBar progressBar;

    protected ColorPickerSwatch.OnColorSelectedListener onColorSelectedListener;
    protected OnOkCancelPressListener onOkCancelPressListener;

    /**
     * Listener for "Ok" button press on the dialog
     */
    protected DialogInterface.OnClickListener onOkPressedListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (onOkCancelPressListener != null) {
                onOkCancelPressListener.onColorPickerDialogOkPressed(getCurrentlySelectedColors());
            }

            if (getTargetFragment() instanceof OnOkCancelPressListener) {
                final OnOkCancelPressListener listener =
                        (OnOkCancelPressListener) getTargetFragment();
                listener.onColorPickerDialogOkPressed(getCurrentlySelectedColors());
            }
        }
    };

    /**
     * Listener for "Cancel" button press on the dialog
     */
    protected DialogInterface.OnClickListener onCancelPressedListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (onOkCancelPressListener != null) {
                onOkCancelPressListener.onColorPickerDialogCancelPressed(getCurrentlySelectedColors());
            }

            if (getTargetFragment() instanceof OnOkCancelPressListener) {
                final OnOkCancelPressListener listener =
                        (OnOkCancelPressListener) getTargetFragment();
                listener.onColorPickerDialogCancelPressed(getCurrentlySelectedColors());
            }
        }
    };


    public ColorPickerDialog() {
        // Empty constructor required for dialog fragments
    }


    private static ColorPickerDialog newInstance(Builder builder) {
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.initialize(builder.titleResId, builder.colors, builder.selectedColors,
                builder.numColumns, builder.swatchSize, builder.maxSelectedColors,
                builder.colorContentDescriptions, builder.useDefaultColorContentDescriptions,
                builder.listener);
        colorPickerDialog.setUseMaterialDialog(builder.useMaterial);
        return colorPickerDialog;
    }


    private void initialize(int titleResId, int[] colors, int[] selectedColors, int numColumns,
                           int swatchSize, int maxSelectedColors, String[] colorContentDescriptions,
                            boolean useDefaultColorContentDescriptions,
                            OnOkCancelPressListener listener) {
        this.maxSelectedColors = maxSelectedColors;
        this.colorContentDescriptions = colorContentDescriptions;
        this.useDefaultColorContentDescriptions = useDefaultColorContentDescriptions;
        setOnOkCancelPressListener(listener);
        setArguments(titleResId, numColumns, swatchSize);
        initializeStateVars(selectedColors, colors);
        setColors(colors, colorSelected);
    }

    private void initialize(int titleResId, int[] colors, int[] selectedColors, int numColumns,
                           int swatchSize) {
        initialize(titleResId, colors, selectedColors, numColumns, swatchSize, Integer.MAX_VALUE,
                null, true, null);
    }


    private void setArguments(int titleResId, int numColumns, int swatchSize) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TITLE_ID, titleResId);
        bundle.putInt(KEY_NUM_COLUMNS, numColumns);
        bundle.putInt(KEY_SWATCH_SIZE, swatchSize);
        setArguments(bundle);
    }

    public void setOnColorSelectedListener(ColorPickerSwatch.OnColorSelectedListener listener) {
        this.onColorSelectedListener = listener;
    }

    public void setOnOkCancelPressListener(OnOkCancelPressListener onOkCancelPressListener) {
        this.onOkCancelPressListener = onOkCancelPressListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            titleResId = getArguments().getInt(KEY_TITLE_ID);
            numColumns = getArguments().getInt(KEY_NUM_COLUMNS);
            swatchSize = getArguments().getInt(KEY_SWATCH_SIZE);
        }

        if (savedInstanceState != null) {
            colors = savedInstanceState.getIntArray(KEY_COLORS);
            colorSelected = savedInstanceState.getBooleanArray(KEY_SELECTED_COLORS);
            colorContentDescriptions = savedInstanceState.getStringArray(
                    KEY_COLOR_CONTENT_DESCRIPTIONS);
            indexOfColor = (HashMap<Integer, Integer>)
                    savedInstanceState.getSerializable(KEY_INDEX_OF_COLOR);
            lastSelectedColorIndex = savedInstanceState.getInt(KEY_LAST_SELECTED_COLOR_INDEX);
            numSelectedColors = savedInstanceState.getInt(KEY_NUM_SELECTED_COLORS);
            maxSelectedColors = savedInstanceState.getInt(KEY_MAX_SELECTED_COLORS);
            useDefaultColorContentDescriptions = savedInstanceState
                    .getBoolean(KEY_USE_DEFAULT_DESC);
            useMaterialDialog = savedInstanceState.getBoolean(KEY_USE_MATERIAL);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Activity activity = getActivity();

        View view = LayoutInflater.from(activity).inflate(R.layout.color_picker_dialog, null);
        progressBar = (ProgressBar)view.findViewById(R.id.progress);
        palette = (ColorPickerPalette)view.findViewById(R.id.color_picker);
        palette.init(swatchSize, numColumns, this);

        if (colors != null) {
            showPaletteView();
        }

        // TODO: This screams refactoring!
        if (useMaterialDialog) {
            return createMaterialDialog(activity, view);
        }
        else {
            return createDefaultDialog(activity, view);
        }
    }

    /**
     * Creates a material dialog.
     * @param context
     * @param view dialog view
     * @return
     */
    private android.support.v7.app.AlertDialog createMaterialDialog(Context context, View view) {
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder =
                new android.support.v7.app.AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(titleResId)
                .setView(view);

        // Add positve and negative buttons to dialog, if needed
        if (showOkCancelButtons) {
            alertDialogBuilder.setPositiveButton(R.string.dialog_positive_button_text, onOkPressedListener)
                    .setNegativeButton(R.string.dialog_negative_button_text, onCancelPressedListener);
        }

        return alertDialogBuilder.create();
    }

    /**
     * Creates a default dialog.
     * @param context
     * @param view dialog view
     * @return
     */
    private android.app.AlertDialog createDefaultDialog(Context context, View view) {
        android.app.AlertDialog.Builder alertDialogBuilder =
                new android.app.AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(titleResId)
                .setView(view);

        // Add positve and negative buttons to dialog, if needed
        if (showOkCancelButtons) {
            alertDialogBuilder.setPositiveButton(R.string.dialog_positive_button_text, onOkPressedListener)
                    .setNegativeButton(R.string.dialog_negative_button_text, onCancelPressedListener);
        }

        return alertDialogBuilder.create();
    }

    /**
     * Hide the progressbar and show the palette.
     */
    public void showPaletteView() {
        if (progressBar != null && palette != null) {
            progressBar.setVisibility(View.GONE);
            refreshPalette();
            palette.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hide the palette and show the progressbar.
     */
    public void showProgressBarView() {
        if (progressBar != null && palette != null) {
            progressBar.setVisibility(View.VISIBLE);
            palette.setVisibility(View.GONE);
        }
    }

    /**
     * Set the colors to display in the palette. Causes the palette to refresh.
     *
     * @param colors colors to display in the palette
     * @param selectedColors selected colors in the palette
     */
    public void setColors(int[] colors, boolean[] selectedColors) {
        if (this.colors != colors || this.colorSelected != selectedColors) {
            this.colors = colors;
            this.colorSelected = selectedColors;
            refreshPalette();
        }
    }

    /**
     * Set the colors to display in the palette. Causes the palette to refresh.
     *
     * @param colors colors to display in the palette
     */
    public void setColors(int[] colors) {
        if (this.colors != colors) {
            this.colors = colors;
            refreshPalette();
        }
    }

    /**
     * Set the selected colors in the palette. Causes the palette to refersh.
     *
     * @param selectedColors selected colors in the platte.
     */
    public void setKeySelectedColors(boolean[] selectedColors) {
        if(this.colorSelected != selectedColors) {
            this.colorSelected = selectedColors;
            refreshPalette();
        }
    }

    /**
     * Sets the content descriptions for the color swatches. Each item at the index i will be
     * attributed to the swatch with color at the portion i in the provided colors array.
     *
     * @param contentDescriptions content descriptions of color swatches
     */
    public void setKeyColorContentDescriptions(String[] contentDescriptions) {
        if (this.colorContentDescriptions != contentDescriptions) {
            this.colorContentDescriptions = contentDescriptions;
            refreshPalette();
        }
    }


    /**
     * Creates a hashmap that maps the color to its position in the colors array and fills the
     * colorSelected array.
     */
    private void initializeStateVars(int[] selectedColors, int[] colors) {
        /* NOTE: be careful if you want to run this on a new thread. If the state vars are not
         * initialized correctly, when a call to ".show()" is made, the dialog might exhibit
         * erroneous behavior.
         */
        numSelectedColors = selectedColors.length;

        HashMap<Integer, Integer> hasMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < colors.length; i++) {
            hasMap.put(colors[i], i);
        }
        indexOfColor = hasMap;
        colorSelected = new boolean[colors.length];
       // Arrays.fill(colorSelected, false);

        for (int color : selectedColors) {
            colorSelected[indexOfColor.get(color)] = true;
        }

        if(selectedColors.length > 0) {
            // by default, use the first color from selectedColors are the lastSelectedColorIndex
            lastSelectedColorIndex = indexOfColor.get(selectedColors[0]);
        }

    }

    public int[] getColors() { return colors; }

    public int[] getSelectedColors() { return selectedColors; }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(KEY_COLORS, colors);
        outState.putBooleanArray(KEY_SELECTED_COLORS, colorSelected);
        outState.putStringArray(KEY_COLOR_CONTENT_DESCRIPTIONS, colorContentDescriptions);
        outState.putSerializable(KEY_INDEX_OF_COLOR, indexOfColor);
        outState.putInt(KEY_LAST_SELECTED_COLOR_INDEX, lastSelectedColorIndex);
        outState.putInt(KEY_NUM_SELECTED_COLORS, numSelectedColors);
        outState.putInt(KEY_MAX_SELECTED_COLORS, maxSelectedColors);
        outState.putBoolean(KEY_USE_DEFAULT_DESC, useDefaultColorContentDescriptions);
        outState.putBoolean(KEY_USE_MATERIAL, useMaterialDialog);
    }

    private void refreshPalette() {
        if (palette != null && colors != null) {
            palette.drawPalette(colors, colorSelected,
                    colorContentDescriptions, useDefaultColorContentDescriptions);
        }
    }

    @Override
    public void onColorSelected(int color) {
        int index;

        index = indexOfColor.get(color);

        if (numSelectedColors >= maxSelectedColors) {
            // if max num of selected colors has been reached, de-select the last one
            if (index != lastSelectedColorIndex && !colorSelected[index]) {
                colorSelected[lastSelectedColorIndex] = false;
                numSelectedColors--;
            }
        }

        colorSelected[index] = !colorSelected[index];

        if(colorSelected[index]) {
            // New color was selected, increment the number of selected colors
            lastSelectedColorIndex = index;
            numSelectedColors++;
        } else {
            // Color de-selected, decremented number of selected colors
            numSelectedColors--;
        }

        palette.drawPalette(colors, colorSelected);
    }

    /**
     * Get an array of currently selected colors.
     */
    public int[] getCurrentlySelectedColors() {
        if (numSelectedColors < 1)
            return null;

        int[] currentlySelectedColors = new int[numSelectedColors];
        int j =0;
        for (int i = 0; i < colorSelected.length; i++) {
            if (colorSelected[i]) {
                currentlySelectedColors[j++] = colors[i];
            }
        }

        if (currentlySelectedColors.length != numSelectedColors) {
            Log.e("MainActivity", "Error in color counting. Expected = " + numSelectedColors +
                    " Obtained = " + currentlySelectedColors.length);
        }

        return currentlySelectedColors;
    }

    /**
     *
     * @return whether the use of material dialog is forced or not
     */
    public boolean isMaterialDialog() {
        return useMaterialDialog;
    }

    protected void setUseMaterialDialog(boolean useMaterialDialog) {
        this.useMaterialDialog = useMaterialDialog;
    }

}
