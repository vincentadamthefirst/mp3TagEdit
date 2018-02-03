package mp3tagedit.de.main;

import android.content.Intent;
import android.graphics.Point;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Handles the Activity for renaming the files
 * Uses a user-defined pattern to rename the files according to their mp3-tags
 *
 * @author Vincent, André
 */
public class tagtofile extends AppCompatActivity implements DialogFragmentResultListener {

    private Drawer mainDrawer;

    private Button changeQueue;
    private Button addToQueue;
    private Button runRename;
    private Button stopRename;
    private TextView patternInput;

    private boolean kill;

    private ArrayList<File> queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tagtofile);

        setupActionBar(getResources().getString(R.string.tagtofile));
        setupDrawer();
        setupButtons();

        queue = new ArrayList<File>();
    }

    /**
     * Sets up all necessary buttons for this activity
     */
    private void setupButtons(){
        changeQueue = findViewById(R.id.changeQueue);
        addToQueue = findViewById(R.id.addToQueue);
        runRename = findViewById(R.id.startRename);
        stopRename = findViewById(R.id.cancelRename);

        patternInput = findViewById(R.id.patternIn);

        changeQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFilesDialog();
            }
        });

        addToQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFilesDialog();
            }
        });

        runRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renameQueue();
            }
        });

        stopRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kill = true;
            }
        });

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
                        } else if (identifier == 4) {
                            open24();
                            return true;
                        } else if (identifier == 6) {
                            openHelp();
                            return true;
                        } else {
                            return true;
                        }
                    }
                }).withDrawerWidthDp(240).build();

        mainDrawer.setSelection(5);

        mainDrawer.openDrawer();
        mainDrawer.closeDrawer();
    }

    /**
     * opens up the editor for id3v24 tags
     */
    private void open24() {
        Intent intent = new Intent(this, id3v24editor.class);
        intent.putExtra("queuePos", 0);
        intent.putExtra("queueStrings", new String[]{"[IDENT]"});
        startActivity(intent);

        finish();
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
     * sets up the actionbar on top of the screen
     * @param title the title to be displayed in the middle of the bar, use "" if no title is needed
     */
    private void setupActionBar(String title) {
        Button openDrawer = findViewById(R.id.open_drawer);
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


        activityTitle.setText(title); //sets the TextViews text
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
        System.out.println(isNewFile);
        if(isNewFile){
            queue.add(f);

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
     * Renames all files in the Queue according to the defined pattern
     */
    public boolean renameQueue(){
        kill = false;
        ArrayList<String[]> parsedPattern = parsePattern(patternInput.getText().toString());

        for(File f:queue){
            if(kill){break;}
            MP3File mp3;

            try {
                mp3 = (MP3File) AudioFileIO.read(f);
            } catch (CannotReadException | TagException | IOException | ReadOnlyFileException | InvalidAudioFrameException e) {
                return false;
            }

            Tag tag = mp3.getTag();

            String artist = tag.getFirst(FieldKey.ARTIST);
            String track = tag.getFirst(FieldKey.TRACK);
            String album = tag.getFirst(FieldKey.ALBUM);
            String genre = tag.getFirst(FieldKey.GENRE);
            String title = tag.getFirst(FieldKey.TITLE);
            String year = tag.getFirst(FieldKey.YEAR);


            String fileName = "";
            for(String[] parseParticle:parsedPattern){
                if(parseParticle[1].equals("1")){
                    switch(parseParticle[0]){
                        case "ARTIST":fileName += artist; break;
                        case "GENRE":fileName += genre; break;
                        case "TITLE":fileName += title; break;
                        case "ALBUM":fileName += album; break;
                        case "TRACK":fileName += track; break;
                        case "YEAR":fileName += year; break;
                        default:fileName += ""; break;
                    }
                }
                else{
                    fileName += parseParticle[0];
                }
            }
            String path = f.getParent() + "/" + fileName + getString(R.string.file_extension);
            if(new File(path).exists()){
                int count = 0;
                while(new File(f.getParent() + "/" + fileName + "(" + count + ")"
                        + getString(R.string.file_extension)).exists() ){
                    count++;
                }
                f.renameTo(new File(f.getParent() + "/" + fileName + "(" + count + ")" +
                        getString(R.string.file_extension)));
            } else{
                f.renameTo(new File(f.getParent() + "/" + fileName + getString(R.string.file_extension)));
            }
        }

        if(kill){
            Toast.makeText(this,  getString(R.string.rename_canceled), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, queue.size() + getString(R.string.files_renamed), Toast.LENGTH_LONG).show();
        }
        queue.clear();

        return true;
    }

    /**
     * parses the inserted pattern into a list that can later be used by other methods
     * @param pattern
     * @return
     */
    public ArrayList<String[]> parsePattern(String pattern) {
        ArrayList<String[]> parsed = new ArrayList<>();
        boolean inTagName = false;
        String storage = "";
        for(char c:pattern.toCharArray()) {
            switch(c){
                case '%':
                    if(inTagName) {
                        parsed.add(new String[]{storage.toUpperCase(), "1"});
                        storage = "";
                        inTagName = false;
                    } else {
                        parsed.add(new String[]{storage, "0"});
                        storage = "";
                        inTagName = true;
                    }
                    break;
                default:
                    storage += c;
                    break;
            }
        }
        return parsed;
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

        frag.dismiss();
    }
}
