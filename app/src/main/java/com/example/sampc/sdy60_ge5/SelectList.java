package com.example.sampc.sdy60_ge5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class SelectList extends Activity implements View.OnClickListener{

    ListView listView;
    private List<String> namesList = new ArrayList<>();
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String selectedFromList;
    public Button btnBrowseFoot, btnBrowseWeel, btnBrowseBicycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_list);

        Button btnBrowseFoot = (Button) findViewById(R.id.buttonBrowseFoot);
        btnBrowseFoot.setOnClickListener(this);
        Button btnBrowseWeel = (Button) findViewById(R.id.buttonBrowseWeel);
        btnBrowseWeel.setOnClickListener(this);
        Button btnBrowseBicycle = (Button) findViewById(R.id.buttonBrowseBicycle);
        btnBrowseBicycle.setOnClickListener(this);
        Button btnExit = (Button) findViewById(R.id.buttonExit);
        btnExit.setOnClickListener(this);
    }

    public void StopActivity(){
        this.finish();
        Intent intent = new Intent(SelectList.this, BrowseRoute.class);
        intent.putExtra("routeString",selectedFromList);
        startActivity(intent);
        //BrowseRoute.BrowseFoot();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonBrowseFoot:

                listView = findViewById(R.id.listview);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> myAdapter, View myView, int pos, long mylng) {
                        selectedFromList =(listView.getItemAtPosition(pos).toString());
                        // this is your selected item
                        //Toast.makeText(SelectList.this,"Patisate : "+selectedFromList, Toast.LENGTH_SHORT).show();
                        StopActivity();

                    }
                });

// whereEqualTo("label",selectedFromList)
                db.collection("onFoot").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        namesList.clear();

                        for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                            String tmp = snapshot.getString("label");
                            if (tmp.contains("onFoot")) {
                                if (!namesList.contains(tmp)) {
//                            if (!namesList.contains(tmp)) {
                                    namesList.add(tmp);
                                    //namesList.add(snapshot.getString("name2"));
                                }
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_selectable_list_item, namesList);
                        adapter.notifyDataSetChanged();
                        adapter.sort(new Comparator<String>() {
                            @Override
                            public int compare(String arg1, String arg0) {
                                return arg1.compareTo(arg0);
                            }
                        });
                        listView.setAdapter(adapter);

                        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                                           int position, long id) {
                                // TODO Auto-generated method stub
                                String delString = namesList.get(position);
                                //********************************************************************************************************************************
//                                db.collection("onFoot").w.document("11111 - onFoot-0009").delete();




                                //********************************************************************************************************************************
                               Toast.makeText(SelectList.this, delString, Toast.LENGTH_SHORT).show();

                                return true;
                            }

                        });




                    }
                });

                break;
        }






        switch (v.getId()) {
            case R.id.buttonBrowseWeel:

                listView = findViewById(R.id.listview);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> myAdapter, View myView, int pos, long mylng) {
                        selectedFromList =(listView.getItemAtPosition(pos).toString());
                        // this is your selected item
                        //Toast.makeText(SelectList.this,"Patisate : "+selectedFromList, Toast.LENGTH_SHORT).show();
                        StopActivity();

                    }
                });

                db.collection("onFoot").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        namesList.clear();

                        for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                            String tmp = snapshot.getString("label");
                            if (tmp.contains("onWeel")) {
                                if (!namesList.contains(tmp)) {
//                            if (!namesList.contains(tmp)) {
                                    namesList.add(tmp);
                                    //namesList.add(snapshot.getString("name2"));
                                }
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_selectable_list_item, namesList);
                        adapter.notifyDataSetChanged();
                        adapter.sort(new Comparator<String>() {
                            @Override
                            public int compare(String arg1, String arg0) {
                                return arg1.compareTo(arg0);
                            }
                        });
                        listView.setAdapter(adapter);
                    }
                });

                break;
        }


        switch (v.getId()) {
            case R.id.buttonBrowseBicycle:

                listView = findViewById(R.id.listview);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> myAdapter, View myView, int pos, long mylng) {
                        selectedFromList =(listView.getItemAtPosition(pos).toString());
                        // this is your selected item
                        //Toast.makeText(SelectList.this,"Patisate : "+selectedFromList, Toast.LENGTH_SHORT).show();
                        StopActivity();

                    }
                });

                db.collection("onFoot").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        namesList.clear();

                        for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                            String tmp = snapshot.getString("label");
                            if (tmp.contains("onBicycle")) {
                                if (!namesList.contains(tmp)) {
//                            if (!namesList.contains(tmp)) {
                                    namesList.add(tmp);
                                    //namesList.add(snapshot.getString("name2"));
                                }
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_selectable_list_item, namesList);
                        adapter.notifyDataSetChanged();
                        adapter.sort(new Comparator<String>() {
                            @Override
                            public int compare(String arg1, String arg0) {
                                return arg1.compareTo(arg0);
                            }
                        });
                        listView.setAdapter(adapter);
                    }
                });

                break;
        }


        switch (v.getId()) {
            case R.id.buttonExit:

                finish();

                break;
        }


    }



}


