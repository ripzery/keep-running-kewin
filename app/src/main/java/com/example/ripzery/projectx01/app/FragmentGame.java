package com.example.ripzery.projectx01.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ripzery.projectx01.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentGame extends Fragment {


    private static final int NUM_PAGES = 2;
    private View rootView;
    private MapsFragment mapsFragment;
    private FragmentGameMultiplayerStatus fragmentGameMultiplayerStatus;
    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private ViewPager.SimpleOnPageChangeListener viewPagerListener;

    public FragmentGame() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_game, container, false);
        this.setTargetFragment(mapsFragment, 123);
        mapsFragment = MapsFragment.newInstance("maps", "maps");
        fragmentGameMultiplayerStatus = FragmentGameMultiplayerStatus.newInstance("multiplayer status", "test");
        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        this.setTargetFragment(mapsFragment, 123);
        this.setTargetFragment(fragmentGameMultiplayerStatus, 1233);
        mPagerAdapter = new ScreenSlidePagerAdapter(getActivity().getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        viewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

            }
        };
        mPager.setOnPageChangeListener(viewPagerListener);
        return rootView;
    }

    @Override
    public void onStop() {
        mapsFragment.onStop();
        fragmentGameMultiplayerStatus.onStop();
        super.onStop();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private long baseId = 0;

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
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         *
         * @param n number of items which have been changed
         */
        public void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }
    }
}
