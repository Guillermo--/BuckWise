package com.gmo.buckwise.model;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.gmo.buckwise.R;
import com.gmo.buckwise.activity.Analytics;
import com.gmo.buckwise.implementation.OverviewImpl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by GMO on 9/15/2015.
 */
public class OverviewTab extends Fragment {

    LineChart lineChart;
    BarChart barChart;
    View view;
    private String chartGridLinesColor = "#00796B";
    private String chartAxisTextColor = "#004D40";
    private String chartDataLineColor = "#004D40";
    private String chartCircleColor =  "#004D40";
    private String chartLineFillColor = "#00796B";

    private String barChartBarColor = "#00796B";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.tab_overview,container,false);
        handleLineChart();
        handleBarChart();

        return view;
    }

    private void handleLineChart() {
        lineChart = (LineChart) view.findViewById(R.id.analytics_overview_chart1);
        lineChart.setDescription("");
        lineChart.animateY(1500);
        lineChart.setTouchEnabled(false);
        lineChart.setDrawGridBackground(false);

        XAxis xl = lineChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setLabelsToSkip(0);
        xl.setDrawGridLines(false);
        xl.setAxisLineColor(Color.parseColor(chartGridLinesColor));
        xl.setTextColor(Color.parseColor(chartAxisTextColor));

        YAxis yr = lineChart.getAxisRight();
        yr.setEnabled(false);

        YAxis yl = lineChart.getAxisLeft();
        yl.setDrawGridLines(true);
        yl.setGridColor(Color.parseColor(chartGridLinesColor));
        yl.setDrawAxisLine(false);
        yl.setTextColor(Color.parseColor(chartAxisTextColor));
        yl.setXOffset(10f);

        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);

        setLineChartData();
    }

    private void setLineChartData() {
        OverviewImpl overviewImpl = new OverviewImpl(Analytics.context);
        Map<String, String> netIncomeDateMap = overviewImpl.getPreviousNetIncomesThisYear();
        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<Entry> yValues = new ArrayList<>();
        int count = 0;
        for(String key : netIncomeDateMap.keySet()){
            xValues.add(key);
            yValues.add(new Entry(Float.parseFloat(netIncomeDateMap.get(key)), count));
            count++;
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
        LineDataSet dataSet = new LineDataSet(yVals, "Average Net Income");
        dataSet.setLineWidth(1f);
        dataSet.setDrawCircles(true);
        dataSet.setDrawCubic(true);
        dataSet.setColor(Color.parseColor(chartDataLineColor));
        dataSet.setCircleColor(Color.parseColor(chartCircleColor));
        dataSet.setCircleColorHole(Color.parseColor(chartCircleColor));
        dataSet.setCircleSize(2f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor(chartLineFillColor));
        dataSet.setFillAlpha(255);

        return dataSet;
    }



    private void handleBarChart(){
        barChart = (BarChart)view.findViewById(R.id.analytics_overview_chart2);
        barChart.setDrawGridBackground(false);
        barChart.setDescription("");
        barChart.animateY(1500);
        barChart.setTouchEnabled(false);

        XAxis xb = barChart.getXAxis();
        xb.setPosition(XAxis.XAxisPosition.BOTTOM);
        xb.setLabelsToSkip(0);
        xb.setDrawGridLines(false);
        xb.setAxisLineColor(Color.parseColor(chartGridLinesColor));
        xb.setTextColor(Color.parseColor(chartAxisTextColor));

        YAxis yr = barChart.getAxisRight();
        yr.setEnabled(false);

        YAxis yl = barChart.getAxisLeft();
        yl.setDrawGridLines(true);
        yl.setGridColor(Color.parseColor(chartGridLinesColor));
        yl.setDrawAxisLine(false);
        yl.setTextColor(Color.parseColor(chartAxisTextColor));
        yl.setXOffset(10f);

        Legend legend = barChart.getLegend();
        legend.setEnabled(false);

        setBarChartData();

    }
    private void setBarChartData() {
        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
        yValues.add(new BarEntry(1900, 0));
        yValues.add(new BarEntry(1800, 1));
        yValues.add(new BarEntry(1700, 2));

        ArrayList<String> xValues = new ArrayList<String>();
        xValues.add("Average");
        xValues.add("Last Month");
        xValues.add("Present");

        BarDataSet dataSet = prepareBarDataSet(yValues);
        BarData data = prepareLineData(xValues, dataSet);
        data.setDrawValues(false);

        barChart.setData(data);
    }
    private BarData prepareLineData(ArrayList<String> xValues, BarDataSet dataSet) {
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
