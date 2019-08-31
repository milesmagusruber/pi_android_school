package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.milesmagusruber.secretserviceflickrsearch.BuildConfig;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

public class GalleryActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 30;
    static final int REQUEST_EXTERNAL_STORAGE = 42;

    //Layout elements
    private Button buttonTakeAPhoto;
    private RecyclerView rvGallery;
    private ImageView imageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        buttonTakeAPhoto = findViewById(R.id.button_take_a_photo);
        //rvGallery = findViewById(R.id.rv_gallery);
        imageView = findViewById(R.id.image_view);

        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, REQUEST_EXTERNAL_STORAGE);
        buttonTakeAPhoto.setClickable(false);

        buttonTakeAPhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                openCamera();

            }
        });

    }

    //...
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //If we get photo from camera
            Uri uri = Uri.parse(currentPhotoPath);
            openCropActivity(uri, uri);
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            //If we get our photo cropped with UCrop library
            Uri uri = UCrop.getOutput(data);
            showImage(uri);
        }
    }

    private void openCamera() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getImageFile(); // 1
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) // 2
            uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.concat(".fileprovider"), file);
        else
            uri = Uri.fromFile(file); // 3
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri); // 4
        startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    String currentPhotoPath = "";

    private File getImageFile(){
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        try {
            File file = File.createTempFile(
                    imageFileName, ".jpg", storageDir
            );
            currentPhotoPath = "file:" + file.getAbsolutePath();
            return file;
        }catch (IOException e){
            return null;
        }
    }

    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        int maxWidth=1600;
        int maxHeight=1600;
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(maxWidth, maxHeight)
                .withAspectRatio(5f, 5f)
                .start(this);
    }

    private void showImage(Uri imageUri) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath(), options);
            imageView.setImageBitmap(bitmap);

        } catch (Exception e) {
            Toast.makeText(this,"Having problems with showing image",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2]==PackageManager.PERMISSION_GRANTED) {
                    buttonTakeAPhoto.setClickable(true);
                }else{
                    Toast.makeText(this,"Having problems with permission requests!!",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
