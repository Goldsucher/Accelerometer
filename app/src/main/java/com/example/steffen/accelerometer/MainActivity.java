package com.example.steffen.accelerometer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView xText, yText, zText;
    private Sensor mySensor;
    private SensorManager SM;

    final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    boolean recordOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        // Record on/off Button
        final Button RecordButton = findViewById(R.id.RecordButton);

        RecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!recordOn) {
                    RecordButton.setText(("Stop Record"));
                    RecordButton.setBackgroundColor(Color.RED);
                    recordOn = true;
                } else {
                    RecordButton.setText(("Start Record"));
                    RecordButton.setBackgroundColor(Color.GREEN);
                    recordOn = false;
                }

            }
        });


        // create Sensor Manager
        SM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // create Accelerometer Sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // register Sensor Listener
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        // assign TextView
        xText = (TextView) findViewById(R.id.xText);
        yText = (TextView) findViewById(R.id.yText);
        zText = (TextView) findViewById(R.id.zText);


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        xText.setText("X: " + event.values[0]);
        yText.setText("Y: " + event.values[1]);
        zText.setText("Z: " + event.values[2]);

        String entry = xText.getText().toString() + "," + yText.getText().toString() + "," + zText.getText().toString() + "," + " \n";

        // create file stream
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/Accelerometer");
            Boolean dirsMade = dir.mkdir();
            System.out.println(dirsMade);
            Log.v("Accel", dirsMade.toString());

            File file = new File(dir, "output.csv");
            FileOutputStream f = new FileOutputStream(file, true);

            if(recordOn) {
                try {
                    f.write(entry.getBytes());
                    f.flush();
                    f.close();
                    // Toast.makeText(getBaseContext(), "Data saved", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    // Permission to write file
    private void checkPermissions() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        // Toast.makeText(getBaseContext(), "Permission is already granted", Toast.LENGTH_LONG).show();
    }
}
