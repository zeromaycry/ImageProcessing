package com.example.student.imageproc;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.Utils;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * Created by student on 4/9/2018.
 */

public class ImageCaptureActivity extends AppCompatActivity implements View.OnClickListener{


    CameraBridgeViewBase cameraBridgeViewBase;
    private JavaCameraView myJCamView;


    Mat mat1, mat2, mat3;
    ImageView imageView;
    Integer REQUEST_CAMERA=1, SELECT_FILE=0;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_capture);

        Button cameraShot = (Button)findViewById(R.id.takePicture);
        cameraShot.setOnClickListener(this);



    }

    private void SelectImage(){
        final CharSequence[] items = {"Camera", "Saved Images", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ImageCaptureActivity.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                if(items[i].equals("Camera")){
                    //start the camera
                    Intent startCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(startCamera, REQUEST_CAMERA);
                }else if(items[i].equals("Saved Images")){
                    //check out image gallery
                    Intent savedImages = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    savedImages.setType("image/*");
                    startActivityForResult(savedImages.createChooser(savedImages, "Select File"), SELECT_FILE);
                }else if(items[i].equals("Cancel")){
                    dialogInterface.dismiss();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){

            if(requestCode == REQUEST_CAMERA){
                Bundle bundle =  data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data");
                imageView.setImageBitmap(bmp);

                /* TO DO NEXT

                to do
                
                //need to make bitmap into bmp32, to to another screen and process?
                //use Core.split for splitting mat

                */

            }else if(requestCode == SELECT_FILE){
                Uri selectedImageURI = data.getData();
                imageView.setImageURI(selectedImageURI);
            }
        }

    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.takePicture:
                //stuff
                //Imgcodecs.imwrite()
                //use another function for when image taken
                //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(intent, 0);
                SelectImage();
                break;
        }
    }



}
