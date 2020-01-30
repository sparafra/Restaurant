package com.example.spara.restaurant.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.example.spara.restaurant.object.Cart;
import com.example.spara.restaurant.R;
import com.example.spara.restaurant.object.Preference;
import com.example.spara.restaurant.object.Restaurant;
import com.example.spara.restaurant.object.User;
import com.example.spara.restaurant.object.WebConnection;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.example.spara.restaurant.object.Preference.savePreferences;

public class activity_map extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,  OnMapReadyCallback{

    private MapView mapView;
    private GoogleMap gmap;

    Cart cartProducts;
    User UserLogged;
    WebConnection Connection;
    Restaurant Rest;

    ProgressDialog pd;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* //Mail Floating Action
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_map);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //MY CODE

        //Get INTENT Extra
        cartProducts = (Cart) getIntent().getParcelableExtra("Cart");
        UserLogged = (User) getIntent().getParcelableExtra("User");
        Connection = (WebConnection) getIntent().getParcelableExtra("WebConnection");
        Rest = (Restaurant) getIntent().getParcelableExtra("Restaurant");

        ImageView imgTransparent = findViewById(R.id.imageView);
        imgTransparent.setAlpha(230);

        if(UserLogged.getAmministratore())
        {
            navigationView.getMenu().findItem(R.id.nav_gestione_ordini).setVisible(true);
        }

        mapView = findViewById(R.id.mapView);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(activity_map.this);

    }

    private void showLoadingDialog() {
        pd = new ProgressDialog(this, R.style.DialogTheme);
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_map);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

        if (id == R.id.nav_home)
        {
            if(!UserLogged.getNumeroTelefono().equals("")) {
                // Handle the camera action
                Intent I = new Intent(activity_map.this, activity_home.class);
                I.putExtra("Cart", cartProducts);
                I.putExtra("User", UserLogged);
                I.putExtra("WebConnection", Connection);
                I.putExtra("Restaurant" ,Rest);

                startActivity(I);
                activity_map.this.finish();
            }
            else{
                Intent I = new Intent(activity_map.this, MainActivity.class);
                startActivity(I);
                activity_map.this.finish();
            }
        }
        else if (id == R.id.nav_ordini)
        {
            if(!UserLogged.getNumeroTelefono().equals("")) {

                Intent I = new Intent(activity_map.this, activity_ordini.class);
                I.putExtra("Cart", cartProducts);
                I.putExtra("User", UserLogged);
                I.putExtra("WebConnection", Connection);
                I.putExtra("Restaurant" ,Rest);

                startActivity(I);
                activity_map.this.finish();
            }
        }
        else if (id == R.id.nav_carrello)
        {
            if(!UserLogged.getNumeroTelefono().equals("")) {

                Intent I = new Intent(activity_map.this, activity_carrello.class);
                I.putExtra("Cart", cartProducts);
                I.putExtra("User", UserLogged);
                I.putExtra("WebConnection", Connection);
                I.putExtra("Restaurant" ,Rest);

                startActivity(I);
                activity_map.this.finish();
            }
        }
        else if (id == R.id.nav_map)
        {
            //NOTHING
        }
        else if (id == R.id.nav_account)
        {
            /*
            Intent I = new Intent(activity_map.this, activity_ordini.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            startActivity(I);
            activity_map.this.finish();
            */
        }
        else if (id == R.id.nav_chiamaci)
        {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CALL_PHONE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            1);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
                callPhone(Rest.getNumeroTelefono());

            }
        }
        else if (id == R.id.nav_exit)
        {
            if(!UserLogged.getNumeroTelefono().equals("")) {

                Preference.savePreferences("", "", "", this);
                Intent I = new Intent(activity_map.this, MainActivity.class);
                I.putExtra("WebConnection" ,Connection);
                I.putExtra("Restaurant" ,Rest);
                startActivity(I);
                activity_map.this.finish();
            }
            else{
                Intent I = new Intent(activity_map.this, MainActivity.class);
                I.putExtra("WebConnection" ,Connection);
                I.putExtra("Restaurant" ,Rest);
                startActivity(I);
                activity_map.this.finish();

            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_map);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    callPhone(Rest.getNumeroTelefono());
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    savePreferences("", "", "", this);
                    startActivity(new Intent(activity_map.this, MainActivity.class));
                    activity_map.this.finish();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void callPhone(String Numero)
    {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel: "+ Numero));
        startActivity(callIntent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        pd.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        showLoadingDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap = googleMap;
        mMap.setMinZoomPreference(12);
        mMap.setIndoorEnabled(true);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        LatLng ny;
        if(Rest.getId() == 1)
            ny = new LatLng(39.3560725, 16.2376736);
        else
            ny = new LatLng(39.3534562, 16.2359288);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(ny);
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(ny));
        pd.dismiss();
    }
}
