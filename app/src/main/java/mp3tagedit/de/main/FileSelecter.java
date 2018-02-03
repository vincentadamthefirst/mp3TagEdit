package mp3tagedit.de.main;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * This class is used to display the current queue and to handle
 * deleting files from it
 *
 * Code modified and partly taken from http://www.indragni.com/android/FileExplorerDemo.rar
 *
 * @author André
 */
public class FileSelecter extends DialogFragment {
    Button buttonDel;
    Button buttonDelAll;
    Dialog dialog;

    public ArrayList<File> fileList = new ArrayList<>();

    /**
     * Sets up all necessary components for the dialog (e.g. buttons)
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.file_select_dialog);
        dialog.setTitle("Custom Dialog");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        buttonDel = dialog.findViewById(R.id.del);
        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<File> keepFiles = new ArrayList<>();
                int count = 0;
                ViewGroup list = (dialog.findViewById(R.id.listing));
                int childCount = (list).getChildCount();
                for (int i = 0; i < childCount; i++ ){
                    CheckBox cb = (CheckBox)(((ViewGroup)(list.getChildAt(i))).getChildAt(1));
                    if(!cb.isChecked()){
                        keepFiles.add(fileList.get(i));
                    }
                    else{
                        count++;
                    }
                }

                File[] selectedFiles = new File[keepFiles.size()];
                for(int i = 0; i < keepFiles.size(); i++){selectedFiles[i] = keepFiles.get(i);}
                Toast.makeText(getActivity(), count + " " +getResources().getString(R.string.files_removed),
                        Toast.LENGTH_LONG).show();
                ((DialogFragmentResultListener) getActivity())
                        .getMultipleFragmentResult(selectedFiles, FileSelecter.this);
            }
        });

        buttonDelAll = dialog.findViewById(R.id.delAll);
        buttonDelAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), fileList.size() + " " + getResources().getString(R.string.files_removed),
                        Toast.LENGTH_LONG).show();
                ((DialogFragmentResultListener) getActivity())
                        .getMultipleFragmentResult(new File[0], FileSelecter.this);
            }
        });


        ListFiles(fileList);

        return dialog;
    }

    /**
     * Lists all files in the List into the Dialog
     * @param files the files that should be displayed in the dialog
     */
    void ListFiles(ArrayList<File> files) {
        for(File file:files){
            ViewGroup viewParent = dialog.findViewById(R.id.listing);
            ViewGroup vg = (ViewGroup) LayoutInflater.from(this.getContext()).inflate(
                    R.layout.file_element, null);
            ((TextView)(vg.getChildAt(0))).setText(file.getName());

            viewParent.addView(vg);
        }
    }
}