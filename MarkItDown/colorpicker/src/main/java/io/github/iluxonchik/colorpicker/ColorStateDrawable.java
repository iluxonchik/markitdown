package io.github.iluxonchik.colorpicker;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

/**
 * A drawable which sets its color filter to a color specified by the user, and changed that color
 * to a slightly darker one when the drawable is pressed or focused.
 */
public class ColorStateDrawable extends LayerDrawable {

    private static final float PRESSED_STATE_MULTIPLIER = 0.70f;

    private int color; // color of the drawable


    /**
     * Create a new layer drawable with the list of specified layers.
     *
     * @param layers A list of drawables to use as layers in this new drawable.
     */
    public ColorStateDrawable(Drawable[] layers, int color) {
        super(layers);
        this.color = color;
    }

    /**
     * Called when the drawable's state gets changed (pressed, focused, etc).
     *
     * @param states the array of the drawable's states
     * @return true if the state change has caused the appearance of
     * the Drawable to change (that is, it needs to be drawn), else false
     * if it looks the same and there is no need to redraw it since its the
     * last state.
     */
    @Override
    public boolean onStateChange(int[] states) {
        boolean pressedOrFocused = false;

        for (int state : states) {
            if (state == android.R.attr.state_pressed || state == android.R.attr.state_focused) {
                pressedOrFocused = true;
                break;
            }
        }
        if (pressedOrFocused) {
            super.setColorFilter(getPressedColor(color), PorterDuff.Mode.SRC_ATOP);
        } else {
            super.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }

        return super.onStateChange(states);
    }

    /**
     * Given a particular color, adjust its value (brightness) by a multiplier.
     */
    private static int getPressedColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= PRESSED_STATE_MULTIPLIER; // set "value" (brightness) to a lower level
        return Color.HSVToColor(hsv);
    }

    @Override
    public boolean isStateful() { return true; }
}
