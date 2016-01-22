package com.gmo.buckwise.implementation;

import android.content.Context;

import com.gmo.buckwise.database.OverviewDAO;
import com.gmo.buckwise.model.Overview;
import com.gmo.buckwise.util.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
        overview.setNetIncome(calculateNetIncome(overview.getIncome(), expensesImpl.getTotalExpensesAmount(datetime)));
        overviewDao.processAdditionOfIncome(overview);
        return overview;
    }

    public double calculateNetIncome(double income, double expenses){
        return income - expenses;
    }

    public ArrayList<String> getPastMonthsWithDataThisYear() {
        ArrayList<String> months = overviewDao.getPastMonthsWithDataThisYear();
        return months;
    }

    public String getLastNetIncomeFromMonthThisYear(String month) {
        String lastNetIncome = overviewDao.getLastNetIncomeForMonth(month);
        return lastNetIncome;
    }

    public String calculateNetIncomeLastMonth() {
        String netIncomeLastMonth = overviewDao.getNetIncomeFromLastMonth();
        if(netIncomeLastMonth == null || netIncomeLastMonth.isEmpty()) {
            netIncomeLastMonth = "0";
        }
        return netIncomeLastMonth;
    }

    public String calculateAverageNetIncome(){
        ArrayList<String> months = overviewDao.getPastMonthsWithDataThisYear();
        int sum = 0;
        int averageNetIncome= 0;

        if(months.size() < 1){
            return "0";
        }

        for(int i = 0; i < months.size(); i++) {
            sum += Integer.parseInt(overviewDao.getLastNetIncomeForMonth(months.get(i)));
        }

        averageNetIncome = sum/ months.size();
        return String.valueOf(averageNetIncome);
    }

    public String calculateInBank() {
        ArrayList<String> months = overviewDao.getPastMonthsWithDataThisYear();
        int inBank = 0;
        if(months.size() < 1) {
            return "0";
        }

        for(int i = 0; i<months.size(); i++) {
            inBank += Integer.parseInt(overviewDao.getLastNetIncomeForMonth(months.get(i)));
        }

        return String.valueOf(inBank);
    }

}
