package com.example.student.imageproc;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by student on 4/9/2018.
 */

public class ImageCaptureActivity extends AppCompatActivity {


    CameraBridgeViewBase cameraBridgeViewBase;


    Mat mat1, mat2, mat3;
    ImageView imageView;
    Integer REQUEST_CAMERA=1, SELECT_FILE=0;
    Uri outputfileuri;
    Bitmap backgroundImage;
    Bitmap foregroundImage;
    Bitmap processedImage;
    Bitmap globalmp;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        backgroundImage = null;
        foregroundImage = null;
        processedImage = null;
        setContentView(R.layout.image_capture);
        imageView = (ImageView)findViewById(R.id.camDisplay);

        Button cameraShot = (Button)findViewById(R.id.takePicture);
        cameraShot.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SelectImage();
            }
        });

        Button theProcess = (Button)findViewById(R.id.processimg);
        theProcess.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){ProcessImage();}
        });



    }

    private void SelectImage(){
        Log.d("cam button clicked", "In selectImage");

        final CharSequence[] items = {"Camera", "Saved Images", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ImageCaptureActivity.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                if(items[i].equals("Camera")){
                    //start the camera
                    Intent startCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    //Allow user to name photo here?
                    File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.bmp");
                    outputfileuri = Uri.fromFile(file);

                    startCamera.putExtra(MediaStore.EXTRA_OUTPUT, outputfileuri);
                    startActivityForResult(startCamera, REQUEST_CAMERA);
                    startCamera.putExtra(MediaStore.EXTRA_OUTPUT, outputfileuri);

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
        builder.show();
    }

    public void ProcessImage(){
        Mat background = new Mat();
        Mat foreground = new Mat();
        List<Mat> background_chans = new ArrayList(3);
        List<Mat> foreground_chans = new ArrayList(3);


        //change bitmap images into a mat
        Utils.bitmapToMat(backgroundImage, background);
        Utils.bitmapToMat(foregroundImage, foreground);
        //we use Core::split to divide the channels

        Core.split(background, background_chans);
        Core.split(foreground, foreground_chans);

        //get the color channels - red is [0] in list, green is [1], blue is [2]

        Mat backRed = background_chans.get(0);
        Mat backGreen = background_chans.get(1);
        Mat backBlue = background_chans.get(2);

        Mat foreRed = foreground_chans.get(0);
        Mat foreGreen = foreground_chans.get(1);
        Mat foreBlue = foreground_chans.get(2);

        Mat blurredBackRed = new Mat();
        Mat blurredForeRed = new Mat();
        Mat blurredBackBlue = new Mat();
        Mat blurredForeBlue = new Mat();
        //now we blur the background channels 50px radius gaussian blur

        //need to use default matrix size? or use desired 50 numbers
        //Size backRedSize = backRed.size();

        Size blurKernel = new Size(5, 5);
        //sigma in MATLAB program = 10*4096
        Imgproc.GaussianBlur(backRed, blurredBackRed, blurKernel, 10*4096, 10*4096);
        Imgproc.GaussianBlur(backBlue, blurredBackBlue, blurKernel, 10*4096, 10*4096);

        //now to blur the foreground channels

        Imgproc.GaussianBlur(foreRed, blurredForeRed, blurKernel, 10*4096, 10*4096);
        Imgproc.GaussianBlur(foreBlue, blurredForeBlue, blurKernel, 10*4096, 10*4096);

        //now that we have blurred channels, we get the overall signal
        Mat blue_signal = new Mat();
        Mat red_signal = new Mat();

        Core.absdiff(blurredForeBlue, blurredBackBlue, blue_signal);
        Core.absdiff(blurredForeRed, blurredBackRed, red_signal);


        //threshold numbers in matrix  that aren't enough signal, average the rest


        //display?
        Mat ratioMatrix = new Mat();
        Core.divide(blue_signal, red_signal, ratioMatrix);
        Utils.matToBitmap(ratioMatrix, processedImage);

        imageView.setImageBitmap(processedImage);

        //setting values below a certain number to 0, then averaging everything else:
        //blue_signal.setTo(0, blue_signal < 2)




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){

            if(requestCode == REQUEST_CAMERA){
                 // Bundle bundle =  data.getExtras();
                outputfileuri = data.getData();
                 Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outputfileuri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(bmp);



                /* TO DO NEXT

                to do

                //need to make bitmap into bmp32, to to another screen and process?
                //use Core.split for splitting mat

                */

            }else if(requestCode == SELECT_FILE){
                final Uri selectedImageURI = data.getData();
                imageView.setImageURI(selectedImageURI);
                try {
                    globalmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageURI);
                    if(globalmp == null){
                        Log.d("null verify", "globalmp is null still");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //imageView.buildDrawingCache(true);
                //bmp = imageView.getDrawingCache(true);


                //////////////////////////////////////////////////////////////////////////////////
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Choose option");
                builder.setMessage("Make taken image foreground or background?");

                builder.setPositiveButton("Foreground", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        imageView.buildDrawingCache(true);
                        foregroundImage = globalmp;

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Background", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        imageView.buildDrawingCache(true);
                        foregroundImage = globalmp;
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                //////////////////////////////////////////////////////////////////////////////////





            }
        }

    }




    /*
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.takePicture:
                //stuff
                //Imgcodecs.imwrite()
                //use another function for when image taken
                //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(intent, 0);
                Log.d("cam button clicked", "Camera Button Click detected");

                //we're here
                SelectImage();
                break;
        }
    }
    */



}
