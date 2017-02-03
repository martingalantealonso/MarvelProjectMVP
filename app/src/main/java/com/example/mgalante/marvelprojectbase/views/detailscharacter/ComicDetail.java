package com.example.mgalante.marvelprojectbase.views.detailscharacter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.example.mgalante.marvelprojectbase.R;
import com.example.mgalante.marvelprojectbase.api.entities.Characters;
import com.example.mgalante.marvelprojectbase.api.entities.Comic;
import com.example.mgalante.marvelprojectbase.control.adapters.ComicsRecyclerViewAdapter;
import com.example.mgalante.marvelprojectbase.utils.BlurBuilder;
import com.example.mgalante.marvelprojectbase.views.resumecharacter.ComicContract;
import com.example.mgalante.marvelprojectbase.views.resumecharacter.ComicPresenter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.tooltip.Tooltip;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.example.mgalante.marvelprojectbase.R.id.imgvw_dialog;
import static com.example.mgalante.marvelprojectbase.utils.Constants.LOGTAG;
import static com.example.mgalante.marvelprojectbase.utils.Utils.addBitmapToMemoryCache;
import static com.example.mgalante.marvelprojectbase.utils.Utils.getBitmapFromMemCache;


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
    private boolean showFloating;
    private String urlImage;
    private Dialog dialog;
    private Tooltip tooltipSave;
    private Tooltip tooltipFabButton1;
    private Tooltip tooltipFabButton2;
    private Tooltip tooltipFabButton3;
    private ImageView mDialogComicImg;
    private ImageButton dialogBtn;
    private FloatingActionMenu mFloatingActionMenu;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;

    @Bind(R.id.comic_detail_list)
    RecyclerView mComicList;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.btn_toogle)
    ImageButton mTootleBtn;
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
        showFloating = true;
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
        //region No comments
        //getWindow().setWindowAnimations(R.style.PauseDialogAnimation);
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
        //endregion

        mComicsAdapter.setOnItemClickListener(onItemClickListener);

        //Aqui es donde se hace la magia
        mComicList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            //Acción a ralizar cuando ocurre un evento (creo) sobre un elemento que no es el Item
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_UP && hideDetail) {

                    if (isPointInsideView(e.getRawX(), e.getRawY(), fab1)) {
                        //Toast.makeText(getApplicationContext(), "Entrando en modo de editar", Toast.LENGTH_LONG).show();

                        Bitmap bitmap = ((GlideBitmapDrawable) mDialogComicImg.getDrawable().getCurrent()).getBitmap();
                        addBitmapToMemoryCache("ComicImage", bitmap);

                        Bitmap icon = getBitmapFromMemCache("ComicImage");
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/jpeg");

                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "title");
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                        OutputStream outstream;
                        try {
                            outstream = getContentResolver().openOutputStream(uri);
                            icon.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                            outstream.close();
                        } catch (Exception ex) {
                            System.err.println(ex.toString());
                        }

                        share.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(share, "Share Image"));

                    }

                    if (isPointInsideView(e.getRawX(), e.getRawY(), fab2))
                        Toast.makeText(getApplicationContext(), "Descargando Comic", Toast.LENGTH_LONG).show();
                    if (isPointInsideView(e.getRawX(), e.getRawY(), fab3))
                        Toast.makeText(getApplicationContext(), "Guardado como favorito", Toast.LENGTH_LONG).show();

                    //Dismiss Dialog
                    hideQuickView();
                    hideDetail = false;
                }
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    //Log.i(LOGTAG, "OnDown");
                }
                if (e.getAction() == MotionEvent.ACTION_MOVE) {
                    //Log.i(LOGTAG, "ACTION_MOVE");

                    if (mFloatingActionMenu != null) {
                        if (isPointInsideView(e.getRawX(), e.getRawY(), mFloatingActionMenu)) {
                            //doSomething()
                            //Log.i(LOGTAG, "ACTION_MOVE dialogBtn");
                            //Display Animation
                            if (showFloating) {
                                //Toast.makeText(getApplicationContext(), "Comic Guardado", Toast.LENGTH_SHORT).show();
                                showFloating = false;
                                tooltipSave = new Tooltip.Builder(mFloatingActionMenu)
                                        .setText("Mas opciones")
                                        .setGravity(Gravity.BOTTOM)
                                        .setCornerRadius(R.dimen.cardview_default_radius)
                                        .setBackgroundColor(Color.DKGRAY)
                                        .show();
                                mFloatingActionMenu.open(true);
                                //floatingMenuOptions(e);
                            }
                        } else {
                            Log.i(LOGTAG, "HideFloating");
                            if (tooltipSave != null) {
                                tooltipSave.dismiss();
                            }
                            showFloating = true;
                            mFloatingActionMenu.close(false);
                        }
                        //region FAB_Butons
                        if (isPointInsideView(e.getRawX(), e.getRawY(), fab1) && tooltipFabButton1 == null) {
                            tooltipFabButton1 = new Tooltip.Builder(fab1)
                                    .setText("Editar")
                                    .setGravity(Gravity.TOP)
                                    .setCornerRadius(R.dimen.cardview_default_radius)
                                    .setBackgroundColor(Color.DKGRAY)
                                    .show();
                        } else if (!isPointInsideView(e.getRawX(), e.getRawY(), fab1)) {
                            if (tooltipFabButton1 != null) {
                                tooltipFabButton1.dismiss();
                                tooltipFabButton1 = null;
                            }
                        }
                        if (isPointInsideView(e.getRawX(), e.getRawY(), fab2) && tooltipFabButton2 == null) {
                            tooltipFabButton2 = new Tooltip.Builder(fab2)
                                    .setText("Descargar")
                                    .setGravity(Gravity.TOP)
                                    .setCornerRadius(R.dimen.cardview_default_radius)
                                    .setBackgroundColor(Color.DKGRAY)
                                    .show();
                        } else if (!isPointInsideView(e.getRawX(), e.getRawY(), fab2)) {
                            if (tooltipFabButton2 != null) {
                                tooltipFabButton2.dismiss();
                                tooltipFabButton2 = null;
                            }
                        }
                        if (isPointInsideView(e.getRawX(), e.getRawY(), fab3) && tooltipFabButton3 == null) {
                            tooltipFabButton3 = new Tooltip.Builder(fab3)
                                    .setText("Guardar favorito")
                                    .setGravity(Gravity.TOP)
                                    .setCornerRadius(R.dimen.cardview_default_radius)
                                    .setBackgroundColor(Color.DKGRAY)
                                    .show();
                        } else if (!isPointInsideView(e.getRawX(), e.getRawY(), fab3)) {
                            if (tooltipFabButton3 != null) {
                                tooltipFabButton3.dismiss();
                                tooltipFabButton3 = null;
                            }
                        }
                        //endregion
                    }
                }
                return false;
            }

            //accion a realizar cuando ocurre un evento (creo) sobre el Item
            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
    }

    /**
     * Para conocer la si posición del puntero/dedo sobre la pantalla, está sobre una Vista específica
     *
     * @param x    posicion en el eje X
     * @param y    posicion en el eje Y
     * @param view la vista sobre la que se sitúa
     * @return True-> si está posicionado sobre esa vista
     */
    public static boolean isPointInsideView(float x, float y, View view) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        if ((x > viewX && x < (viewX + view.getWidth())) &&
                (y > viewY && y < (viewY + view.getHeight()))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Listener para cada elemento del RecyclerView.
     * Puede hacerse de otra manera, implementada en la clase EventDetail
     */
    ComicsRecyclerViewAdapter.OnItemClickListener onItemClickListener = new ComicsRecyclerViewAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View view, int position) {
            urlImage = comicList.get(position).getThumbnail().getPath() + "." + comicList.get(position).getThumbnail().getExtension();
            Log.i("ImgResource", comicList.get(position).getThumbnail().getPath() + "." + comicList.get(position).getThumbnail().getExtension());
            hideDetail = true;
            showQuickView();
        }
    };

    /**
     * Para mostrar el Dialog con la imagen del comic
     */
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
        ImageView imageZoom = (ImageView) dialog.findViewById(imgvw_dialog);
        Glide.with(this).load(urlImage).into(imageZoom);

        dialogBtn = (ImageButton) dialog.findViewById(R.id.dialog_floating_1);
        mFloatingActionMenu = (FloatingActionMenu) dialog.findViewById(R.id.dialog_floating_menu);
        mDialogComicImg = (ImageView) dialog.findViewById(R.id.imgvw_dialog);
        fab1 = (FloatingActionButton) dialog.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) dialog.findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) dialog.findViewById(R.id.fab3);

        dialog.show();
    }

    /**
     * Para ocultar el Dialog con la imagen del cómic
     */
    public void hideQuickView() {
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
