package mp3tagedit.de.main;

import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class FileManager extends AppCompatActivity {

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
    private ArrayList<HashMap<String,String>> getPlayList(String rootPath, String fileExtension) {
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
}
