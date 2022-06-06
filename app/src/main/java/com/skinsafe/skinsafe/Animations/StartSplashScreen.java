package com.skinsafe.skinsafe.Animations;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.skinsafe.skinsafe.MainMenu.MainActivity;
import com.skinsafe.skinsafe.R;

@SuppressLint("CustomSplashScreen")
public class StartSplashScreen extends Activity {
    @Override
    @SuppressLint("UseCompatLoadingForDrawables")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_start_splash);

        // Start the animation
        AnimatedVectorDrawable d = (AnimatedVectorDrawable) getDrawable(R.drawable.start_splash_anim);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageDrawable(d);
        d.start();

        int secondsDelayed = 2;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                // Animation length is 2 seconds, so start new intent after the elapsed time
                startActivity(new Intent(StartSplashScreen.this, MainActivity.class));
                overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
                finish();
            }
        }, secondsDelayed * 1000);
    }
}