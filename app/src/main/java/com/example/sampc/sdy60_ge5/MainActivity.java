package com.example.sampc.sdy60_ge5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btnInsert, btnBrowse, btnPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnInsert =(Button) findViewById(R.id.buttonLoadInsert);
        InsertData();

        btnBrowse =(Button) findViewById(R.id.buttonLoadBrowse);
        BrowseData();

        btnPoints =(Button) findViewById(R.id.buttonPoints);
        Points();
    }

    public void InsertData() {
        btnInsert.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, InsertRoute.class);
                        startActivity(intent);
                        Toast.makeText( MainActivity.this, "Ο Χάρτης εισαγωγής δεδομένων φορτώθηκε ...",Toast.LENGTH_SHORT ).show();
                    }
                }
        );
    }

    public void BrowseData() {
        btnBrowse.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, SelectList.class);
                        startActivity(intent);
                        //Toast.makeText( MainActivity.this, "Ο Χάρτης προβολής δεδομένων φορτώθηκε ...",Toast.LENGTH_SHORT ).show();
                    }
                }
        );
    }

    public void Points() {
        btnPoints.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Points.class);
                        startActivity(intent);
                        //Toast.makeText( MainActivity.this, "Ο Χάρτης προβολής δεδομένων φορτώθηκε ...",Toast.LENGTH_SHORT ).show();
                    }
                }
        );
    }

}
