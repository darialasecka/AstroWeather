package com.example.astroweather;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private SunFragment sun_fragment;
    private MoonFragment moon_fragment;


    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SunFragment();
            case 1:
                return new MoonFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment)super.instantiateItem(container, position);
        switch (position) {
            case 0:
                sun_fragment = (SunFragment)createdFragment;
                break;
            case 1:
                moon_fragment = (MoonFragment)createdFragment;
                break;
        }
        return createdFragment;
    }

}