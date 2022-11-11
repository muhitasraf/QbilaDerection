package com.insafsoft.qbiladerection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    // record the compass picture angle turned
    private float currentDegree = 0f;
    private float currentDegreeNeedle = 0f;
    Context context;
    Location userLoc = new Location("service Provider");
    // device sensor manager
    private static SensorManager mSensorManager;
    private Sensor sensor;
    public static TextView tvHeading;
    ImageView image;
    ImageView arrow;
    double longi = 23.79, lati = 90.33, alti = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //QiblaDirectionCompass qiblaDirectionCompass = new QiblaDirectionCompass();
        image = findViewById(R.id.imageCompass);
        arrow = findViewById(R.id.needle);

        Location MockLoc = new Location("service Provider");

        // TextView that will tell the user what degree is he heading
        tvHeading = findViewById(R.id.heading);
        userLoc.setLongitude(longi);
        userLoc.setLatitude(lati);
        userLoc.setAltitude(alti);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if (sensor != null) {
            // for the system's orientation sensor registered listeners
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);//SensorManager.SENSOR_DELAY_Fastest
        } else {
            Toast.makeText(context, "Not Supported", Toast.LENGTH_SHORT).show();
        }
        // initialize your android device sensor capabilities
        //this.context = context;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // for the system's orientation sensor registered listeners
        //mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        if (sensor != null) {
            // for the system's orientation sensor registered listeners
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);//SensorManager.SENSOR_DELAY_Fastest
        } else {
            Toast.makeText(context, "Not Supported", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged (SensorEvent sensorEvent){

        float degree = Math.round(sensorEvent.values[0]);
        float head = Math.round(sensorEvent.values[0]);
        float bearTo;
        Location destinationLoc = new Location("service Provider");

        destinationLoc.setLatitude(21.422487); //kaaba latitude setting
        destinationLoc.setLongitude(39.826206); //kaaba longitude setting
        bearTo = userLoc.bearingTo(destinationLoc);

        //bearTo = The angle from true north to the destination location from the point we're your currently standing.(asal image k N se destination taak angle )

        //head = The angle that you've rotated your phone from true north. (jaise image lagi hai wo true north per hai ab phone jitne rotate yani jitna image ka n change hai us ka angle hai ye)

        GeomagneticField geoField = new GeomagneticField(Double.valueOf(userLoc.getLatitude()).floatValue(), Double
                .valueOf(userLoc.getLongitude()).floatValue(),
                Double.valueOf(userLoc.getAltitude()).floatValue(),
                System.currentTimeMillis());
        head -= geoField.getDeclination(); // converts magnetic north into true north

        if (bearTo < 0) {
            bearTo = bearTo + 360;
            //bearTo = -100 + 360  = 260;
        }

        //This is where we choose to point it
        float direction = bearTo - head;

        // If the direction is smaller than 0, add 360 to get the rotation clockwise.
        Log.d("TAG", "onSensorChanged: "+direction);
        if (direction < 0) {
            direction = direction + 360;
        }
        tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

        RotateAnimation raQibla = new RotateAnimation(currentDegreeNeedle, direction, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        raQibla.setDuration(210);
        raQibla.setFillAfter(true);

        arrow.startAnimation(raQibla);

        currentDegreeNeedle = direction;

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        // how long the animation will take place
        ra.setDuration(210);


        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        image.startAnimation(ra);

        currentDegree = -degree;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor,int i){

    }

//    @Nullable
//    public IBinder onBind (Intent intent){
//        return null;
//    }
}