package com.example.sampc.sdy60_ge5;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.sampc.sdy60_ge5.DataHolder.pointsCounter;

public class InsertRoute extends FragmentActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener {

    private GoogleMap mMap;
    private LocationManager mLocationManager = null;
    private String provider = null;
    private Marker mCurrentPosition = null;

    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;

    public Button btnStartFoot, btnStartWeel, btnStartBicycle, btnRoadWorks, btnSlope, btnStairs, btnPicture;

    public String lat_val, lng_val, flag, label, type;

    public String innerCounter;
    Timer cTimer = null;
    GPSTracker gps;
    DatabaseReference mDatabaseLocationDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_route);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLatitudeTextView = (TextView) findViewById((R.id.latitude_textview));
        mLongitudeTextView = (TextView) findViewById((R.id.longitude_textview));

        Button btnStartFoot = (Button) findViewById(R.id.buttonStartFoot);
        btnStartFoot.setOnClickListener(this);
        Button btnStartWeel = (Button) findViewById(R.id.buttonStartWeel);
        btnStartWeel.setOnClickListener(this);
        Button btnStartBicycle = (Button) findViewById(R.id.buttonStartBicycle);
        btnStartBicycle.setOnClickListener(this);
        Button btnRoadWorks = (Button) findViewById(R.id.buttonRoadWorks);
        btnRoadWorks.setOnClickListener(this);
        Button btnSlope = (Button) findViewById(R.id.buttonSlope);
        btnSlope.setOnClickListener(this);
        Button btnStairs = (Button) findViewById(R.id.buttonStairs);
        btnStairs.setOnClickListener(this);
        Button btnPicture = (Button) findViewById(R.id.buttonPicture);
        btnPicture.setOnClickListener(this);
        Button btnEndRec = (Button) findViewById(R.id.buttonEndRecording);
        btnEndRec.setOnClickListener(this);


        btnRoadWorks.setEnabled(false);
        btnSlope.setEnabled(false);
        btnStairs.setEnabled(false);
        btnPicture.setEnabled(false);
        btnEndRec.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
        }
        mMap.setMyLocationEnabled(true);

        UiSettings ui = mMap.getUiSettings();
        ui.setZoomControlsEnabled(true);

        if (isProviderAvailable() && (provider != null)) {
            locateCurrentPosition();
        }
    }

    private void locateCurrentPosition() {

        int status = getPackageManager().checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION,
                getPackageName());

        if (status == PackageManager.PERMISSION_GRANTED) {
            Location location = mLocationManager.getLastKnownLocation(provider);
            updateWithNewLocation(location);
            //  mLocationManager.addGpsStatusListener(this);
            long minTime = 5000;// ms
            float minDist = 5.0f;// meter
            mLocationManager.requestLocationUpdates(provider, minTime, minDist,
                    this);
        }
    }


    private boolean isProviderAvailable() {
        mLocationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        provider = mLocationManager.getBestProvider(criteria, true);
        if (mLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;

            return true;
        }

        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
            return true;
        }

        if (provider != null) {
            return true;
        }
        return false;
    }

    private void updateWithNewLocation(Location location) {

        if (location != null && provider != null) {
            double lng = location.getLongitude();
            double lat = location.getLatitude();

            addBoundaryToCurrentPosition(lat, lng);

            CameraPosition camPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat, lng)).zoom(18f).build();

            if (mMap != null)
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(camPosition));
        } else {
            Log.d("Location error", "Something went wrong");
        }
    }


    private void addBoundaryToCurrentPosition(double lat, double lang) {

        MarkerOptions mMarkerOptions = new MarkerOptions();
        mMarkerOptions.position(new LatLng(lat, lang));
        mMarkerOptions.icon(BitmapDescriptorFactory
                .fromResource(R.drawable.marker_custom));
        mMarkerOptions.anchor(0.5f, 0.5f);

        CircleOptions mOptions = new CircleOptions()
                .center(new LatLng(lat, lang)).radius(10) //(10000)
                .strokeColor(0x110000FF).strokeWidth(1).fillColor(0x110000FF);
        mMap.addCircle(mOptions);
        if (mCurrentPosition != null)
            mCurrentPosition.remove();
        mCurrentPosition = mMap.addMarker(mMarkerOptions);
    }

    @Override
    public void onLocationChanged(Location location) {

        updateWithNewLocation(location);
        mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
        mLongitudeTextView.setText(String.valueOf(location.getLongitude() ));

        lat_val=String.valueOf(location.getLatitude());
        lng_val=String.valueOf(location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {

        updateWithNewLocation(null);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                break;
            case LocationProvider.AVAILABLE:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.buttonStartFoot:
                StartRecording();

                flag="Foot";
                type="Normal";

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText text = new EditText(this);

                builder.setTitle("Νέα Διαδρομή").setMessage("Δώστε το όνομα της διαδρομής σας ...").setView(text);
                builder.setPositiveButton("Έναρξη", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface di, int i) {
                        label="";
                        label = text.getText().toString()+" - onFoot";
                        DataHolder.labelForPhoto=label;
                        startLoop();
                    }
                });
                builder.setNegativeButton("Ακύρωση", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface di, int i) {
                        di.cancel();
                        EndRecording();
                    }
                });
                builder.create().show();

                break;

            case R.id.buttonStartWeel:
                StartRecording();

                flag="Weel";
                type="Normal";

                AlertDialog.Builder builderWeel = new AlertDialog.Builder(this);
                final EditText textWeel = new EditText(this);

                builderWeel.setTitle("Νέα Διαδρομή").setMessage("Δώστε το όνομα της διαδρομής σας ...").setView(textWeel);
                builderWeel.setPositiveButton("Έναρξη", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface di, int i) {
                        label="";
                        label = textWeel.getText().toString()+" - onWeel";
                        DataHolder.labelForPhoto=label;
                        startLoop();
                    }
                });
                builderWeel.setNegativeButton("Ακύρωση", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface di, int i) {
                        di.cancel();
                        EndRecording();
                    }
                });
                builderWeel.create().show();

                break;

            case R.id.buttonStartBicycle:
                StartRecording();

                flag="Bicycle";
                type="Normal";

                AlertDialog.Builder builderBicycle = new AlertDialog.Builder(this);
                final EditText textBicycle = new EditText(this);

                builderBicycle.setTitle("Νέα Διαδρομή").setMessage("Δώστε το όνομα της διαδρομής σας ...").setView(textBicycle);
                builderBicycle.setPositiveButton("Έναρξη", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface di, int i) {
                        label="";
                        label = textBicycle.getText().toString()+" - onBicycle";
                        DataHolder.labelForPhoto=label;
                        startLoop();
                    }
                });
                builderBicycle.setNegativeButton("Ακύρωση", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface di, int i) {
                        di.cancel();
                        EndRecording();

                    }
                });
                builderBicycle.create().show();

                break;

            case R.id.buttonRoadWorks:
                // do your code
                StoreRoadWorks();
                break;

            case R.id.buttonSlope:
                // do your code
                StoreSlope();
                break;

            case R.id.buttonStairs:
                // do your code
                StoreStairs();
                break;

            case R.id.buttonEndRecording:
                // do your code
                EndRecording();
                cancelLoop();
                addPoints();

                break;

            case R.id.buttonPicture:
                // do your code
                Intent intent = new Intent(InsertRoute.this, TakePicture.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    public void addPoints(){

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("points").document("6uQZYAZJChRIYejnFfU0");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                innerCounter = documentSnapshot.getString("userPoints");
                int total = Integer.parseInt(innerCounter);
                    total = total + pointsCounter;
                DocumentReference points = db.collection("points").document("6uQZYAZJChRIYejnFfU0");
                    points.update("userPoints", String.valueOf(total))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(InsertRoute.this, "Προστέθηκαν "+pointsCounter+" πόντοι", Toast.LENGTH_SHORT).show();
                                    DataHolder.pointsCounter=0;
                                }
                            });
            }
        });
    }


    public void startLoop() {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        String DateTime = new SimpleDateFormat("dd-MM-yyyy' @ 'HH:mm:ss", Locale.getDefault()).format(new Date());
        final String date = (String) DateTime.subSequence(0,10);
        final String time = (String) DateTime.subSequence(13,21);
        Long tsLong = System.currentTimeMillis()/1000;
        final String ts = tsLong.toString();

        final Handler handler = new Handler();

        TimerTask timertask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressLint("DefaultLocale")
                    public void run() {

                        gps = new GPSTracker(InsertRoute.this);
                        if(gps.canGetLocation()){
                            String latitude = String.valueOf(gps.getLatitude());
                            String longitude = String.valueOf(gps.getLongitude());

                            Map<String, String> map = new HashMap<>();
                            map.put("label", label);
                            map.put("type", "Normal");
                            map.put("date", date);
                            map.put("time", time);
                            map.put("latitude", latitude);
                            map.put("longitute", longitude);
                            map.put("timestamp", ts);


                            db.collection("onFoot").document(label+"-"+String.format("%04d",DataHolder.documentCounter)).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(),"Data saved",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            gps.showSettingsAlert();
                        }
                    }
                });
                pointsCounter = pointsCounter +1;
                DataHolder.documentCounter=DataHolder.documentCounter+1;
            }
        };
        cTimer = new Timer();
        cTimer.schedule(timertask, 0, 5000);
    }

    public void StoreRoadWorks(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        String DateTime = new SimpleDateFormat("dd-MM-yyyy' @ 'HH:mm:ss", Locale.getDefault()).format(new Date());
        final String date = (String) DateTime.subSequence(0,10);
        final String time = (String) DateTime.subSequence(13,21);
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

                        gps = new GPSTracker(InsertRoute.this);
                        if(gps.canGetLocation()){
                            String latitude = String.valueOf(gps.getLatitude());
                            String longitude = String.valueOf(gps.getLongitude());

                            Map<String, String> map = new HashMap<>();
                            map.put("label", label);
                            map.put("type", "RoadWorks");
                            map.put("date", date);
                            map.put("time", time);
                            map.put("latitude", latitude);
                            map.put("longitute", longitude);
                            map.put("timestamp", ts);

                            db.collection("onFoot").document(label+"-"+String.valueOf(DataHolder.documentCounter)).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(),"Data saved",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            gps.showSettingsAlert();
                        }
        pointsCounter = pointsCounter +1;
        DataHolder.documentCounter=DataHolder.documentCounter+1;
    }

    public void StoreSlope(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        String DateTime = new SimpleDateFormat("dd-MM-yyyy' @ 'HH:mm:ss", Locale.getDefault()).format(new Date());
        final String date = (String) DateTime.subSequence(0,10);
        final String time = (String) DateTime.subSequence(13,21);
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        gps = new GPSTracker(InsertRoute.this);
        if(gps.canGetLocation()){
            String latitude = String.valueOf(gps.getLatitude());
            String longitude = String.valueOf(gps.getLongitude());

            Map<String, String> map = new HashMap<>();
            map.put("label", label);
            map.put("type", "Slope");
            map.put("date", date);
            map.put("time", time);
            map.put("latitude", latitude);
            map.put("longitute", longitude);
            map.put("timestamp", ts);

            db.collection("onFoot").document(label+"-"+String.valueOf(DataHolder.documentCounter)).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Data saved",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            gps.showSettingsAlert();
        }
        pointsCounter = pointsCounter +1;
        DataHolder.documentCounter=DataHolder.documentCounter+1;
    }

    public void StoreStairs(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        String DateTime = new SimpleDateFormat("dd-MM-yyyy' @ 'HH:mm:ss", Locale.getDefault()).format(new Date());
        final String date = (String) DateTime.subSequence(0,10);
        final String time = (String) DateTime.subSequence(13,21);
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        gps = new GPSTracker(InsertRoute.this);
        if(gps.canGetLocation()){
            String latitude = String.valueOf(gps.getLatitude());
            String longitude = String.valueOf(gps.getLongitude());

            Map<String, String> map = new HashMap<>();
            map.put("label", label);
            map.put("type", "Stairs");
            map.put("date", date);
            map.put("time", time);
            map.put("latitude", latitude);
            map.put("longitute", longitude);
            map.put("timestamp", ts);

            db.collection("onFoot").document(label+"-"+String.valueOf(DataHolder.documentCounter)).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Data saved",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            gps.showSettingsAlert();
        }
        pointsCounter = pointsCounter +1;
        DataHolder.documentCounter=DataHolder.documentCounter+1;
    }

    public void EndRecording(){
        Button btnEndRec = (Button) findViewById(R.id.buttonEndRecording);
        btnEndRec.setVisibility(View.INVISIBLE);
        Button btnStartFoot = (Button) findViewById(R.id.buttonStartFoot);
        btnStartFoot.setVisibility(View.VISIBLE);
        Button btnStartWeel = (Button) findViewById(R.id.buttonStartWeel);
        btnStartWeel.setVisibility(View.VISIBLE);
        Button btnStartBicycle = (Button) findViewById(R.id.buttonStartBicycle);
        btnStartBicycle.setVisibility(View.VISIBLE);
        Button btnRoadWorks = (Button) findViewById(R.id.buttonRoadWorks);
        btnRoadWorks.setEnabled(false);
        Button btnSlope = (Button) findViewById(R.id.buttonSlope);
        btnSlope.setEnabled(false);
        Button btnBridge = (Button) findViewById(R.id.buttonStairs);
        btnBridge.setEnabled(false);
        Button btnPicture = (Button) findViewById(R.id.buttonPicture);
        btnPicture.setEnabled(false);
    }

    public void StartRecording() {
        Button btnEndRec = (Button) findViewById(R.id.buttonEndRecording);
        btnEndRec.setVisibility(View.VISIBLE);
        Button btnStartFoot = (Button) findViewById(R.id.buttonStartFoot);
        btnStartFoot.setVisibility(View.INVISIBLE);
        Button btnStartWeel = (Button) findViewById(R.id.buttonStartWeel);
        btnStartWeel.setVisibility(View.INVISIBLE);
        Button btnStartBicycle = (Button) findViewById(R.id.buttonStartBicycle);
        btnStartBicycle.setVisibility(View.INVISIBLE);
        Button btnRoadWorks = (Button) findViewById(R.id.buttonRoadWorks);
        btnRoadWorks.setEnabled(true);
        Button btnSlope = (Button) findViewById(R.id.buttonSlope);
        btnSlope.setEnabled(true);
        Button btnBridge = (Button) findViewById(R.id.buttonStairs);
        btnBridge.setEnabled(true);
        Button btnPicture = (Button) findViewById(R.id.buttonPicture);
        btnPicture.setEnabled(true);
    }

    public void cancelLoop() {

        if(cTimer!=null)
            cTimer.cancel();
    }

}
