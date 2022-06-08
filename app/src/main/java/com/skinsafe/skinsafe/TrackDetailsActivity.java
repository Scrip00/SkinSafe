package com.skinsafe.skinsafe;

import static com.skinsafe.skinsafe.MainMenu.ScanFragment.PICK_IMAGE;
import static com.skinsafe.skinsafe.MainMenu.ScanFragment.REQUEST_IMAGE_CAPTURE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.skinsafe.skinsafe.Adapters.TrackSpinnerAdapter;
import com.skinsafe.skinsafe.Database.TrackDaoClass;
import com.skinsafe.skinsafe.Database.TrackDatabaseClass;
import com.skinsafe.skinsafe.Database.TrackModel;
import com.skinsafe.skinsafe.MainMenu.MainActivity;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackDetailsActivity extends AppCompatActivity {
    private Spinner spinner;
    private Button takeNewBtn, uploadNewBtn, saveChangesBtn, trackListBtn;
    private EditText editTextName;
    private ImageView photoImage;
    private TextView probabilityTextView, comparisonTextView, progressTextView;
    private int id;
    private String currentPhotoPath;
    private CardView cardView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_details);

        spinner = findViewById(R.id.items_spinner);
        takeNewBtn = findViewById(R.id.takeNewBtn);
        uploadNewBtn = findViewById(R.id.uploadNewBtn);
        saveChangesBtn = findViewById(R.id.saveChangesBtn);
        trackListBtn = findViewById(R.id.trackListBtn);
        editTextName = findViewById(R.id.editTextName);
        photoImage = findViewById(R.id.photoImage);
        probabilityTextView = findViewById(R.id.probabilityTextView);
        comparisonTextView = findViewById(R.id.comparisonTextView);
        progressTextView = findViewById(R.id.progressTextView);
        cardView = findViewById(R.id.CardView);
        loadAd(this);
        setupUI();
    }

    public static void loadAd(Activity activity) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
        int numAds = sharedPrefs.getInt("Ads", 0);
        int maxAds = 7;
        if (numAds >= maxAds) {
            if (!isNetworkAvailable(activity)) {
                Toast.makeText(activity.getBaseContext(), "Please, turn on wifi to see the results", Toast.LENGTH_SHORT).show();
                activity.finish();
            } else {
                MobileAds.initialize(activity.getBaseContext(), new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                    }
                });
                AdRequest adRequest = new AdRequest.Builder().build();

                InterstitialAd.load(activity.getBaseContext(), "", adRequest,
                        new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                // The mInterstitialAd reference will be null until
                                // an ad is loaded.
                                interstitialAd.show(activity);
                                SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();
                                sharedPrefsEditor.putInt("Ads", 0);
                                sharedPrefsEditor.apply();
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                // Handle the error
                                Log.d("Failed", String.valueOf(loadAdError));
                            }
                        });
            }
        } else {
            SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();
            sharedPrefsEditor.putInt("Ads", numAds + 1);
            sharedPrefsEditor.apply();
        }
    }

    private void setupUI() {
        ArrayList<String> list = new ArrayList<>(Arrays.asList("Anterior torso", "Head/neck", "Lateral torso", "Lower extremity", "Oral/genital", "Palms/soles", "Posterior torso", "Upper extremity"));
        spinner.setAdapter(new TrackSpinnerAdapter(this, R.layout.custom_spinner, list));

        if (getIntent().getExtras() == null) {
            id = -1;
        } else {
            id = Integer.parseInt(getIntent().getExtras().get("ID").toString());
        }
        if (id == -1) {
            probabilityTextView.setText("You haven't created a new track yet");
            comparisonTextView.setText("To do this, scan new formation by pressing the buttons below");
            progressTextView.setText("Do not forget to enter track name and place on your body");
            saveChangesBtn.setVisibility(View.INVISIBLE);
            trackListBtn.setVisibility(View.INVISIBLE);
            cardView.setVisibility(View.INVISIBLE);
        } else {
            TrackModel model;
            if (!getIntent().getBooleanExtra("list", false)) id = findTale(id);
            model = TrackDatabaseClass.getDatabase(this).getDao().loadSingle(id);
            photoImage.setImageBitmap(model.getImage());
            editTextName.setText(model.getName());
            switch (model.getPlace()) {
                case "Anterior torso":
                    spinner.setSelection(0);
                    break;
                case "Head/neck":
                    spinner.setSelection(1);
                    break;
                case "Lateral torso":
                    spinner.setSelection(2);
                    break;
                case "Lower extremity":
                    spinner.setSelection(3);
                    break;
                case "Oral/genital":
                    spinner.setSelection(4);
                    break;
                case "Palms/soles":
                    spinner.setSelection(5);
                    break;
                case "Posterior torso":
                    spinner.setSelection(6);
                    break;
                case "Upper extremity":
                    spinner.setSelection(7);
                    break;
            }
            probabilityTextView.setText(generateProbabilityText());
            comparisonTextView.setText(generateComparisonText("comparison"));
            progressTextView.setText(generateComparisonText("progress"));
        }

        takeNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkCorrectness()) dispatchTakePictureIntent();
            }
        });

        uploadNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkCorrectness()) requestPermissionForGallery(getBaseContext());
            }
        });

        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int copyId = getHead(id);
                TrackModel model = TrackDatabaseClass.getDatabase(getApplicationContext()).getDao().loadSingle(copyId);
                String spinnerData = (String) spinner.getSelectedItem();
                String textFieldData = String.valueOf(editTextName.getText());
                while (model.getNext() != -1) {
                    model = TrackDatabaseClass.getDatabase(getApplicationContext()).getDao().loadSingle(model.getNext());
                    TrackDatabaseClass.getDatabase(getApplicationContext()).getDao().updateData(spinnerData, textFieldData, copyId);
                    copyId = model.getKey();
                }
                TrackDatabaseClass.getDatabase(getApplicationContext()).getDao().updateData(spinnerData, textFieldData, copyId);
            }
        });

        trackListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TrackListActivity.class);
                intent.putExtra("ID", id);
                startActivity(intent);
            }
        });

        probabilityTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id > 0) {
                    TrackModel model = TrackDatabaseClass.getDatabase(getApplicationContext()).getDao().loadSingle(id);
                    Intent intent = new Intent(getBaseContext(), ResultActivity.class);
                    intent.putExtra("output", model.getResults());
                    intent.putExtra("imageBitmap", model.getImage());
                    intent.putExtra("saveOrNot", false);
                    startActivity(intent);
                }
            }
        });
    }

    private static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void requestPermissionForGallery(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 &&
                    grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission is needed to continue", Toast.LENGTH_SHORT).show();
            } else {
                openGallery();
            }
        }
    }

    private String generateComparisonText(String sw) {
        String str = "";
        TrackModel model = TrackDatabaseClass.getDatabase(this).getDao().loadSingle(id);
        if (model.isHead()) {
            str += "Progress cannot be calculated yet";
        } else {
            float[] outputOrig = model.getResults();
            float[] output;
            if (sw.equals("comparison")) {
                str += "Since previous scan ";
                output = TrackDatabaseClass.getDatabase(this).getDao().findParent(id).getResults();
            } else if (sw.equals("progress")) {
                str += "Since very first scan ";
                output = TrackDatabaseClass.getDatabase(this).getDao().loadSingle(getHead(id)).getResults();
            } else return "Not supported yet";
            Map<Float, String> digMap = new HashMap<>();
            Map<Float, String> digMapS = new HashMap<>();
            digMapS.put(outputOrig[0] - output[0], "Actinic Keratosis");
            digMapS.put(outputOrig[1] - output[1], "Basal Cell Carcinoma");
            digMapS.put(outputOrig[2] - output[2], "Melanoma");
            digMapS.put(outputOrig[3] - output[3], "Nevus");
            digMapS.put(outputOrig[4] - output[4], "Seborrheic Keratosis");
            digMapS.put(outputOrig[5] - output[5], "Squamous Cell Carcinoma");
            String outputMax, outputOrigMax;
            digMap.put(output[0], "Actinic Keratosis");
            digMap.put(output[1], "Basal Cell Carcinoma");
            digMap.put(output[2], "Melanoma");
            digMap.put(output[3], "Nevus");
            digMap.put(output[4], "Seborrheic Keratosis");
            digMap.put(output[5], "Squamous Cell Carcinoma");
            List<Float> list = new ArrayList<>();
            list.addAll(digMap.keySet());
            Collections.sort(list);
            outputMax = digMap.get(list.get(5));
            digMap.clear();
            list.clear();
            digMap.put(outputOrig[0], "Actinic Keratosis");
            digMap.put(outputOrig[1], "Basal Cell Carcinoma");
            digMap.put(outputOrig[2], "Melanoma");
            digMap.put(outputOrig[3], "Nevus");
            digMap.put(outputOrig[4], "Seborrheic Keratosis");
            digMap.put(outputOrig[5], "Squamous Cell Carcinoma");
            list.addAll(digMap.keySet());
            Collections.sort(list);
            outputOrigMax = digMap.get(list.get(5));
            DecimalFormat df = new DecimalFormat("#.##");
            if (outputMax.equals(outputOrigMax)) {
                for (Float key : digMapS.keySet()) {
                    if (digMapS.get(key).equals(outputMax)) {
                        if (key >= 0) {
                            str += "The " + digMapS.get(key) + " became worse by " + df.format(key * 100) + "%";
                        } else {
                            str += "The " + digMapS.get(key) + " became better by " + df.format(-key * 100) + "%";
                        }
                        break;
                    }
                }
            } else {
                for (Float key : digMapS.keySet()) {
                    if (digMapS.get(key).equals(outputMax)) {
                        if (key >= 0) {
                            str += "the " + digMapS.get(key) + " became worse by " + df.format(key * 100) + "%";
                        } else {
                            str += "the " + digMapS.get(key) + " became better by " + df.format(-key * 100) + "%";
                        }
                        break;
                    }
                }
                str += ", but ";
                for (Float key : digMapS.keySet()) {
                    if (digMapS.get(key).equals(outputOrigMax)) {
                        if (key >= 0) {
                            str += "the " + digMapS.get(key) + " became worse by " + df.format(key * 100) + "%";
                        } else {
                            str += "the " + digMapS.get(key) + " became better by " + df.format(-key * 100) + "%";
                        }
                        break;
                    }
                }

            }
        }
        return str;
    }

    private String generateProbabilityText() {
        TrackModel model = TrackDatabaseClass.getDatabase(this).getDao().loadSingle(id);
        float[] output = model.getResults();
        String str = "";
        Map<Float, String> digMap = new HashMap<>();
        digMap.put(output[0], "Actinic Keratosis");
        digMap.put(output[1], "Basal Cell Carcinoma");
        digMap.put(output[2], "Melanoma");
        digMap.put(output[3], "Nevus");
        digMap.put(output[4], "Seborrheic Keratosis");
        digMap.put(output[5], "Squamous Cell Carcinoma");
        List<Float> list = new ArrayList<>();
        list.addAll(digMap.keySet());
        Collections.sort(list);
        DecimalFormat df = new DecimalFormat("#.##");
        str += "Probably it's: " + digMap.get(list.get(5)) + "\nThe chance is: " + df.format(list.get(5) * 100) + "%";
        if (model.isHead()) str += "\nThis is your very first scan";
        str += "\nClick here to see details";
        return str;
    }

    private Boolean checkCorrectness() {
        if (String.valueOf(editTextName.getText()).equals("")) {
            Toast.makeText(this, "Name should not be empty and place must be selected", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.skinsafe.skinsafe.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            FileUtils utils = new FileUtils(getBaseContext());
            currentPhotoPath = utils.getPath(data.getData());
            Intent intent = new Intent(this, ChooseResultActivity.class);
            intent.putExtra("filePath", currentPhotoPath);
            intent.putExtra("place", (String) spinner.getSelectedItem());
            intent.putExtra("name", String.valueOf(editTextName.getText()));
            intent.putExtra("track", findTale(id));
            startActivity(intent);
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, ChooseResultActivity.class);
            intent.putExtra("filePath", currentPhotoPath);
            intent.putExtra("place", (String) spinner.getSelectedItem());
            intent.putExtra("name", String.valueOf(editTextName.getText()));
            intent.putExtra("track", findTale(id));
            startActivity(intent);
        }
    }

    private int findTale(int id) {
        if (id == -1) {
            return -1;
        }
        TrackModel model = TrackDatabaseClass.getDatabase(this).getDao().loadSingle(id);
        while (model.getNext() != -1) {
            model = TrackDatabaseClass.getDatabase(this).getDao().loadSingle(model.getNext());
        }
        return model.getKey();
    }

    private int getHead(int id) {
        TrackDaoClass dao = TrackDatabaseClass.getDatabase(this).getDao();
        TrackModel model = dao.loadSingle(id);
        while (!model.isHead()) {
            model = dao.findParent(model.getKey());
        }
        return model.getKey();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("selectedFragment", "track");
        ActivityOptions options =
                ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_right);
        startActivity(intent, options.toBundle());
    }
}