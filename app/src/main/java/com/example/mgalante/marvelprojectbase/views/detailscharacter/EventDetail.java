package com.example.mgalante.marvelprojectbase.views.detailscharacter;

import android.os.Bundle;
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
import com.example.mgalante.marvelprojectbase.api.entities.Event;
import com.example.mgalante.marvelprojectbase.control.adapters.EventsRecyclerViewAdapter;
import com.example.mgalante.marvelprojectbase.control.callbacks.CommonListCallBack;
import com.example.mgalante.marvelprojectbase.views.resumecharacter.EventContract;
import com.example.mgalante.marvelprojectbase.views.resumecharacter.EventPresenterImpl;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EventDetail extends AppCompatActivity implements EventContract.View ,CommonListCallBack {

    private static final String EXTRA_CHARACTER = "character";
    private Characters mCharacter;
    private List<Event> eventList;
    private EventsRecyclerViewAdapter mEventsAdapter;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private EventPresenterImpl eventPresenter;
    private Menu menu;
    private boolean isListView;

    @Bind(R.id.event_list_detail)
    RecyclerView mEventRecyclerList;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ButterKnife.bind(this);

        if (toolbar != null) {
            setActionBar(toolbar);
            getActionBar().setDisplayHomeAsUpEnabled(false);
            getActionBar().setDisplayShowTitleEnabled(true);
            getActionBar().setElevation(7);
        }

        //Obtener el heroe
        Gson gson = new Gson();
        String json = getIntent().getExtras().getString(EXTRA_CHARACTER);
        mCharacter = gson.fromJson(json, Characters.class);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        isListView=true;

        if (eventPresenter == null) {
            eventPresenter = new EventPresenterImpl();
        }

        eventPresenter.attach(this, this);

        eventList = new ArrayList<>();
        mEventsAdapter = new EventsRecyclerViewAdapter(this, eventList,this);
        //mEventRecyclerList.setLayoutManager(new LinearLayoutManager(EventDetail.this));
        mEventRecyclerList.setLayoutManager(mStaggeredLayoutManager);
        mEventRecyclerList.setAdapter(mEventsAdapter);

        eventPresenter.getEvents(mCharacter.getId());

    }


    @Override
    public void setPresenter(EventContract.Presenter presenter) {

    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void fillData(List<Event> list) {
        this.eventList = list;
        //Los agregamos a la lista con un adapter
        mEventsAdapter.fillData(eventList);
        mEventsAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            isListView = false;
        } else {
            mStaggeredLayoutManager.setSpanCount(2);
            item.setIcon(R.drawable.ic_action_grid);
            item.setTitle("Show as list");
            isListView = true;
        }
    }

    @Override
    public void onLongClickElement(View v, Event item) {
        Toast.makeText(getApplicationContext(),"Pulsado "+item.getTitle(),Toast.LENGTH_SHORT).show();
    }
}
