package com.example.ripzery.projectx01.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.example.ripzery.projectx01.R;

/**
 * Created by visit on 2/8/15 AD.
 */
public class MultiplayerMapsActivity extends ActionBarActivity implements MapsFragment.OnFragmentInteractionListener, FragmentGameMultiplayerStatus.OnFragmentInteractionListener {
    private static final int NUM_PAGES = 2;
    private MapsFragment mapsFragment;
    private FragmentGameMultiplayerStatus fragmentGameMultiplayerStatus;
    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private ViewPager.SimpleOnPageChangeListener viewPagerListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_maps);

        mPager = (ViewPager) findViewById(R.id.pager);
        mapsFragment = MapsFragment.newInstance("maps", "maps");
        fragmentGameMultiplayerStatus = FragmentGameMultiplayerStatus.newInstance("multiplayer status", "test");
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        viewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

            }
        };
        mPager.setOnPageChangeListener(viewPagerListener);

        Singleton.myRealTimeMessageReceived.setMultiplayerMapsActivity(this);

    }

    @Override
    protected void onPause() {
//        mapsFragment.unRegisterAllListener();
        Log.d("MultiplayerMaps", "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
//        mapsFragment.registerAllListener();
        Log.d("MultiplayerMaps", "Resume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d("MultiplayerMaps", "Destroy");
//        mapsFragment.destroyAllPending();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mapsFragment.unRegisterAllListener();
                mapsFragment.destroyAllPending();
                finish();
                MultiplayerMapsActivity.super.onBackPressed();

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.setTitle("Stop playing?");
        dialog.setMessage("Good bye " + Singleton.getParticipantFromId(Singleton.myId).getDisplayName() + " ลาก่อย!");
        dialog.show();
    }

    public FragmentGameMultiplayerStatus getFragmentMultiplayerStatus() {
        return fragmentGameMultiplayerStatus;
    }

    public MapsFragment getMapsFragment() {
        return mapsFragment;
    }

    @Override
    public void onBroadcastPlayerStatus() {

    }

    @Override
    public void onUpdate() {

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {


        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mapsFragment;
                default:
                    return fragmentGameMultiplayerStatus;
            }

        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }


    }
}
