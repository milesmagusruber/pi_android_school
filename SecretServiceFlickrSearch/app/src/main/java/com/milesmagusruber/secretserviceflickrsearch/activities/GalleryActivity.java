package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.milesmagusruber.secretserviceflickrsearch.BuildConfig;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.adapters.PhotoFilesAdapter;
import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;
import com.milesmagusruber.secretserviceflickrsearch.fs.FileHelper;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 30;
    static final int REQUEST_CAMERA_AND_STORAGE = 42;
    public static final String EXTRA_GALLERY_ITEM = BuildConfig.APPLICATION_ID + ".extra.gallery.item";

    //Layout elements
    private MaterialButton buttonTakeAPhoto;
    private RecyclerView rvGallery;


    private ItemTouchHelper itemTouchHelper; //For touch swipes

    //Permissions
    private String[] permissions;

    //FileHelper
    private FileHelper fileHelper;
    //PhotoPath
    private String currentPhotoPath = "";

    //photo files adapter
    private PhotoFilesAdapter photoFilesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        buttonTakeAPhoto = findViewById(R.id.button_take_a_photo);
        rvGallery = findViewById(R.id.rv_gallery);

        //Initialize itemTouchHelper
        itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        int position = viewHolder.getAdapterPosition();
                        removePhotoFile(position);
                    }
                });


        //permissions
        permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        //checkingGalleryPermissions
        if (checkGalleryPermissions()) {
            initializeCameraAndStorageFunctionaly();
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CAMERA_AND_STORAGE);
        }
    }

    /*if we get result from camera go to uCrop activity
     * if we get result from uCrop activity show image in imageView*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Log.d("FILETT", Integer.toString(resultCode));
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //If we get photo from camera
            Log.d("FILETT", "Camera returns image");
            Uri uri = Uri.parse(currentPhotoPath);
            openCropActivity(uri, uri);
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            //If we get our photo cropped with UCrop library
            Uri uri = UCrop.getOutput(data);
            showImage(uri);
        }
    }

    //This method is used to take a photo via camera
    private void takeAPhoto() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File file = fileHelper.createUserPhotoFile();

            currentPhotoPath = "file:" + file.getAbsolutePath();
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.concat(".fileprovider"), file);
            else
                uri = Uri.fromFile(file);
            Log.d("FILETT", uri.toString());
            pictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (NullPointerException e) {
            Toast.makeText(this, R.string.problem_with_filesystem, Toast.LENGTH_LONG).show();
        }
    }


    //this method is used to process photo with UCrop library
    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        int maxWidth = 1600;
        int maxHeight = 1600;
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(maxWidth, maxHeight)
                .withAspectRatio(5f, 5f)
                .start(this);
    }

    //This method shows image in imageView
    private void showImage(Uri imageUri) {
        try {
            photoFilesAdapter.addNewPhotoFile(new File(imageUri.getPath()));

        } catch (Exception e) {
            Toast.makeText(this, "Having problems with showing image", Toast.LENGTH_LONG).show();
        }
    }

    //If we have permissions for camera and storage camera button will work
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_AND_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    initializeCameraAndStorageFunctionaly();
                } else {
                    Toast.makeText(this, "Having problems with permission requests!!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //checks gallery permissions
    private boolean checkGalleryPermissions() {
        boolean result = true;
        for (String perm : permissions) {
            result = result && (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED);
        }
        return result;
    }

    //activate camera button
    private void activateCameraButton() {
        buttonTakeAPhoto.setClickable(true);
        buttonTakeAPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeAPhoto();
            }
        });
    }

    //shows photos in recycler view
    private void showPhotoFiles(ArrayList<File> photoFiles) {
        photoFilesAdapter = new PhotoFilesAdapter(photoFiles, new PhotoFilesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(File photoFile) {
                //go to GalleryViewItemActivity
                Intent intent = new Intent(GalleryActivity.this, GalleryViewItemActivity.class);
                intent.putExtra(EXTRA_GALLERY_ITEM, photoFile.getAbsolutePath());
                startActivity(intent);
            }

        });
        // Attach the adapter to the recyclerview to populate items
        rvGallery.setAdapter(photoFilesAdapter);

        // Set layout manager
        rvGallery.setLayoutManager(new LinearLayoutManager(this));

        // Add the functionality to swipe items in the
        // recycler view to delete that item
        itemTouchHelper.attachToRecyclerView(rvGallery);

        rvGallery.setVisibility(View.VISIBLE);
    }

    //removes file from recycler view
    private void removePhotoFile(int position) {
        if (fileHelper.deletePhotoFile(photoFilesAdapter.getPhotoFileAtPosition(position))) {
            photoFilesAdapter.removePhotoFile(position);
        } else {
            Toast.makeText(this, R.string.file_not_removed, Toast.LENGTH_LONG).show();
        }
    }

    private void initializeCameraAndStorageFunctionaly() {
        activateCameraButton();
        //file helper
        CurrentUser.getInstance().setFileHelper(this);
        fileHelper = CurrentUser.getInstance().getFileHelper();
        ArrayList<File> photoFiles = new ArrayList<File>();
        photoFiles.addAll(fileHelper.getAllUserPhotos());
        photoFiles.addAll(fileHelper.getAllFlickrPhotos());
        showPhotoFiles(photoFiles);
    }


}
