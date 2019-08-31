package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.milesmagusruber.secretserviceflickrsearch.BuildConfig;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.fs.FileHelper;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 30;
    static final int REQUEST_CAMERA_AND_STORAGE = 42;

    //Layout elements
    private Button buttonTakeAPhoto;
    private RecyclerView rvGallery;
    private ImageView imageView;

    //Permissions
    private String[] permissions;

    //FileHelper
    private FileHelper fileHelper;
    //PhotoPath
    private String currentPhotoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        buttonTakeAPhoto = findViewById(R.id.button_take_a_photo);
        //rvGallery = findViewById(R.id.rv_gallery);
        imageView = findViewById(R.id.image_view);
        //file helper
        fileHelper=FileHelper.getInstance();
        fileHelper.checkLogin();
        //permissions
        permissions=new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
        //checkingGalleryPermissions
        if(checkGalleryPermissions()){
            activateCameraButton();
        }else{
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CAMERA_AND_STORAGE);
        }
    }

    /*if we get result from camera go to uCrop activity
    * if we get result from uCrop activity show image in imageView*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_IMAGE_CAPTURE){
            Log.d("FILETT",Integer.toString(resultCode));
        }
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //If we get photo from camera
            Log.d("FILETT","Camera returns image");
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
            File file = fileHelper.createUserPhotoFile(this);

            currentPhotoPath = "file:" + file.getAbsolutePath();
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.concat(".fileprovider"), file);
            else
                uri = Uri.fromFile(file);
            Log.d("FILETT",uri.toString());
            pictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);
        }catch (NullPointerException e){
            Toast.makeText(this,R.string.problem_with_filesystem,Toast.LENGTH_LONG).show();
        }
    }


    //this method is used to process photo with UCrop library
    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        int maxWidth=1600;
        int maxHeight=1600;
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(maxWidth, maxHeight)
                .withAspectRatio(5f, 5f)
                .start(this);
    }

    //This method shows image in imageView
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

    //If we have permissions for camera and storage camera button will work
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_AND_STORAGE: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2]==PackageManager.PERMISSION_GRANTED) {
                    activateCameraButton();
                }else{
                    Toast.makeText(this,"Having problems with permission requests!!",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //checks gallery permissions
    public boolean checkGalleryPermissions(){
        boolean result = true;
        for(String perm: permissions){
            result = result && (ContextCompat.checkSelfPermission(this,perm) == PackageManager.PERMISSION_GRANTED);
        }
        return result;
    }

    //activate camera button
    public void activateCameraButton(){
        buttonTakeAPhoto.setClickable(true);
        buttonTakeAPhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                takeAPhoto();
            }
        });
    }



}
