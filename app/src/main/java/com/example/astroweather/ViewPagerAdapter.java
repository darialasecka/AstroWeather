package com.example.astroweather;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private SunFragment sun_fragment = new SunFragment();
    private MoonFragment moon_fragment = new MoonFragment();
    private List<Fragment> fragments = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments.add(sun_fragment);
        fragments.add(moon_fragment);
    }

    public void addWeatherFragment() {
        fragments.add(new WeatherFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        // save the appropriate reference depending on position
        if (position > -1 && position < getCount()) fragments.set(position, createdFragment);
        return createdFragment;
    }
}