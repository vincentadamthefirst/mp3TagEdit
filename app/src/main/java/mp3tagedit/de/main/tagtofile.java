package mp3tagedit.de.main;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class tagtofile extends AppCompatActivity {

    private Drawer mainDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tagtofile);

        setupActionBar(getResources().getString(R.string.tagtofile));
        setupDrawer();
    }

    private void options() {

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

    private void open24() {
        Intent intent = new Intent(this, id3v24editor.class);
        startActivity(intent);
    }

    private void open23() {
        Intent intent = new Intent(this, id3v23editor.class);
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
}
