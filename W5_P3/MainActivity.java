package com.example.w5_p3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ListView workout_list;
    String workout_type;
    Button startButton;
    Button stopButton;
    TextView stepCount;

    MediaPlayer mp;

    private SensorManager mSensorManager;

    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    private static int easy_shake = 250000;   //tweak this as necessary
    private static int medium_shake = 500000;
    private static int hard_shake = 750000;

    int step_count;
    int current_shake;
    long startTime;
    long stopTime;

    boolean inProgress;

    // Camera part
    private CameraManager CamManager;
    private String CamID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        workout_list = (ListView) findViewById(R.id.workout_list);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        stepCount = (TextView) findViewById(R.id.stepCount);
        inProgress = false;

        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;


        final String[] workouts = {"Easy: Jumping Jacks", "Medium: Squats", "Hard: Mountain Climber"};
        ArrayAdapter adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, workouts);
        workout_list.setAdapter(adapter);

        workout_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(inProgress){
                    Toast.makeText(getApplicationContext(),"Please stop your current workout session before changing a new workout type", Toast.LENGTH_SHORT).show();
                }
                else{
                    workout_type = String.valueOf(parent.getItemAtPosition(position));
                    if(workout_type.equals("Easy: Jumping Jacks")){
                        //superman
                        mp = MediaPlayer.create(MainActivity.this, R.raw.superman);
                        current_shake = easy_shake;
                    }
                    else if(workout_type.equals("Medium: Squats")){
                        //starwars
                        mp = MediaPlayer.create(MainActivity.this, R.raw.starwar);
                        current_shake = medium_shake;
                    }
                    else{
                        //workout_type.equals("Hard: Mountain Climber")
                        //rocky
                        mp = MediaPlayer.create(MainActivity.this, R.raw.rocky);
                        current_shake = hard_shake;
                    }
                }
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickStart();
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Stop button is clicked!", Toast.LENGTH_SHORT).show();
                ClickStop();
            }
        });

        // camera set up for flash
        CamManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CamID = CamManager.getCameraIdList()[0];  //rear camera is at index 0
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            step_count = Integer.parseInt(stepCount.getText().toString());
            if (step_count == 100) {
                //Toast.makeText(getApplicationContext(),"Current workout is finished!", Toast.LENGTH_SHORT).show();
                // can not call onStop()
                ClickStop();
            }
            else if (step_count == 20 && current_shake == easy_shake) {
                // easy mode
                mp.start();
            }
            else if (step_count == 50 && current_shake == medium_shake) {
                // medium mode
                mp.start();
            }
            else if (step_count == 60 && current_shake == hard_shake) {
                // hard mode
                // play rocky
                mp.start();
            }

            if (current_shake == hard_shake) {
                if (step_count >= 20 && step_count % 2 == 0) {
                    LightOn();
                } else if (step_count > 20 && step_count % 2 == 1) {
                    LightOff();
                }
            }
//            else if (Integer.parseInt(stepCount.getText().toString()) == 20 && current_shake == hard_shake) {
//                // hard mode flash
//                blinkingFlash();
//            }
//            else if (Integer.parseInt(stepCount.getText().toString()) == 60 && current_shake == hard_shake) {
//                // hard mode music
//                mp.start();
//            }

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = x * x + y * y + z * z;
            mAccel = mAccelCurrent * (mAccelCurrent - mAccelLast);

            // if the acceleration is above a certain threshold
            if (mAccel > current_shake) {
                int temp = Integer.parseInt(stepCount.getText().toString())+1;
                stepCount.setText(Integer.toString(temp));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void ClickStart(){
        if(inProgress){
            Toast.makeText(getApplicationContext(),"Please stop before restart", Toast.LENGTH_SHORT).show();
        }
        else if(workout_type != null){
            Toast.makeText(getApplicationContext(),"\""+workout_type+"\" starts now!", Toast.LENGTH_SHORT).show();
            enableAccelerometerListening();
            stepCount.setText("0");
            inProgress = true;
            startTime = SystemClock.elapsedRealtime();
        }
        else{
            Toast.makeText(getApplicationContext(),"No selected activity", Toast.LENGTH_SHORT).show();
        }
    }
    private void ClickStop(){
        if(inProgress == false){
            Toast.makeText(getApplicationContext(), "No event is in progress", Toast.LENGTH_SHORT).show();
        }
        else{
            disableAccelerometerListening();
            workout_type = null;
            current_shake = 0;
            mp.stop();
            mp.reset();
            inProgress = false;
            LightOff();

            stopTime = SystemClock.elapsedRealtime();
            long elapsedMilliSeconds = stopTime - startTime;
            double elapsedSeconds = elapsedMilliSeconds / 1000.0;

            Toast.makeText(getApplicationContext(), "Total elapsed time is: " + Double.toString(elapsedSeconds)+"s", Toast.LENGTH_SHORT).show();
        }

    }

    private void enableAccelerometerListening() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    // disable listening for accelerometer events
    private void disableAccelerometerListening() {
        // get the SensorManager
        SensorManager sensorManager =
                (SensorManager) this.getSystemService(
                        Context.SENSOR_SERVICE);

        // stop listening for accelerometer events
        sensorManager.unregisterListener(mSensorListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    @Override
    protected void onStop() {
        super.onStop();
        disableAccelerometerListening();
    }

    // flash part
    public void LightOn()
    {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                CamManager.setTorchMode(CamID, true);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void LightOff()
    {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                CamManager.setTorchMode(CamID, false);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void blinkingFlash() {
        long blinkDelay = 50;

        // while sensor is not off blink

        SensorManager sensorManager =
                (SensorManager) this.getSystemService(
                        Context.SENSOR_SERVICE);
        while (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)  != null) {
            LightOn();
            try {
                Thread.sleep(blinkDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LightOff();
            try {
                Thread.sleep(blinkDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}