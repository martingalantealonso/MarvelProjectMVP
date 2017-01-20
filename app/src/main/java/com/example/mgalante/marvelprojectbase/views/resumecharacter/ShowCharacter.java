package com.example.mgalante.marvelprojectbase.views.resumecharacter;

import android.animation.Animator;

import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
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
import com.example.mgalante.marvelprojectbase.api.entities.Url;
import com.example.mgalante.marvelprojectbase.ormlite.DBHelper;

import com.example.mgalante.marvelprojectbase.views.BaseActivity;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShowCharacter extends BaseActivity {

    private static final String EXTRA_CHARACTER = "character";
    private static final String TAG = "Marvel_ShowCharacter";
    private Characters mCharacter;
    private boolean isEditTextVisible;
    private boolean isFavHero;
    private Animatable mAnimatable;
    private DBHelper mDBHelper;

    //region Binds
    @Bind(R.id.main_information_holder)
    RelativeLayout mHolder;
    @Bind(R.id.txtInfoFav)
    TextView mFavTextView;
    @Bind(R.id.avatar)
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
    @Bind(R.id.btn_fav)
    ImageButton mFavButton;
    @Bind((R.id.llEditTextHolder))
    LinearLayout llTextHolder;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_character);

        ButterKnife.bind(this);

        isEditTextVisible = false;

        Gson gson = new Gson();
        String json = getIntent().getExtras().getString(EXTRA_CHARACTER);
        mCharacter = gson.fromJson(json, Characters.class);
        setTitle(mCharacter.getName());
        String urlImage = mCharacter.getThumbnail().getPath() + "." + mCharacter.getThumbnail().getExtension();
        Glide.with(this).load(urlImage).into(mAvatar);

        mName.setText(mCharacter.getName());
        mDescription.setText(mCharacter.getDescription());

        llTextHolder.setVisibility(View.INVISIBLE);
        //mFavButton.setVisibility(View.INVISIBLE);

        //mFloatingButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        mFloatingButton.setImageResource(R.drawable.icn_morp);

        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartBtnAnimation(v);
            }
        });

        mFavTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFavorite();
            }
        });
        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFavorite();
            }
        });
        mComicLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl(mCharacter.getUrls(), "comiclink");

            }
        });
        mDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl(mCharacter.getUrls(), "detail");
            }
        });
        mWiki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl(mCharacter.getUrls(), "wiki");
            }
        });

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        fillTabs();

        loadFav();
        showAnimImageButton(mFloatingButton);
    }

    private void loadFav() {
        mDBHelper = DBHelper.getHelper(this);
        try {
            Dao dao = mDBHelper.getCharacterDao();
            //Se encuentra que el heroe ha sido guardado
            if (dao.queryForId(mCharacter.getId()) != null) {
                //mostrar boton
                //mFavButton.setVisibility(View.VISIBLE);
                mFavButton.setImageResource(R.drawable.ic_favorite_black_24dp);
                mFavTextView.setText(R.string.delete_from_favs);
                isFavHero = true;
                Log.i(TAG, "Heroe " + mCharacter.getName() + " encontrado");
            } else {

            }

        } catch (SQLException e) {
            Log.e("AddActivity", e.toString());
            e.printStackTrace();
        }
    }

    private void saveFavorite() {
        mDBHelper = DBHelper.getHelper(this);
        Dao dao;
        try {
            dao = mDBHelper.getCharacterDao();
            Characters character = new Characters(mCharacter.getId(), mCharacter.getName(), mCharacter.getDescription(), mCharacter.getResourceURI());
            if (!isFavHero) {
                //Si no es fav, se guarda
                dao.create(character);
                setResult(RESULT_OK);
                mDBHelper.close();
                mFavTextView.setText(R.string.delete_from_favs);
                mFavButton.setImageResource(R.drawable.ic_favorite_black_24dp);
                showAnimImageButton(mFavButton);
                Log.i(TAG, "Heroe " + mCharacter.getName() + " creado");
                isFavHero = true;
                Snackbar.make(findViewById(android.R.id.content), "Heroe guardado", Snackbar.LENGTH_LONG)
                        //.setActionTextColor(Color.CYAN)
                        .setAction("Deshacer", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.i("Snackbar", "Pulsada acción snackbar!");
                            }
                        })
                        .show();
            } else {
                //Si ya es fav, se elimina
                dao.delete(character);
                setResult(RESULT_OK);
                mFavTextView.setText(R.string.txt_add_fav);
                mFavButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                showAnimImageButton(mFavButton);
                Log.i(TAG, "Heroe " + mCharacter.getName() + " eliminado");
                isFavHero = false;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error adding the Hero");
        }
    }

    private void fillTabs() {

        mTablayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTablayout.setupWithViewPager(mViewPager);

        String title = "";

        for (int i = 0; i < mTablayout.getTabCount(); i++) {
            int iconId = -1;
            switch (i) {
                case 0:
                    title = mCharacter.getComics().getAvailable() + " " + getString(R.string.comics);
                    break;
                case 1:
                    title = mCharacter.getEvents().getAvailable() + " " + getString(R.string.events);
                    break;

            }
            mTablayout.getTabAt(i).setText(title);
        }



    }

    private void openUrl(List<Url> urls, String type) {
        for (Url url : urls
                ) {
            if (url.getType().equals(type)) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url.getUrl()));
                startActivity(i);
            }
        }
    }

    private void showAnimImageButton(ImageButton btn) {
        getWindow().setEnterTransition(makeEnterTransition());
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(1000);
        btn.startAnimation(alphaAnimation);
        btn.setVisibility(View.VISIBLE);
    }

    public static Transition makeEnterTransition() {
        Transition fade = new Fade();
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        return fade;

    }

    private void StartBtnAnimation(View view) {
        int cx = llTextHolder.getRight();

        int cy = llTextHolder.getBottom() + 32;
        //int cx = (llTextHolder.getLeft() + llTextHolder.getRight()) / 2;
        //int cy = (llTextHolder.getTop() + llTextHolder.getBottom()) / 2;

        if (!isEditTextVisible) {
            isEditTextVisible = true;
            int finalRadius = Math.max(llTextHolder.getWidth(), llTextHolder.getHeight() + llTextHolder.getWidth());
            Animator anim = ViewAnimationUtils.createCircularReveal(llTextHolder, cx, cy, 0, finalRadius);
            anim.setDuration(800);
            llTextHolder.setVisibility(View.VISIBLE);
            anim.start();

            mFloatingButton.setImageResource(R.drawable.icn_morp);
            mAnimatable = (Animatable) mFloatingButton.getDrawable();
            mAnimatable.start();
        } else {
            int initialRadius = llTextHolder.getWidth();
            Animator anim = ViewAnimationUtils.createCircularReveal(llTextHolder, cx, cy, initialRadius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    llTextHolder.setVisibility(View.INVISIBLE);
                }
            });
            isEditTextVisible = false;
            anim.start();

            mFloatingButton.setImageResource(R.drawable.icn_morph_reverse);
            mAnimatable = (Animatable) (mFloatingButton).getDrawable();
            mAnimatable.start();
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_show_character;
    }

    @Override
    public void onBackPressed() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(100);
        if (mFavButton.getVisibility() == View.VISIBLE) mFavButton.startAnimation(alphaAnimation);
        mFloatingButton.startAnimation(alphaAnimation);


        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFloatingButton.setVisibility(View.GONE);

                mFavButton.setVisibility(View.GONE);

                finishAfterTransition();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //Esto se hace tantas veces como retorne el método getCount. Dependiendo de la posicion que le llegue al switch, llenará cada Tab con un fragment u otro
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    //Se llena la tab de índice 0 con el Fragment que contiene los Comics
                    ComicFragment comicFragment = ComicFragment.newInstance(mCharacter.getId());
                    ComicPresenter comicPresenter = new ComicPresenter();
                    comicPresenter.attach(ShowCharacter.this, comicFragment);
                    fragment = comicFragment;
                    break;
                case 1:
                    /*
                    EventFragment eventsFragment = EventFragment.newInstance(mCharacter.getId());
                    EventPresenterImpl mEventPresenter = new EventPresenterImpl();
                    mEventPresenter.attach(ShowCharacter.this, eventsFragment);
                    fragment = eventsFragment;
                      */
                    fragment = new ExampleFragment();

                    break;
            }
            return fragment;
        }


        //Con este método se crearán N numero de Tabs
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }

    //TODO delete
    public static class ExampleFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_example, container, false);
        }
    }

}