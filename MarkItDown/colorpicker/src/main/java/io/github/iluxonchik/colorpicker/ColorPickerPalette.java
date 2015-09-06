package io.github.iluxonchik.colorpicker;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;


/**
 * Palette that contains the colors.
 */
public class ColorPickerPalette extends TableLayout{

    public ColorPickerSwatch.OnColorSelectedListener onColorSelectedListener;

    private String description;
    private String descriptionSelected;

    private int swatchLength;
    private int marginSize;
    private int numColumns;
    private boolean useDefaultColorContentDescriptions;

    public ColorPickerPalette(Context context) { super(context); }

    public ColorPickerPalette(Context context, AttributeSet attrs) { super(context, attrs); }

    /**
     * Initialize color swatches size and palette's columns and onColorSelectedListener. size should be a
     * pre-defined size (SIZE_LARGE or SIZE_SMALL) from ColorPuckerDialog.
     */
    public void init(int size, int numColumns, ColorPickerSwatch.OnColorSelectedListener listener) {
        this.numColumns = numColumns;
        Resources res = getResources();
        if (size == ColorPickerDialog.SIZE_LARGE) {
            swatchLength = res.getDimensionPixelSize(R.dimen.color_swatch_large);
            marginSize = res.getDimensionPixelSize(R.dimen.color_swatch_margin_large);
        } else {
            swatchLength = res.getDimensionPixelSize(R.dimen.color_swatch_small);
            marginSize = res.getDimensionPixelSize(R.dimen.color_swatch_margin_small);
        }
        this.onColorSelectedListener = listener;

        description = res.getString(R.string.color_swatch_description);
        descriptionSelected = res.getString(R.string.color_swatch_description_selected);
    }

    /**
     * Creates and returns an empty table row
     * @return
     */
    private TableRow createTableRow() {
        TableRow tableRow = new TableRow(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(params);
        return  tableRow;
    }

    /**
     * Adds swatched to table in a serpentine format.
     */
    public void drawPalette(int[] colors, boolean[] selectedColors) {
        drawPalette(colors, selectedColors, null, true);
    }

    /**
     * Adds swatches to table in a serpentine format.
     */
    public void drawPalette(int[] colors, boolean[] selectedColors,
                            String[] colorContentDescriptions, boolean useDefaultColorContentDesc) {
        if (colors == null) {
            return;
        }

        this.removeAllViews();
        int tableElements = 0;
        int rowElements = 0;
        int rowNumber = 0;
        this.useDefaultColorContentDescriptions = useDefaultColorContentDesc;

        // Fill the table with swatches based on the colors array
        TableRow row = createTableRow();
        int color;
        for (int i = 0; i < colors.length; i++) {
            color = colors[i];
            View colorSwatch = createColorSwatch(color, selectedColors[i]);
            setSwatchDescription(rowNumber, tableElements, rowElements, selectedColors[i],
                    colorSwatch, colorContentDescriptions);
            addSwatchToRow(row, colorSwatch, rowNumber);

            tableElements++;
            rowElements++;

            if(rowElements == numColumns) {
                // if num of elements in the row matches the num of columns, pass to the next row
                addView(row);
                row = createTableRow();
                rowElements = 0;
                rowNumber++;
            }
        }

        // Create blank views to fill the row, if the last row has not been filled
        if (rowElements > 0) {
            while(rowElements != numColumns) {
                addSwatchToRow(row, createBlankSpace(), rowNumber);
                rowElements++;
            }
            addView(row);
        }
    }

    /**
     * Add a content description to the specified swatch view. Because the colors get added in a
     * snaking form, every other row will need to compensate for the fact that the colors are added
     * in an opposite direction from their left->right/top->bottom order, which is how the system
     * will arrange them for accessibility purposes.
     *
     * @param rowNumber row in which the descriptions will be added
     * @param index index of the swatch to which the description will be added to (number of
     *              swatches so far in the palette - 1)
     * @param rowElements number of elements in the row so far
     * @param selected indicates whether the current swatch is selected
     * @param swatch the swatch to which the description will be added
     * @param contentDescriptions color content descriptions
     */
    private void setSwatchDescription(int rowNumber, int index, int rowElements, boolean selected,
                                      View swatch, String[] contentDescriptions) {
        String description = null;
        if (contentDescriptions != null && contentDescriptions.length > index){
            // If descriptions are provided, use them
            description = contentDescriptions[index];
        } else {
            // If the descriptions are not provided, generate them
            if (useDefaultColorContentDescriptions) {
                // Only generate descriptions if it's needed
                int accessibilityIndex;

                if (rowNumber % 2 == 0) {
                    // We're in a regular-ordered row
                    accessibilityIndex = index + 1;
                } else {
                    // We're in a backwards-ordered row
                    int rowMax = ((rowNumber + 1) * numColumns); // maximum possible index
                    accessibilityIndex = rowMax - rowElements;
                }

                if (selected) {
                    description = String.format(this.descriptionSelected, accessibilityIndex);
                } else {
                    description = String.format(this.description, accessibilityIndex);
                }
            }
        }

        if (description != null) {
            Log.d("DESC", description);
            swatch.setContentDescription(description);
        }

        /*
            NOTE: an alternative design would be to use an interface to simulate a function
            pointer (that would remove the nested "if" in the "else"), but that would add complexity
            and since the task in question is simple and the cost is a boolean test, I decided to
            stick with this solution (not to mention that the alternative method would require
            object instantiations, which puts in question the performance gains). If more options
            to the setSwatchDescription are added, then the alternative solution should be
            re-considered, since it might improve code readability.
         */

    }

    /**
     * Appends a swatch to the end of the row for even-numbered rows and to the beginning of the row
     * for odd-numbered rows.
     */
    private static void addSwatchToRow(TableRow row, View swatch, int rowNumber) {
        if (rowNumber % 2 == 0) {
            row.addView(swatch);
        } else {
            row.addView(swatch, 0);
        }
    }

    /**
     * Create a blank image to fill the row.
     */
    private ImageView createBlankSpace() {
        ImageView view = new ImageView(getContext());
        view.setLayoutParams(getSwatchLayoutParams());
        return view;
    }

    /**
     * Creates a color swatch.
     */
    private ColorPickerSwatch createColorSwatch(int color, boolean isColorSelected) {
        //  Pass the ColorPickerDialog (onColorSelectedListener) and register it as onColorSelectedListener
        //      in ColorPickerSwatch, that way a click on the color circle will be
        //      sent to ColorPickerDialog.
        ColorPickerSwatch view = new ColorPickerSwatch(getContext(), color,
                isColorSelected, onColorSelectedListener);
        view.setLayoutParams(getSwatchLayoutParams());
        return view;
    }

    private TableRow.LayoutParams getSwatchLayoutParams() {
        TableRow.LayoutParams params = new TableRow.LayoutParams(swatchLength, swatchLength);
        params.setMargins(marginSize, marginSize, marginSize, marginSize);
        return params;
    }

}
