package mp3tagedit.de.main;

import android.app.Dialog;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*public class FileManager extends AppCompatActivity {

    public ArrayList<File> files = new ArrayList<File>();
    public ArrayList<HashMap<String, String>> fileLoc;
    private LinearLayout fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteChecked(v);
            }
        });

        //files = (ArrayList<File>) getPlayListFile("/storage/3232-3265"+"/Music", "mp3").clone();
        fileList = (LinearLayout) findViewById(R.id.fileList);

        Intent intent = getIntent();
        String[] filesDir = intent.getStringArrayExtra("files");
        if(filesDir != null){
            for(String str:filesDir){
                addFiles(new File(str));
            }
        }

        ArrayList<HashMap<String, String>> fls = getPlayList("/storage/3232-3265"+"/Music", "mp3");

        System.out.println(fls.size() + " Files found");

        //addFiles(fls);
        //addFiles(new File(Environment.getExternalStoragePublicDirectory("").getPath() + "/Music/EureMuetter"));
        //System.out.println(Environment.getExternalStoragePublicDirectory("").getPath() + "/Music/EureMuetter");
        System.out.println(files.size());
        for(File f:files){
            addFileViews(fileList, f.getName());
        }

        System.out.println(fileList.getChildCount()+"--------------------------------------------");

    }

    @Override
    protected void onStart(){
        super.onStart();

        for(int i = fileList.getChildCount()-1; i >= 0; i--){
            LinearLayout ll = (LinearLayout)(fileList.getChildAt(i));
            TextView tv = (TextView)(ll.getChildAt(0));
            System.out.println(tv.getText());
            ll.getChildAt(0).setSelected(true);

        }
    }

    public void test(){
        String path = Environment.getExternalStorageDirectory().toString()+"/Music/EureMuetter";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }
    }

    public void deleteChecked(View v){
        int index = 0;
        while(index < fileList.getChildCount()){
            LinearLayout child = (LinearLayout) fileList.getChildAt(index);
            CheckBox chckbx = (CheckBox) (child.getChildAt(1));
            if (chckbx.isChecked()) {
                fileList.removeViewAt(index);
                files.remove(index);
            }
            else{
                index++;
            }
        }
    }

    public void addFileViews(View parent, String fileName){
        ViewGroup parentVg = (ViewGroup) parent;
        ViewGroup vg = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(
                R.layout.file_element, null);
        /*vg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return true;
            }
        });*/
/*
        TextView name = (TextView)(vg.getChildAt(0));
        name.setText(fileName);

        parentVg.addView(vg);
    }

    public void addFiles(ArrayList<File> files, boolean add){
        if(add){
            this.files.addAll(files);
        }
        else {
            this.files = files;
        }
    }

    public void addFiles(File dir){
        System.out.println(dir);
        if(dir.isFile()){
            System.out.println("This is a file" + dir);
            System.out.println(dir == null);
            System.out.println(dir.exists());
            files.add(dir);
        }
        if(dir.isDirectory()){
            System.out.println("dir:" + dir);
            File[] files = dir.listFiles();
            System.out.println(files.length);
            for (File f:files){
                addFiles(f);
            }
        }
    }

    public void addFiles(ArrayList<HashMap<String, String>> fileLocations){
        for(int i = 0; i < fileLocations.size(); i++){
            addFile(fileLocations.get(i));
        }
    }

    public void addFile(HashMap<String, String> fileLocation){
        addFiles(new File(fileLocation.get("file_path")+fileLocation.get("file_name")));
    }

    /**
     * Returns a list of Hashmaps containing the path and name of every file with a certain extension.
     * @param rootPath The root directory which should be searched
     * @param fileExtension The extension the to be found files should have
     * @return null if the file is not a directory or a list of Hashmaps
     */
    /*private ArrayList<HashMap<String,String>> getPlayList(String rootPath, String fileExtension) {
        ArrayList<HashMap<String,String>> fileList = new ArrayList<>();

        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles();
            for (File file : files) {

                System.out.println("File:" + file.getAbsolutePath());

                if (file.isDirectory()) {
                    ArrayList<HashMap<String,String>> tmp = getPlayList(file.getAbsolutePath(), fileExtension);
                    if (tmp != null) {
                        fileList.addAll(tmp);
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(fileExtension)) {

                    System.out.println("SONG FOUND:" + file.getAbsolutePath());

                    HashMap<String, String> song = new HashMap<>();
                    song.put("file_path", file.getAbsolutePath());
                    song.put("file_name", file.getName());
                    fileList.add(song);
                }
            }
            return fileList;
        } catch (Exception e) {
            System.out.println("Something failed");
            return null;
        }
    }

    private ArrayList<File> getPlayListFile(String rootPath, String fileExtension) {
        ArrayList<File> fileList = new ArrayList<File>();

        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles();
            for (File file : files) {

                System.out.println("File:" + file.getAbsolutePath());

                if (file.isDirectory()) {
                    ArrayList<File> tmp = getPlayListFile(file.getAbsolutePath(), fileExtension);
                    if (tmp != null) {
                        fileList.addAll(tmp);
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(fileExtension)) {

                    System.out.println("SONG FOUND:" + file.getAbsolutePath());

                    HashMap<String, String> song = new HashMap<>();
                    song.put("file_path", file.getAbsolutePath());
                    song.put("file_name", file.getName());
                    fileList.add(file);
                }
            }
            return fileList;
        } catch (Exception e) {
            System.out.println("Something failed");
            return null;
        }
    }
}*/


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


public class FileSelecter extends DialogFragment {

    //code partly taken from "http://www.indragni.com/android/FileExplorerDemo.rar"
    public static final int PERMISSIONS_REQUEST_READ_STORAGE = 42;

    Button buttonDel;
    Button buttonDelAll;
    TextView textFolder;
    Dialog dialog;

    String KEY_TEXTPSS = "TEXTPSS";
    static final int CUSTOM_DIALOG_ID = 0;
    ListView dialog_ListView;

    File root;
    File curFolder;

    private String fileExt;

    public ArrayList<File> fileList = new ArrayList<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        System.out.println("Create Dialog FileSelecter");

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.file_select_dialog);
        dialog.setTitle("Custom Dialog");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        buttonDel = (Button) dialog.findViewById(R.id.del);
        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<File> keepFiles = new ArrayList<>();
                int count = 0;
                ViewGroup list = (ViewGroup)(dialog.findViewById(R.id.listing));
                int childCount = (list).getChildCount();
                for (int i = 0; i < childCount; i++ ){
                    CheckBox cb = (CheckBox)(((ViewGroup)(list.getChildAt(i))).getChildAt(1));
                    if(!cb.isChecked()){
                        keepFiles.add(fileList.get(i));
                        /*keepFiles.add(new File(((TextView)(((ViewGroup)(list.getChildAt(i)))
                                .getChildAt(0))).getText().toString()));*/
                    }
                    else{
                        count++;
                    }
                }

                File[] selectedFiles = new File[keepFiles.size()];
                for(int i = 0; i < keepFiles.size(); i++){selectedFiles[i] = keepFiles.get(i);}
                Toast.makeText(getActivity(), count + " Dateien wurde aus der Liste entfert",
                        Toast.LENGTH_LONG).show();
                ((DialogFragmentResultListener) getActivity())
                        .getMultipleFragmentResult(selectedFiles, FileSelecter.this);
            }
        });

        buttonDelAll = (Button) dialog.findViewById(R.id.delAll);
        buttonDelAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), fileList.size() + " Dateien wurde aus der Liste entfert",
                        Toast.LENGTH_LONG).show();
                ((DialogFragmentResultListener) getActivity())
                        .getMultipleFragmentResult(new File[0], FileSelecter.this);
            }
        });


        ListFiles(fileList);

        return dialog;
    }

    void ListFiles(ArrayList<File> files) {
        for(File file:files){
            ViewGroup viewParent = (ViewGroup) dialog.findViewById(R.id.listing);
            ViewGroup vg = (ViewGroup) LayoutInflater.from(this.getContext()).inflate(
                    R.layout.file_element, null);
            ((TextView)(vg.getChildAt(0))).setText(file.getName());

            viewParent.addView(vg);
        }

        /*ArrayAdapter<String> directoryList = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, fileList);
        dialog_ListView.setAdapter(directoryList);*/
    }

    /*private ArrayList<String> getFilesOfDir(String dir, String fileExtension) {
        ArrayList<String> fileList = new ArrayList<>();

        try {
            File rootFolder = new File(dir);
            File[] files = rootFolder.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    ArrayList<String> tmp = getFilesOfDir(file.getAbsolutePath(), fileExtension);
                    if (tmp != null) {
                        fileList.addAll(tmp);
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(fileExtension)) {
                    fileList.add(file.getAbsolutePath());
                }
            }
            return fileList;
        } catch (Exception e) {
            return null;
        }
    }*/
}