package mp3tagedit.de.main;

import android.content.Context;
import android.graphics.Point;
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
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;

public class id3v23editor extends AppCompatActivity {

    private Drawer mainDrawer;

    private final static int PERM_REQ_WRITE_STORAGE = 42;
    private final static int PERM_REQ_READ_STORAGE = 43;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_id3v23editor);

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Home");
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withIcon(FontAwesome.Icon.faw_search).withName("Settings");

        //create the drawer and remember the `Drawer` object
        mainDrawer = new DrawerBuilder()
                .withActivity(this)
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
                        return true;
                    }
                }).withDrawerWidthDp(240).build();

        setupActionBar(true, true, "id3v2.4 Editor");
        setupEditorHead();
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
        Point size = new Point();
        display.getSize(size);

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
        Button playButton = findViewById(R.id.play_button);
        Button saveButton = findViewById(R.id.save_button);
        Button shareButton = findViewById(R.id.share_button);

        playButton.setBackgroundDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_play_circle_outline).sizeDp(20).color(getResources().getColor(R.color.colorPrimary)));
        shareButton.setBackgroundDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_share).sizeDp(20).color(getResources().getColor(R.color.colorPrimary)));

        saveButton.setText("TEST");
    }
}
