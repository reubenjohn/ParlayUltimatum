package com.aspirephile.parlayultimatum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.aspirephile.parlayultimatum.point.PointContent;
import com.aspirephile.parlayultimatum.point.PointCreatorActivity;
import com.aspirephile.parlayultimatum.point.PointListFragment;
import com.aspirephile.parlayultimatum.point.PointViewerActivity;
import com.aspirephile.parlayultimatum.preferences.SettingsActivity;
import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;

import java.sql.SQLException;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PointListFragment.OnListFragmentInteractionListener {

    private Logger l = new Logger(HomeActivity.class);
    private NullPointerAsserter asserter = new NullPointerAsserter(l);
    private SearchView searchView;

    private PointListFragment pointListF;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl_home);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_point_list);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, PointCreatorActivity.class);
                startActivityForResult(i, Constants.codes.result.point_creator);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        openPointListFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        l.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.codes.result.point_creator) {
            if (resultCode == Activity.RESULT_OK) {
                Snackbar.make(coordinatorLayout, R.string.point_creator_success, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, null).show();
            } else {
                Snackbar.make(coordinatorLayout, R.string.point_creator_failure, Snackbar.LENGTH_LONG)
                        .setAction(R.string.try_again, null).show();
                //TODO Also pass and display the reason for the error from the point creator
            }
        } else {
            l.w("Unhandled request code");
        }
    }


    private void openPointListFragment() {
        // find the retained fragment on activity restarts
        FragmentManager fm = getSupportFragmentManager();
        pointListF = (PointListFragment) fm.findFragmentByTag(Constants.tags.pointListFragment);

        if (!asserter.assertPointerQuietly(pointListF)) {
            l.i("Creating new " + PointListFragment.class.getSimpleName() + " fragment");
            pointListF = PointListFragment.newInstance(1);
            fm.beginTransaction()
                    .replace(R.id.container_home, pointListF, Constants.tags.pointListFragment)
                    .commit();
        }
        if (asserter.assertPointer(pointListF, searchView)) {
            searchView.setOnQueryTextListener(pointListF);
        }
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
        getMenuInflater().inflate(R.menu.home, menu);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.home_action_search));
        if (pointListF != null)
            searchView.setOnQueryTextListener(pointListF);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home_action_settings) {
            Intent i = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(i);
            return true;
        } else if (id == R.id.home_action_search) {

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

    @Override
    public void onPointListItemSelected(PointContent.PointItem item) {
        //TODO Open the right point here
        Intent i = new Intent(HomeActivity.this, PointViewerActivity.class);
        startActivityForResult(i, Constants.codes.result.point_viewer);
    }

    @Override
    public void onPointListLoadFailed(SQLException e) {
        Snackbar.make(coordinatorLayout, R.string.point_list_fetch_failed, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (asserter.assertPointer(pointListF))
                            pointListF.onRefresh();
                    }
                }).show();
    }
}
