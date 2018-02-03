package mp3tagedit.de.main;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
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
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.Calendar;

/**
 * This class handles everything needed for editing id3v24 tags.
 * It is an extended version of the id3v23 editor as it also handles
 * multiple artists as well as multiple genres
 *
 * TODO find a way to save to SD-Card
 *
 * @author Vincent, André
 */
public class id3v24editor extends AppCompatActivity implements DialogFragmentResultListener{

    // Navigation Drawer
    private Drawer mainDrawer;

    // all EditTexts used for input
    private EditText et_title;
    private EditText et_album;
    private EditText et_year;
    private EditText et_track;
    private EditText et_comment;
    public static ArrayList<EditText> artistList;
    public static ArrayList<EditText> genreList;
    // ImageButton for the Cover
    private ImageButton ib_artwork;

    // MediaPlayer to listen to the current song
    MediaPlayer mediaPlayer;
    private int currentPos;

    private AdapterView.OnItemSelectedListener slcItmListener;

    // used to handle what the current file is and what the previous/next files are
    private ArrayList<File> queue;
    int currentQueuePos = 0;
    File currentFile;

    // buttons for nabigating through files / interact with them
    private Button playButton;
    private Button saveButton;
    private Button shareButton;
    private Button nextButton;
    private Button prevButton;
    private Button saveQueue;

    private Bitmap currentCover;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_id3v24editor);

        queue = new ArrayList<>();

        artistList = new ArrayList<>();
        artistList.add((EditText)(findViewById(R.id.artistIn)));
        genreList = new ArrayList<>();
        genreList.add((EditText)(findViewById(R.id.genreIn)));

        et_title = findViewById(R.id.edit_title);
        et_album = findViewById(R.id.albumIn);
        et_track = findViewById(R.id.trackIn);
        et_year = findViewById(R.id.yearIn);
        et_comment = findViewById(R.id.commentIn);

        et_year.setText("" + (Calendar.getInstance().get(Calendar.YEAR)-2));

        ib_artwork = findViewById(R.id.coverArt);

        setupNavigationButtons();
        setupDrawer();
        setupActionBar(getResources().getString(R.string.id3v24edit));
        setupEditorHead();

        String[] newQueue = getIntent().getStringArrayExtra("queueStrings");
        int newPos = getIntent().getIntExtra("queuePos", -1);
        if ((newQueue.length == 1) & (newQueue[0].equals("[IDENT]"))) {
            // request new queue
        } else {
            for (String s : newQueue) {
                queue.add(new File(s));
            }

            if (newPos != -1) {
                currentQueuePos = newPos;
            } else {
                currentQueuePos = 0;
            }

            currentFile = queue.get(currentQueuePos);
            load();
        }
    }

    /**
     * Overrides existing method, waits for the result of any activity launched in this activity
     * @param requestCode  The code of the terminating Activity
     * @param data The data transmitted by the terminating Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 64) {
            if (resultCode == RESULT_OK) {
                byte[] toBmp = data.getByteArrayExtra("path");
                CoverGenTags cgt = (CoverGenTags) data.getSerializableExtra("tags");

                if (cgt.getAlbumName() != null) {
                    et_album.setText(cgt.getAlbumName());
                }


                if (toBmp.length != 1) {
                    currentCover = BitmapFactory.decodeByteArray(toBmp, 0, toBmp.length);
                }

                ib_artwork.setImageBitmap(currentCover);
            }
        }
    }

    /**
     * Adds Listeners to the Navigation Buttons on the bottom of the screen
     */
    private void setupNavigationButtons() {
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

        saveQueue = findViewById(R.id.saveQueue);
        saveQueue.setBackgroundDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_save).sizeDp(15)
                .color(getResources().getColor(R.color.colorPrimary)));
        saveQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getApplicationContext().getSharedPreferences("queueSavePrefs24", 0);
                SharedPreferences.Editor editor = prefs.edit();

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < queue.size(); i++) {
                    sb.append(queue.get(i)).append("|");
                }

                editor.putString("queueSave", sb.toString());
                editor.putInt("queuePos", currentQueuePos);

                editor.apply();

                finish();
            }
        });
    }

    /**
     * Resets all input fields to refill them with the information of the next file
     */
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

            playButton.setEnabled(false);
            saveButton.setEnabled(false);
            shareButton.setEnabled(false);
        }

        ib_artwork.setImageResource(android.R.color.transparent);
    }

    /**
     * Adds a new line for the list of artists
     */
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

    /**
     * Adds a new line for the list of genres
     */
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

    /**
     * loads the metadata of "currentFile"
     * @return if all operations succeeded
     */
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

        // trys to load an artwork, if it catches an exception it leaves it out
        try {
            Artwork cover = tag.getFirstArtwork();
            byte[] data = cover.getBinaryData();
            currentCover = BitmapFactory.decodeByteArray(data, 0, data.length);

            if (currentCover.getWidth() > 1000 | currentCover.getHeight() > 1000) {
                currentCover = Bitmap.createScaledBitmap(currentCover, 1000, 1000, false);
            }

            ib_artwork.setImageBitmap(currentCover);
        } catch (Exception e) {}


        playButton.setEnabled(true);
        saveButton.setEnabled(true);
        shareButton.setEnabled(true);

        return true;
    }

    /**
     * saves the currently defined metadata
     * @return if all operations succeeded
     */
    private boolean save() {
        MP3File mp3;
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

        if (currentCover != null) {
            File tmp = new File(getExternalFilesDir(null) + "tmp.jpeg");
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(tmp);
                currentCover.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                Artwork art = ArtworkFactory.createArtworkFromFile(tmp);

                tag.setField(art);
            } catch (IOException | FieldDataInvalidException e) {
                e.printStackTrace();
            }
            tmp.delete();
        }

        mp3.setTag(tag);

        try {
            AudioFileIO.write(mp3);
        } catch (CannotWriteException e) {
            return false;
        }

        Toast.makeText(getBaseContext(), getResources().getString(R.string.save_complete), Toast.LENGTH_LONG).show();
        return true;
    }

    /**
     * sets up all items in the navigation drawer
     */
    private void setupDrawer() {
        PrimaryDrawerItem homItem = new PrimaryDrawerItem().withIdentifier(1)
                .withIcon(GoogleMaterial.Icon.gmd_home).withName(R.string.home)
                .withTextColor(getResources().getColor(R.color.defaultText))
                .withSelectedTextColor(getResources().getColor(R.color.colorPrimaryDark))
                .withIconColor(getResources().getColor(R.color.defaultText))
                .withSelectedIconColor(getResources().getColor(R.color.colorPrimaryDark));
        SecondaryDrawerItem setItem = new SecondaryDrawerItem().withIdentifier(2)                   //currently unused as there are no options needed, reserved for future usage
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
                        helItem
                        //setItem
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

    /**
     * opens up the editor for id3v23 tags
     */
    private void open23() {
        Intent intent = new Intent(this, id3v23editor.class);
        intent.putExtra("queuePos", 0);
        intent.putExtra("queueStrings", new String[]{"[IDENT]"});
        startActivity(intent);

        finish();
    }

    /**
     * opens the tag to file converter
     */
    private void openTagToFile() {
        Intent intent = new Intent(this, tagtofile.class);
        startActivity(intent);

        finish();
    }

    /**
     * opens the help window
     */
    private void openHelp() {
        Intent intent = new Intent(this, help.class);
        startActivity(intent);

        finish();
    }

    /**
     * opens the settings window
     */
    private void openSettings() {
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);

        finish();
    }

    /**
     * opens up the home screen
     */
    private void openHome() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);

        finish();
    }

    /**
     * Method is called when "Options"-button is pressed
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

    /**
     * Opens the Dialog to add files to the queue
     */
    private void addFilesDialog(){
        DialogFragment df = new FileExplorer();
        df.show(this.getSupportFragmentManager(), "Choose Files");
    }

    /**
     * Opens the Dialog to delete files from the queue
     */
    private void deleteFilesDialog(){
        FileSelecter df = new FileSelecter();
        df.fileList = queue;
        df.show(this.getSupportFragmentManager(), "Select Files");
    }

    /**
     * Checks whether the File f is already in the Queue, adds it to it if no.
     * @param f The File that should be added
     * @return if the file was added or not
     */
    public boolean addFile(File f){
        boolean isNewFile = true;
        for(File g:queue){
            if(f.getAbsolutePath().equals(g.getAbsolutePath())){
                isNewFile = false;

                break;
            }
        }
        if(isNewFile){
            queue.add(f);
            nextButton.setVisibility(View.VISIBLE);
            nextButton.setEnabled(true);
            return true;
        }
        return false;
    }

    /**
     * Adds the File f directly to the Queue, skipping any double-checking
     * @param f The File that should be added
     */
    public void clearAddFile(File f){
        queue.add(f);
    }

    /**
     * sets up the actionbar on top of the screen
     * @param title the title to be displayed in the middle of the bar, use "" if no title is needed
     */
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

    /**
     * sets up all necessary components for the head of the editor (e.g. listeners for the buttons)
     */
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

        ib_artwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CoverGenTags coverGenTags = new CoverGenTags(et_title.getText().toString(), artistList.get(0).getText().toString(), genreList.get(0).getText().toString(), et_year.getText().toString());
                if (currentCover != null) {
                    coverGenTags.setImage(currentCover);
                }

                Intent i = new Intent(getApplicationContext(), AlbumCoverActivity.class);
                i.putExtra("tags", coverGenTags);

                startActivityForResult(i, 64);
            }
        });

        playButton.setEnabled(false);
        saveButton.setEnabled(false);
        shareButton.setEnabled(false);
    }

    /**
     * shares the current mp3 File after saving it as an Audio File
     */
    private void share() {
        if (currentFile != null) {
            save();

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("audio/*");
            sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.fromFile(currentFile));
            startActivity(Intent.createChooser(sharingIntent,getResources().getString(R.string.share)));
        } else {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.share_error), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Plays the currently selected song
     */
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

    /**
     * pauses the player and saves its state to be later able to resume the song
     */
    private void pauseSong(){
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            currentPos = mediaPlayer.getCurrentPosition();
        }
    }

    /**
     * Overrides existing method, waits for the result of the file chooser and handles it (1 file)
     * @param file The file returned by the Dialog
     * @param frag The fragment terminating
     */
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

    /**
     * Overrides existing method, waits for the result of the file choosers and handles it (>1 file)
     * @param multiFile The files returned by the Dialog
     * @param frag The fragment terminating
     */
    @Override
    public void getMultipleFragmentResult(File[] multiFile, DialogFragment frag) {
        if(frag.getClass().equals(FileExplorer.class)){
            int counter = 0;
            for(File file:multiFile){
                if(addFile(file)){
                    counter++;
                }
            }
            Toast.makeText(this, counter + getResources().getString(R.string.files_added), Toast.LENGTH_LONG).show();
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

    /**
     * Method to initialize the first loaded file and displaying all necessary information
     */
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