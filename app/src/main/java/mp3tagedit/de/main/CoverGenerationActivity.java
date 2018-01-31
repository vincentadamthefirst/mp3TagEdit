package mp3tagedit.de.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

public class CoverGenerationActivity extends AppCompatActivity {

    EditText ET_Cover;
    CoverGenConfig CoverConfig;
    CoverGenTags defaultTestTags = new CoverGenTags("<title>", "<artist>", "<genre>", "<year>");
    CoverGenTags activeTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover_generation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ET_Cover = findViewById(R.id.XML_ET);

        activeTags = (CoverGenTags) getIntent().getSerializableExtra("tags");
        if(activeTags == null){
            activeTags = defaultTestTags;
        }

        newXML();
        readXML();

        Button B_Show_Image = (Button) findViewById(R.id.button1);
        B_Show_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CoverConfig == null){
                    return;
                }
                readXML();
                dialog();
            }
        });
        Button B_Load_XML = (Button) findViewById(R.id.button2);
        B_Load_XML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readFile();
                readXML();
            }
        });
        Button B_Save_XML = (Button) findViewById(R.id.button3);
        B_Save_XML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveXML();
            }
        });
        Button B_New_XML = (Button) findViewById(R.id.button4);
        B_New_XML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newXML();
                readXML();
            }
        });
        Button B_Save_Image = (Button) findViewById(R.id.button5);
        B_Save_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activeTags.setAlbumName(CoverConfig.getAbb() + "_" + String.format("%04d", CoverConfig.getIndex()));

                Intent i = new Intent();
                Bitmap b = draw(); // your bitmap
                activeTags.setImage(Bitmap.createScaledBitmap(b, 1000, 1000, false));
                i.putExtra("active", activeTags);
                setResult(RESULT_OK, i);
                finish();
            }
        });

    }

    public Bitmap draw(){
        Bitmap src = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.carbon), 3000, 3000, false);
        Bitmap dest = Bitmap.createBitmap(src.getWidth() , src.getHeight() , Bitmap.Config.ARGB_8888);

        CoverGenTags tags;
        String[] order = new String[4];

        tags = activeTags;
        int index = CoverConfig.getIndex();

        int dist = src.getHeight()/6;
        int x0 = 50;
        int y = -100;

        Canvas cs = new Canvas(dest);
        cs.drawBitmap(src, 0f, 0f, null);
        Paint tPaint = new Paint();
        tPaint.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + CoverConfig.getFonts()[0] + ".ttf"));
        tPaint.setStyle(Paint.Style.FILL);
        if(CoverConfig.isPL()){
            tPaint.setColor(Color.parseColor(CoverConfig.getColors()[0]));
            tPaint.setTextSize(250);
            cs.save();  // saves the current state of the canvas
            cs.rotate(-5.0f); //rotates 30 degrees
            cs.drawText(CoverConfig.getPL_Name() , 100, 500, tPaint);
            cs.restore(); //return to 0 degree
            y+= 500;
            dist = (src.getHeight()-600)/6;

            tPaint.setColor(Color.parseColor(CoverConfig.getColors()[1]));
            tPaint.setTextSize(200);
            cs.drawText(String.format("%04d", index), src.getWidth()-tPaint.measureText("0000")-100, 550, tPaint);
        }else{
            tPaint.setColor(Color.parseColor(CoverConfig.getColors()[1]));
            tPaint.setTextSize(200);
            cs.drawText(String.format("%04d", index), src.getWidth()-tPaint.measureText("0000")-100, 2800, tPaint);
        }

        tPaint.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + CoverConfig.getFonts()[1] + ".ttf"));
        tPaint.setColor(Color.parseColor(CoverConfig.getColors()[2]));
        tPaint.setTextSize(250);

        Paint tPaint2 = new Paint();
        tPaint2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + CoverConfig.getFonts()[1] + ".ttf"));
        tPaint2.setStyle(Paint.Style.FILL);
        tPaint2.setColor(Color.parseColor(CoverConfig.getColors()[3]));
        tPaint2.setTextSize(150);

        y += dist;

        for(int i = 0; i < 4; i++){
            if(CoverConfig.getEnableds()[i]){
                order[CoverConfig.getPositions()[i]] = CoverConfig.getTagNames()[i];
            }
        }
        tags.verifyLengths(tPaint, tPaint2);
        for(String s:order){
            if(s != null){
                switch (s){
                    case "Title:":
                        cs.drawText("Title:" , x0, y, tPaint);
                        cs.drawText(tags.getT1() , x0+tPaint.measureText("Title: "), y-10, tPaint2);
                        y += dist;
                        cs.drawText(tags.getT2() , x0, y-10, tPaint2);
                        y += dist;
                        break;
                    case "Artist:":
                        cs.drawText("Artist:" , x0, y, tPaint);
                        cs.drawText(tags.getA1() , x0+tPaint.measureText("Artist: "), y-10, tPaint2);
                        y += dist;
                        cs.drawText(tags.getA2() , x0, y-10, tPaint2);
                        y += dist;
                        break;
                    case "Genre:":
                        cs.drawText("Genre:" , x0, y, tPaint);
                        cs.drawText(tags.getGenre() , x0+tPaint.measureText("Genre: "), y-10, tPaint2);
                        y += dist;
                        break;
                    case "Year:":
                        cs.drawText("Year:" , x0, y, tPaint);
                        cs.drawText(tags.getYear() , x0+tPaint.measureText("Year: "), y-10, tPaint2);
                        y += dist;
                        break;
                }
            }
        }



//        tPaint.setTextSize(125);
//        tPaint.setColor(Color.WHITE);
//        tPaint.setStyle(Paint.Style.FILL);
//        float height = tPaint.measureText("yY");
//        float width = tPaint.measureText(yourText);
//        float x_coord = (src.getWidth() - width)/2;
//        cs.save();  // saves the current state of the canvas
//        cs.rotate(-30.0f); //rotates 30 degrees
//        cs.drawText("3AM", 200, 500, tPaint);
//        cs.restore(); //return to 0 degree
//        cs.drawText(yourText, x_coord, height+15f, tPaint);
        return dest;
    }

    private void dialog() {
        AlertDialog.Builder ImageDialog = new AlertDialog.Builder(this);
        ImageDialog.setTitle("Album Cover Test");
        ImageView showImage = new ImageView(this);
        showImage.setImageBitmap(draw()); //getResources().getDrawable(R.drawable.carbon));
        ImageDialog.setView(showImage);

        ImageDialog.setNegativeButton("ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {
            }
        });
        AlertDialog myDialog = ImageDialog.create();
        myDialog.show();
        myDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg2blurred));
        myDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);

    }

        public void readXML(){

        XmlPullParserFactory factory;

        try {

            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(ET_Cover.getText().toString()));

            boolean PL = true;
            int index = 0;
            String Abb = "";
            String PL_Name = "";
            String[] Colors = new String[4];
            String[] Fonts = new String[2];
            boolean[] Enabled = new boolean[4];
            int[] Positions = new int[4];

            String lastTag = "";

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                }else if (eventType == XmlPullParser.START_TAG){
                    switch (xpp.getName()){
                        case "Cover":
                            PL = Boolean.valueOf(xpp.getAttributeValue(0));
                            index = Integer.valueOf(xpp.getAttributeValue(1));
                            break;
                        case "Title":
                            Enabled[0] = Boolean.valueOf(xpp.getAttributeValue(0));
                            Positions[0] = Integer.valueOf(xpp.getAttributeValue(1));
                            break;
                        case "Artist":
                            Enabled[1] = Boolean.valueOf(xpp.getAttributeValue(0));
                            Positions[1] = Integer.valueOf(xpp.getAttributeValue(1));
                            break;
                        case "Genre":
                            Enabled[2] = Boolean.valueOf(xpp.getAttributeValue(0));
                            Positions[2] = Integer.valueOf(xpp.getAttributeValue(1));
                            break;
                        case "Year":
                            Enabled[3] = Boolean.valueOf(xpp.getAttributeValue(0));
                            Positions[3] = Integer.valueOf(xpp.getAttributeValue(1));
                            break;
                        case "PLName":
                            Abb = xpp.getAttributeValue(0);
                        default:
                            lastTag = xpp.getName();

                    }

                }else if (eventType == XmlPullParser.END_TAG){
                    lastTag = "";
                }else if (eventType == XmlPullParser.TEXT){
                    switch (lastTag){
                        case "PlaylistColor":
                            Colors[0] = xpp.getText();
                            break;
                        case "IndexColor":
                            Colors[1] = xpp.getText();
                            break;
                        case "TagColor":
                            Colors[2] = xpp.getText();
                            break;
                        case "TextColor":
                            Colors[3] = xpp.getText();
                            break;
                        case "PlaylistFont":
                            Fonts[0] = xpp.getText();
                            break;
                        case "TextFont":
                            Fonts[1] = xpp.getText();
                            break;
                        case "PLName":
                            PL_Name = xpp.getText();
                            break;
                    }
                }
                eventType = xpp.next();
            }
            CoverConfig = new CoverGenConfig(PL, Abb, index, PL_Name, Colors, Fonts, Enabled, Positions);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void readFile() {
        File f = new File(getExternalFilesDir("").getAbsolutePath(), "CoverSettings.xml");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            fis.close();

            ET_Cover.setText(sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void saveXML() {

        try {
            File f = new File(getExternalFilesDir("").getAbsolutePath(), "CoverSettings.xml");

            if(!f.exists()) {
                f.createNewFile();
            }

            FileWriter fw = new FileWriter(f, false);
            fw.write(ET_Cover.getText().toString());
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newXML(){
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output",true);
            serializer.startDocument("UTF-8", true);

            serializer.startTag("", "Cover");
            serializer.attribute("", "PL", "true");
            serializer.attribute("", "Index", "0");

            serializer.startTag("", "PLName");
            serializer.attribute("", "Abbreviation", "NAP");
            serializer.text("New Awesome Playlist");
            serializer.endTag("", "PLName");

            serializer.startTag("", "Colors");
            serializer.startTag("", "PlaylistColor");
            serializer.text("#00FFFF");
            serializer.endTag("", "PlaylistColor");
            serializer.startTag("", "IndexColor");
            serializer.text("#FF0000");
            serializer.endTag("", "IndexColor");
            serializer.startTag("", "TagColor");
            serializer.text("#FFC800");
            serializer.endTag("", "TagColor");
            serializer.startTag("", "TextColor");
            serializer.text("#FFC800");
            serializer.endTag("", "TextColor");
            serializer.endTag("", "Colors");

            serializer.startTag("", "Fonts");
            serializer.startTag("", "PlaylistFont");
            serializer.text("OldEngl");
            serializer.endTag("", "PlaylistFont");
            serializer.startTag("", "TextFont");
            serializer.text("OldEngl");
            serializer.endTag("", "TextFont");
            serializer.endTag("", "Fonts");

            serializer.startTag("", "Tags");
            serializer.startTag("", "Title");
            serializer.attribute("", "Enabled", "true");
            serializer.attribute("", "Pos", "0");
            serializer.text("");
            serializer.endTag("", "Title");

            serializer.startTag("", "Artist");
            serializer.attribute("", "Enabled", "true");
            serializer.attribute("", "Pos", "1");
            serializer.text("");
            serializer.endTag("", "Artist");

            serializer.startTag("", "Genre");
            serializer.attribute("", "Enabled", "true");
            serializer.attribute("", "Pos", "2");
            serializer.text("");
            serializer.endTag("", "Genre");

            serializer.startTag("", "Year");
            serializer.attribute("", "Enabled", "true");
            serializer.attribute("", "Pos", "3");
            serializer.text("");
            serializer.endTag("", "Year");
            serializer.endTag("", "Tags");

            serializer.endTag("", "Cover");

            serializer.endDocument();
            ET_Cover.setText(writer.toString());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}