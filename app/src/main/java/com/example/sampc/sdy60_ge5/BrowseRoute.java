package com.example.sampc.sdy60_ge5;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class BrowseRoute extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener{

    private GoogleMap mMap;
    public Button btnExit;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<LatLng> routeList = new ArrayList<>();
    private List<String> typeList = new ArrayList<>();
    private List<String> photoList = new ArrayList<>();
    private Context mContext;
    public String ppp, ttt;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_route);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.vmap);
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        Button btnExit = (Button) findViewById(R.id.buttonExit);
        btnExit.setOnClickListener(this);

        Intent intent = getIntent();
        String selectedFromList = Objects.requireNonNull(intent.getExtras()).getString("routeString");

        db.collection("onFoot").orderBy(FieldPath.documentId()).whereEqualTo("label",selectedFromList).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                routeList.clear();
                typeList.clear();
                assert queryDocumentSnapshots != null;
                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Double tmpLat = Double.parseDouble(snapshot.getString("latitude"));
                    Double tmpLng = Double.parseDouble(snapshot.getString("longitute"));
                    String type = snapshot.getString("type");
                    String URI = snapshot.getString("imageURI");

                    routeList.add(new LatLng(tmpLat, tmpLng));

                    if (type.equals("RoadWorks")){
                        MarkerOptions mMarkerOptions = new MarkerOptions();
                        mMarkerOptions.position(new LatLng(tmpLat, tmpLng));
                        mMarkerOptions.icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.marker_custom_works));
                        mMarkerOptions.title("Έργα Οδοποιίας");

                        mMap.addMarker(mMarkerOptions);
                    }

                    if (type.equals("Slope")){
                        MarkerOptions mMarkerOptions = new MarkerOptions();
                        mMarkerOptions.position(new LatLng(tmpLat, tmpLng));
                        mMarkerOptions.icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.marker_custom_slope));
                        mMarkerOptions.title("Κλίση Εδάφους");

                        mMap.addMarker(mMarkerOptions);
                    }

                    if (type.equals("Stairs")){
                        MarkerOptions mMarkerOptions = new MarkerOptions();
                        mMarkerOptions.position(new LatLng(tmpLat, tmpLng));
                        mMarkerOptions.icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.marker_custom_stairs));
                        mMarkerOptions.title("Σκαλιά");

                        mMap.addMarker(mMarkerOptions);
                    }

                    if (type.equals("Photo")){
                        MarkerOptions mMarkerOptions = new MarkerOptions();
                        mMarkerOptions.position(new LatLng(tmpLat, tmpLng));
                        mMarkerOptions.icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.marker_custom_photo));
                        mMarkerOptions.title("Φωτογραφία με URI: "+URI);

                        mMap.addMarker(mMarkerOptions);
                    }

                    typeList.add(snapshot.getString("type"));
                }

                LatLng camLoc = routeList.get(0);
                PolylineOptions polylineOptions = new PolylineOptions();

                polylineOptions.addAll(routeList);
                polylineOptions
                        .width(5)
                        .color(Color.RED);

                mMap.addPolyline(polylineOptions);

                Marker markerStart = mMap.addMarker(new MarkerOptions()
                        .position(routeList.get(0))
                        .title("Αρχή"));
                markerStart.showInfoWindow();

                Marker markerEnd = mMap.addMarker(new MarkerOptions()
                        .position(routeList.get(routeList.size()-1))
                        .title("Τέλος"));
                markerEnd.showInfoWindow();

                CameraPosition camPosition = new CameraPosition.Builder()
                        .target(camLoc).zoom(17f).build();
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(camPosition));
            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        UiSettings ui = mMap.getUiSettings();
        ui.setZoomControlsEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override

            public boolean onMarkerClick(final Marker marker) {
                String ttt = marker.getTitle();
                String ppp = ttt.substring(ttt.length() - 3);

                if (ppp.equals("jpg")){
                    int start = ttt.indexOf(":");
                    DataHolder.imagePath= ttt.substring(start + 1);
                    Intent intent = new Intent(BrowseRoute.this, ShowPicture.class);
                    startActivity(intent);
                   // aaa();

//                    img= (ImageView) findViewById(R.id.img);
//                    File downloadsFolder= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//                    Uri file= Uri.fromFile(new File(downloadsFolder,"1524554172762.jpg"));
//                    if(file.toString() != null && file.toString().length()>0)
//                    {
//                        Picasso.with(mContext).load(file).into(img);
//                        //nameTxt.setText(file.toString());
//                    }else
//                    {
//                        Toast.makeText(BrowseRoute.this, "Empty URI", Toast.LENGTH_SHORT).show();
//                    }


                }
//Toast.makeText(BrowseRoute.this,ppp, Toast.LENGTH_SHORT).show();

//
//
//                //Toast.makeText(BrowseRoute.this,"kai emeine :"+suffix, Toast.LENGTH_SHORT).show();
//
//                ImageView imgView = new ImageView(BrowseRoute.this);
//                InputStream is = getClass().getResourceAsStream(suffix);
//                imgView.setImageDrawable(Drawable.createFromStream(is, ""));
//
//                Intent intent = new Intent(BrowseRoute.this, ShowPicture.class);
//                intent.putExtra("routeString",ttt);
//                startActivity(intent);

                return true;
            }
        });
    }

    public void aaa(){

        Intent intent = new Intent(BrowseRoute.this, ShowPicture.class);
        startActivity(intent);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonExit:
finish();

                break;
        }
    }

}
