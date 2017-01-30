package com.example.mgalante.marvelprojectbase.views;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mgalante.marvelprojectbase.R;
import com.example.mgalante.marvelprojectbase.views.main.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.example.mgalante.marvelprojectbase.utils.Constants.LOGTAG;

public class SplashScreen extends AppCompatActivity {

    private Thread splashTread;
    @Bind(R.id.splash)
    TextView mSplash;
    @Bind(R.id.lin_lay)
    RelativeLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        StartAnimations();
    }

    private void StartAnimations() {


        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        mLinearLayout.clearAnimation();
        mLinearLayout.startAnimation(anim);


        anim = AnimationUtils.loadAnimation(this, R.anim.splash_anim);
        anim.reset();
        mSplash.clearAnimation();
        mSplash.startAnimation(anim);


        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    SplashScreen.this.finish(); //finish the SplashScreen
                } catch (InterruptedException e) {
                    Log.e(LOGTAG, e.getMessage());
                } finally {
                    SplashScreen.this.finish();
                }
            }
        };
        splashTread.start();
    }

}
