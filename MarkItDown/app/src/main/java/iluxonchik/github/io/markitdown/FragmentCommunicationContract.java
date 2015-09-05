package iluxonchik.github.io.markitdown;

import android.os.Bundle;

/**
 * Contains constants used for the communication protocol between fragments and the MainActivity.
 */
public abstract class FragmentCommunicationContract {
    /**
     * An interface used for communications from fragments to the MainActivity. It uses a custom
     * protocol.
     */
    public interface OnMessageSendingNeeded {
        void onMessageSent(int msgCode, Bundle args);
    }

    /**
     * Contains message identifier constants
     *
     * All variables prefixed with "MESSAGE" indicate that it's a message sent from some fragment
     * which will want the activity to perform some action. Those values are passed as the first
     * argument to the
    */
    public static class Message {
        public static final int MESSAGE_START_NOTES_FROM_NOTEBOOKS = 0;
        public static final int MESSAGE_CHANGE_ACTION_BAR_TITLE = 1;

    }

    public static class StartNotesFromNotebooks {
        public static final String ARG_CUSTOM_SELECTION = NotesFragment.EXTRA_CUSTOM_SELECTION;
        public static final String ARG_CUSTOM_SELECTION_ARGS =
                NotesFragment.EXTRA_CUSTOM_SELECTION_ARGS;
    }

    public static class SetTitleBarTitle {
        public static final String ARG_FRAGMENT_TITLE = "fragmentTitle";
    }
}
