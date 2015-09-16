package com.gmo.buckwise.model;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmo.buckwise.R;

/**
 * Created by GMO on 9/15/2015.
 */
public class ExpensesTab extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_expense,container,false);
        return v;
    }
}
