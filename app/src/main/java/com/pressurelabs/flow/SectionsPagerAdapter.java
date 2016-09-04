package com.pressurelabs.flow;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Flow_app
 *
 * @author Robert Simoes, 2016-09-03
 *         Copyright (c) 2016, Robert Simoes All rights reserved.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private Flow parent;

    public SectionsPagerAdapter(FragmentManager fm, Flow inParent) {
        super(fm);
        this.parent = inParent;
    }

    @Override
    public Fragment getItem(int position) {
        //TODO Fix Position
        // getItem is called to instantiate the fragment for the given page.
        // Return a ShowElementFragment (defined as a static inner class below).
        return ShowElementFragment.newInstance(parent.getChildAt(position));
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "SECTION 1";
            case 1:
                return "SECTION 2";
            case 2:
                return "SECTION 3";
        }
        return null;
    }
}