package com.example.mgalante.marvelprojectbase.views.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.mgalante.marvelprojectbase.ormlite.DBHelper;
import com.example.mgalante.marvelprojectbase.views.BaseActivity;
import com.example.mgalante.marvelprojectbase.views.resumecharacter.ShowCharacter;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

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

    private MainPresenterImpl presenter;
    private List<Characters> characters;
    private CharactersRecyclerViewAdapter adapter;
    private DBHelper mDBHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this, this);
        //mMainImageView.setImageResource(R.drawable.marvellogo);

        //Hide the keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if (presenter == null) {
            presenter = new MainPresenterImpl(new ServiceMarvel());
        }

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
                }
            }
        });


        characters = new ArrayList<>();
        adapter = new CharactersRecyclerViewAdapter(characters, this, this);
        mListItem.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mListItem.setAdapter(adapter);

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
        ImageView mHolder = (ImageView) v.findViewById((R.id.avatar));
        Pair<View, String> holderPair = Pair.create((View) mHolder, "t_item_character");
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
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, holderPair, navPair, statusPair);
        } else {
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, holderPair, statusPair);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
}
