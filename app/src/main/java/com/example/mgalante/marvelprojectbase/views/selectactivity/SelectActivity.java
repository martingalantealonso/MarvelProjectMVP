package com.example.mgalante.marvelprojectbase.views.selectactivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.mgalante.marvelprojectbase.R;
import com.example.mgalante.marvelprojectbase.views.bluetoothchat.ChatMainActivity;
import com.example.mgalante.marvelprojectbase.views.main.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SelectActivity extends AppCompatActivity {

    /*  @Bind(R.id.optionMarvel)
      FrameLayout optionMarvel;
      @Bind(R.id.optionBluetooth)
      FrameLayout optionBluetooth;*/
    @Bind(R.id.btn_marvel)
    TextView optionMarvel;
    @Bind(R.id.btn_music)
    TextView btnMusic;
    @Bind(R.id.btn_chat)
    TextView optionBluetooth;
    @Bind(R.id.btn_video)
    TextView btnVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_select_v2);
        ButterKnife.bind(this);

        // Retrieve the AppCompact Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // Set the padding to match the Status Bar height
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);


        optionMarvel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        SelectActivity.this, new Pair<View, String>(optionMarvel, MainActivity.VIEW_NAME_HEADER_IMAGE));

                //ActivityCompat.startActivity(getApplicationContext(), intent, activityOptionsCompat.toBundle());

                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SelectActivity.this).toBundle());
            }
        });
        optionBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatMainActivity.class);
                startActivity(intent);
            }
        });
    }

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
