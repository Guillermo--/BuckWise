package com.gmo.buckwise.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;

import com.gmo.buckwise.activity.Dashboard;
import com.gmo.buckwise.database.BudgetsDAO;
import com.gmo.buckwise.database.ExpensesDAO;
import com.gmo.buckwise.database.MySQLiteHelper;
import com.gmo.buckwise.database.OverviewDAO;
import com.gmo.buckwise.model.Budget;
import com.gmo.buckwise.model.Expense;
import com.gmo.buckwise.model.Overview;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by GMO on 5/27/2015.
 */
public class Util {
    public static Typeface typefaceRobotoLight = Typeface.createFromAsset(Dashboard.context.getAssets(),"fonts/Roboto-Light.ttf");
    public static Typeface typefaceRobotoBold = Typeface.createFromAsset(Dashboard.context.getAssets(),"fonts/Roboto-Bold.ttf");
    public static Typeface typefaceRobotoThin = Typeface.createFromAsset(Dashboard.context.getAssets(),"fonts/Roboto-Thin.ttf");
    public static Typeface typefaceRobotoMedium = Typeface.createFromAsset(Dashboard.context.getAssets(),"fonts/Roboto-Medium.ttf");
    public static Typeface typefaceRobotoRegular = Typeface.createFromAsset(Dashboard.context.getAssets(),"fonts/Roboto-Regular.ttf");
    public static Typeface typefaceBadScript = Typeface.createFromAsset(Dashboard.context.getAssets(), "fonts/BadScript-Regular.ttf");

    public static String getCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        return new SimpleDateFormat("MMM").format(cal.getTime());
    }

    public static String getDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        return String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
    }

    public String getDayOfWeek(){
        Calendar calendar = Calendar.getInstance();
        String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
        return dayOfWeek;
    }

    public String getDaysString(){
        String dayName = getDayOfWeek();
        String dayNumber = String.format("%02d", Integer.parseInt(getDayOfMonth()));
        return dayName+", "+dayNumber;
    }

    public static String getCurrentDateTime(){
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormatter.format(Calendar.getInstance().getTime());
    }

    public static Date stringToDate(String dateStr){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
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

    public static String monthNumberToString(String monthNumber) {
        Map<String, String> monthNumberAndString = new HashMap<>();
        monthNumberAndString.put("01", "Jan");
        monthNumberAndString.put("02", "Feb");
        monthNumberAndString.put("03", "Mar");
        monthNumberAndString.put("04", "Apr");
        monthNumberAndString.put("05", "May");
        monthNumberAndString.put("06", "Jun");
        monthNumberAndString.put("07", "Jul");
        monthNumberAndString.put("08", "Aug");
        monthNumberAndString.put("09", "Sep");
        monthNumberAndString.put("10", "Oct");
        monthNumberAndString.put("11", "Nov");
        monthNumberAndString.put("12", "Dec");

        return monthNumberAndString.get(monthNumber);

    }

}
