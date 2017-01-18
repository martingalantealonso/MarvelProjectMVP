package com.example.mgalante.marvelprojectbase.views.resumecharacter;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mgalante.marvelprojectbase.R;
import com.example.mgalante.marvelprojectbase.api.entities.Characters;
import com.example.mgalante.marvelprojectbase.views.BaseActivity;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShowCharacter extends BaseActivity {

    private static final String EXTRA_CHARACTER = "character";
    private Characters mCharacter;

    @Bind(R.id.main_information_holder)
    LinearLayout mHolder;
    @Bind(R.id.character_image)
    ImageView mAvatar;
    @Bind(R.id.name)
    TextView mName;
    @Bind(R.id.subname)
    TextView mDescription;
    @Bind(R.id.comiclink)
    Button mComicLink;
    @Bind(R.id.detail)
    Button mDetail;
    @Bind(R.id.wiki)
    Button mWiki;
    @Bind(R.id.tablayout)
    TabLayout mTablayout;
    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    //private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_character);

        ButterKnife.bind(this);

        Gson gson = new Gson();
        String json = getIntent().getExtras().getString(EXTRA_CHARACTER);
        mCharacter = gson.fromJson(json, Characters.class);
        setTitle(mCharacter.getName());
        String urlImage = mCharacter.getThumbnail().getPath() + "." + mCharacter.getThumbnail().getExtension();
        Glide.with(this).load(urlImage).into(mAvatar);

        mName.setText(mCharacter.getName());
        mDescription.setText(mCharacter.getDescription());


    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_show_character;
    }
}
