package mp3tagedit.de.main;

import java.io.Serializable;


public class CoverGenConfig implements Serializable {

    String Abb;
    boolean PL;
    int index;
    String PL_Name;
    String[] Colors = new String[4];
    String[] Fonts = new String[2];
    boolean[] Enabled = new boolean[4];
    int[] Positions = new int[4];

    String[] TagNames = {"Title:", "Artist:", "Genre:", "Year:"};

    public CoverGenConfig() {
    }

    public CoverGenConfig(boolean PL, String Abb, int index, String PL_Name, String[] colors, String[] fonts, boolean[] enabled, int[] positions) {
        this.PL = PL;
        this.Abb = Abb;
        this.index = index;
        this.PL_Name = PL_Name;
        Colors = colors;
        Fonts = fonts;
        Enabled = enabled;
        Positions = positions;
    }

    public boolean isPL() {
        return PL;
    }

    public void setPL(boolean PL) {
        this.PL = PL;
    }

    public String getPL_Name() {
        return PL_Name;
    }

    public void setPL_Name(String PL_Name) {
        this.PL_Name = PL_Name;
    }

    public String[] getColors() {
        return Colors;
    }

    public void setColors(String[] colors) {
        Colors = colors;
    }

    public String[] getFonts() {
        return Fonts;
    }

    public void setFonts(String[] fonts) {
        Fonts = fonts;
    }

    public boolean[] getEnableds() {
        return Enabled;
    }

    public void setEnableds(boolean[] enabled) {
        Enabled = enabled;
    }

    public int[] getPositions() {
        return Positions;
    }

    public void setPositions(int[] positions) {
        Positions = positions;
    }

    public String[] getTagNames() {
        return TagNames;
    }

    public void setTagNames(String[] tagNames) {
        TagNames = tagNames;
    }

    public String getAbb() {
        return Abb;
    }

    public void setAbb(String abb) {
        Abb = abb;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
