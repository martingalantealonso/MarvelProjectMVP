package com.example.mgalante.marvelprojectbase.views.selectactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.example.mgalante.marvelprojectbase.R;
import com.example.mgalante.marvelprojectbase.views.main.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SelectActivity extends AppCompatActivity {

    @Bind(R.id.optionMarvel)
    FrameLayout optionMarvel;
    @Bind(R.id.optionBluetooth)
    FrameLayout optionBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_select);
        ButterKnife.bind(this);


        optionMarvel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

             /*   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        SelectActivity.this, new Pair<View, String>(findViewById(R.id.optionimgmarvel), MainActivity.VIEW_NAME_HEADER_IMAGE));

                ActivityCompat.startActivity(getApplicationContext(), intent, activityOptionsCompat.toBundle());*/

                startActivity(intent);
            }
        });
        optionBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
