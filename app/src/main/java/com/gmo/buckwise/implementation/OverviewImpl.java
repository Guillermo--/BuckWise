package com.gmo.buckwise.implementation;

import android.content.Context;
import android.util.Log;

import com.gmo.buckwise.database.OverviewDAO;
import com.gmo.buckwise.model.Overview;
import com.gmo.buckwise.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by GMO on 5/29/2015.
 */
public class OverviewImpl extends Overview {
    private Context context;
    private OverviewDAO overviewDao;

    public OverviewImpl(Context context){
        this.context = context;
        overviewDao = new OverviewDAO(context);
    }

    public Overview getLatestOverview() {
        Overview overview = overviewDao.getLatestOverview();
        return overview;
    }

    public Overview getSpecificOverview(String date) {
        Overview overview = overviewDao.getSpecificOverview(date);
        return overview;
    }

    public Overview createOverview(Overview overview){
        String datetime = Util.getCurrentDateTime();
        ExpensesImpl expensesImpl = new ExpensesImpl(context);

        overview.setDateCreated(datetime);
        overview.setNetIncome(calculateNetIncome(overview.getIncome(), expensesImpl.getTotalExpensesAmount()));
        overviewDao.processAdditionOfIncome(overview);
        return overview;
    }

    public double calculateNetIncome(double income, double expenses){
        return income - expenses;
    }

    public Map<String, String> getPreviousNetIncomesThisYear() {
        ArrayList<String> months = new ArrayList<>();
        Map<String, String> monthsAndIncome = new HashMap<String, String>();
        months = overviewDao.getPastMonthsWithDataThisYear();

        for(int i = 0; i<months.size(); i++) {
            String netIncome = overviewDao.getLastNetIncomeForMonthThisYear(months.get(i));
            monthsAndIncome.put(months.get(i), netIncome);
        }
        return monthsAndIncome;
    }

    public void calculateAverageNetIncome(){
        //How many months have passed?
        //Calculate average net income every 1st of month
        //old ((latestAverageNetIncomeFromPreviousMonth * monthsPassed) + latestAverageNetIncomeFromCurrentMonth) / monthsPassed
    }

}
