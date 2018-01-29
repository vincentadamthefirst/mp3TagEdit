package mp3tagedit.de.main;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FileExplorer extends DialogFragment {

    //code partly taken from "http://www.indragni.com/android/FileExplorerDemo.rar"
    public static final int PERMISSIONS_REQUEST_READ_STORAGE = 42;

    Button buttonOpenDialog;
    Button buttonUp;
    Button buttonAdd;
    TextView textFolder;

    String KEY_TEXTPSS = "TEXTPSS";
    static final int CUSTOM_DIALOG_ID = 0;
    ListView dialog_ListView;

    File root;
    File curFolder;

    private String fileExt;

    private ArrayList<File> fileList = new ArrayList<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        System.out.println("Create Dialog");

        fileExt = getString(R.string.file_extension);

        Dialog dialog = null;

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.file_explore_dialog);
        dialog.setTitle("Custom Dialog");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        root = Environment.getExternalStoragePublicDirectory("");
        System.out.println(root.getAbsolutePath());
        curFolder = root;
        textFolder = (TextView) dialog.findViewById(R.id.folder);
        buttonUp = (Button) dialog.findViewById(R.id.up);
        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListDir(curFolder.getParentFile(), fileExt);
            }
        });

        buttonAdd = (Button) dialog.findViewById(R.id.add_folder);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<File> filesDir = getFilesOfDir(curFolder, fileExt);
                File[] multiFile = new File[filesDir.size()];
                for(int i = 0; i < multiFile.length; i++){
                    multiFile[i] = filesDir.get(i);
                }

                ((DialogFragmentResultListener)getActivity())
                        .getMultipleFragmentResult(multiFile, FileExplorer.this);
            }
        });

        dialog_ListView = (ListView) dialog.findViewById(R.id.dialoglist);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File selected = fileList.get(position);
                if(selected.isDirectory()) {
                    ListDir(selected, fileExt);
                } else {
                    ((DialogFragmentResultListener)getActivity())
                            .getFragmentResult(selected,FileExplorer.this);
                }
            }
        });

        ListDir(curFolder, fileExt);

        return dialog;
    }

    void ListDir(File f, String fileExtension) {
        if(f.equals(root)) {
            buttonUp.setEnabled(false);
        } else {
            buttonUp.setEnabled(true);
        }

        curFolder = f;
        textFolder.setText(f.getPath());

        File[] files = f.listFiles();
        fileList.clear();

        System.out.println(files.length);

        for(File file : files) {
            if(file.getName().endsWith(fileExtension) || file.isDirectory()){
                fileList.add(file);
            }

        }

        ArrayList<String> fileListStr = new ArrayList<>();
        for(File file:fileList){fileListStr.add(file.getName());}

        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, fileListStr);
        dialog_ListView.setAdapter(directoryList);
    }

    private ArrayList<File> getFilesOfDir(File dir, String fileExtension) {
        ArrayList<File> fileList = new ArrayList<>();

        try {
            File rootFolder = dir;
            File[] files = rootFolder.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    ArrayList<File> tmp = getFilesOfDir(file, fileExtension);
                    if (tmp != null) {
                        fileList.addAll(tmp);
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(fileExtension)) {
                    fileList.add(file);
                }
            }
            return fileList;
        } catch (Exception e) {
            return null;
        }
    }
}