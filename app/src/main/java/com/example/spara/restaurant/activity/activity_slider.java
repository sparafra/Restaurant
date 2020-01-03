package com.example.spara.restaurant.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.spara.restaurant.R;
import com.example.spara.restaurant.custom_adapter.SlideAdapter;
import com.example.spara.restaurant.object.Cart;
import com.example.spara.restaurant.object.JSONUtility;
import com.example.spara.restaurant.object.Preference;
import com.example.spara.restaurant.object.Restaurant;
import com.example.spara.restaurant.object.Setting;
import com.example.spara.restaurant.object.User;
import com.example.spara.restaurant.object.WebConnection;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.spara.restaurant.object.JSONUtility.downloadJSON;
import static com.example.spara.restaurant.object.JSONUtility.fillRestaurants;
import static com.example.spara.restaurant.object.JSONUtility.fillUser;

public class activity_slider extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    Cart cartProducts;
    User UserLogged;
    WebConnection Connection;
    Restaurant Rest;

    String NumeroTelefono;
    String Mail;
    String Password;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        if(Setting.getDebug())
            System.out.println("onCreate");


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

        /*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        */

        //MY CODE
        Connection = new WebConnection();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        2);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted

        }

        /*
        new Thread(new Runnable() {
            public void run() {

                if(Setting.getDebug())
                {
                    System.out.println("NUMERO TELEFONO: " + NumeroTelefono);
                    System.out.println("MAIL: " + Mail);
                    System.out.println("PASSWORD: " + Password);
                }



                try{
                    String tmpJSON = downloadJSON(Connection.getURL(WebConnection.query.ALLLOCALS));
                    ArrayList<Restaurant> listRestaurant = fillRestaurants(tmpJSON);


                }catch (Exception e){}

            }
        }).start();

         */
        String[] listURL_images;
        String[] lst_title;
        String[] lst_description;

        try{
            String tmpJSON = downloadJSON(Connection.getURL(WebConnection.query.ALLLOCALS));
            ArrayList<Restaurant> listRestaurant = fillRestaurants(tmpJSON);
            listURL_images = new String[listRestaurant.size()];
            lst_title = new String[listRestaurant.size()];
            lst_description = new String[listRestaurant.size()];

            for(int k=0; k<listRestaurant.size(); k++)
            {
                listURL_images[k] = listRestaurant.get(k).getLogoURL();
                lst_title[k] = listRestaurant.get(k).getNome();
                lst_description[k] = listRestaurant.get(k).getIndirizzo();
            }

            ViewPager viewPager;
            SlideAdapter myadapter;
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            myadapter = new SlideAdapter(this, listURL_images, lst_title, lst_description);
            viewPager.setAdapter(myadapter);

        }catch (Exception e){}



    }
    @Override
    protected void onStart()
    {
        super.onStart();
        if(Setting.getDebug())
            System.out.println("onStart");

        /*
        showLoadingDialog();

        new Thread(new Runnable() {
            public void run() {

                loadPreferences();
                if(Setting.getDebug())
                {
                    System.out.println("NUMERO TELEFONO: " + NumeroTelefono);
                    System.out.println("MAIL: " + Mail);
                    System.out.println("PASSWORD: " + Password);
                }


                if(!NumeroTelefono.equals("") && !Mail.equals("") && !Password.equals(""))
                {
                    try{
                        String par = "idLocale=" + Restaurant.getId() + "&Mail=" + Mail;
                        String tmpJSON = downloadJSON(Connection.getURL(WebConnection.query.SEARCHACCOUNT, par));
                        JSONArray jsonArray = new JSONArray(tmpJSON);
                        if (jsonArray.length() > 0) {
                            JSONObject obj = jsonArray.getJSONObject(0);
                            UserLogged = fillUser(obj.toString());

                            cartProducts = new Cart();
                            Intent I = new Intent(activity_slider.this, activity_home.class);
                            I.putExtra("Cart", cartProducts);
                            I.putExtra("User", UserLogged);
                            I.putExtra("WebConnection", Connection);
                            pd.dismiss();
                            startActivity(I);
                            activity_slider.this.finish();
                        }
                    }catch (Exception e){UserLogged = new User();}
                }
                pd.dismiss();
            }
        }).start();

         */
    }
    private void showLoadingDialog() {
        pd = new ProgressDialog(this, R.style.DialogTheme);
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        pd.show();
    }

    private void loadPreferences() {

        SharedPreferences settings = getSharedPreferences("alPachino",
                Context.MODE_PRIVATE);

        // Get value
        NumeroTelefono = settings.getString("NumeroTelefono", "");
        Mail = settings.getString("Mail", "");
        Password = settings.getString("Password", "");

        if(Setting.getDebug())
        {
            System.out.println("LOADED PREFERENCES");
            System.out.println("NUMERO TELEFONO: " + NumeroTelefono);
            System.out.println("MAIL: " + Mail);
            System.out.println("PASSWORD: " + Password);

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

        if (id == R.id.nav_home) {
            // Handle the camera action
            Toast.makeText(getApplicationContext(), "Devi effettuare prima il login o registrarti!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_ordini) {
            Toast.makeText(getApplicationContext(), "Devi effettuare prima il login o registrarti!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_carrello) {
            Toast.makeText(getApplicationContext(), "Devi effettuare prima il login o registrarti!", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_map) {
            Intent I = new Intent(activity_slider.this, activity_map.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            startActivity(I);
            activity_slider.this.finish();
        } else if (id == R.id.nav_account) {
            Toast.makeText(getApplicationContext(), "Devi effettuare prima il login o registrarti!", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_chiamaci) {

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                    Preference.savePreferences("", "", "", this);

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
}