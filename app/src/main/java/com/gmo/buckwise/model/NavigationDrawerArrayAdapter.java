package com.gmo.buckwise.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmo.buckwise.R;
import com.gmo.buckwise.activity.Budgets;
import com.gmo.buckwise.activity.Dashboard;
import com.gmo.buckwise.activity.Expenses;
import com.gmo.buckwise.util.Util;

/**
 * Created by GMO on 6/4/2015.
 */
public class NavigationDrawerArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    Util util = new Util();

    public NavigationDrawerArrayAdapter(Context context, String[] values) {
        super(context, R.layout.navigation_drawer_row_layout, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.navigation_drawer_row_layout, parent, false);
        final TextView textView = (TextView) rowView.findViewById(R.id.navigationDrawer_item_text);
        textView.setTypeface(util.typefaceRobotoLight);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.navigationDrawer_item_icon);
        textView.setText(values[position]);

        String listItem = values[position];
        if (listItem.equals("Overview")) {
            imageView.setImageResource(R.drawable.ic_book_open_grey600_24dp);
        }
        if (listItem.equals("Expenses")) {
            imageView.setImageResource(R.drawable.ic_cart_outline_grey600_24dp);
        }
        if (listItem.equals("Budgets")) {
            imageView.setImageResource(R.drawable.ic_clipboard_text_grey600_24dp);
        }
        if (listItem.equals("Settings")) {
            imageView.setImageResource(R.drawable.ic_settings_grey600_24dp);
        }

        if (context == Dashboard.context && textView.getText().equals("Overview")) {
            textView.setTextColor(context.getResources().getColor(R.color.dialogAccent));
            imageView.setColorFilter(context.getResources().getColor(R.color.dialogAccent));
        }
        else if (context == Expenses.context && textView.getText().equals("Expenses")) {
            textView.setTextColor(context.getResources().getColor(R.color.dialogAccent));
            imageView.setColorFilter(context.getResources().getColor(R.color.dialogAccent));
        }
        else if (context == Budgets.context && textView.getText().equals("Budgets")) {
            textView.setTextColor(context.getResources().getColor(R.color.dialogAccent));
            imageView.setColorFilter(context.getResources().getColor(R.color.dialogAccent));
        }

        return rowView;
    }
}
