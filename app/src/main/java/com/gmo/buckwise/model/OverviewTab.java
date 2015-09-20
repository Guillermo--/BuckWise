package com.gmo.buckwise.model;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.gmo.buckwise.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by GMO on 9/15/2015.
 */
public class OverviewTab extends Fragment {

    LineChart lineChart;
    View view;
    private DecimalFormat formatter;
    private String chartGridLinesColor = "#00796B";
    private String chartAxisTextColor = "#004D40";
    private String chartDataLineColor = "#FFFFFF";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.tab_overview,container,false);
        handleLineChart();

        return view;
    }

    private void handleLineChart() {
        lineChart = (LineChart) view.findViewById(R.id.analytics_overview_chart1);
        lineChart.setDescription("");
        lineChart.animateX(1500, Easing.EasingOption.EaseInOutQuart);
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
        ArrayList<Entry> yValues = new ArrayList<Entry>();
        yValues.add(new Entry(1900, 0));
        yValues.add(new Entry(1800, 1));
        yValues.add(new Entry(1700, 2));
        yValues.add(new Entry(1800, 3));

        ArrayList<String> xValues = new ArrayList<String>();
        xValues.add("Jan");
        xValues.add("Feb");
        xValues.add("Mar");
        xValues.add("Apr");
        xValues.add("May");
        xValues.add("Jun");
        xValues.add("Jul");
        xValues.add("Aug");
        xValues.add("Sep");
        xValues.add("Oct");
        xValues.add("Nov");
        xValues.add("Dec");

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
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(true);
        dataSet.setDrawCubic(true);
        dataSet.setColor(Color.parseColor(chartGridLinesColor));
        dataSet.setCircleColor(Color.parseColor(chartAxisTextColor));
        dataSet.setCircleColorHole(Color.parseColor(chartAxisTextColor));
        dataSet.setCircleSize(2f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor(chartGridLinesColor));
        dataSet.setFillAlpha(255);

        return dataSet;
    }


}
