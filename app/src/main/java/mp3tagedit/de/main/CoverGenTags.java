package mp3tagedit.de.main;

import java.io.Serializable;


public class CoverGenTags implements Serializable {

    String Title;
    String Artist;
    String Genre;
    String Year;

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
}
