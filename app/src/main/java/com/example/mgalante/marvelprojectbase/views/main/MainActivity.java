package com.example.mgalante.marvelprojectbase.views.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mgalante.marvelprojectbase.R;
import com.example.mgalante.marvelprojectbase.api.ServiceMarvel;
import com.example.mgalante.marvelprojectbase.api.entities.Characters;
import com.example.mgalante.marvelprojectbase.control.adapters.CharactersRecyclerViewAdapter;
import com.example.mgalante.marvelprojectbase.control.callbacks.CharacterListCallBack;
import com.example.mgalante.marvelprojectbase.eventbus.NetworkStateChanged;
import com.example.mgalante.marvelprojectbase.ormlite.DBHelper;
import com.example.mgalante.marvelprojectbase.views.BaseActivity;
import com.example.mgalante.marvelprojectbase.views.resumecharacter.ShowCharacter;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainContract.View, CharacterListCallBack {

    private static final String EXTRA_CHARACTER = "character";
    @Bind(R.id.heroName)
    EditText mEdTHeroName;
    @Bind(R.id.list_item)
    RecyclerView mListItem;
    @Bind(R.id.main_image)
    ImageView mMainImageView;
    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.btn_img)
    FloatingActionButton mImgBtn;

    ImageView mHolder;

    private MainPresenterImpl presenter;
    private List<Characters> characters;
    private CharactersRecyclerViewAdapter adapter;
    private DBHelper mDBHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this, this);

        //region Cosas que no importan
        setExitSharedElementCallback(new SharedElementCallback() {
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

        //Hide the keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setSupportActionBar(mToolBar);
        mCollapsingToolbarLayout.setTitle(" ");
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mCollapsingToolbarLayout.setTitle("Marvel Heroes");
                    isShow = true;
                } else if (isShow) {
                    mCollapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
        mImgBtn.setImageResource(R.drawable.ic_favorite_border_black_24dp);


        if (presenter == null) {
            presenter = new MainPresenterImpl(new ServiceMarvel());
        }
        //endregion

        presenter.attach(this, this);

        mEdTHeroName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String trimmedText = mEdTHeroName.getText().toString().trim();
                if (trimmedText.length() >= 3) {
                    //Iniciar busqueda de Heroes con ese nombre
                    presenter.getHeroes(trimmedText);
                } else if (trimmedText.length() <= 2) {
                    recoverList();
                }
            }
        });


        characters = new ArrayList<>();
        adapter = new CharactersRecyclerViewAdapter(characters, this, this);
        mListItem.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mListItem.setAdapter(adapter);

        mImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEdTHeroName.setText("");
                recoverList();
            }
        });
        //load Saved Heroes
        recoverList();

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void fillData(List<Characters> characters) {
        //Obtenemos los resultados
        this.characters = characters;
        //Los agregamos a la lista con un adapter
        adapter.fillData(characters);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClickCharacter(View v, Characters item) {
/*
        //showMessage(item.getName());
        Gson gson = new Gson();
        String json = gson.toJson(item);

        Intent i = new Intent(getBaseContext(), ShowCharacter.class);
        i.putExtra(EXTRA_CHARACTER, json);
*/
        //LinearLayout mHolder = (LinearLayout) v.findViewById(R.id.main_information_holder);
        mHolder = (ImageView) v.findViewById((R.id.avatar));
        Pair<View, String> holderPair = Pair.create((View) mHolder, "t_item_character");
        Pair<View, String> holderPair2 = Pair.create((View) mImgBtn, "t_imgbtn");
        Pair<View, String> navPair = null;
        Pair<View, String> statusPair = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            View navigationBar = findViewById(android.R.id.navigationBarBackground);
            View statusBar = findViewById(android.R.id.statusBarBackground);
            navPair = Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME);
            statusPair = Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME);
        }

        Gson gson = new Gson();
        String json = gson.toJson(item);

        Intent i = new Intent(getBaseContext(), ShowCharacter.class);
        i.putExtra(EXTRA_CHARACTER, json);

        ActivityOptionsCompat options;
        if (ViewConfiguration.get(getApplicationContext()).hasPermanentMenuKey()) {
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, holderPair, holderPair2, navPair, statusPair);
        } else {
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,  holderPair, holderPair2, statusPair);
        }

        ActivityCompat.startActivity(this, i, options.toBundle());
    }

    private void recoverList() {
        mDBHelper = DBHelper.getHelper(this);
        String letters = "";
        try {
            Dao dao = mDBHelper.getCharacterDao();
            QueryBuilder<Characters, Integer> qb = dao.queryBuilder();
            qb.orderBy("name", true);
            characters = qb.query();
            adapter.fillData(characters);
            adapter.notifyDataSetChanged();
            mDBHelper.close();

        } catch (SQLException e) {
            Log.e("MainActivity", e.toString());
            e.printStackTrace();
        }
    }

    public static class NetworkStateReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Log.d("app", "Network connectivity change");
            if (intent.getExtras() != null) {
                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                    Toast.makeText(context.getApplicationContext(), "Conexión a internet", Toast.LENGTH_SHORT).show();
                } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                    Toast.makeText(context.getApplicationContext(), "Sin conexión a internet", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(NetworkStateChanged event) {
        if (!event.isInternetConected()) {
            Toast.makeText(this, "Conexión a internet", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sin conexión a internet", Toast.LENGTH_SHORT).show();
        }
    }

    //region isNetworkAvailable
    /*
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }*/
//endregion
}
