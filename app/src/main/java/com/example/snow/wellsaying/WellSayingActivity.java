package com.example.snow.wellsaying;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WellSayingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "WellSaying";
    private TextView wellSaying;
    private ListView mListComments;
    private SwipeRefreshLayout mRefreshLayout;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_well_saying);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                Intent intent = new Intent(WellSayingActivity.this, CommentsEditor.class);
                intent.putExtra("content_id", 1);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        wellSaying = (TextView)findViewById(R.id.wellsaying);
        mRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refreshlayout);
        mListComments = (ListView)findViewById(R.id.list_comments);
        dbHelper = new DBHelper(this);
        initFreshLayout();
        initWellSaying();
        initCommentList();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.well_saying, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initWellSaying() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("wellsaying", new String[]{"content", "author"}, "_id = 1", null, null, null, null);

        if(cursor.moveToFirst()) {
            wellSaying.setText(cursor.getString(0));
        }
    }

    private void initFreshLayout() {
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //mRefreshLayout.setRefreshing(true);
                //do something to refresh ui
                (new Handler()).post(new Runnable() {
                    @Override
                    public void run() {
                        initCommentList();
                        mRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }
        );
    }
    private void initCommentList() {
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("comments", new String[]{"comment", "author"}, null, null, null, null, null);

        while(cursor.moveToNext()) {
            Map<String,Object> map = new HashMap<String, Object>();
            Log.d(TAG,"content = "+cursor.getString(0));
            map.put("title",cursor.getString(1));
            map.put("content",cursor.getString(0));
            list.add(map);
        }
        mListComments.setAdapter(new CommentsAdapter(this,list));
    }
}
