package com.example.mgalante.marvelprojectbase.views.resumecharacter;

import android.animation.Animator;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    RelativeLayout mHolder;
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
    @Bind(R.id.btn_add)
    ImageButton mFloatingButton;
    @Bind(R.id.txtInfoFav)
    TextView mTxtInfoFav;
    @Bind((R.id.llEditTextHolder))
    LinearLayout llTextHolder;
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

        llTextHolder.setVisibility(View.INVISIBLE);

        mFloatingButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartBtnAnimation(v);
            }
        });

        windowTransition();
    }

    private void windowTransition() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(2000);
        mFloatingButton.startAnimation(alphaAnimation);
            }


    private void StartBtnAnimation(View view) {
        int cx = llTextHolder.getRight() - 30;
        int cy = llTextHolder.getBottom() - 60;
        int finalRadius = Math.max(view.getWidth(), llTextHolder.getHeight());
        Animator anim = ViewAnimationUtils.createCircularReveal(llTextHolder, cx, cy, 0, finalRadius);
        llTextHolder.setVisibility(View.VISIBLE);
        anim.start();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_show_character;
    }

    @Override
    public void onBackPressed() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(100);
        mFloatingButton.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFloatingButton.setVisibility(View.GONE);
                finishAfterTransition();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
