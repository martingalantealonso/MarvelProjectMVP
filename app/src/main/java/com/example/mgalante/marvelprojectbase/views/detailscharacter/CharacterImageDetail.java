package com.example.mgalante.marvelprojectbase.views.detailscharacter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.mgalante.marvelprojectbase.R;
import com.example.mgalante.marvelprojectbase.utils.TouchImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.example.mgalante.marvelprojectbase.utils.Utils.getBitmapFromMemCache;

public class CharacterImageDetail extends AppCompatActivity {

    @Bind(R.id.imgCharacterDetailMax)
    TouchImageView mTouchImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_image_detail);
        ButterKnife.bind(this);

        mTouchImgView.setImageBitmap(getBitmapFromMemCache("CharacterImage"));

    }
}
