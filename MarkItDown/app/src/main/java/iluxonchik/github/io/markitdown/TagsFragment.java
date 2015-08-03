package iluxonchik.github.io.markitdown;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.rjeschke.txtmark.Processor.*;

public class TagsFragment extends Fragment {

    public TagsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.v("Markdown", com.github.rjeschke.txtmark.Processor.process("**aaa**"));

        return inflater.inflate(R.layout.fragment_top, container, false);
    }
}
