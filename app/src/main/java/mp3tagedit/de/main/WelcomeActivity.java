package mp3tagedit.de.main;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

/**
 * This class handles everything displayed on the start screen of the app,
 * e.g. the processing of saved queues
 *
 * @author Vincent
 */
public class WelcomeActivity extends AppCompatActivity {

    private Drawer mainDrawer;

    private final static int PERM_REQ_WRITE_STORAGE = 42;
    private final static int PERM_REQ_READ_STORAGE = 43;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        setupActionBar("");
        setupDrawer();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERM_REQ_WRITE_STORAGE);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERM_REQ_READ_STORAGE);
        }

        setupResume();
    }

    /**
     * Overrides existing method, gets called when any opened activity terminates
     * and handles the redrawing of this activity to show resume-buttons if necessary
     */
    @Override
    protected void onRestart() {
        super.onRestart();

        setupResume();

        mainDrawer.setSelection(1);
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
                        finish(); //denied
                    } else {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            // granted
                        } else {
                            finish(); //not clicked
                        }
                    }
                }
            } case PERM_REQ_READ_STORAGE: {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    finish(); //denied
                } else {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        // granted
                    } else {
                        finish(); //not clicked
                    }
                }
            }
        }
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

                        if (identifier == 2) {
                            openSettings();
                            return true;
                        } else if (identifier == 3) {
                            open23();
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

        mainDrawer.setSelection(1);

        mainDrawer.openDrawer();
        mainDrawer.closeDrawer();
    }

    /**
     * Checks if there are saved queues for the id3v23 and id3v24 editors
     * if it finds saved queues it converts them into lists and sets up
     * the resume buttons to start the editors with these lists
     */
    private void setupResume() {
        final SharedPreferences prefs24 = getApplicationContext().getSharedPreferences("queueSavePrefs24", 0);
        int newQueuePos24 = prefs24.getInt("queuePos", -1);

        if (newQueuePos24 == -1) {
            LinearLayout ll = findViewById(R.id.resume24layout);
            ll.setVisibility(View.GONE);
        } else {
            LinearLayout ll = findViewById(R.id.resume24layout);
            ll.setVisibility(View.VISIBLE);

            Button delRes24 = findViewById(R.id.deleteResume24);
            delRes24.setBackgroundDrawable(new IconicsDrawable(this)
                    .icon(GoogleMaterial.Icon.gmd_delete).sizeDp(15)
                    .color(getResources().getColor(R.color.colorPrimary)));

            delRes24.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = prefs24.edit();
                    editor.putInt("queuePos", -1);
                    editor.apply();

                    LinearLayout ll = findViewById(R.id.resume24layout);
                    ll.setVisibility(View.GONE);
                }
            });

            Button res24 = findViewById(R.id.resumeButton24);
            res24.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    open24WithQueue();
                }
            });
        }

        final SharedPreferences prefs23 = getApplicationContext().getSharedPreferences("queueSavePrefs23", 0);
        int newQueuePos23 = prefs23.getInt("queuePos", -1);

        if (newQueuePos23 == -1) {
            LinearLayout ll = findViewById(R.id.resume23layout);
            ll.setVisibility(View.GONE);
        } else {
            LinearLayout ll = findViewById(R.id.resume23layout);
            ll.setVisibility(View.VISIBLE);

            Button delRes23 = findViewById(R.id.deleteResume23);
            delRes23.setBackgroundDrawable(new IconicsDrawable(this)
                    .icon(GoogleMaterial.Icon.gmd_delete).sizeDp(15)
                    .color(getResources().getColor(R.color.colorPrimary)));

            delRes23.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = prefs23.edit();
                    editor.putInt("queuePos", -1);
                    editor.apply();

                    LinearLayout ll = findViewById(R.id.resume23layout);
                    ll.setVisibility(View.GONE);
                }
            });

            Button res23 = findViewById(R.id.resumeButton23);
            res23.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    open23WithQueue();
                }
            });
        }
    }

    /**
     * opens up the editor for id3v24 tags with the saved queue
     */
    private void open24WithQueue() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("queueSavePrefs24", 0);
        String newQueueString = prefs.getString("queueSave","0");
        if (!newQueueString.equals("0")) {
            String[] newQueue = newQueueString.split("|");
            int newQueuePos = prefs.getInt("queuePos", 0);

            Intent intent = new Intent(this, id3v24editor.class);
            intent.putExtra("queuePos", newQueuePos);
            intent.putExtra("queueStrings", newQueue);
            startActivity(intent);
        } else {
            open24();
        }
    }

    /**
     * opens up the editor for id3v23 tags with the saved queue
     */
    private void open23WithQueue() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("queueSavePrefs23", 0);
        String newQueueString = prefs.getString("queueSave","0");
        if (!newQueueString.equals("0")) {
            String[] newQueue = newQueueString.split("|");
            int newQueuePos = prefs.getInt("queuePos", 0);

            Intent intent = new Intent(this, id3v23editor.class);
            intent.putExtra("queuePos", newQueuePos);
            intent.putExtra("queueStrings", newQueue);
            startActivity(intent);
        } else {
            open23();
        }
    }

    /**
     * opens up the editor for id3v24 tags
     */
    private void open24() {
        Intent intent = new Intent(this, id3v24editor.class);
        intent.putExtra("queuePos", 0);
        intent.putExtra("queueStrings", new String[]{"[IDENT]"});
        startActivity(intent);
    }

    /**
     * opens up the editor for id3v23 tags
     */
    private void open23() {
        Intent intent = new Intent(this, id3v23editor.class);
        intent.putExtra("queuePos", 0);
        intent.putExtra("queueStrings", new String[]{"[IDENT]"});
        startActivity(intent);
    }

    /**
     * opens the tag to file converter
     */
    private void openTagToFile() {
        Intent intent = new Intent(this, tagtofile.class);
        startActivity(intent);
    }

    /**
     * opens the help window
     */
    private void openHelp() {
        Intent intent = new Intent(this, help.class);
        startActivity(intent);
    }

    /**
     * opens the settings window
     */
    private void openSettings() {
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
    }

    /**
     * sets up the actionbar on top of the screen
     * @param title the title to be displayed in the middle of the bar, use "" if no title is needed
     */
    private void setupActionBar(String title) {
        Button openDrawer = findViewById(R.id.open_drawer);
        TextView activityTitle = findViewById(R.id.activity_title);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point(); display.getSize(size);

        openDrawer.setBackgroundDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_menu).sizeDp(30).color(getResources().getColor(R.color.colorPrimary)));
        openDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainDrawer.openDrawer();
            }
        });

        activityTitle.setText(title); //sets the TextViews text
    }
}
