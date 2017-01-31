package com.example.mgalante.marvelprojectbase.views.resumecharacter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
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
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.mgalante.marvelprojectbase.R;
import com.example.mgalante.marvelprojectbase.api.entities.Characters;
import com.example.mgalante.marvelprojectbase.api.entities.Comic;
import com.example.mgalante.marvelprojectbase.api.entities.Url;
import com.example.mgalante.marvelprojectbase.ormlite.DBHelper;
import com.example.mgalante.marvelprojectbase.utils.BlurBuilder;
import com.example.mgalante.marvelprojectbase.views.BaseActivity;
import com.example.mgalante.marvelprojectbase.views.detailscharacter.ComicDetail;
import com.example.mgalante.marvelprojectbase.views.detailscharacter.EventDetail;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.mgalante.marvelprojectbase.R.id.avatar;
import static com.example.mgalante.marvelprojectbase.utils.Constants.LOGTAG;
import static com.example.mgalante.marvelprojectbase.utils.Utils.addBitmapToMemoryCache;
import static com.example.mgalante.marvelprojectbase.utils.Utils.createFileWithActivity;
import static com.example.mgalante.marvelprojectbase.utils.Utils.getBitmapFromMemCache;
import static com.example.mgalante.marvelprojectbase.utils.Utils.shareCacheImage;

public class ShowCharacter extends BaseActivity {

    private static final String EXTRA_CHARACTER = "character";
    private static final String TAG = "Marvel_ShowCharacter";
    private Characters mCharacter;
    private ComicPresenter mComicPresenter;
    private boolean isEditTextVisible;
    private boolean isFavHero;
    private Animatable mAnimatable;
    private DBHelper mDBHelper;
    private List<Comic> mComics;
    private String urlImage;
    private DriveFolder marvelFolder;
    private Activity thisActivity;

    //region Binds
    @Bind(R.id.main_information_holder)
    RelativeLayout mHolder;
    @Bind(R.id.txtInfoFav)
    TextView mFavTextView;
    @Bind(R.id.txtOther)
    TextView mOtherOptions;
    @Bind(avatar)
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
    FloatingActionButton mFavButton;
    @Bind((R.id.llEditTextHolder))
    LinearLayout llTextHolder;
    @Bind(R.id.btnShowComics)
    Button mBtnShowComics;
    @Bind(R.id.btnShowEvents)
    Button mBtnShowEvents;

    //Dialog
    // @Bind(R.id.circleImageViewDialog)
    //CircleImageView mCircleDialogImage;
    /*@Bind(R.id.dialog_hero_name)
    TextView mDialogHeroName;
    @Bind(R.id.imageButtonDrive)
    ImageButton mDriveBtn;
    @Bind(R.id.imageButtonShare)
    ImageButton mShareBtn;
    */

    private SectionsPagerAdapter mSectionsPagerAdapter;

    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_character);

        ButterKnife.bind(this);

        thisActivity = this;

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        isEditTextVisible = false;

        Gson gson = new Gson();
        String json = getIntent().getExtras().getString(EXTRA_CHARACTER);
        mCharacter = gson.fromJson(json, Characters.class);
        setTitle(mCharacter.getName());
        urlImage = null;
        if (mCharacter.getThumbnail() != null) {
            urlImage = mCharacter.getThumbnail().getPath() + "." + mCharacter.getThumbnail().getExtension();
        } else {
            urlImage = mCharacter.getImageUrl();
        }

        Glide.with(this)
                .load(urlImage)
                .into(new GlideDrawableImageViewTarget(mAvatar) {
                    @Override
                    protected void setResource(GlideDrawable resource) {
                        super.setResource(resource);
                        Bitmap bitmap = ((GlideBitmapDrawable) mAvatar.getDrawable().getCurrent()).getBitmap();
                        addBitmapToMemoryCache("CharacterImage", bitmap);
                    }
                });


        mName.setText(mCharacter.getName());
        mDescription.setText(mCharacter.getDescription());

        llTextHolder.setVisibility(View.INVISIBLE);
        //mFavButton.setVisibility(View.INVISIBLE);

        //mFloatingButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        mFloatingButton.setImageResource(R.drawable.icn_morph_reverse);
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
        mOtherOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
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

        mBtnShowComics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //region StartActivity ComicDetail
                Gson gson = new Gson();
                String json = gson.toJson(mCharacter);
                Intent intent = new Intent(getApplicationContext(), ComicDetail.class);
                intent.putExtra(EXTRA_CHARACTER, json);

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ShowCharacter.this, v, "hello");
                startActivity(intent, options.toBundle());
                //startActivity(intent);
                //endregion
            }
        });

        mBtnShowEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //region StartActivity EventDetail
                Gson gson = new Gson();
                String json = gson.toJson(mCharacter);
                Intent intent = new Intent(getApplicationContext(), EventDetail.class);
                intent.putExtra(EXTRA_CHARACTER, json);

                //ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ShowCharacter.this, v, "comic_transition");
                //startActivity(intent, options.toBundle());
                startActivity(intent);
                //endregion
            }
        });


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        fillTabs();

        loadFav();

        showAnimImageButton(mFloatingButton);

        if (mComicPresenter == null) {
            mComicPresenter = new ComicPresenter();
        }


        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public View onCreateSnapshotView(Context context, Parcelable snapshot) {
                View view = new View(context);
                view.setBackground(new BitmapDrawable((Bitmap) snapshot));
                return view;
            }

            @Override
            public void onSharedElementStart(List<String> sharedElementNames,
                                             List<View> sharedElements,
                                             List<View> sharedElementSnapshots) {
                for (int i = 0; i < sharedElements.size(); i++) {
                    if (sharedElements.get(i) == mAvatar) {
                        View snapshot = sharedElementSnapshots.get(i);
                        Drawable snapshotDrawable = snapshot.getBackground();
                        mAvatar.setBackground(snapshotDrawable);
                        mAvatar.setImageAlpha(0);
                        forceSharedElementLayout();
                        break;
                    }
                }
            }

            private void forceSharedElementLayout() {
                int widthSpec = View.MeasureSpec.makeMeasureSpec(mAvatar.getWidth(),
                        View.MeasureSpec.EXACTLY);
                int heightSpec = View.MeasureSpec.makeMeasureSpec(mAvatar.getHeight(),
                        View.MeasureSpec.EXACTLY);
                int left = mAvatar.getLeft();
                int top = mAvatar.getTop();
                int right = mAvatar.getRight();
                int bottom = mAvatar.getBottom();
                mAvatar.measure(widthSpec, heightSpec);
                mAvatar.layout(left, top, right, bottom);
            }

            @Override
            public void onSharedElementEnd(List<String> sharedElementNames,
                                           List<View> sharedElements,
                                           List<View> sharedElementSnapshots) {
                mAvatar.setBackground(null);
                mAvatar.setImageAlpha(255);
            }
        });

        /*
        setExitSharedElementCallback(new android.support.v4.app.SharedElementCallback() {
            @Override
            public Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
                int bitmapWidth = Math.round(screenBounds.width());
                int bitmapHeight = Math.round(screenBounds.height());
                Bitmap bitmap = null;
                if (bitmapWidth > 0 && bitmapHeight > 0) {
                    Matrix matrix = new Matrix();
                    matrix.set(viewToGlobalMatrix);
                    matrix.postTranslate(-screenBounds.left, -screenBounds.top);
                    bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.concat(matrix);
                    sharedElement.draw(canvas);
                }
                return bitmap;
            }
        });

        */

    }

    private void showCustomDialog() {
        final Activity activity = this;
        final View content = activity.findViewById(android.R.id.content).getRootView();

        final Dialog dialog = new Dialog(this, R.style.PauseDialogAnimation);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_hero_detail_options);

        //region Set the blur background
        if (content.getWidth() > 0) {
            Bitmap image = BlurBuilder.blur(content);
            dialog.getWindow().setBackgroundDrawable(new BitmapDrawable(activity.getResources(), image));
        } else {
            content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Bitmap image = BlurBuilder.blur(content);
                    dialog.getWindow().setBackgroundDrawable(new BitmapDrawable(activity.getResources(), image));
                }
            });
        }
        //endregion

        //set the custom dialog components
        final CircleImageView mCircleDialogImage = (CircleImageView) dialog.findViewById(R.id.circleImageViewDialog);
        TextView mDialogHeroName=(TextView)dialog.findViewById(R.id.dialog_hero_name);
        mDialogHeroName.setText(mCharacter.getName());
        //Glide.with(this).load(urlImage).into(mCircleDialogImage);
        mCircleDialogImage.setImageBitmap(getBitmapFromMemCache("CharacterImage"));
        ImageButton btnDrive = (ImageButton) dialog.findViewById(R.id.imageButtonDrive);
        ImageButton btnShare = (ImageButton) dialog.findViewById(R.id.imageButtonShare);

        btnDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        //createFolder("Marvel");
                        //createFileWithActivity(thisActivity,urlImage);
                        //Bitmap bitmap = ((GlideBitmapDrawable) mCircleDialogImage.getDrawable().getCurrent()).getBitmap();
                        //addBitmapToMemoryCache("CharacterImage", bitmap);
                        createFileWithActivity(thisActivity, mCharacter.getName());
                    }
                }.start();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Intent.createChooser(shareCacheImage("CharacterImage"), "Share Image"));
            }
        });
        dialog.show();
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
        Dao characterDao;

        try {
            characterDao = mDBHelper.getCharacterDao();
            Characters character = new Characters(mCharacter.getId(), mCharacter.getName(), mCharacter.getDescription(), mCharacter.getResourceURI(), mCharacter.getThumbnail().getPath() + "." + mCharacter.getThumbnail().getExtension());

            if (!isFavHero) {
                //Si no es fav, se guarda
                characterDao.create(character);
                setResult(RESULT_OK);
                mDBHelper.close();
                Log.i(TAG, "Lista de comics: " + mCharacter.getComics().getCollectionURI());
                mComicPresenter.saveComics(mCharacter);
                mFavTextView.setText(R.string.delete_from_favs);
                mFavButton.setImageResource(R.drawable.ic_favorite_black_24dp);
                showAnimImageButton(mFavButton);
                Log.i(TAG, "Heroe " + mCharacter.getName() + " creado");
                isFavHero = true;
                makeSnackbar("Heroe guardado", 0);
            } else {
                //Si ya es fav, se elimina
                characterDao.delete(character);
                setResult(RESULT_OK);
                mFavTextView.setText(R.string.txt_add_fav);
                mFavButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                showAnimImageButton(mFavButton);
                Log.i(TAG, "Heroe " + mCharacter.getName() + " eliminado");
                isFavHero = false;
                makeSnackbar("Heroe eliminado", 1);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error adding the Hero");
        }
    }

    private void makeSnackbar(String s, final Integer code) {
        Snackbar.make(findViewById(android.R.id.content), s, Snackbar.LENGTH_LONG)
                //.setActionTextColor(Color.CYAN)
                .setAction("Deshacer", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (code) {
                            case 0:
                                Log.i("Snackbar", "Eliminar Heroe");
                                Toast.makeText(getApplicationContext(), "Accion aun no implementada", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Log.i("Snackbar", "Guardar Heroe");
                                Toast.makeText(getApplicationContext(), "Accion aun no implementada", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                })
                .show();
    }

    private void fillTabs() {

        mTablayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTablayout.setupWithViewPager(mViewPager);

        String title = "";

        if (mCharacter.getComics() == null) {
            mDBHelper = DBHelper.getHelper(this);
            String letters = "";
            try {
                Dao dao = mDBHelper.getComicsDao();
                QueryBuilder<Comic, Integer> qb = dao.queryBuilder();
                qb.where().eq("character_id", mCharacter.getId());
                mComics = qb.query();
                Log.i("HEY", "HEY");
                //mCharacter.setComics(qb.query());
            } catch (SQLException e) {
                Log.e("AddActivity", e.toString());
                e.printStackTrace();
            }
            //
        } else {
            for (int i = 0; i < mTablayout.getTabCount(); i++) {
                int iconId = -1;
                switch (i) {
                    case 0:
                        title = mCharacter.getComics().getAvailable() + " " + getString(R.string.comics);
                        mBtnShowComics.setText(title);
                        break;
                    case 1:
                        title = mCharacter.getEvents().getAvailable() + " " + getString(R.string.events);
                        mBtnShowEvents.setText(title);
                        break;
                }
                mTablayout.getTabAt(i).setText(title);
            }
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
        //From center ->
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
        //if (mFavButton.getVisibility() == View.VISIBLE) mFavButton.startAnimation(alphaAnimation);
        mFloatingButton.startAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFloatingButton.setVisibility(View.GONE);

                //mFavButton.setVisibility(View.GONE);

                finishAfterTransition();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /*@Override
    public void onLongClickElement(View v, Event item) {
       Toast.makeText(getApplicationContext(),"Pulsado "+item.getTitle(),Toast.LENGTH_SHORT).show();
    }*/

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
                    fragment = new Fragment();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 001:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    Log.i(LOGTAG, "Fichero creado con ID = " + driveId);
                    //makeSnackbar("Heroe Creado",2);
                    Toast.makeText(getApplicationContext(), "Imagen guardada", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
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
