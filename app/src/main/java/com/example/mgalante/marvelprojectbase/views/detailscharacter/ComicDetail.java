package com.example.mgalante.marvelprojectbase.views.detailscharacter;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.mgalante.marvelprojectbase.R;
import com.example.mgalante.marvelprojectbase.api.entities.Characters;
import com.example.mgalante.marvelprojectbase.api.entities.Comic;
import com.example.mgalante.marvelprojectbase.control.adapters.ComicsRecyclerViewAdapter;
import com.example.mgalante.marvelprojectbase.views.resumecharacter.ComicContract;
import com.example.mgalante.marvelprojectbase.views.resumecharacter.ComicPresenter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ComicDetail extends AppCompatActivity implements ComicContract.View {

    private static final String EXTRA_CHARACTER = "character";
    private Characters mCharacter;
    private ComicPresenter mComicPresenter;
    private List<Comic> comicList;
    private ComicsRecyclerViewAdapter mComicsAdapter;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private Menu menu;
    private boolean isListView;


    @Bind(R.id.comic_detail_list)
    RecyclerView mComicList;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.btn_toogle)
    FloatingActionButton mTootleBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_detail);
        ButterKnife.bind(this);

        isListView = true;
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        setUpActionBar();
        toolbar.setTitle("Comics");
        setTitle("Comics");
        //Obtener el heroe
        Gson gson = new Gson();
        String json = getIntent().getExtras().getString(EXTRA_CHARACTER);
        mCharacter = gson.fromJson(json, Characters.class);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mTootleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        //region Rellenando el Recycler
        if (mComicPresenter == null) {
            mComicPresenter = new ComicPresenter();
        }

        mComicPresenter.attach(this, this);

        comicList = new ArrayList<>();
        mComicsAdapter = new ComicsRecyclerViewAdapter(this, comicList);
        mComicList.setLayoutManager(mStaggeredLayoutManager);
        mComicList.setAdapter(mComicsAdapter);

        mComicPresenter.getComics(mCharacter.getId());
        //endregion

        /*
        //Transition que no funciona :(
        setEnterSharedElementCallback(new SharedElementCallback() {
            View mSnapshot;

            @Override
            public void onSharedElementStart(List<String> sharedElementNames,
                                             List<View> sharedElements,
                                             List<View> sharedElementSnapshots) {
                addSnapshot(sharedElementNames, sharedElements, sharedElementSnapshots, false);
                if (mSnapshot != null) {
                    mSnapshot.setVisibility(View.VISIBLE);
                }
                mComicList.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSharedElementEnd(List<String> sharedElementNames,
                                           List<View> sharedElements,
                                           List<View> sharedElementSnapshots) {
                addSnapshot(sharedElementNames, sharedElements, sharedElementSnapshots,
                        true);
                if (mSnapshot != null) {
                    mSnapshot.setVisibility(View.INVISIBLE);
                }
                mComicList.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                mComicList.setVisibility(View.INVISIBLE);
            }

            private void addSnapshot(List<String> sharedElementNames,
                                     List<View> sharedElements,
                                     List<View> sharedElementSnapshots,
                                     boolean relayoutContainer) {
                if (mSnapshot == null) {
                    for (int i = 0; i < sharedElementNames.size(); i++) {
                        if ("hello".equals(sharedElementNames.get(i))) {
                            FrameLayout element = (FrameLayout) sharedElements.get(i);
                            mSnapshot = sharedElementSnapshots.get(i);
                            int width = mSnapshot.getWidth();
                            int height = mSnapshot.getHeight();
                            FrameLayout.LayoutParams layoutParams =
                                    new FrameLayout.LayoutParams(width, height);
                            layoutParams.gravity = Gravity.CENTER;
                            int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
                            int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
                            mSnapshot.measure(widthSpec, heightSpec);
                            mSnapshot.layout(0, 0, width, height);
                            mSnapshot.setTransitionName("snapshot");
                            if (relayoutContainer) {
                                ViewGroup container = (ViewGroup) findViewById(R.id.container);
                                int left = (container.getWidth() - width) / 2;
                                int top = (container.getHeight() - height) / 2;
                                element.measure(widthSpec, heightSpec);
                                element.layout(left, top, left + width, top + height);
                            }
                            element.addView(mSnapshot, layoutParams);
                            break;
                        }
                    }
                }
            }
        });
        */
    }

    @Override
    public void finishAfterTransition() {
        super.finishAfterTransition();
        findViewById(R.id.comic_detail_list).setVisibility(View.VISIBLE);
    }

    private void setUpActionBar() {
        if (toolbar != null) {
            setActionBar(toolbar);
            getActionBar().setDisplayHomeAsUpEnabled(false);
            getActionBar().setDisplayShowTitleEnabled(true);
            getActionBar().setElevation(7);
        }
    }

    @Override
    public void fillData(List<Comic> list) {
        //Obtenemos los resultados
        this.comicList = list;
        //Los agregamos a la lista con un adapter
        mComicsAdapter.fillData(comicList);
        mComicsAdapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(ComicContract.Presenter presenter) {

    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_toggle) {
            toggle();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        MenuItem item = menu.findItem(R.id.action_toggle);
        if (isListView) {
            mStaggeredLayoutManager.setSpanCount(1);
            item.setIcon(R.drawable.ic_action_list);
            item.setTitle("Show as grid");
            mTootleBtn.setImageResource(R.drawable.ic_action_list);
            isListView = false;
        } else {
            mStaggeredLayoutManager.setSpanCount(2);
            item.setIcon(R.drawable.ic_action_grid);
            item.setTitle("Show as list");
            mTootleBtn.setImageResource(R.drawable.ic_action_grid);

            isListView = true;
        }
    }

}
