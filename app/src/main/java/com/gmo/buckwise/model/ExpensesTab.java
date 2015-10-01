package com.gmo.buckwise.model;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.gmo.buckwise.R;
import com.gmo.buckwise.activity.Analytics;
import com.gmo.buckwise.implementation.ExpensesImpl;
import com.gmo.buckwise.util.Util;

import java.util.ArrayList;

/**
 * Created by GMO on 9/15/2015.
 */
public class ExpensesTab extends Fragment {
    LineChart lineChart;
    BarChart barChart;
    View view;
    private String lineChartGridLinesColor = "#00796B";
    private String lineChartAxisTextColor = "#004D40";
    private String lineChartDataLineColor = "#004D40";
    private String lineChartCircleColor =  "#004D40";
    private String lineChartLineFillColor = "#00796B";
    private String barChartBarColor = "#00796B";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       view =inflater.inflate(R.layout.tab_expense, container, false);
       handleLineChart();
        handleBarChart();
        return view;

    }

    private void handleLineChart() {
        lineChart = (LineChart) view.findViewById(R.id.analytics_expense_chart1);
        lineChart.setDescription("");
        lineChart.animateY(1500);
        lineChart.setTouchEnabled(false);
        lineChart.setDrawGridBackground(false);

        XAxis xl = lineChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setLabelsToSkip(0);
        xl.setDrawGridLines(false);
        xl.setAxisLineColor(Color.parseColor(lineChartGridLinesColor));
        xl.setTextColor(Color.parseColor(lineChartAxisTextColor));

        YAxis yr = lineChart.getAxisRight();
        yr.setEnabled(false);

        YAxis yl = lineChart.getAxisLeft();
        yl.setDrawGridLines(true);
        yl.setGridColor(Color.parseColor(lineChartGridLinesColor));
        yl.setDrawAxisLine(false);
        yl.setTextColor(Color.parseColor(lineChartAxisTextColor));
        yl.setXOffset(10f);

        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);

        setLineChartData();
    }

    private void setLineChartData() {

        ExpensesImpl expensesImpl = new ExpensesImpl(Analytics.context);
        ArrayList<String> monthsWithData = expensesImpl.getPastMonthsWithDataThisYear();
        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<Entry> yValues = new ArrayList<>();
        int count = 0;

        if(monthsWithData.size() > 0) {
            for (int i = 0; i < monthsWithData.size(); i++) {
                String month = monthsWithData.get(i);
                String netIncome = expensesImpl.getLatestExpenseTotalForMonth(month);
                xValues.add(Util.monthNumberToString(month));
                yValues.add(new Entry(Float.parseFloat(netIncome), count));
                count++;
                System.out.println("MONTHS WITH EXPENSE TOTALS:" + month + ", " + netIncome);
            }
        }
        else {
            xValues.add(Util.getCurrentMonth());
            yValues.add(new Entry(Float.parseFloat("0"), 0));
        }

        LineDataSet dataSet = prepareLineDataSet(yValues);
        LineData data = prepareLineData(xValues, dataSet);
        data.setDrawValues(false);

        lineChart.setData(data);
    }

    private LineData prepareLineData(ArrayList<String> xValues, LineDataSet dataSet) {
        LineData data = new LineData(xValues, dataSet);
        data.setValueTextSize(11f);
        return data;
    }

    private LineDataSet prepareLineDataSet(ArrayList<Entry> yVals) {
        LineDataSet dataSet = new LineDataSet(yVals, "Average Epense Totals");
        dataSet.setLineWidth(1f);
        dataSet.setDrawCircles(true);
        dataSet.setDrawCubic(true);
        dataSet.setColor(Color.parseColor(lineChartDataLineColor));
        dataSet.setCircleColor(Color.parseColor(lineChartCircleColor));
        dataSet.setCircleColorHole(Color.parseColor(lineChartCircleColor));
        dataSet.setCircleSize(2f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor(lineChartLineFillColor));
        dataSet.setFillAlpha(255);

        return dataSet;
    }

    private void handleBarChart(){
        barChart = (BarChart)view.findViewById(R.id.analytics_expense_chart2);
        barChart.setDrawGridBackground(false);
        barChart.setDescription("");
        barChart.animateY(1500);
        barChart.setTouchEnabled(false);

        XAxis xb = barChart.getXAxis();
        xb.setPosition(XAxis.XAxisPosition.BOTTOM);
        xb.setLabelsToSkip(0);
        xb.setDrawGridLines(false);
        xb.setAxisLineColor(Color.parseColor(lineChartGridLinesColor));
        xb.setTextColor(Color.parseColor(lineChartAxisTextColor));

        YAxis yr = barChart.getAxisRight();
        yr.setEnabled(false);

        YAxis yl = barChart.getAxisLeft();
        yl.setDrawGridLines(true);
        yl.setGridColor(Color.parseColor(lineChartGridLinesColor));
        yl.setDrawAxisLine(false);
        yl.setTextColor(Color.parseColor(lineChartAxisTextColor));
        yl.setXOffset(10f);

        Legend legend = barChart.getLegend();
        legend.setEnabled(false);

        setBarChartData();

    }

    private void setBarChartData() {
        ExpensesImpl expensesImpl = new ExpensesImpl(Analytics.context);
        String expensesLastMonth = expensesImpl.calculateExpensesLastMonth();
        String expensesCurrent = String.valueOf(expensesImpl.getLatestExpenses().getExpenseTotal());
        String expensesAverageThisYear = expensesImpl.calculateAverageExpenses();

        System.out.println("Average: "+expensesAverageThisYear);
        System.out.println("Last Month: "+expensesLastMonth);
        System.out.println("Current: "+expensesCurrent);

        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
        yValues.add(new BarEntry(Float.parseFloat(expensesAverageThisYear), 0));
        yValues.add(new BarEntry(Float.parseFloat(expensesLastMonth), 1));
        yValues.add(new BarEntry(Float.parseFloat(expensesCurrent), 2));

        ArrayList<String> xValues = new ArrayList<String>();
        xValues.add("Average");
        xValues.add("Last Month");
        xValues.add("Present");

        BarDataSet dataSet = prepareBarDataSet(yValues);
        BarData data = prepareBarData(xValues, dataSet);
        data.setDrawValues(false);

        barChart.setData(data);
    }

    private BarData prepareBarData(ArrayList<String> xValues, BarDataSet dataSet) {
        BarData data = new BarData(xValues, dataSet);
        data.setValueTextSize(11f);
        return data;
    }

    private BarDataSet prepareBarDataSet(ArrayList<BarEntry> yVals) {
        BarDataSet dataSet = new BarDataSet(yVals, "Month, Average, Present");
        dataSet.setColor(Color.parseColor(barChartBarColor));
        dataSet.setBarSpacePercent(40);

        return dataSet;
    }
}
