package com.example.sampc.sdy60_ge5;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import static com.example.sampc.sdy60_ge5.DataHolder.pointsCounter;

public class TakePicture extends AppCompatActivity {

    final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    GPSTracker gps;

    Uri imageUri                      = null;
    static TextView imageDetails      = null;
    public  static ImageView showImg  = null;
    TakePicture CameraActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        CameraActivity = this;

        imageDetails = (TextView) findViewById(R.id.imageDetails);
        showImg = (ImageView) findViewById(R.id.showImg);
        final Button photo = (Button) findViewById(R.id.photo);
        final Button saveAndExit =(Button) findViewById(R.id.savePhotoAndExit) ;
        final Button exit = (Button) findViewById(R.id.buttonExit);

        photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                /*************************** Camera Intent Start ************************/

                // Define the file-name to save photo taken by Camera activity

                String fileName = "Camera_Example.jpg";

                // Create parameters for Intent with filename

                ContentValues values = new ContentValues();

                values.put(MediaStore.Images.Media.TITLE, fileName);

                values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");

                // imageUri is the current activity attribute, define and save it for later usage

                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                /**** EXTERNAL_CONTENT_URI : style URI for the "primary" external storage volume. ****/


                // Standard Intent action that can be sent to have the camera
                // application capture an image and return it.

                Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                startActivityForResult( intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                /*************************** Camera Intent End ************************/


            }

        });

        saveAndExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Toast.makeText(TakePicture.this,""+DataHolder.imagePath,Toast.LENGTH_SHORT).show();

                final FirebaseFirestore db = FirebaseFirestore.getInstance();

                String DateTime = new SimpleDateFormat("dd-MM-yyyy' @ 'HH:mm:ss", Locale.getDefault()).format(new Date());
                final String date = (String) DateTime.subSequence(0,10);
                final String time = (String) DateTime.subSequence(13,21);
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();

                gps = new GPSTracker(TakePicture.this);
                if(gps.canGetLocation()){
                    String latitude = String.valueOf(gps.getLatitude());
                    String longitude = String.valueOf(gps.getLongitude());

                    Map<String, String> map = new HashMap<>();
                    map.put("label", DataHolder.labelForPhoto);
                    map.put("type", "Photo");
                    map.put("date", date);
                    map.put("time", time);
                    map.put("latitude", latitude);
                    map.put("longitute", longitude);
                    map.put("timestamp", ts);
                    map.put("imageURI", DataHolder.imagePath);

                    db.collection("onFoot").document(DataHolder.labelForPhoto+"-"+String.valueOf(DataHolder.documentCounter)).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Data saved",Toast.LENGTH_SHORT).show();
                                DataHolder.imagePath="";
                            }
                        }
                    });
                }else{
                    gps.showSettingsAlert();
                }
                pointsCounter = pointsCounter +1;
                DataHolder.documentCounter=DataHolder.documentCounter+1;
            }

        });



        exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });




    }



    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data)
    {
        if ( requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

            if ( resultCode == RESULT_OK) {

                /*********** Load Captured Image And Data Start ****************/

                String imageId = convertImageUriToFile( imageUri,CameraActivity);


                //  Create and excecute AsyncTask to load capture image

                new LoadImagesFromSDCard().execute(""+imageId);

                /*********** Load Captured Image And Data End ****************/


            } else if ( resultCode == RESULT_CANCELED) {

                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /************ Convert Image Uri path to physical path **************/

    public String convertImageUriToFile(Uri imageUri, Activity activity)  {

        Cursor cursor = null;
        int imageID = 0;

        try {

            /*********** Which columns values want to get *******/
            String [] proj={
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Thumbnails._ID,
                    MediaStore.Images.ImageColumns.ORIENTATION
            };

            cursor = activity.managedQuery(

                    imageUri,         //  Get data for specific image URI
                    proj,             //  Which columns to return
                    null,             //  WHERE clause; which rows to return (all rows)
                    null,             //  WHERE clause selection arguments (none)
                    null              //  Order-by clause (ascending by name)

            );

            //  Get Query Data

            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int columnIndexThumb = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
            int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            int size = cursor.getCount();

            /*******  If size is 0, there are no images on the SD Card. *****/

            if (size == 0) {


                imageDetails.setText("No Image");
            }
            else
            {

                int thumbID = 0;
                if (cursor.moveToFirst()) {

                    /**************** Captured image details ************/

                    /*****  Used to show image on view in LoadImagesFromSDCard class ******/
                    imageID     = cursor.getInt(columnIndex);

                    thumbID     = cursor.getInt(columnIndexThumb);

                    String Path = cursor.getString(file_ColumnIndex);

                    //String orientation =  cursor.getString(orientation_ColumnIndex);

                    String CapturedImageDetails = " CapturedImageDetails : \n\n"
                            +" ImageID :"+imageID+"\n"
                            +" ThumbID :"+thumbID+"\n"
                            +" Path :"+Path+"\n";

                    // Show Captured Image detail on activity
                    imageDetails.setText( CapturedImageDetails );

                    DataHolder.imagePath=Path;

                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
                enableSave();

            }

        }

        // Return Captured Image ImageID ( By this ImageID Image will load from sdcard )

        return ""+imageID;
    }

    public void enableSave() {
        findViewById(R.id.savePhotoAndExit).setEnabled(true);
    }

    @SuppressLint("StaticFieldLeak")
    public class LoadImagesFromSDCard  extends AsyncTask<String, Void, Void> {

        private ProgressDialog Dialog = new ProgressDialog(TakePicture.this);

        Bitmap mBitmap;

        protected void onPreExecute() {
            Dialog.setMessage(" Loading image from Sdcard..");
            Dialog.show();
        }

        protected Void doInBackground(String... urls) {

            Bitmap bitmap = null;
            Bitmap newBitmap = null;
            Uri uri = null;

            try {

                uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + urls[0]);

                /**************  Decode an input stream into a bitmap. *********/
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));

                if (bitmap != null) {

                    /********* Creates a new bitmap, scaled from an existing bitmap. ***********/

                    newBitmap = Bitmap.createScaledBitmap(bitmap, 170, 170, true);

                    bitmap.recycle();

                    if (newBitmap != null) {

                        mBitmap = newBitmap;
                    }
                }
            } catch (IOException e) {
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            Dialog.dismiss();
            if(mBitmap != null)
            {
                showImg.setImageBitmap(mBitmap);
            }
        }
    }
}