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
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;

import com.example.spara.restaurant.R;
import com.example.spara.restaurant.object.Cart;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    Cart cartProducts;
    User UserLogged;
    WebConnection Connection;

    String NumeroTelefono;
    String Mail;
    String Password;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("onCreate");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new Listener(),PhoneStateListener.LISTEN_CALL_STATE);


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
        Connection = new WebConnection();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);


        Button login = findViewById(R.id.login);


        TextView t1 = findViewById(R.id.textView);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"font/robotoregular.ttf");
        t1.setTypeface(typeface);

        TextView Iscriviti = findViewById(R.id.iscriviti);

        ImageView imgTransparent = findViewById(R.id.imageView);
        imgTransparent.setAlpha(230);

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
                            String par = "idLocale=" + Restaurant.getId() + "&Mail=" + mail;
                            System.out.println(Connection.getURL(WebConnection.query.SEARCHACCOUNT, par));
                            String tmpJSON = downloadJSON(Connection.getURL(WebConnection.query.SEARCHACCOUNT, par));
                            System.out.println("--" + tmpJSON);

                            JSONArray jsonArray = new JSONArray(tmpJSON);
                            if (jsonArray.length() > 0) {
                                JSONObject obj = jsonArray.getJSONObject(0);
                                UserLogged = new User();
                                UserLogged.setMail(obj.getString("Mail"));
                                UserLogged.setCognome(obj.getString("Cognome"));
                                UserLogged.setPassword(obj.getString("Password"));
                                UserLogged.setNome(obj.getString("Nome"));
                                UserLogged.setIndirizzo(obj.getString("Indirizzo"));
                                UserLogged.setNumeroTelefono(obj.getString("NumeroTelefono"));
                                UserLogged.setConfermato(obj.getBoolean("Confermato"));
                                UserLogged.setAmministratore(obj.getBoolean("Amministratore"));

                                UserLogged.setIdLocale(obj.getLong("idLocale"));
                                UserLogged.setDisabilitato(obj.getBoolean("Disabilitato"));
                                /*
                                if (obj.getString("Disabilitato").equals("0")) {
                                    UserLogged.setDisabilitato(false);

                                } else {
                                    UserLogged.setDisabilitato(true);
                                }
                                */
                                System.out.println(UserLogged.getPassword());
                                System.out.println(txtPassword.getText().toString());
                                if(UserLogged.getPassword().equals(txtPassword.getText().toString().trim()) && UserLogged.getConfermato() == true && UserLogged.getDisabilitato() == false) {

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
                                            if(!UserLogged.getPassword().equals(txtPassword.getText().toString().trim())) {
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
        System.out.println("onStart");
        showLoadingDialog();
        new Thread(new Runnable() {
            public void run() {

                //downloadJSON(Connection.getURL(WebConnection.query.LISTACCOUNTS));
                loadPreferences();
                System.out.println(NumeroTelefono);
                System.out.println(Mail);
                System.out.println(Password);

                if(!NumeroTelefono.equals("") && !Mail.equals("") && !Password.equals(""))
                {
                    try{
                        String par = "idLocale=" + Restaurant.getId() + "&Mail=" + Mail;
                        String tmpJSON = downloadJSON(Connection.getURL(WebConnection.query.SEARCHACCOUNT, par));
                        JSONArray jsonArray = new JSONArray(tmpJSON);
                        if (jsonArray.length() > 0) {
                            JSONObject obj = jsonArray.getJSONObject(0);
                            UserLogged = new User();

                            UserLogged.setMail(obj.getString("Mail"));
                            UserLogged.setCognome(obj.getString("Cognome"));
                            UserLogged.setPassword(obj.getString("Password"));
                            UserLogged.setNome(obj.getString("Nome"));
                            UserLogged.setIndirizzo(obj.getString("Indirizzo"));
                            UserLogged.setNumeroTelefono(obj.getString("NumeroTelefono"));
                            System.out.println("Confermato: " + obj.getString("Confermato"));
                            if(obj.getString("Confermato").equals("0"))
                            {
                                UserLogged.setConfermato(false);
                            }
                            else{
                                UserLogged.setConfermato(true);
                            }
                            if(obj.getString("Amministratore").equals("0"))
                            {
                                UserLogged.setAmministratore(false);
                            }
                            else {
                                UserLogged.setAmministratore(true);
                            }
                            UserLogged.setIdLocale(obj.getLong("idLocale"));
                            if (obj.getString("Disabilitato").equals("0")) {
                                UserLogged.setDisabilitato(false);

                            } else {
                                UserLogged.setDisabilitato(true);
                            }
                            cartProducts = new Cart();
                            Intent I = new Intent(MainActivity.this, activity_home.class);
                            I.putExtra("Cart", cartProducts);
                            I.putExtra("User", UserLogged);
                            I.putExtra("WebConnection", Connection);
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
    /*
    private void savePreferences(String NumeroTelefono, String Mail, String Password) {
        SharedPreferences settings = getSharedPreferences("alPachino",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        // Edit and commit
        editor.putString("NumeroTelefono", NumeroTelefono);
        editor.putString("Mail", Mail);
        editor.putString("Password", Password);
        editor.commit();
    }
     */
    private void loadPreferences() {

        SharedPreferences settings = getSharedPreferences("alPachino",
                Context.MODE_PRIVATE);

        // Get value
        NumeroTelefono = settings.getString("NumeroTelefono", "");
        Mail = settings.getString("Mail", "");
        Password = settings.getString("Password", "");
    }

    /*
    private String downloadJSON(final String urlWebService) {

        try {

            URL url = new URL(urlWebService);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String json;
            while ((json = bufferedReader.readLine()) != null) {
                sb.append(json + "\n");
            }
            return sb.toString();
        } catch (Exception e) {
            System.out.println(e.toString());
            return "";
        }

    }

     */


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
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+Restaurant.getNumeroTelefono()));
                startActivity(callIntent);
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
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+Restaurant.getNumeroTelefono()));
                    startActivity(callIntent);
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
    private class Listener extends PhoneStateListener
    {
        @Override
        public void onCallStateChanged(int state, String incomingNumber)
        {
            switch (state)
            {
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i("CHIAMATA IN USCITA",   "TERMINALE IMPEGNATO");
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i("CHIAMATA IN USCITA",   "IDLE");
            }
        }
    }
}
