package com.techplicit.mycarnival.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.techplicit.mycarnival.ui.activities.fragments.BandsAlphaSortFragment;
import com.techplicit.mycarnival.ui.activities.fragments.BandsDistanceFragment;
import com.techplicit.mycarnival.ui.activities.fragments.BandsMyFavourites;

/**
 * Created by pnaganjane001 on 18/12/15.
 */
public class BandsTabsPageAdapter extends FragmentPagerAdapter {

    public BandsTabsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new BandsDistanceFragment();
            case 1:
                // Games fragment activity
                return new BandsAlphaSortFragment();
            case 2:
                // Movies fragment activity
                return new BandsMyFavourites();
            case 3:
                // Movies fragment activity
                return new BandsMyFavourites();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}
