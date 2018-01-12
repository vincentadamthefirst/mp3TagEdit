package mp3tagedit.de.main;

import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Random;

public class WelcomeActivity extends AppCompatActivity {

    //minimal Radius for the circles
    int minRadius = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        drawCircles(30, this.getCurrentFocus());
    }

    public void drawCircles(int k, View v) {
        Random random = new Random();
        int w = v.getWidth();
        int h = v.getHeight();

        for (int i = 0; i < k; i++) {
            int ranX = random.nextInt(w);
            int ranY = random.nextInt(h);
            int ranR = minRadius + random.nextInt(100);
        }
    }

}
