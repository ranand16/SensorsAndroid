package com.example.ranand16.accelerometer;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity  {
    final float a = (float) 0.98f;
    static final float ALPHA = 0.9f; // if ALPHA = 1 OR 0, no filter applies.

    boolean startorstop = false;
    float[] gravity = new float[3];
    float[] accelVals = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        final Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        final TextView xLabel = (TextView) findViewById(R.id.textView1);
        final TextView yLabel = (TextView) findViewById(R.id.textView2);
        final TextView zLabel = (TextView) findViewById(R.id.textView3);
        Button start = (Button) findViewById(R.id.startBtn);
        Button stop = (Button) findViewById(R.id.stopBtn);

        if(sensor == null) {
            finish(); // we will close the app
        } else{
            Log.wtf("Its present", "Its present");
        }

        final SensorEventListener sel = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

//                Log.wtf("This is accel:- ", "X: "+gravity[0]+" Y: "+gravity[1]+" Z: "+gravity[2] );

                // removing the gravity affect

                gravity[0] = a * gravity[0] + (1 - a) * event.values[0];
                gravity[1] = a * gravity[1] + (1 - a) * event.values[1];
                gravity[2] = a * gravity[2] + (1 - a) * event.values[2];
//
//                event.values[0] = event.values[0] - gravity[0];
//                event.values[1] = event.values[1] - gravity[1];
//                event.values[2] = event.values[2] - gravity[2];

                // applying low pass filter
                accelVals = lowPassFilter(event.values.clone(), accelVals);

                // to make result correct to 2 places decimal value
                NumberFormat formatter = NumberFormat.getNumberInstance();
                formatter.setMinimumFractionDigits(2);
                formatter.setMaximumFractionDigits(2);
                String output = formatter.format(accelVals[0]);

                if(startorstop){
                    appendLog("\n"+formatter.format(accelVals[0]) + "," + formatter.format(accelVals[1]) + "," + formatter.format(accelVals[2]));
                }
                xLabel.setText(""+formatter.format(accelVals[0]));
                yLabel.setText(""+formatter.format(accelVals[1]));
                zLabel.setText(""+formatter.format(accelVals[2]));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("This is onClickListener's onCLick function","Clicked start button");
                sm.registerListener((SensorEventListener) sel, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                startorstop = true;
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("This is onCLickListener's onCLick function","Clicked Stop button");
                sm.unregisterListener((SensorEventListener) sel);

                xLabel.setText("0");
                yLabel.setText("0");
                zLabel.setText("0");
                startorstop = false;


            }
        });
//        sm.registerListener(sel, sensor, 1 * 1000 * 1000 );
//        sm.registerListener(sel, sensor, SensorManager.SENSOR_DELAY_NORMAL );
    }


    protected float[] lowPassFilter( float[] input, float[] output ) {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    public void appendLog(String text)
    {
        File logFile = new File("sdcard/log.csv");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();

                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append("x,y,z");
                buf.newLine();
                buf.close();

                Log.wtf("GOOD","file created");
            }
            catch (IOException e)
            {
                Log.wtf("ERROR","couldnot create file");
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
            Log.wtf("GOOD","string appended");

        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            Log.wtf("ERROR","couldnot write");

            e.printStackTrace();

        }
    }

}
