package com.example.student.imageproc;

import android.content.Intent;
import android.media.Image;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";

    static{
        if(!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV Not Loaded");
        }else{
            Log.d(TAG, "OpenCV loaded");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button next = (Button) findViewById(R.id.camButton);
        next.setOnClickListener( this);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.camButton:
                Intent camScreen = new Intent(this, ImageCaptureActivity.class);
                startActivity(camScreen);
                break;
        }
    }
}
