package mp3tagedit.de.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

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
import java.util.HashMap;

public class id3v24 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /*
    https://musicmachinery.com/music-apis/
    https://www.programmableweb.com/category/music/apis?category=19990

    https://wiki.musicbrainz.org/Development/XML_Web_Service/Version_2
    https://www.last.fm/api/account/create
    https://www.last.fm/api/account/create
    http://developer.openaura.com/
    */

    /*
    I bims 1 Test
     */



    // request integers for permissions
    public static final int PERMISSIONS_REQUEST_READ_STORAGE = 42;
    public static final int PERMISSIONS_REQUEST_WRITE_STORAGE = 43;




    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        File[] allStorage = getExternalFilesDirs(null);
        for (File f : allStorage) {
            System.out.println(f.getAbsolutePath());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Does it work? Maybe...", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                /*
                ArrayList<HashMap<String,String>> songList=getPlayList(Environment.getExternalStorageDirectory().getAbsolutePath(), ".mp3");
                if(songList!=null){
                    for(int i=0;i<songList.size();i++){
                        String fileName=songList.get(i).get("file_name");
                        String filePath=songList.get(i).get("file_path");

                        //here you will get list of file name and file path that present in your device
                        System.out.println("name ="+fileName +" path = "+filePath);
                    }
                } else {
                    System.out.println("null");
                }
                */



                test2();
                //test();
            }
        });

        Button play = (Button) findViewById(R.id.play_button);
        play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        Button addArtist = (Button) findViewById(R.id.addArtist);
        addArtist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick (View v) {
                ViewGroup vg =  (ViewGroup) v.getParent();

                vg.removeView(v);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void test2() {
        CoordinatorLayout v = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        Bitmap bmp = v.getDrawingCache();

        RenderScript rs = RenderScript.create(this.getApplicationContext());

        final Allocation input = Allocation.createFromBitmap(rs, bmp); //use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(20f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bmp);

        View overall = (View) findViewById(R.id.overall);
        BitmapDrawable bmpd = new BitmapDrawable(getResources(), bmp);
        overall.setBackground(bmpd);
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
                if (file.isDirectory()) {
                    ArrayList<HashMap<String,String>> tmp = getPlayList(file.getAbsolutePath(), fileExtension);
                    if (tmp != null) {
                        fileList.addAll(tmp);
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(fileExtension)) {
                    HashMap<String, String> song = new HashMap<>();
                    song.put("file_path", file.getAbsolutePath());
                    song.put("file_name", file.getName());
                    fileList.add(song);
                }
            }
            return fileList;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Testing method, called when the button is clicked
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void test() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            File mp3 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/test.mp3");
            System.out.println(mp3.getAbsolutePath());


            AudioFile audio = null;

            try {
                audio = AudioFileIO.read(mp3);
            } catch (CannotReadException | TagException | IOException | ReadOnlyFileException | InvalidAudioFrameException e) {
                e.printStackTrace();
            }

            if (audio == null) {
                System.out.println("somethings off");
            } else {
                System.out.println("metadata exists.");
                try {
                    // get the tags
                    Tag tag = audio.getTag();

                    // get the object
                    ImageButton butt = (ImageButton) findViewById(R.id.coverArt);

                    // get artwork and convert to bmp
                    Artwork a = tag.getFirstArtwork();
                    byte[] data = a.getBinaryData();
                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

                    // set image
                    butt.setImageBitmap(bmp);



                    /*
                    System.out.println(tag.getFirst(FieldKey.ARTIST));

                    System.out.println("writing");

                    tag.setField(FieldKey.ARTIST, "weintraube");

                    System.out.println(tag.getFirst(FieldKey.ARTIST));

                    AudioFileIO.write(audio);

                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(mp3.getAbsolutePath());

                    System.out.println("MMR Name: " + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                    */

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_STORAGE);
        }
    }

    /**
     * Overrides existing Method
     * Used to check if a Permission was granted by the user during runtime
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_STORAGE: {
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
            }
        }
    }

    public void onClick(View v){
        v.getParent();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
