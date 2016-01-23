package com.gmo.buckwise.activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.gmo.buckwise.R;
import com.gmo.buckwise.model.NavigationDrawerArrayAdapter;
import com.gmo.buckwise.model.NavigationDrawerItemClickListener;
import com.gmo.buckwise.model.TabsFragmentAdapter;
import com.gmo.buckwise.util.Util;


public class Analytics extends AppCompatActivity {

    public static Context context;
    ListView mDrawerList;
    public static DrawerLayout mDrawerLayout;
    TextView navigationDrawerTitle;
    ListView navigationDrawerItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_analytics);
        context = this;
        initializeViews();
        setUpNavigationDrawer();
        setTypeFaces();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Net Income"));
        tabLayout.addTab(tabLayout.newTab().setText("Expenses"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        final TabsFragmentAdapter adapter = new TabsFragmentAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void initializeViews() {
        navigationDrawerTitle = (TextView)findViewById(R.id.navigationDrawer_title);
    }

    public void setTypeFaces(){
        navigationDrawerTitle.setTypeface(Util.typefaceRobotoLight);
        navigationDrawerTitle.setTypeface(Util.typefaceBadScript, Typeface.BOLD);

    }

    private void setHamburgerIconClick() {
        mDrawerList = (ListView)findViewById(R.id.navigationDrawer_items);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.DrawerLayout);
        ImageButton drawerButton = (ImageButton) findViewById(R.id.analytics_iconHamburger);
        drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
    }

    private void setUpNavigationDrawer() {
        String[] values = new String[] {"Overview", "Expenses", "Budgets", "Analytics"};
        NavigationDrawerArrayAdapter adapter = new NavigationDrawerArrayAdapter(this, values);
        navigationDrawerItems = (ListView)findViewById(R.id.navigationDrawer_items);
        navigationDrawerItems.setAdapter(adapter);
        setHamburgerIconClick();
        mDrawerList.setOnItemClickListener(new NavigationDrawerItemClickListener(mDrawerLayout, context));

    }


}
