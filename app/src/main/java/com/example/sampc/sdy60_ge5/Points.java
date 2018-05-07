package com.example.sampc.sdy60_ge5;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.sampc.sdy60_ge5.DataHolder.pointsCounter;

public class Points extends AppCompatActivity {
    public String innerCounter;
    private List<String> typeList = new ArrayList<>();
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

        final Button exit = (Button) findViewById(R.id.buttonExit);

        exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        showPoints();
        showRoutes();
    }

    public  void showPoints(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("points").document("6uQZYAZJChRIYejnFfU0");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                innerCounter = documentSnapshot.getString("userPoints");
                TextView pointsView = (TextView) findViewById(R.id.txtPoints);
                pointsView.setText(innerCounter);
            }
        });

    }

    public  void showRoutes(){

        db.collection("onFoot").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                typeList.clear();
                assert queryDocumentSnapshots != null;
                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    typeList.add(snapshot.getString("type"));
                }

                int listLength = typeList.size();

                TextView routesView = (TextView) findViewById(R.id.txtRoutes);
                routesView.setText(String.valueOf(listLength));

            }
        });
    }
}
