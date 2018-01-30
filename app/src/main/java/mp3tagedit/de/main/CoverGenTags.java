package mp3tagedit.de.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;


public class CoverGenTags implements Serializable {

    String Title = "";
    String Artist = "";
    String Genre = "";
    String Year = "";

    String AlbumName = "";

    String a1 = "";
    String a2 = "";
    String t1 = "";
    String t2 = "";

    byte[] image;

    public CoverGenTags() {
    }

    public CoverGenTags(String title, String artist, String genre, String year) {
        Title = title;
        Artist = artist;
        Genre = genre;
        Year = year;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public String getGenre() {
        return Genre;
    }

    public void setGenre(String genre) {
        Genre = genre;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public String getA1() {
        return a1;
    }

    public void setA1(String a1) {
        this.a1 = a1;
    }

    public String getA2() {
        return a2;
    }

    public void setA2(String a2) {
        this.a2 = a2;
    }

    public String getT1() {
        return t1;
    }

    public void setT1(String t1) {
        this.t1 = t1;
    }

    public String getT2() {
        return t2;
    }

    public void setT2(String t2) {
        this.t2 = t2;
    }

    public Bitmap getImage() {
        return BitmapFactory.decodeByteArray(image,0,image.length);
    }

    public void setImage(Bitmap image) {

        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, bs);
        this.image = bs.toByteArray();
    }

    public String getAlbumName() {
        return AlbumName;
    }

    public void setAlbumName(String albumName) {
        AlbumName = albumName;
    }

    public void verifyLengths(Paint tPaint0, Paint tPaint){
        String a = getArtist();
        String t = getTitle();
        a1 = "";
        t1 = "";
        a2 = "";
        t2 = "";
        String[] aa;
        String[] ta;
        float widthA = tPaint.measureText(a);
        float widthT = tPaint.measureText(t);

        float LengthA1 = 2900 - tPaint0.measureText("Artist: ");
        float LengthA2 = 2900;
        float LengthT1 = 2900 - tPaint0.measureText("Title: ");;
        float LengthT2 = 2900;

        if(widthA < LengthA1){
            a1 = a;
        }else if(widthA < LengthA2){
            a2 = a;
        }else{
            System.out.println(a + "\t" + widthA);
            if(a.contains("/")){
                aa = a.split("/");
                if(tPaint.measureText(aa[0]) < LengthA1){
                    a1 = aa[0] + "/";
                    a2 = aa[1];
                }else if(tPaint.measureText(aa[1]) < LengthA1){
                    a1 = aa[1] + "/";
                    a2 = aa[0];
                }else{
                    System.out.println("#####fuck " + aa[0] + " " + tPaint.measureText(aa[0]) + " " + aa[1] + " " + tPaint.measureText(aa[1]));
                }
            }else if(a.contains("(")){
                aa = a.split("\\(");
                if(tPaint.measureText(aa[0]) < LengthA1){
                    a1 = aa[0];
                    a2 = "(" + aa[1];
                }else if(tPaint.measureText(aa[1]) < LengthA1){
                    a1 = "(" + aa[1];
                    a2 = aa[0];
                }else{
                    System.out.println("#####fuck " + aa[0] + " " + tPaint.measureText(aa[0]) + " " + aa[1] + " " + tPaint.measureText(aa[1]));
                }
            }else if(a.contains("-")){
                aa = a.split("-");
                if(tPaint.measureText(aa[0]) < LengthA1){
                    a1 = aa[0];
                    a2 = aa[1];
                }else if(tPaint.measureText(aa[1]) < LengthA1){
                    a1 = aa[1];
                    a2 = aa[0];
                }else{
                    System.out.println("#####fuck " + aa[0] + " " + tPaint.measureText(aa[0]) + " " + aa[1] + " " + tPaint.measureText(aa[1]));
                }
            }else{
                int j = 0;
                aa = a.split(" ");
                while(tPaint.measureText(a1 + aa[j]) < LengthA1){
                    a1 = a1 + aa[j] + " ";
                    j++;
                }
                while(j < aa.length){
                    a2 = a2 + aa[j] + " ";
                    j++;
                }
            }


        }

        if(widthT < LengthT1){
            t1 = t;
        }else if(widthT < LengthT2){
            t2 = t;
        }else{
            System.out.println(t + "\t" + widthT);
            if(t.contains("/")){
                ta = t.split("/");
                if(tPaint.measureText(ta[0]) < LengthT1){
                    t1 = ta[0] + "/";
                    t2 = ta[1];
                }else if(tPaint.measureText(ta[1]) < LengthT1){
                    t1 = ta[1] + "/";
                    t2 = ta[0];
                }else{
                    System.out.println("#####fuck " + ta[0] + " " + tPaint.measureText(ta[0]) + " " + ta[1] + " " + tPaint.measureText(ta[1]));
                }
            }else if(t.contains("(")){
                ta = t.split("\\(");
                if(tPaint.measureText(ta[0]) < LengthT1){
                    t1 = ta[0];
                    t2 = "(" + ta[1];
                }else if(tPaint.measureText(ta[1]) < LengthT1){
                    t1 = "(" + ta[1];
                    t2 = ta[0];
                }else{
                    System.out.println("#####fuck " + ta[0] + " " + tPaint.measureText(ta[0]) + " " + ta[1] + " " + tPaint.measureText(ta[1]));
                }
            }else if(t.contains("-")){
                ta = t.split("-");
                if(tPaint.measureText(ta[0]) < LengthT1){
                    t1 = ta[0];
                    t2 = ta[1];
                }else if(tPaint.measureText(ta[1]) < LengthT1){
                    t1 = ta[1];
                    t2 = ta[0];
                }else{
                    System.out.println("#####fuck " + ta[0] + " " + tPaint.measureText(ta[0]) + " " + ta[1] + " " + tPaint.measureText(ta[1]));
                }
            }else{
                int j = 0;
                ta = t.split(" ");
                while(tPaint.measureText(t1 + ta[j]) < LengthT1){
                    t1 = t1 + ta[j] + " ";
                    j++;
                }
                while(j < ta.length){
                    t2 = t2 + ta[j] + " ";
                    j++;
                }
            }
        }
    }
}
