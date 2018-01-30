package mp3tagedit.de.main;

import android.support.v4.app.DialogFragment;

import java.io.File;

public interface DialogFragmentResultListener {

    void getFragmentResult(File str, DialogFragment frag);

    void getMultipleFragmentResult(File[] multiStr, DialogFragment frag);
}
