package iluxonchik.github.io.markitdown.helpers;

import com.github.rjeschke.txtmark.*;

import iluxonchik.github.io.markitdown.helpers.MarkdownProcessor;

public class TxtmarkProcessor extends MarkdownProcessor {

    public TxtmarkProcessor() {

    }

    @Override
    public String mdToHTML(String markdownText) {
        return Processor.process(markdownText);
    }
}
