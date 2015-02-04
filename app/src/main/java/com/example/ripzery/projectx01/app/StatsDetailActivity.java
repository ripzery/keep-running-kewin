package com.example.ripzery.projectx01.app;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.ripzery.projectx01.R;
import com.example.ripzery.projectx01.util.TypefaceSpan;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nineoldandroids.animation.Animator;

public class StatsDetailActivity extends ActionBarActivity {

    private ActionBar mActionBar;
    private GoogleMap mMap;
    private View decorView;
    private Handler handler;
    private FrameLayout layoutMap;
    private LinearLayout layoutDuration;
    private LinearLayout layoutSpeed;
    private LinearLayout layoutCalories;
    private LinearLayout layoutDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        SpannableString mStringTitle = new SpannableString("Activity Detail");
        mStringTitle.setSpan(new TypefaceSpan(this, "Roboto-Medium.ttf"), 0, mStringTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mActionBar.setTitle(mStringTitle);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);


        initVar();
        setUpMapIfNeeded();

        initListener();

        startAnimation();

    }

    public void initVar() {
        layoutMap = (FrameLayout) findViewById(R.id.layoutMap);
        layoutDuration = (LinearLayout) findViewById(R.id.layoutDuration);
        layoutSpeed = (LinearLayout) findViewById(R.id.layoutSpeed);
        layoutCalories = (LinearLayout) findViewById(R.id.layoutCalories);
        layoutDistance = (LinearLayout) findViewById(R.id.layoutDistance);


        TextView tvDistance = (TextView) findViewById(R.id.tvDistance);
        TextView tvDuration = (TextView) findViewById(R.id.tvTotalDuration);
        TextView tvSpeed = (TextView) findViewById(R.id.tvAverageSpeed);
        TextView tvCalories = (TextView) findViewById(R.id.tvBurnCalories);
        TextView tvDistanceDetail = (TextView) findViewById(R.id.tvDistanceDetail);
        TextView tvDurationDetail = (TextView) findViewById(R.id.tvTotalDurationDetail);
        TextView tvSpeedDetail = (TextView) findViewById(R.id.tvAverageSpeedDetail);
        TextView tvCaloriesDetail = (TextView) findViewById(R.id.tvBurnCaloriesDetail);
        Typeface light = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        tvDistance.setTypeface(light);
        tvDuration.setTypeface(light);
        tvSpeed.setTypeface(light);
        tvCalories.setTypeface(light);
        tvDistanceDetail.setTypeface(light);
        tvDurationDetail.setTypeface(light);
        tvSpeedDetail.setTypeface(light);
        tvCaloriesDetail.setTypeface(light);

        String distance = getIntent().getStringExtra("distance");
        String totalDuration = getIntent().getStringExtra("totalDuration");
        String averageSpeed = getIntent().getStringExtra("averageSpeed");
        String burnCalories = getIntent().getStringExtra("burnCalories");

        tvDistanceDetail.setText(distance);
        tvDurationDetail.setText(totalDuration);
        tvSpeedDetail.setText(averageSpeed);
        tvCaloriesDetail.setText(burnCalories + " KCals");

        decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        handler = new Handler();
    }

    public void initListener() {
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if ((i & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    // TODO: The system bars are visible. Make any desired
                    // adjustments to your UI, such as showing the action bar or
                    // other navigational controls.
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            decorView.setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
                        }
                    }, 3000);

                    Log.d("visible", "true");
                } else {
                    // TODO: The system bars are NOT visible. Make any desired
                    // adjustments to your UI, such as hiding the action bar or
                    // other navigational controls.
                    Log.d("visible", "false");
                }
            }
        });

//        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
//            @Override
//            public void onMapLoaded() {
//                if (Singleton.getAllPlayerPositions().size() > 2) {
//                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
//
//                    PolylineOptions polylineOptions = new PolylineOptions();
//                    polylineOptions.color(getResources().getColor(R.color.hp_good_dark));
//                    polylineOptions.width(10);
//                    for (LatLng latLng : Singleton.getAllPlayerPositions()) {
//                        polylineOptions.add(latLng);
//                        builder.include(latLng);
//                    }
//                    LatLngBounds bounds = builder.build();
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
//                    mMap.addPolyline(polylineOptions);
//                }
//            }
//        });
    }

    public void startAnimation() {
        YoYo.with(Techniques.FadeIn)
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (Singleton.getAllPlayerPositions().size() > 2) {
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();

                            PolylineOptions polylineOptions = new PolylineOptions();
                            polylineOptions.color(getResources().getColor(R.color.hp_good_dark));
                            polylineOptions.width(10);
                            for (LatLng latLng : Singleton.getAllPlayerPositions()) {
                                polylineOptions.add(latLng);
                                builder.include(latLng);
                            }
                            LatLngBounds bounds = builder.build();
                            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                            mMap.addPolyline(polylineOptions);
                        }

                        layoutDistance.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeInLeft)
                                .duration(500)
                                .withListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        layoutDuration.setVisibility(View.VISIBLE);
                                        YoYo.with(Techniques.FadeInLeft)
                                                .duration(500)
                                                .withListener(new Animator.AnimatorListener() {
                                                    @Override
                                                    public void onAnimationStart(Animator animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        layoutSpeed.setVisibility(View.VISIBLE);
                                                        YoYo.with(Techniques.FadeInLeft)
                                                                .duration(500)
                                                                .withListener(new Animator.AnimatorListener() {
                                                                    @Override
                                                                    public void onAnimationStart(Animator animation) {

                                                                    }

                                                                    @Override
                                                                    public void onAnimationEnd(Animator animation) {
                                                                        layoutCalories.setVisibility(View.VISIBLE);
                                                                        YoYo.with(Techniques.FadeInLeft)
                                                                                .duration(500)
                                                                                .playOn(layoutCalories);
                                                                    }

                                                                    @Override
                                                                    public void onAnimationCancel(Animator animation) {

                                                                    }

                                                                    @Override
                                                                    public void onAnimationRepeat(Animator animation) {

                                                                    }
                                                                }).playOn(layoutSpeed);
                                                    }

                                                    @Override
                                                    public void onAnimationCancel(Animator animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationRepeat(Animator animation) {

                                                    }
                                                }).playOn(layoutDuration);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                }).playOn(layoutDistance);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).duration(1000).playOn(layoutMap);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stats_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map. instance
        if (mMap == null) {
            mMap = (((SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map)).getMap());
            // Check if we were successful in obtaining the map.
        }
    }
}
