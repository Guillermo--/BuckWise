package com.gmo.buckwise.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gmo.buckwise.R;
import com.gmo.buckwise.activity.Analytics;
import com.gmo.buckwise.activity.Budgets;
import com.gmo.buckwise.activity.Dashboard;
import com.gmo.buckwise.activity.Expenses;


/**
 * Created by GMO on 6/20/2015.
 */
public class NavigationDrawerItemClickListener implements ListView.OnItemClickListener {

    Context context;
    DrawerLayout drawerLayout;
    public NavigationDrawerItemClickListener(DrawerLayout drawerLayout, Context context){
        this.context = context;
        this.drawerLayout = drawerLayout;
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        handleClick(view);
    }

    public void handleClick(View view){
        String itemTitle = ((TextView)view.findViewById(R.id.navigationDrawer_item_text)).getText().toString();
        if(itemTitle.equals("Overview")){
            if(context != Dashboard.context){
                drawerLayout.closeDrawer(Gravity.LEFT);
                drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        Intent intent = new Intent(context, Dashboard.class);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {

                    }
                });
            }
            else{
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        }

        else if(itemTitle.equals("Expenses")){
            if(context != Expenses.context){
                drawerLayout.closeDrawer(Gravity.LEFT);
                drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        Intent intent = new Intent(context, Expenses.class);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {

                    }
                });
            }
            else{
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        }

        else if(itemTitle.equals("Budgets")){
            if(context != Budgets.context){
                drawerLayout.closeDrawer(Gravity.LEFT);
                drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        Intent intent = new Intent(context, Budgets.class);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {

                    }
                });
            }
            else{
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        }
        else if(itemTitle.equals("Analytics")){
            if(context != Analytics.context){
                drawerLayout.closeDrawer(Gravity.LEFT);
                drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        Intent intent = new Intent(context, Analytics.class);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {

                    }
                });
            }
            else{
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        }
    }

}

