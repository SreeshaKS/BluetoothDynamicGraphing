package com.example.tut1;

import android.app.Activity;
import android.content.Intent;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.achartengine.GraphicalView;


public class StartScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen_activity);
    }



    //Start plotting the chart when button is clicked
    public void startChart(View v) {
        Intent intent = new Intent(this, ECGPLotActivity.class);
       intent.putExtra("FLAG",1);
        startActivity(intent);
    }
    public void startChartDynamic(View v) {
        //Intent intent = new Intent(this, MainActivity22Activity.class);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
   /* public void startBluetooth(View v) {

        Intent intent = new Intent(this, MainActivity2Activity.class);
        startActivity(intent);
    }*/



}
