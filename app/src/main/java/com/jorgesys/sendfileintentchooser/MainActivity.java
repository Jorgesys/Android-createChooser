package com.jorgesys.sendfileintentchooser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    /*Read the docs
    https://developer.android.com/training/sharing/send.html
     */


    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


         //Start Asynctask!
         new sendImageTask().execute();

    }



    public class sendImageTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {

            try {
                URL url = new URL("http://i.stack.imgur.com/oURrw.png"); //Define your own image!
                Bitmap imagen = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                // Salva bitmap a disco.
                try {

                    File cachePath = new File(getCacheDir(), "imagenes"); //cache path.
                    cachePath.mkdirs(); // Create directory.
                    FileOutputStream stream = new FileOutputStream(cachePath + "/imagen.jpg"); // Write imagen.
                    imagen.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();

                } catch (IOException e) {
                   Log.e(TAG,  e.getMessage());
                }


                File imagePath = new File(getCacheDir(), "imagenes"); //get directory.
                File newFile = new File(imagePath, "imagen.jpg"); //get image.

                String PACKAGE_NAME = getApplicationContext().getPackageName() + ".providers.FileProvider";

                Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), PACKAGE_NAME, newFile);

                if (contentUri != null) {

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                    shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    startActivity(Intent.createChooser(shareIntent, "Choose your application:"));

                }

            } catch (IOException e) {
                Log.e(TAG,  e.getMessage());
            }

            return null;
        }


    }


}
