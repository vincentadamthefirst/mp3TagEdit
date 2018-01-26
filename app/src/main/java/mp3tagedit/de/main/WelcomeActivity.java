package mp3tagedit.de.main;

import android.content.Intent;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button b1 = findViewById(R.id.button1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open24();
            }
        });


        Button b2 = findViewById(R.id.button2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open23();
            }
        });
    }

    public void open24() {
        Intent intfeed = new Intent(this, id3v24editor.class);
        startActivity(intfeed);
    }

    public void open23() {
        Intent intfeed = new Intent(this, id3v23editor.class);
        startActivity(intfeed);
    }
}
