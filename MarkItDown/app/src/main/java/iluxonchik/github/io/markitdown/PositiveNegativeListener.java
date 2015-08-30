package iluxonchik.github.io.markitdown;


import android.app.DialogFragment;
import android.os.Bundle;


public interface PositiveNegativeListener {
    void onDialogPositiveClick(DialogFragment dialog, Bundle args);
    void onDialogNegativeClick(DialogFragment dialog, Bundle args);
}
