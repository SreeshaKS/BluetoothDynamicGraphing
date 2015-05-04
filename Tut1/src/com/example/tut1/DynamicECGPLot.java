package com.example.tut1;

import android.content.Context;
import android.graphics.Color;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**
 * Created by Sreesha on 24-04-2015.
 */
public class DynamicECGPLot{
    private final String tag = "In Dynamic ECG PLot";

    private TimeSeries dataSet = new TimeSeries("Time Interval");
    private GraphicalView view;
    private XYSeriesRenderer renderer = new XYSeriesRenderer();
    private XYMultipleSeriesDataset mDataSet = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();





    public DynamicECGPLot(){
        mDataSet.addSeries(dataSet);


        renderer.setColor(Color.BLACK);

        renderer.setPointStyle(PointStyle.CIRCLE);
        mRenderer.setPointSize((float)0.002);
        renderer.setFillPoints(true);
        mRenderer.setXLabels(10);
        mRenderer.setYLabels(10);



        mRenderer.setGridColor(Color.BLUE);
        mRenderer.setBarSpacing(30);
        mRenderer.setShowGrid(true);
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setXTitle("Time Stamp");
        mRenderer.setYTitle("ECG Data");


        //Add single renderer to multi renderer
        mRenderer.addSeriesRenderer(renderer);
    }

    public GraphicalView getView(Context context){

        view = ChartFactory.getLineChartView(context, mDataSet, mRenderer);
        return view;
    }

    public void addNewPoint(point p){
        Log.i(tag, "addNewPointReached");


        dataSet.add(p.getX(),p.getY());
    }


}
