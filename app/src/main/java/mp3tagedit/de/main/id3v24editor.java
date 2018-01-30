package mp3tagedit.de.main;

import android.Manifest;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

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
import org.jaudiotagger.tag.id3.ID3v24Tag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.Calendar;

public class id3v24editor extends AppCompatActivity implements DialogFragmentResultListener{

    private Drawer mainDrawer;

    private EditText et_title;
    private EditText et_album;
    private EditText et_year;
    private EditText et_track;
    private EditText et_comment;
    public static ArrayList<EditText> artistList;
    public static ArrayList<EditText> genreList;

    private ImageButton ib_artwork;

    MediaPlayer mediaPlayer;
    private int AudioSession = 1;
    private int currentPos;

    private AdapterView.OnItemSelectedListener slcItmListener;

    private Activity thisEditor;

    //private FileManager fManager;
    private ArrayList<File> queue;
    int currentQueuePos = 0;
    File currentFile;

    private Button playButton;
    private Button saveButton;
    private Button shareButton;
    private Button nextButton;
    private Button prevButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.content_id3v24editor);

        thisEditor = this;

        artistList = new ArrayList<>();
        artistList.add((EditText)(findViewById(R.id.artistIn)));
        genreList = new ArrayList<>();
        genreList.add((EditText)(findViewById(R.id.genreIn)));

        prevButton = findViewById(R.id.prev);
        prevButton.setBackgroundDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_forward).sizeDp(20)
                .color(getResources().getColor(R.color.colorPrimary)));

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentQueuePos > 0){
                    currentQueuePos--;
                    save();
                    currentFile = queue.get(currentQueuePos);
                    load();
                }
                if(currentQueuePos < queue.size()-1){
                    nextButton.setEnabled(true);
                    nextButton.setVisibility(View.VISIBLE);
                }
                if(currentQueuePos <= 0){
                    prevButton.setEnabled(false);
                    prevButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        nextButton = findViewById(R.id.next);
        nextButton.setBackgroundDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_forward).sizeDp(20)
                .color(getResources().getColor(R.color.colorPrimary)));
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentQueuePos < queue.size()-1){
                    currentQueuePos++;
                    save();
                    currentFile = queue.get(currentQueuePos);
                    load();
                }
                if(currentQueuePos >= queue.size()-1){
                    nextButton.setEnabled(false);
                    nextButton.setVisibility(View.INVISIBLE);
                }
                if(currentQueuePos > 0){
                    prevButton.setEnabled(true);
                    prevButton.setVisibility(View.VISIBLE);

                }
            }
        });

        et_title = findViewById(R.id.edit_title);
        et_album = findViewById(R.id.albumIn);
        et_track = findViewById(R.id.trackIn);
        et_year = findViewById(R.id.yearIn);
        et_comment = findViewById(R.id.commentIn);


        ib_artwork = findViewById(R.id.coverArt);
        ib_artwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AlbumCoverActivity.class);
                startActivityForResult(i, 64);
            }
        });

        et_year.setText("" + (Calendar.getInstance().get(Calendar.YEAR)-2));

        //fManager = new FileManager();
        queue = new ArrayList<File>();

        /*
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
        */


        setupDrawer();
        setupActionBar(getResources().getString(R.string.id3v24edit));
        setupEditorHead();

    }

    private void resetAll() {
        et_title.setText("");
        et_album.setText("");
        et_track.setText("");
        et_comment.setText("");
        et_year.setText("");

        for (int i = 1; i < artistList.size(); i++) {
            EditText et = artistList.get(i);
            ViewGroup parent = (ViewGroup) et.getParent().getParent();
            parent.removeViewAt(1);
        }
        artistList = new ArrayList<>();
        artistList.add((EditText) findViewById(R.id.artistIn));

        for (int i = 1; i < genreList.size(); i++) {
            EditText et = genreList.get(i);
            ViewGroup parent = (ViewGroup) et.getParent().getParent();
            parent.removeViewAt(1);
        }
        genreList = new ArrayList<>();
        genreList.add((EditText) findViewById(R.id.genreIn));

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }

            mediaPlayer.release();
            mediaPlayer = null;

            playButton.setBackgroundDrawable(new IconicsDrawable(id3v24editor.this)
                    .icon(GoogleMaterial.Icon.gmd_play_arrow).sizeDp(20)
                    .color(getResources().getColor(R.color.colorPrimary)));

            playButton.setEnabled(true);
            saveButton.setEnabled(true);
            shareButton.setEnabled(true);
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

    private boolean load() {
        resetAll();
        MP3File mp3;

        try {
            mp3 = (MP3File) AudioFileIO.read(currentFile);
        } catch (CannotReadException | TagException | IOException | ReadOnlyFileException | InvalidAudioFrameException e) {
            return false;
        }

        Tag tag = mp3.getTag();

        List<String> artists = tag.getAll(FieldKey.ARTIST);
        if(artists.size() != 0) {
            for (int i = 1; i < artists.size(); i++) {
                addInputLineArtist(findViewById(R.id.addArtist));
            }

            for (int i = 0; i < artists.size(); i++) {
                artistList.get(i).setText(artists.get(i));
            }
        } else {
            ((EditText) findViewById(R.id.artistIn)).setText("");
        }

        if (mp3.hasID3v2Tag()) {
            List<String> genres = tag.getAll(FieldKey.GENRE);
            if(genres.size() != 0) {
                if (genres.size() != 0) {
                    for (int i = 1; i < genres.size(); i++) {
                        addInputLineGenre(findViewById(R.id.addGenre));
                    }

                    for (int i = 0; i < genres.size(); i++) {
                        genreList.get(i).setText(genres.get(i));
                    }
                }
            } else {
                ((EditText) findViewById(R.id.genreIn)).setText("");
            }
        } else {
            genreList.get(0).setText("");
        }

        et_title.setText(tag.getFirst(FieldKey.TITLE));
        et_album.setText(tag.getFirst(FieldKey.ALBUM));
        et_year.setText(tag.getFirst(FieldKey.YEAR));
        et_comment.setText(tag.getFirst(FieldKey.COMMENT));
        et_track.setText(tag.getFirst(FieldKey.TRACK));

        /*
        try {
            Artwork cover = tag.getFirstArtwork();
            byte[] data = cover.getBinaryData();
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

            ib_artwork.setImageBitmap(bmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        playButton.setEnabled(true);
        saveButton.setEnabled(true);
        shareButton.setEnabled(true);

        return true;
    }

    private boolean save() {
        MP3File mp3;
        System.out.println("Save comming");
        try {
            mp3 = (MP3File) AudioFileIO.read(currentFile);
        } catch (CannotReadException | TagException | IOException | ReadOnlyFileException | InvalidAudioFrameException e) {
            return false;
        }

        ID3v24Tag tag = new ID3v24Tag();

        try {
            for (EditText et : artistList) {
                tag.addField(FieldKey.ARTIST, et.getText().toString());
            }
            for (EditText et : genreList) {
                tag.addField(FieldKey.GENRE, et.getText().toString());
            }

            tag.setField(FieldKey.TITLE, et_title.getText().toString());
            tag.setField(FieldKey.ALBUM, et_album.getText().toString());
            tag.setField(FieldKey.YEAR, et_year.getText().toString());
            tag.setField(FieldKey.COMMENT, et_comment.getText().toString());
            tag.setField(FieldKey.TRACK, et_track.getText().toString());
        } catch (FieldDataInvalidException e) {
           return false;
        }

        mp3.setTag(tag);

        try {
            AudioFileIO.write(mp3);
        } catch (CannotWriteException e) {
            return false;
        }

        System.out.println("Toast comming");
        Toast.makeText(getBaseContext(), "Der Tag wurde gespeichert", Toast.LENGTH_LONG).show();
        //does not work for no reason
        return true;
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
                        } else if (identifier == 3) {
                            open23();
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

        mainDrawer.setSelection(4);
    }

    private void open23() {
        Intent intent = new Intent(this, id3v23editor.class);
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    /**
     * Method is called when "Options"-button is pressed (if it exists)
     * Leave empty if "hasDrawerButton" in setupActionBar was false
     */
    private void options() {
        View optionBtn = findViewById(R.id.open_options);

        PopupMenu popup = new PopupMenu(this, optionBtn);

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.action, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String itemTitle = item.getTitle().toString();

                System.out.println(itemTitle);
                if(itemTitle.equals(getString(R.string.action_add_to_queue))) {
                    addFilesDialog();
                }
                else if(itemTitle.equals(getString(R.string.action_change_queue))){
                    deleteFilesDialog();
                }
                return false;
            }
        });

        popup.show();
    }

    private void addFilesDialog(){
        DialogFragment df = new FileExplorer();
        df.show(this.getSupportFragmentManager(), "Choose Files");
    }

    private void deleteFilesDialog(){
        FileSelecter df = new FileSelecter();
        df.fileList = queue;
        df.show(this.getSupportFragmentManager(), "Select Files");
    }

    public boolean addFile(File f){
        boolean isNewFile = true;
        for(File g:queue){
            System.out.println(f.getAbsolutePath() + "|" + g.getAbsolutePath());
            if(f.getAbsolutePath().equals(g.getAbsolutePath())){
                isNewFile = false;

                break;
            }
        }
        System.out.println(isNewFile);
        if(isNewFile){
            queue.add(f);
            nextButton.setVisibility(View.VISIBLE);
            nextButton.setEnabled(true);
            return true;
        }//TODO enable SDCard Exploration + Alles auf 23 copieren + string machen
        return false;
    }

    public void clearAddFile(File f){
        queue.add(f);
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
        saveButton = findViewById(R.id.save_button);
        shareButton = findViewById(R.id.share_button);

        playButton.setBackgroundDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_play_arrow).sizeDp(20)
                .color(getResources().getColor(R.color.colorPrimary)));
        shareButton.setBackgroundDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_share).sizeDp(20)
                .color(getResources().getColor(R.color.colorPrimary)));

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });


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

        playButton.setEnabled(false);
        saveButton.setEnabled(false);
        shareButton.setEnabled(false);
    }

    private void share() {
        if (currentFile != null) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("audio/*");
            sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.fromFile(currentFile));
            startActivity(Intent.createChooser(sharingIntent,"Share using"));
        }
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

    @Override
    public void getFragmentResult(File file, DialogFragment frag) {
        if(frag.getClass().equals(FileExplorer.class)){
            addFile(file);
        }
        else{
            queue.clear();
            clearAddFile(file);
        }

        firstLoad();

        frag.dismiss();
    }

    @Override
    public void getMultipleFragmentResult(File[] multiFile, DialogFragment frag) {
        if(frag.getClass().equals(FileExplorer.class)){
            int counter = 0;
            for(File file:multiFile){
                if(addFile(file)){
                    counter++;
                }
            }
            Toast.makeText(this, counter + " Dateien wurden hinzugefügt", Toast.LENGTH_LONG).show();
        }
        else{
            queue.clear();
            for(File file:multiFile){
                clearAddFile(file);
            }
        }

        firstLoad();

        frag.dismiss();
    }

    public void firstLoad() {
        if(queue.isEmpty()){
            currentQueuePos = 0;
            prevButton.setEnabled(false);
            prevButton.setVisibility(View.INVISIBLE);
            nextButton.setEnabled(false);
            nextButton.setVisibility(View.INVISIBLE);
        }
        else{
            if(currentQueuePos <= 0){
                currentQueuePos = 0;
                prevButton.setEnabled(false);
                prevButton.setVisibility(View.INVISIBLE);
            }
            else if(currentQueuePos >= queue.size()){
                currentQueuePos = queue.size()-1;
                nextButton.setEnabled(false);
                nextButton.setVisibility(View.INVISIBLE);
            }
            currentFile = queue.get(currentQueuePos);
            resetAll();
            load();
        }
    }
}


