package com.example.ripzery.projectx01.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.ripzery.projectx01.R;

public class MainActivity extends Activity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(UILApplication.orientation);
        mContext = this;

        ((ImageButton) findViewById(R.id.playBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ChoosePlayerActivity.class));
            }
        });
        ((ImageButton) findViewById(R.id.rotateBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UILApplication.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    UILApplication.orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    UILApplication.orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                }
            }
        });
    }

}
