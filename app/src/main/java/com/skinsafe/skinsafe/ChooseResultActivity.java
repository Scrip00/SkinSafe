package com.skinsafe.skinsafe;

import static com.skinsafe.skinsafe.MainMenu.ScanFragment.bitmapToInputArray;
import static com.skinsafe.skinsafe.MainMenu.ScanFragment.cropCenter;
import static com.skinsafe.skinsafe.MainMenu.ScanFragment.getResizedBitmap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.theartofdev.edmodo.cropper.CropImageView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;


public class ChooseResultActivity extends AppCompatActivity {
    ImageView detectionResult;
    List<RectF> coordinates;
    double scale;
    boolean isOk;
    Bitmap initialImage;
    List<Detection> detectionResultsSaved;
    int screenWidth, screenHeight, from, track;
    Button selectBtn, cropBtn;
    RectF savedBox;
    CropImageView cropImageView;
    String trackPlace, trackName;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_result);

        detectionResult = findViewById(R.id.detectionResult);
        selectBtn = findViewById(R.id.selectBtn);
        cropBtn = findViewById(R.id.cropBtn);
        cropImageView = findViewById(R.id.cropImageView);
        cropImageView.setVisibility(View.INVISIBLE);
        cropImageView.setAspectRatio(1, 1);

        coordinates = new ArrayList<>();
        Intent intent = getIntent();
        String currentPhotoPath = (String) intent.getExtras().get("filePath");
        if (intent.getIntExtra("track", -2) != -2) {
            track = intent.getIntExtra("track", -2);
            trackPlace = (String) intent.getExtras().get("place");
            trackName = (String) intent.getExtras().get("name");
        } else {
            track = -2;
        }
        Log.d("LMAO", String.valueOf(track));
        Bitmap bitMap = BitmapFactory.decodeFile(currentPhotoPath);
        if (intent.getBooleanExtra("rotate", true)) {
            bitMap = rotateImage(bitMap, 90f);
        }
        try {
            runObjectDetection(bitMap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cropBtn.getText().equals("Crop")) {
                    if (savedBox != null) {
                        Bitmap resizedBmp;
                        int x = 0, y = 0, width = 0;
                        if (savedBox.right - savedBox.left > savedBox.bottom - savedBox.top) {
                            x = (int) savedBox.left;
                            y = (int) savedBox.top;
                            width = (int) (savedBox.right - savedBox.left);
                        } else {
                            int horWodrth = (int)(savedBox.right - savedBox.left);
                            width = (int)(savedBox.bottom - savedBox.top);
                            x = (int) savedBox.left - (width - horWodrth) / 2;
                            y = (int) savedBox.top;
                        }
                        double scaleInt = 0.15;
                        x = (int) (x - width * scaleInt);
                        y = (int) (y - width * scaleInt);
                        width = (int) (width * (1 + scaleInt * 2));
                        if (x < 0) x = 0;
                        if (y < 0) y = 0;
                        // If box is too big !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        // По-моему хуйня
                        if (x + width > initialImage.getWidth()) x = initialImage.getWidth() - width;
                        if (y + width > initialImage.getHeight()) y = initialImage.getHeight() - width;
                        if (x + width > initialImage.getWidth()) width = initialImage.getWidth();
                        if (y + width > initialImage.getHeight()) width = initialImage.getHeight();
                        resizedBmp = Bitmap.createBitmap(initialImage, x, y, width, width);
                        try {
                            if (x + width > initialImage.getWidth() || y + width > initialImage.getHeight()) { // Скорее всего нахуй не нужно
                                Toast.makeText(getApplicationContext(), "Please, try again or crop by hand", Toast.LENGTH_SHORT);
                            } else {
                                newCalcNN(resizedBmp);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Choose smth or crop by hand", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Bitmap resizedBmp = cropImageView.getCroppedImage();
                    try {
                        newCalcNN(resizedBmp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        cropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cropBtn.getText().equals("Crop")) {
                    detectionResult.setVisibility(View.INVISIBLE);
                    cropImageView.setVisibility(View.VISIBLE);
                    cropBtn.setText("Back to results");
                } else {
                    detectionResult.setVisibility(View.VISIBLE);
                    cropImageView.setVisibility(View.INVISIBLE);
                    cropBtn.setText("Crop");
                    cropImageView.resetCropRect();
                }
            }
        });

        detectionResult.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        int imageX = (int) motionEvent.getX();
                        int imageY = (int) motionEvent.getY();
                        RectF box = checkIfInside(coordinates, imageX, imageY);
                        savedBox = box;
                        Bitmap imgWithResult = drawDetectionResult(initialImage, detectionResultsSaved, box);
                        detectionResult.setImageBitmap(imgWithResult);
                        break;
                }
                return true;
            }

        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        screenWidth = detectionResult.getWidth();
        screenHeight = detectionResult.getHeight();
        int width = screenWidth;
        int height = screenHeight;
        if ((double) width / height >= (double) initialImage.getWidth() / initialImage.getHeight()) {
            scale = (double)  height / initialImage.getHeight();
            isOk = false;
            from = (int) ((width - (double) initialImage.getWidth() * scale) / 2);
        } else {
            scale = (double)  width / initialImage.getWidth();
            isOk = true;
            from = (int) ((height - (double) initialImage.getHeight() * scale) / 2);
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void newCalcNN(Bitmap imgBitmap) throws IOException {
        int width = 200, height = 200;
        Interpreter tflite;
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
            openResultActivity(output, bitmap);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void openResultActivity(float[][] output, Bitmap imageBitmap) {
        float[] out = new float[6];
        for (int i = 0; i < 6; i++){
            out[i] = output[0][i];
        }
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("output", out);
        intent.putExtra("imageBitmap", imageBitmap);
        intent.putExtra("track", track);
        intent.putExtra("place", trackPlace);
        intent.putExtra("name", trackName);
        startActivity(intent);
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd("convolutional_NN.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startOffset=fileDescriptor.getStartOffset();
        long declareLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declareLength);
    }

    private RectF checkIfInside(List<RectF> coordinates, int x, int y) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = screenWidth;
        int height = screenHeight;
        if (isOk) {
            y -= from;
        } else {
            x -= from;
        }
        for (RectF box : coordinates) {
            int scaleFactor = (int) (width / (height / (box.bottom - box.top)));
            if (height / (box.bottom - box.top) > 20 || width / (box.right - box.left) > 10) {
                if (x >= box.left * scale - scaleFactor && x <= box.right * scale + scaleFactor && y <= box.bottom * scale + scaleFactor && y >= box.top * scale - scaleFactor) {
                    return box;
                }
            } else {
                if (x >= box.left * scale && x <= box.right * scale && y <= box.bottom * scale && y >= box.top * scale) {
                    return box;
                }
            }
        }
        return null;
    }

    private Bitmap rotateImage(Bitmap source, Float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(
                source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true
        );
    }

    private void runObjectDetection(Bitmap bitmap) throws IOException {
        initialImage = bitmap;
        TensorImage image = TensorImage.fromBitmap(bitmap);

        ObjectDetector.ObjectDetectorOptions options = ObjectDetector.ObjectDetectorOptions.builder()
                .setMaxResults(100)
                .setScoreThreshold(0.2f)
                .build();
        ObjectDetector detector = ObjectDetector.createFromFileAndOptions(
                    this,
                "detection_NN.tflite",
                    options
            );

        List<Detection> results = detector.detect(image);
        detectionResultsSaved = results;
        Bitmap imgWithResult = drawDetectionResult(bitmap, results, null);
        detectionResult.setImageBitmap(imgWithResult);
        cropImageView.setImageBitmap(initialImage);
    }

    private Bitmap drawDetectionResult(Bitmap bitmap, List<Detection> detectionResults, RectF selected) {
        Bitmap outputBitmap =bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(outputBitmap);
        Paint pen = new Paint();
        if (!coordinates.isEmpty()) coordinates.clear();
        for (Detection detection: detectionResults) {
            if (selected != null && selected.equals(detection.getBoundingBox())) {
                pen.setColor(Color.RED);
                pen.setStrokeWidth(8F);
                pen.setStyle(Paint.Style.STROKE);
                RectF box = detection.getBoundingBox();
                canvas.drawRect(box, pen);
                coordinates.add(box);
            } else {
                pen.setColor(Color.WHITE);
                pen.setStrokeWidth(8F);
                pen.setStyle(Paint.Style.STROKE);
                RectF box = detection.getBoundingBox();
                canvas.drawRect(box, pen);
                coordinates.add(box);
            }
        }
        return outputBitmap;
    }

}