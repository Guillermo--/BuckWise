package com.gmo.buckwise.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;

import com.gmo.buckwise.activity.Dashboard;
import com.gmo.buckwise.database.MySQLiteHelper;

import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by GMO on 5/27/2015.
 */
public class Util {
    public static Typeface typefaceRobotoLight = Typeface.createFromAsset(Dashboard.context.getAssets(),"fonts/Roboto-Light.ttf");
    public static Typeface typefaceRobotoBold = Typeface.createFromAsset(Dashboard.context.getAssets(),"fonts/Roboto-Bold.ttf");
    public static Typeface typefaceRobotoThin = Typeface.createFromAsset(Dashboard.context.getAssets(),"fonts/Roboto-Thin.ttf");
    public static Typeface typefaceRobotoMedium = Typeface.createFromAsset(Dashboard.context.getAssets(),"fonts/Roboto-Medium.ttf");
    public static Typeface typefaceRobotoRegular = Typeface.createFromAsset(Dashboard.context.getAssets(),"fonts/Roboto-Regular.ttf");


    public String getCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        return new SimpleDateFormat("MMMM").format(cal.getTime());
    }

    public String getDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        return String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
    }

    public String getDayOfWeek(){
        Format formatter = new SimpleDateFormat("EEEE");
        return formatter.format(new Date());

    }

    public String getDaysString(){
        String dayName = getDayOfWeek();
        String dayNumber = String.format("%02d", Integer.parseInt(getDayOfMonth()));
        return dayName+", "+dayNumber;
    }

    public static String getCurrentDateTime(){
        DateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormatter.format(Calendar.getInstance().getTime());
    }

    public static Date stringToDate(String dateStr){
        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        try {
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String doubleToCurrency(double str){
        NumberFormat formatter;
        formatter  = NumberFormat.getCurrencyInstance();
        return formatter.format(str);
    }

}
