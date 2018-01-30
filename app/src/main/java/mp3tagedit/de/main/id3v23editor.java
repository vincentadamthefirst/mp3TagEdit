package mp3tagedit.de.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.jaudiotagger.tag.images.Artwork;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class id3v23editor extends AppCompatActivity {

    private Drawer mainDrawer;

    private final static int PERM_REQ_WRITE_STORAGE = 42;
    private final static int PERM_REQ_READ_STORAGE = 43;

    private File currentFile;

    private MediaPlayer mediaPlayer;
    private int currentPos;

    private EditText et_artist;
    private EditText et_genre;
    private EditText et_album;
    private EditText et_title;
    private EditText et_track;

    private ImageButton ib_artwork;

    private Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_id3v23editor);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERM_REQ_WRITE_STORAGE);

        et_artist = findViewById(R.id.artistIn);
        et_genre = findViewById(R.id.genreIn);
        et_title = findViewById(R.id.edit_title);
        et_album = findViewById(R.id.albumIn);

        ib_artwork = findViewById(R.id.coverArt);
        ib_artwork.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AlbumCoverActivity.class);
                startActivityForResult(i, 64);
            }
        });
        setupActionBar(getResources().getString(R.string.id3v23edit));
        setupDrawer();
        setupEditorHead();

        File[] allRootPaths = getExternalFilesDirs(null);
        for (File f : allRootPaths) {
            System.out.println(f.getAbsolutePath());
            ArrayList<String> playL = getPlayList(f.getAbsolutePath().replace("Android/data/thejetstream.de.mp3tagedit/files", ""), ".mp3");
            for (String s : playL) {
                System.out.println(s);
                if (s.contains("Nessum")) {
                    currentFile = new File(s);
                    System.out.println("GEFUNDEN !!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    break;
                }
            }
        }

        load();

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

    private boolean load() {
        MP3File mp3;

        try {
            mp3 = (MP3File) AudioFileIO.read(currentFile);
        } catch (CannotReadException | TagException | IOException | ReadOnlyFileException | InvalidAudioFrameException e) {
            return false;
        }

        Tag tag = mp3.getTag();

        et_artist.setText(tag.getFirst(FieldKey.ARTIST));
        et_title.setText(tag.getFirst(FieldKey.TITLE));
        et_genre.setText(tag.getFirst(FieldKey.GENRE));
        et_album.setText(tag.getFirst(FieldKey.ALBUM));

        Artwork cover = tag.getFirstArtwork();
        byte[] data = cover.getBinaryData();
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

        ib_artwork.setImageBitmap(bmp);

        return true;
    }

    private boolean save() {
        MP3File mp3;
        try {
            mp3 = (MP3File) AudioFileIO.read(currentFile);
        } catch (CannotReadException | TagException | IOException | ReadOnlyFileException | InvalidAudioFrameException e) {
            return false;
        }

        ID3v23Tag toSave = (ID3v23Tag) mp3.getTag();

        try {
            toSave.setField(FieldKey.ARTIST, et_artist.getText().toString());
            toSave.setField(FieldKey.ALBUM, et_album.getText().toString());
            toSave.setField(FieldKey.TITLE, et_title.getText().toString());
        } catch (FieldDataInvalidException e) {
            e.printStackTrace();
            return false;
        }



        mp3.setTag(toSave);

        /*
        try {
            mp3.save();
        } catch (IOException | TagException e) {
            e.printStackTrace();
            return false;
        }
        */

        try {
            AudioFileIO.write(mp3);
        } catch (CannotWriteException e) {
            e.printStackTrace();
            return false;
        }

        return true;

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

    private void setupDrawer() {
        PrimaryDrawerItem homItem = new PrimaryDrawerItem().withIdentifier(1)
                .withIcon(GoogleMaterial.Icon.gmd_home).withName(R.string.home)
                .withTextColor(getResources().getColor(R.color.defaultText))
                .withSelectedTextColor(getResources().getColor(R.color.colorPrimaryDark))
                .withIconColor(getResources().getColor(R.color.defaultText))
                .withSelectedIconColor(getResources().getColor(R.color.colorPrimaryDark));
        SecondaryDrawerItem setItem = new SecondaryDrawerItem().withIdentifier(2)
                .withIcon(GoogleMaterial.Icon.gmd_settings).withName(R.string.settings)
                .withTextColor(getResources().getColor(R.color.defaultText))
                .withSelectedTextColor(getResources().getColor(R.color.colorPrimaryDark))
                .withIconColor(getResources().getColor(R.color.defaultText))
                .withSelectedIconColor(getResources().getColor(R.color.colorPrimaryDark));
        SecondaryDrawerItem v24Item = new SecondaryDrawerItem().withIdentifier(4)
                .withIcon(GoogleMaterial.Icon.gmd_insert_drive_file).withName(R.string.id3v24edit)
                .withTextColor(getResources().getColor(R.color.defaultText))
                .withSelectedTextColor(getResources().getColor(R.color.colorPrimaryDark))
                .withIconColor(getResources().getColor(R.color.defaultText))
                .withSelectedIconColor(getResources().getColor(R.color.colorPrimaryDark));
        SecondaryDrawerItem v23Item = new SecondaryDrawerItem().withIdentifier(3)
                .withIcon(GoogleMaterial.Icon.gmd_insert_drive_file).withName(R.string.id3v23edit)
                .withTextColor(getResources().getColor(R.color.defaultText))
                .withSelectedTextColor(getResources().getColor(R.color.colorPrimaryDark))
                .withIconColor(getResources().getColor(R.color.defaultText))
                .withSelectedIconColor(getResources().getColor(R.color.colorPrimaryDark));
        SecondaryDrawerItem tagItem = new SecondaryDrawerItem().withIdentifier(5)
                .withIcon(GoogleMaterial.Icon.gmd_find_replace).withName(R.string.tagtofile)
                .withTextColor(getResources().getColor(R.color.defaultText))
                .withSelectedTextColor(getResources().getColor(R.color.colorPrimaryDark))
                .withIconColor(getResources().getColor(R.color.defaultText))
                .withSelectedIconColor(getResources().getColor(R.color.colorPrimaryDark));
        SecondaryDrawerItem helItem = new SecondaryDrawerItem().withIdentifier(6)
                .withIcon(GoogleMaterial.Icon.gmd_help_outline).withName(R.string.help)
                .withTextColor(getResources().getColor(R.color.defaultText))
                .withSelectedTextColor(getResources().getColor(R.color.colorPrimaryDark))
                .withIconColor(getResources().getColor(R.color.defaultText))
                .withSelectedIconColor(getResources().getColor(R.color.colorPrimaryDark));

        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View headerImage = li.inflate(R.layout.drawer_header, null);

        //create the drawer and remember the `Drawer` object
        mainDrawer = new DrawerBuilder()
                .withActivity(this)
                .withActionBarDrawerToggle(true)
                .withHeader(headerImage)
                .withSliderBackgroundColor(getResources().getColor(R.color.drawer_main))
                .addDrawerItems(
                        homItem,
                        new DividerDrawerItem(),
                        v23Item,
                        v24Item,
                        tagItem,
                        new DividerDrawerItem(),
                        helItem,
                        setItem
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        long identifier = drawerItem.getIdentifier();

                        if (identifier == 1) {
                            openHome();
                            return true;
                        } else if (identifier == 2) {
                            openSettings();
                            return true;
                        } else if (identifier == 4) {
                            open24();
                            return true;
                        } else if (identifier == 5) {
                            openTagToFile();
                            return true;
                        } else if (identifier == 6) {
                            openHelp();
                            return true;
                        } else {
                            return true;
                        }
                    }
                }).withDrawerWidthDp(240).build();

        mainDrawer.setSelection(3);

        mainDrawer.openDrawer();
        mainDrawer.closeDrawer();
    }

    private void open24() {
        Intent intent = new Intent(this, id3v24editor.class);
        startActivity(intent);
    }

    private void openTagToFile() {
        Intent intent = new Intent(this, tagtofile.class);
        startActivity(intent);
    }

    private void openHelp() {
        Intent intent = new Intent(this, help.class);
        startActivity(intent);
    }

    private void openSettings() {
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
    }

    private void openHome() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }

    private void setupActionBar(String title) {
        Button openDrawer = findViewById(R.id.open_drawer);
        Button openOptions = findViewById(R.id.open_options);
        TextView activityTitle = findViewById(R.id.activity_title);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        openDrawer.setBackgroundDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_menu).sizeDp(30).color(getResources().getColor(R.color.colorPrimary)));
        openDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainDrawer.openDrawer();
            }
        });

        openOptions.setBackgroundDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_more_vert).sizeDp(30).color(getResources().getColor(R.color.colorPrimary)));
        openOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options();
            }
        });

        activityTitle.setText(title); //sets the TextViews text
    }

    private void setupEditorHead() {
        playButton = findViewById(R.id.play_button);
        Button saveButton = findViewById(R.id.save_button);
        Button shareButton = findViewById(R.id.share_button);

        playButton.setBackgroundDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_play_arrow).sizeDp(20).color(getResources().getColor(R.color.colorPrimary)));
        shareButton.setBackgroundDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_share).sizeDp(20).color(getResources().getColor(R.color.colorPrimary)));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!mediaPlayer.isPlaying()) {
                        playButton.setBackgroundDrawable(new IconicsDrawable(id3v23editor.this)
                                .icon(GoogleMaterial.Icon.gmd_pause).sizeDp(20)
                                .color(getResources().getColor(R.color.colorPrimary)));
                        try {
                            playSong();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        playButton.setBackgroundDrawable(new IconicsDrawable(id3v23editor.this)
                                .icon(GoogleMaterial.Icon.gmd_play_arrow).sizeDp(20)
                                .color(getResources().getColor(R.color.colorPrimary)));
                        pauseSong();
                    }
                } catch (NullPointerException e) {
                    playButton.setBackgroundDrawable(new IconicsDrawable(id3v23editor.this)
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
    }
}
