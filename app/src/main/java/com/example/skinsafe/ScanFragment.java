package com.example.skinsafe;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ScanFragment extends Fragment {
    private Button takePicBtn, uploadFromFileBtn;
    public static final int PICK_IMAGE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    private View rootView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_scan, container, false);
        takePicBtn = rootView.findViewById(R.id.takePicBtn);
        uploadFromFileBtn = rootView.findViewById(R.id.uploadFromFileBtn);
        takePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        uploadFromFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        return rootView;
    }




    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }


    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
            }
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                newCalcNN(imageBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //imageView.setImageBitmap(imageBitmap);
            try {
                newCalcNN(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("SkinSafe", "LLOOOOL");
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void newCalcNN(Bitmap imgBitmap) throws IOException {
        int width = 200, height = 200;
        Interpreter tflite;
        //Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open("ISIC_0000142.jpg"));
        Bitmap bitmap = imgBitmap.copy(imgBitmap.getConfig(), true);
        bitmap = cropCenter(bitmap);
        bitmap = getResizedBitmap(bitmap, width, height);
        float[][][][] newinp = bitmapToInputArray(bitmap);
        //['actinic keratosis', 'basal cell carcinoma', 'melanoma', 'nevus', 'seborrheic keratosis', 'squamous cell carcinoma']
        float[][] output=new float[1][6];
        try {
            tflite = new Interpreter(loadModelFile());
            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
            tensorImage.load(bitmap);
            tflite.run(newinp, output);
            //textView.setText(String.format("%.3g", output[0][0]) + "  " + String.format("%.3g", output[0][1]) + "  " + String.format("%.3g", output[0][2]) + "  " + String.format("%.3g", output[0][3]) + "  " + String.format("%.3g", output[0][4]) + "  " + String.format("%.3g", output[0][5]));
            openResultActivity(output, bitmap);
        } catch (Exception ex){
            ex.printStackTrace();
            Log.d("SkinSafe", "DDDDD");
        }
    }

    private void openResultActivity(float[][] output, Bitmap imageBitmap) {
        float[] out = new float[6];
        for (int i = 0; i < 6; i++){
            out[i] = output[0][i];
        }
        Intent intent = new Intent(getActivity(), ResultActivity.class);
        intent.putExtra("output", out);
        intent.putExtra("imageBitmap", imageBitmap);
        startActivity(intent);
    }

    private float[][][][] bitmapToInputArray(Bitmap oldbitmap) {
        Bitmap bitmap= oldbitmap;
        bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
        int batchNum = 0;
        float[][][][] input = new float[1][200][200][3];
        for (int x = 0; x < 200; x++) {
            for (int y = 0; y < 200; y++) {
                int pixel = bitmap.getPixel(x, y);
                input[batchNum][x][y][0] = (Color.red(pixel) - 127) / 128.0f;
                input[batchNum][x][y][1] = (Color.green(pixel) - 127) / 128.0f;
                input[batchNum][x][y][2] = (Color.blue(pixel) - 127) / 128.0f;

            }
        }
        return input;
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getActivity().getAssets().openFd("degree.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startOffset=fileDescriptor.getStartOffset();
        long declareLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declareLength);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public static Bitmap cropCenter(Bitmap img) {
        Bitmap retImg = img.copy(img.getConfig(), true);
        if (retImg.getHeight() > retImg.getWidth()){
            retImg = Bitmap.createBitmap(retImg, 0, (retImg.getHeight() - retImg.getWidth()) / 2, retImg.getWidth(), retImg.getWidth());
        } else if (retImg.getHeight() < retImg.getWidth()){
            retImg = Bitmap.createBitmap(retImg,(retImg.getWidth() - retImg.getHeight()) / 2, 0, retImg.getHeight(), retImg.getHeight());
        }
        return retImg;
    }

}