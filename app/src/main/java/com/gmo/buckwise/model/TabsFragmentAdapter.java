package com.gmo.buckwise.model;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.Toast;

/**
 * Created by GMO on 9/14/2015.
 */

public class TabsFragmentAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public TabsFragmentAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                OverviewTab ot = new OverviewTab();
                return ot;
            case 1:
                ExpensesTab et = new ExpensesTab();
                return et;
            case 2:
                BudgetsTab bt = new BudgetsTab();
                return bt;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}