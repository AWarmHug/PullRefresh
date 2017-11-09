package com.warm.pullrefresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.warm.library.WaveView;


public class WaveActivity extends AppCompatActivity {

    private WaveView wave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave);
        wave= (WaveView) this.findViewById(R.id.wave);
        wave.run();
    }
}
