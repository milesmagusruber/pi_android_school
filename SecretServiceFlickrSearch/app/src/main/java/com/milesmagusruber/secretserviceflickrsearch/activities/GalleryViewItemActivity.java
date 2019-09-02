package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.milesmagusruber.secretserviceflickrsearch.R;

import java.io.File;

import static com.milesmagusruber.secretserviceflickrsearch.activities.GalleryActivity.EXTRA_GALLERY_ITEM;

public class GalleryViewItemActivity extends AppCompatActivity {

    private ImageView galleryViewItem;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view_item);
        galleryViewItem = findViewById(R.id.gallery_view_item_imageview);
        photoFile = new File(getIntent().getStringExtra(EXTRA_GALLERY_ITEM));
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getPath());
        galleryViewItem.setImageBitmap(bitmap);
    }
}
