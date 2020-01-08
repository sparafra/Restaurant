package com.example.spara.restaurant.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.StrictMode;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;

import com.example.spara.restaurant.R;
import com.example.spara.restaurant.object.Cart;
import com.example.spara.restaurant.object.JSONUtility;
import com.example.spara.restaurant.object.Preference;
import com.example.spara.restaurant.object.Restaurant;
import com.example.spara.restaurant.object.Setting;
import com.example.spara.restaurant.object.User;
import com.example.spara.restaurant.object.WebConnection;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.spara.restaurant.object.JSONUtility.downloadJSON;
import static com.example.spara.restaurant.object.JSONUtility.fillUser;

public class MainActivity extends AppCompatActivity
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
        setContentView(R.layout.activity_main);


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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //MY CODE
        //Connection = new WebConnection();
        Rest = (Restaurant) getIntent().getParcelableExtra("Restaurant");
        Connection = (WebConnection) getIntent().getParcelableExtra("WebConnection");

        if(Setting.getDebug())
        {
            System.out.println("ID: " + Rest.getId());
            System.out.println("Attivo:_" + Rest.getAttivo());
            System.out.println("Indirizzo: " + Rest.getIndirizzo());
            System.out.println("Mail: " + Rest.getMail());
            System.out.println("Nome: " + Rest.getNome());
            System.out.println("NumeroTelefono: " + Rest.getNumeroTelefono());

        }




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


        Button login = findViewById(R.id.login);


        TextView t1 = findViewById(R.id.indirizzo_main);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"font/robotoregular.ttf");
        t1.setTypeface(typeface);

        TextView Iscriviti = findViewById(R.id.iscriviti);
        ConstraintLayout layoutslide = (ConstraintLayout) findViewById(R.id.content_main);

        ImageView imgTransparent = findViewById(R.id.imageView);
        imgTransparent.setAlpha(200);

        ImageView imgLogo = findViewById(R.id.logo);

        t1.setText(Rest.getIndirizzo());

        Picasso.get().load(Connection.getURL(WebConnection.query.PRODUCTIMAGE,Rest.getLogoURL())).into(imgLogo);

        new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println(Connection.getURL(WebConnection.query.PRODUCTIMAGE, Rest.getBackgroundURL()));
                    layoutslide.setBackground(new BitmapDrawable(Picasso.get().load(Connection.getURL(WebConnection.query.PRODUCTIMAGE, Rest.getBackgroundURL())).get()));
                    //layoutslide.setBackgroundColor(lst_backgroundcolor[position]);
                    //layoutslide.setAlpha((float)0.2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();



        /*
        if(Restaurant.getId() == 1) {
            ConstraintLayout content_main = (ConstraintLayout) findViewById(R.id.content_main);
            TextView indirizzo_main = findViewById(R.id.indirizzo_main);

            content_main.setBackgroundResource(R.drawable.mi_ndujo);
            imgLogo.setImageResource(R.drawable.logo_panino_genuino);
            indirizzo_main.setText(Restaurant.getIndirizzo());

            setTitle("Panino Genuino");
        }

         */

        TextView txtMail = findViewById(R.id.mail);
        TextView txtPassword = findViewById(R.id.password);



        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                String mail = txtMail.getText().toString();
                String password = txtPassword.getText().toString();
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            String par = "idLocale=" + Rest.getId() + "&Mail=" + mail;
                            String tmpJSON = downloadJSON(Connection.getURL(WebConnection.query.SEARCHACCOUNT, par));
                            if(Setting.getDebug())
                            {
                                System.out.println("URL SEARCH ACCOUNT: " + Connection.getURL(WebConnection.query.SEARCHACCOUNT, par));
                                System.out.println("JSON RESULT" + tmpJSON);
                            }

                            JSONArray jsonArray = new JSONArray(tmpJSON);
                            if (jsonArray.length() > 0) {
                                JSONObject obj = jsonArray.getJSONObject(0);

                                UserLogged = JSONUtility.fillUser(obj.toString());
                                if(Setting.getDebug())
                                    System.out.println("USER LOGGED: " + UserLogged.getNumeroTelefono() + ", NOMINATIVO= " + UserLogged.getCognome() + ", " + UserLogged.getNome());

                                if(Setting.getDebug()) {
                                    if(UserLogged.getPassword().equals(txtPassword.getText().toString()))
                                        System.out.println("PASSWORD MATCHED");
                                    else {
                                        System.out.println("PASSWORD NOT EQUALS");
                                        System.out.println("PASSWORD INPUT: " + txtPassword.getText().toString());
                                        System.out.println("PASSWORD CORRECT: " + UserLogged.getPassword());
                                    }
                                }


                                if(UserLogged.getPassword().equals(password.trim()) && UserLogged.getConfermato() == true && UserLogged.getDisabilitato() == false) {

                                    Preference.savePreferences(UserLogged.getNumeroTelefono(), UserLogged.getMail(), UserLogged.getPassword(), MainActivity.this);
                                    cartProducts = new Cart();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Stuff that updates the UI
                                            Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                                            animation1.setDuration(1000);
                                            v.startAnimation(animation1);
                                        }
                                    });

                                    Intent I = new Intent(MainActivity.this, activity_home.class);
                                    I.putExtra("Cart", cartProducts);
                                    I.putExtra("User", UserLogged);
                                    I.putExtra("WebConnection", Connection);
                                    I.putExtra("Restaurant", Rest);
                                    startActivity(I);
                                    MainActivity.this.finish();
                                }
                                else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Stuff that updates the UI
                                            Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                                            animation1.setDuration(1000);
                                            v.startAnimation(animation1);
                                            if(!UserLogged.getPassword().equals(password.trim())) {
                                                Toast.makeText(getApplicationContext(), "Mail e/o Password non corretta", Toast.LENGTH_SHORT).show();
                                            }
                                            else if(!UserLogged.getConfermato())
                                            {
                                                Toast.makeText(getApplicationContext(), "Utente non confermato, controlla la mail ricevuta", Toast.LENGTH_SHORT).show();
                                            }
                                            else if(UserLogged.getDisabilitato())
                                            {
                                                Toast.makeText(getApplicationContext(), "Utente disabilitato", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Stuff that updates the UI
                                        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                                        animation1.setDuration(1000);
                                        v.startAnimation(animation1);
                                        Toast.makeText(getApplicationContext(), "Utente non trovato", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Login non riuscito", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();

            }
        });

        Iscriviti.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                        animation1.setDuration(1000);
                        v.startAnimation(animation1);
                    }
                });
                Intent I = new Intent(MainActivity.this, activity_signin.class);
                I.putExtra("Restaurant", Rest);
                startActivity(I);
                MainActivity.this.finish();

            }
        });

        txtMail.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!txtMail.getText().toString().equals("") && txtMail.getText().toString().equals(getString(R.string.SampleMail))) {
                    txtMail.setText("");
                    txtMail.setTextColor(Color.WHITE);
                }
                if (!hasFocus) {
                    if(txtMail.getText().toString().equals(""))
                    {
                        txtMail.setText(getString(R.string.SampleMail));
                        txtMail.setTextColor(Color.WHITE);
                    }
                }
                else{
                    txtMail.setTextColor(Color.WHITE);
                }

            }
        });
        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!txtPassword.getText().toString().equals("") && txtPassword.getText().toString().equals(getString(R.string.SamplePassword))) {
                    txtPassword.setText("");
                    txtPassword.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    txtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txtPassword.setTextColor(Color.WHITE);
                }
                if (!hasFocus) {
                    if(txtPassword.getText().toString().equals(""))
                    {
                        txtPassword.setTransformationMethod(null);
                        txtPassword.setText(getString(R.string.SamplePassword));
                        txtPassword.setTextColor(Color.WHITE);
                    }
                }
                else
                {
                    txtPassword.setTextColor(Color.WHITE);
                }

            }
        });
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        if(Setting.getDebug())
            System.out.println("onStart");


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
                        String par = "idLocale=" + Rest.getId() + "&Mail=" + Mail;
                        String tmpJSON = downloadJSON(Connection.getURL(WebConnection.query.SEARCHACCOUNT, par));
                        JSONArray jsonArray = new JSONArray(tmpJSON);
                        if (jsonArray.length() > 0) {
                            JSONObject obj = jsonArray.getJSONObject(0);
                            UserLogged = fillUser(obj.toString());

                            cartProducts = new Cart();
                            Intent I = new Intent(MainActivity.this, activity_home.class);
                            I.putExtra("Cart", cartProducts);
                            I.putExtra("User", UserLogged);
                            I.putExtra("WebConnection", Connection);
                            I.putExtra("Restaurant", Rest);

                            pd.dismiss();
                            startActivity(I);
                            MainActivity.this.finish();
                        }
                    }catch (Exception e){UserLogged = new User();}
                }
                pd.dismiss();
            }
        }).start();
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
            Intent I = new Intent(MainActivity.this, activity_map.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            I.putExtra("Restaurant" ,Rest);

            startActivity(I);
            MainActivity.this.finish();
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
        else if(id == R.id.nav_exit)
        {
            Preference.savePreferences("", "", "", this);
            Intent I = new Intent(MainActivity.this, activity_slider.class);
            startActivity(I);
            MainActivity.this.finish();
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
