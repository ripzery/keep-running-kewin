package com.example.ripzery.projectx01.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.ripzery.projectx01.R;

public class SinglePlayerActivity extends Activity {

    private ImageButton campaignBtn;
    private ImageButton continueBtn;
    private ImageButton newBtn;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);
        setRequestedOrientation(UILApplication.orientation);
        mContext = this;
        campaignBtn = (ImageButton) findViewById(R.id.campaignBtn);
        continueBtn = (ImageButton) findViewById(R.id.continueBtn);
        newBtn = (ImageButton) findViewById(R.id.newBtn);

        campaignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout) findViewById(R.id.campaignSection)).setVisibility(View.INVISIBLE);
                ((FrameLayout) findViewById(R.id.continueSection)).setVisibility(View.VISIBLE);
                ((FrameLayout) findViewById(R.id.newSection)).setVisibility(View.VISIBLE);
            }
        });

        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, CampaignActivity.class));
            }
        });
    }

}
