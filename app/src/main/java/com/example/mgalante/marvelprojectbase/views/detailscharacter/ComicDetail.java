package com.example.mgalante.marvelprojectbase.views.detailscharacter;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.mgalante.marvelprojectbase.R;
import com.example.mgalante.marvelprojectbase.api.entities.Characters;
import com.example.mgalante.marvelprojectbase.api.entities.Comic;
import com.example.mgalante.marvelprojectbase.control.adapters.ComicsRecyclerViewAdapter;
import com.example.mgalante.marvelprojectbase.utils.BlurBuilder;
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
    private boolean hideDetail;
    private String urlImage;
    private Dialog dialog;

    @Bind(R.id.comic_detail_list)
    RecyclerView mComicList;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.btn_toogle)
    FloatingActionButton mTootleBtn;
    @Bind(R.id.comic_image_detail_frame)
    FrameLayout mDetailComicMax;
    @Bind(R.id.img_comic_detail_max)
    ImageView mComicDetailMax;
    @Bind(R.id.container)
    FrameLayout mFrameMainContainer;
    @Bind(R.id.comic_detail_main_container)
    LinearLayout mLinearMainContainer;


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

        mComicsAdapter.setOnItemClickListener(onItemClickListener);
        mComicList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_UP && hideDetail) {
                    //Toast.makeText(getApplicationContext(), "HET", Toast.LENGTH_SHORT).show();
                    //AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                    //alphaAnimation.setDuration(1000);
                    //mDetailComicMax.startAnimation(alphaAnimation);
                    //mDetailComicMax.setVisibility(View.INVISIBLE);

                    //Dismiss Dialog
                    hideQuickView();
                    hideDetail = false;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
    }


    ComicsRecyclerViewAdapter.OnItemClickListener onItemClickListener = new ComicsRecyclerViewAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View view, int position) {
            urlImage = comicList.get(position).getThumbnail().getPath() + "." + comicList.get(position).getThumbnail().getExtension();
            Log.i("ImgResource", comicList.get(position).getThumbnail().getPath() + "." + comicList.get(position).getThumbnail().getExtension());
            hideDetail = true;
            showQuickView();
        }
    };


    private void showQuickView() {

        final Activity activity = this;
        final View content = activity.findViewById(android.R.id.content).getRootView();
        mComicList.setNestedScrollingEnabled(false);

        dialog = new Dialog(this, R.style.PauseDialogAnimation);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_imagevw);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorTransparent)));
        //dialog.getWindow().setLayout(, 400); //Dialog size

        //Set the blur background
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

        //set the custom dialog components
        ImageView imageZoom = (ImageView) dialog.findViewById(R.id.imgvw_dialog);
        Glide.with(this).load(urlImage).into(imageZoom);

        dialog.show();
    }

    public void hideQuickView() {
        mComicList.setNestedScrollingEnabled(true);

        if (dialog != null) dialog.dismiss();
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
