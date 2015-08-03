package iluxonchik.github.io.markitdown;

import android.view.View;
import android.widget.TextView;

/**
 * Created by ILUXONCHIK on 03/08/2015.
 */
public abstract class MarkdownProcessor {

    public void mdToHTML(TextView view, String markdownText) {
        throw new UnsupportedOperationException();
    }

    public String mdToHTML(String markdownText) {
        throw new UnsupportedOperationException();
    }
}
