package mp3tagedit.de.main;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewManager;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class id3v24editor extends AppCompatActivity {

    private static Button openDrawer;
    private static Button openOptions;
    private Drawer mainDrawer;
    private TextView activityTitle;

    private final static int ACTIONBARSIZE = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_id3v24editor);


        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Home");
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withIcon(FontAwesome.Icon.faw_search).withName("Settings");

        //create the drawer and remember the `Drawer` result object
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
                }).withDrawerWidthDp(220).build();
        setupActionBar(true, true, "Test");
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    /**
     * Method get called when "Options"-button is pressed (if it exists)
     * Leave empty if "hasDrawerButton" in setupActionBar was false
     */
    private void options() {

    }

    private void setupActionBar(boolean hasOptionsButton, boolean hasDrawerButton, String title) {
        openDrawer = findViewById(R.id.open_drawer);
        openOptions = findViewById(R.id.open_options);
        activityTitle = findViewById(R.id.activity_title);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point(); display.getSize(size);

        if (hasDrawerButton) {
            //openDrawer.setWidth(ACTIONBARSIZE); openDrawer.setHeight(ACTIONBARSIZE);
            openDrawer.setBackgroundDrawable(new IconicsDrawable(this).icon(FontAwesome.Icon.faw_bars).sizeDp(30).color(Color.WHITE));
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
            //penOptions.setWidth(ACTIONBARSIZE); openOptions.setHeight(ACTIONBARSIZE);
            openOptions.setBackgroundDrawable(new IconicsDrawable(this).icon(FontAwesome.Icon.faw_ellipsis_v).sizeDp(30).color(Color.WHITE));
            openOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    options();
                }
            });
        } else {
            ((ViewManager) openOptions.getParent()).removeView(openOptions);
        }

        activityTitle.setWidth(size.x - 2*ACTIONBARSIZE);
        activityTitle.setHeight(ACTIONBARSIZE);
        activityTitle.setText(title); //sets the TextViews text
    }
}
