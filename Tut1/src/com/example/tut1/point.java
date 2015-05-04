package com.example.tut1;

import android.util.Log;

/**
 * Created by Sreesha on 28-04-2015.
 */
public class point {
    private String tag = "In Point Class";

    private int x;
    private double y;

    public point(double Y,int X){
        this.x=X;
        this.y=Y;

    }
    public int getX(){
        Log.i(tag, "Returned X");


        return x;

    }
    public double getY(){
        Log.i(tag, "Returned Y");
        return y;

    }
}