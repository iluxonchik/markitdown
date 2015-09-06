package io.github.iluxonchik.colorpicker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Creates a circular swatch of specified color. Adds a checkmark if marked as checked.
 */
public class ColorPickerSwatch extends FrameLayout implements View.OnClickListener {
    private int color;
    private ImageView swatchImage;
    private ImageView checkmarkImage;
    private OnColorSelectedListener onColorSelectedListener;

    /**
     * Interface for a callback when a color circle is selected
     */
    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

    public ColorPickerSwatch(Context context, int color, boolean checked,
                             OnColorSelectedListener listener) {
        super(context);
        this.color = color;
        onColorSelectedListener = listener;

        LayoutInflater.from(context)
                .inflate(R.layout.color_picker_swatch, this); // attachToRoot = true
        swatchImage = (ImageView)findViewById(R.id.color_picker_swatch);
        checkmarkImage = (ImageView) findViewById(R.id.color_picker_checkmark);
        setColor(color);
        setChecked(checked);
        setOnClickListener(this);
    }

    /**
     * Set the color of the circular swatch, by creating a new ColorStateDrawable and setting it
     * as an image drawable of the swatch.
     * @param color the color of the circular swatch
     */
    protected void setColor(int color) {
        Drawable[] layers = new Drawable[] { getContext().getResources()
                .getDrawable(R.drawable.color_picker_swatch) };
        swatchImage.setImageDrawable(new ColorStateDrawable(layers, color));
    }

    private void setChecked(boolean isChecked) {
        if (isChecked) {
            checkmarkImage.setVisibility(View.VISIBLE);
        } else {
            checkmarkImage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (onColorSelectedListener != null) {
            onColorSelectedListener.onColorSelected(color);
        }
    }
}
