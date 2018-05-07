package com.example.sampc.sdy60_ge5;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ShowPicture extends AppCompatActivity implements View.OnClickListener{


        @Override
        protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_picture);

            Button btnExit = (Button) findViewById(R.id.exit);
            btnExit.setOnClickListener(this);
            String uri = DataHolder.imagePath;

            ImageView jpgView = (ImageView)findViewById(R.id.showTakenImage);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bm = BitmapFactory.decodeFile(uri, options);
            jpgView.setImageBitmap(bm);

            Toast.makeText(this,"Η φωτογραφία που επιλέχθηκε : "+uri, Toast.LENGTH_LONG).show();


    }

        @Override
        public void onClick (View v){
        switch (v.getId()) {

                case R.id.exit:
                    DataHolder.imagePath="";
                    finish();

                    break;
            }
        }

}
