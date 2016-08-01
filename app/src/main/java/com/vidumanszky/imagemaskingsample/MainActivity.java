package com.vidumanszky.imagemaskingsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private SnakyView snakyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        snakyView = (SnakyView) findViewById(R.id.snakyView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        snakyView.stop();
    }
}
