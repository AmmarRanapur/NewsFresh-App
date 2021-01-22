package com.example.tabactivity;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.tabactivity.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class PageAdapter extends FragmentPagerAdapter {

    int tabcount;

    public PageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        tabcount=behavior;
    }

    @Override
    public Fragment getItem(int position) {

       switch(position){
           case 0: return MyFragment.newInstance("1","1");
           case 1: return MyFragment.newInstance("2","2");
           case 2: return MyFragment.newInstance("3","3");
           case 3: return MyFragment.newInstance("4","4");
           case 4: return MyFragment.newInstance("5","5");
           case 5: return MyFragment.newInstance("6","6");
           case 6: return MyFragment.newInstance("7","7");
           default: return null;
       }
    }
    @Override
    public int getCount() {
        return tabcount;
    }
}