package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;
import com.milesmagusruber.secretserviceflickrsearch.db.model.SearchRequest;

import java.io.File;

import static com.milesmagusruber.secretserviceflickrsearch.activities.GalleryActivity.EXTRA_GALLERY_ITEM;

public class GalleryViewItemActivity extends AppCompatActivity {

    private ImageView galleryViewItem;
    private File photoFile;
    private Bitmap fileBitmap;

    //asyncTask to work with files
    private AsyncTask<Void,Void,Boolean> fileWorkAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view_item);
        galleryViewItem = findViewById(R.id.gallery_view_item_imageview);

        //file operation, getting image
        fileWorkAsyncTask = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                try{
                photoFile = new File(getIntent().getStringExtra(EXTRA_GALLERY_ITEM));
                fileBitmap = BitmapFactory.decodeFile(photoFile.getPath());
                return true;
                }catch (Exception e){
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result) {
                    galleryViewItem.setImageBitmap(fileBitmap);
                }
            }
        };
        fileWorkAsyncTask.execute();
    }
}
