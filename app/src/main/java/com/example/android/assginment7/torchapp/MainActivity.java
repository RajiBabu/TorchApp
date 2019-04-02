package com.example.android.assginment7.torchapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private View root;
    private float maxValue;
    boolean feature_camera_flash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = findViewById(R.id.root);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        maxValue = lightSensor.getMaximumRange();
        if (lightSensor == null) {
            Toast.makeText(this, "The device has no light sensor !", Toast.LENGTH_SHORT).show();
            finish();
        }

        feature_camera_flash = isSupportCameraLedFlash(getBaseContext().getPackageManager());

        boolean camera_light = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 60);

    }

    public static boolean isSupportCameraLedFlash(PackageManager pm) {
        if (pm != null) {
            FeatureInfo[] features = pm.getSystemAvailableFeatures();
            if (features != null) {
                for (FeatureInfo f : features) {
                    if (f != null && PackageManager.FEATURE_CAMERA_FLASH.equals(f.name))
                        return true;
                }
            }
        }
        return false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightEventListener, lightSensor, 2 * 1000 * 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightEventListener);
    }


    private SensorEventListener lightEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float value = sensorEvent.values[0];
            int newValue = (int) (255f * value / maxValue);
           // getSupportActionBar().setTitle("Luminosity : " + value + " " + newValue);
            root.setBackgroundColor(Color.rgb(newValue, newValue, newValue));

            try {
                CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                String cameraId = cameraManager.getCameraIdList()[0];
                if(feature_camera_flash) {
                    if (newValue == 0f) {
                       cameraManager.setTorchMode(cameraId, true);
                    } else {
                        cameraManager.setTorchMode(cameraId, false);
                    }
                }else {
                    Toast.makeText(getBaseContext(), "The device has no  flashlight!", Toast.LENGTH_SHORT).show();
                }

            } catch (CameraAccessException e) {
            }

        }

            @Override
            public void onAccuracyChanged (Sensor sensor,int accuracy){
            }
        };


    @Override
    protected void onStop() {
        super.onStop();

    }

}
