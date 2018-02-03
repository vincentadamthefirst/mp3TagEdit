package mp3tagedit.de.main;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * This class handles the FileExplorer used to add files to the various
 * queues in the editors and in the tag-to-file converter
 *
 * Code modified and partly taken from http://www.indragni.com/android/FileExplorerDemo.rar
 *
 * @author André
 */
public class FileExplorer extends DialogFragment {

    public static final int PERMISSIONS_REQUEST_READ_STORAGE = 42;

    Button buttonUp;
    Button buttonAdd;
    TextView textFolder;
    Spinner rootPath;

    ListView dialog_ListView;

    File root;
    File curFolder;

    private String fileExt;

    private ArrayList<File> fileList = new ArrayList<>();

    /**
     * Sets up all necessary parts for the Dialog (e.g. buttons)
     * @author Vincent, André
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        System.out.println("Create Dialog");

        fileExt = getString(R.string.file_extension);

        Dialog dialog;

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.file_explore_dialog);
        dialog.setTitle("Custom Dialog");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        rootPath = dialog.findViewById(R.id.root_path_spinner);
        File[] allRootPaths = getActivity().getExternalFilesDirs(null);

        ArrayList<String> allRootPathsArrayL = new ArrayList<>();
        for(File f:allRootPaths){
            allRootPathsArrayL.add(f.getAbsolutePath()
                    .replace("Android/data/thejetstream.de.mp3tagedit/files", ""));
            allRootPathsArrayL.add("");
        }
        allRootPathsArrayL.remove(allRootPathsArrayL.size()-1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_spinner_item, allRootPathsArrayL);
        rootPath.setAdapter(adapter);
        rootPath.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeRoot();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        for(File f:allRootPaths) {
            System.out.println(f.getAbsolutePath());
        }

        root = Environment.getExternalStoragePublicDirectory("");
        System.out.println(root.getAbsolutePath());
        curFolder = root;
        textFolder = dialog.findViewById(R.id.folder);
        buttonUp = dialog.findViewById(R.id.up);
        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListDir(curFolder.getParentFile(), fileExt);
            }
        });

        buttonAdd = dialog.findViewById(R.id.add_folder);
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

        dialog_ListView = dialog.findViewById(R.id.dialoglist);
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

    /**
     * Changes the current root folder and updates the dialog
     */
    private void changeRoot(){
        String path = rootPath.getSelectedItem().toString();
        if(path.equals("")){
            rootPath.setSelection(rootPath.getSelectedItemPosition() - 1);
        }
        this.root = new File(rootPath.getSelectedItem().toString());
        curFolder = root;
        ListDir(curFolder, fileExt);
    }

    /**
     * Lists all the files with the specified extension (or if they are
     * directories) into the dialog
     * @param f the directory whose files should be displayed
     * @param fileExtension the extension that the files can have
     */
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

    /**
     * Goes through a given folder and all sub-folders and returns a list containing
     * Files with the extension fileExtension
     * @param dir The directory that should be searched
     * @param fileExtension The extension all the searched files should have (e.g. .mp3)
     * @author Vincent
     */
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