package mp3tagedit.de.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.jaudiotagger.tag.id3.reference.MediaPlayerRating;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class id3v24editor extends AppCompatActivity {

    private Drawer mainDrawer;

    private final static int PERM_REQ_WRITE_STORAGE = 42;
    private final static int PERM_REQ_READ_STORAGE = 43;
    private final static int PERM_REQ_CAMERA = 44;

    public static ArrayList<EditText> artistList;
    public static ArrayList<EditText> genreList;

    MediaPlayer mediaPlayer;
    private int AudioSession = 1;
    private int currentPos;

    private Button playButton;

    // TODO
    File currentFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_id3v24editor);

        artistList = new ArrayList<>();
        artistList.add((EditText)(findViewById(R.id.artistIn)));
        genreList = new ArrayList<>();
        genreList.add((EditText)(findViewById(R.id.genreIn)));

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERM_REQ_WRITE_STORAGE);

        File[] allRootPaths = getExternalFilesDirs(null);
        for (File f : allRootPaths) {
            System.out.println(f.getAbsolutePath());
            ArrayList<String> playL = getPlayList(f.getAbsolutePath().replace("Android/data/thejetstream.de.mp3tagedit/files", ""), ".mp3");
            for (String s : playL) {
                System.out.println(s);
                if (s.contains("schlawinerwiener")) {
                    currentFile = new File(s);
                    System.out.println("GEFUNDEN !!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    break;
                }
            }
        }

        setupDrawer();
        setupActionBar(true, true, "id3v2.4 Editor");
        setupEditorHead();

    }

    public void setupDrawer() {
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withIcon(GoogleMaterial.Icon.gmd_home).withName("Home");
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withIcon(GoogleMaterial.Icon.gmd_settings).withName("Settings");

        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(this)
                //.withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon(GoogleMaterial.Icon.gmd_3d_rotation)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();



        //create the drawer and remember the `Drawer` object
        mainDrawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(header)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        new SecondaryDrawerItem().withName("Settings")
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        long identifier = drawerItem.getIdentifier();
                        if (identifier == 1) {
                            return true;
                        } else if (identifier == 2) {
                            return true;
                        } else {
                            return true;
                        }
                    }
                }).withDrawerWidthDp(240).build();
    }

    /**
     * Overrides existing Method
     * Used to check if a Permission was granted by the user during runtime
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERM_REQ_WRITE_STORAGE: {
                if (grantResults.length > 0) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // denied
                        System.out.println("DENIED");
                    } else {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            // granted
                            System.out.println("GRANTED");
                        } else {
                            // not clicked
                            System.out.println("NOT CLICKED");
                        }
                    }
                }
            } case PERM_REQ_READ_STORAGE: {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // denied
                    System.out.println("DENIED");
                } else {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        // granted
                        System.out.println("GRANTED");
                    } else {
                        // not clicked
                        System.out.println("NOT CLICKED");
                    }
                }
            }
        }
    }

    public void addInputLineArtist(View view){
        ViewGroup viewParent = (ViewGroup)(view.getParent().getParent());
        ViewGroup vg = (ViewGroup) LayoutInflater.from(viewParent.getContext()).inflate(
                R.layout.input_line, null);

        artistList.add( (EditText) vg.getChildAt(0));

        vg.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup parent = (ViewGroup) v.getParent().getParent();
                int index = parent.indexOfChild((View) v.getParent());
                artistList.remove(index);
                parent.removeViewAt(index);
            }
        });

        viewParent.addView(vg);
    }

    public void addInputLineGenre(View view){
        ViewGroup viewParent = (ViewGroup)(view.getParent().getParent());
        ViewGroup vg = (ViewGroup) LayoutInflater.from(viewParent.getContext()).inflate(
                R.layout.input_line, null);

        genreList.add( (EditText) vg.getChildAt(0) );

        vg.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup parent = (ViewGroup) v.getParent().getParent();
                int index = parent.indexOfChild((View) v.getParent());
                genreList.remove(index);
                parent.removeViewAt(index);
            }
        });

        viewParent.addView(vg);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    /**
     * Method is called when "Options"-button is pressed (if it exists)
     * Leave empty if "hasDrawerButton" in setupActionBar was false
     */
    private void options() {

    }

    private void setupActionBar(boolean hasOptionsButton, boolean hasDrawerButton, String title) {
        Button openDrawer = findViewById(R.id.open_drawer);
        Button openOptions = findViewById(R.id.open_options);
        TextView activityTitle = findViewById(R.id.activity_title);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point(); display.getSize(size);

        if (hasDrawerButton) {
            //openDrawer.setWidth(ACTIONBARSIZE); openDrawer.setHeight(ACTIONBARSIZE);
            openDrawer.setBackgroundDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_menu).sizeDp(30).color(getResources().getColor(R.color.colorPrimary)));
            openDrawer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainDrawer.openDrawer();
                }
            });
        } else {
            ((ViewManager) openDrawer.getParent()).removeView(openDrawer);
        }

        if (hasOptionsButton) {
            //openOptions.setWidth(ACTIONBARSIZE); openOptions.setHeight(ACTIONBARSIZE);
            openOptions.setBackgroundDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_more_vert).sizeDp(30).color(getResources().getColor(R.color.colorPrimary)));
            openOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    options();
                }
            });
        } else {
            ((ViewManager) openOptions.getParent()).removeView(openOptions);
        }

        //activityTitle.setWidth(size.x - 2*ACTIONBARSIZE);
        //activityTitle.setHeight(ACTIONBARSIZE);
        activityTitle.setText(title); //sets the TextViews text
    }

    private void setupEditorHead() {
        playButton = findViewById(R.id.play_button);
        Button saveButton = findViewById(R.id.save_button);
        Button shareButton = findViewById(R.id.share_button);

        playButton.setBackgroundDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_play_arrow).sizeDp(20)
                .color(getResources().getColor(R.color.colorPrimary)));
        shareButton.setBackgroundDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_share).sizeDp(20)
                .color(getResources().getColor(R.color.colorPrimary)));

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!mediaPlayer.isPlaying()) {
                        playButton.setBackgroundDrawable(new IconicsDrawable(id3v24editor.this)
                                .icon(GoogleMaterial.Icon.gmd_pause).sizeDp(20)
                                .color(getResources().getColor(R.color.colorPrimary)));
                        try {
                            playSong();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        playButton.setBackgroundDrawable(new IconicsDrawable(id3v24editor.this)
                                .icon(GoogleMaterial.Icon.gmd_play_arrow).sizeDp(20)
                                .color(getResources().getColor(R.color.colorPrimary)));
                        pauseSong();
                    }
                } catch (NullPointerException e) {
                    playButton.setBackgroundDrawable(new IconicsDrawable(id3v24editor.this)
                            .icon(GoogleMaterial.Icon.gmd_pause).sizeDp(20)
                            .color(getResources().getColor(R.color.colorPrimary)));
                    try {
                        playSong();
                    } catch (IOException e2) {
                        e.printStackTrace();
                    }
                }
            }
        });

        saveButton.setText("TEST");
    }

    private void playSong() throws IOException {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(currentFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } else {
            mediaPlayer.seekTo(currentPos);
            mediaPlayer.start();
        }
    }

    private void pauseSong(){
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            currentPos = mediaPlayer.getCurrentPosition();
        }
    }

    /**
     * Returns a list of Hashmaps containing the path and name of every file with a certain extension.
     * @param rootPath The root directory which should be searched
     * @param fileExtension The extension the to be found files should have
     * @return null if the file is not a directory or a list of Hashmaps
     */
    private ArrayList<String> getPlayList(String rootPath, String fileExtension) {
        ArrayList<String> fileList = new ArrayList<>();

        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    ArrayList<String> tmp = getPlayList(file.getAbsolutePath(), fileExtension);
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
    }
}
