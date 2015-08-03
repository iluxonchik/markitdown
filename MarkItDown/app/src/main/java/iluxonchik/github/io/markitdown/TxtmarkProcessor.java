package iluxonchik.github.io.markitdown;

import android.view.View;

import com.github.rjeschke.txtmark.*;

public class TxtmarkProcessor extends MarkdownProcessor{

    public TxtmarkProcessor() {

    }

    @Override
    public String mdToHTML(String markdownText) {
        return Processor.process(markdownText);
    }
}
