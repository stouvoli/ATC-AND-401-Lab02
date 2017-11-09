package com.numeris_ci.simpleflashlight;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    private Camera camera;
    private boolean isFlashOn;
    android.hardware.Camera.Parameters param;
    final String TAG = "MainActivity123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check if flash light is supported
        boolean hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            //flash not supported
            //show alert message and close app
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //Add button Ok
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setTitle("Your device doesn't support flash light!");
            //Create the AlertDialog
            AlertDialog alert = builder.create();
            alert.show();
        }

        getCamera();
        ToggleButton flashSwitch = (ToggleButton) findViewById(R.id.flash_switch);
        flashSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    turnOnFlash();
                } else {
                    turnOffFlash();
                }
            }
        });

    }

    private void getCamera() {
        if (camera == null) {
            try {
                camera = android.hardware.Camera.open();
                param = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e(TAG, "Camera Error: " + e.getMessage());

            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        turnOffFlash();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || param == null) {
                return;
            }
            param = camera.getParameters();
            param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(param);
            camera.startPreview();
            isFlashOn = true;

            Log.v(TAG, "Flash has been turned on ...");
        }
    }

    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || param == null) {
                return;
            }
            param = camera.getParameters();
            param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(param);
            camera.stopPreview();
            isFlashOn = false;

            Log.v(TAG, "Flash has been turned off ...");
        }
    }
}
