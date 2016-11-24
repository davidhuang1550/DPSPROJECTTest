package com.example.david.dpsproject;

import android.content.res.ObbInfo;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by david on 2016-11-23.
 */
public class SreenSlidePagerActivity extends FragmentActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private int NUM_PAGES = 2;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(2);

       /* mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==1){
                    mPager.
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/




    }

    @Override
    public void onBackPressed() {

        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            //mPager.setCurrentItem(mPager.getCurrentItem() - 1);
         //  NUM_PAGES=1;
          //  mPagerAdapter.getItemPosition(new Object());
           // mPagerAdapter.getItemPosition();
          //  mPagerAdapter.notifyDataSetChanged();
       //     mPager.setOffscreenPageLimit(1);

        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */

    public void setN(int n){
        this.NUM_PAGES=n;
    }
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new ScreenSlidePageFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }
        public void deletePage(int position){
            deletePage(position);
            notifyDataSetChanged();
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment f = getItem(position);
            if(position==1){
                Fragment fe = getItem(2);
                if (f != null) {
                    destroyItem(container,2,fe);
                    return f;
                }

            }
            return f;
        }
    }
}
