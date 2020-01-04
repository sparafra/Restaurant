package com.example.spara.restaurant.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;

import com.example.spara.restaurant.object.Cart;
import com.example.spara.restaurant.R;
import com.example.spara.restaurant.object.Restaurant;
import com.example.spara.restaurant.object.Setting;
import com.example.spara.restaurant.object.User;
import com.example.spara.restaurant.object.WebConnection;
import com.google.android.material.navigation.NavigationView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.example.spara.restaurant.object.Restaurant;
import com.squareup.picasso.Picasso;

import static com.example.spara.restaurant.object.JSONUtility.downloadJSON;

public class activity_signin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String tmpJSON = null;
    Cart cartProducts;
    User UserLogged;
    WebConnection Connection;
    Restaurant Rest;


    ProgressDialog pd;

    Button signin;
    ImageView back;
    TextView Mail;
    TextView Password;
    TextView ConfermaPassword;
    TextView Nome;
    TextView Cognome;
    TextView Indirizzo;
    TextView Citta;
    TextView Cap;
    TextView NumeroTelefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
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


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);


        //Button and TextView Declaration
        signin = findViewById(R.id.signin);
        back = findViewById(R.id.indietro);
        Mail = findViewById(R.id.Mail);
        Password = findViewById(R.id.Password);
        ConfermaPassword = findViewById(R.id.ConfermaPassword);
        Nome = findViewById(R.id.Nome);
        Cognome = findViewById(R.id.Cognome);
        Indirizzo = findViewById(R.id.Indirizzo);
        Citta = findViewById(R.id.Citta);
        Cap = findViewById(R.id.Cap);
        NumeroTelefono = findViewById(R.id.NumeroTelefono);

        //Background Image Declaration
        ImageView imgTransparent = findViewById(R.id.imageView);
        imgTransparent.setAlpha(230);

        Rest = (Restaurant) getIntent().getParcelableExtra("Restaurant");

        ConstraintLayout layoutslide = (ConstraintLayout) findViewById(R.id.content_signin);

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


        signin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                String mail = Mail.getText().toString().trim().replaceAll(" ", "%20");
                String password = Password.getText().toString().trim().replaceAll(" ", "%20");
                String confermaPassword = ConfermaPassword.getText().toString().trim().replaceAll(" ", "%20");
                String nome = Nome.getText().toString().trim().replaceAll(" ", "%20");
                String cognome = Cognome.getText().toString().trim().replaceAll(" ", "%20");
                String indirizzo = Indirizzo.getText().toString().trim().replaceAll(" ", "%20");
                String citta = Citta.getText().toString().trim().replaceAll(" ", "%20");
                String cap = Cap.getText().toString().trim().replaceAll(" ", "%20");
                String numeroTelefono = NumeroTelefono.getText().toString().trim();




                if(!mail.equals(getString(R.string.SampleMail)) && confermaPassword.equals(password) && !password.equals(getString(R.string.SamplePassword)) && !confermaPassword.equals(getString(R.string.SampleConfermaPassword)) && !nome.equals(getString(R.string.SampleNome)) && !cognome.equals(getString(R.string.SampleCognome))
                        && !indirizzo.equals(getString(R.string.SampleIndirizzo)) && !numeroTelefono.equals(getString(R.string.SampleTelefono)) && numeroTelefono.length() == 10)
                {

                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                Connection = new WebConnection();

                                String IndirizzoCompleto = indirizzo + ", " + cap + ", " + citta;
                                IndirizzoCompleto = IndirizzoCompleto.replaceAll(" ", "%20");
                                if(Setting.getDebug())
                                    System.out.println("INITIALIZE USER");
                                User U = new User(numeroTelefono, nome, cognome, mail, IndirizzoCompleto, password, false, false, Rest.getId(), false);

                                String par = "NumeroTelefono=" + U.getNumeroTelefono();
                                String tmpJSON = downloadJSON(Connection.getURL(WebConnection.query.SEARCHACCOUNTBYID, par));
                                JSONObject obj = new JSONObject(tmpJSON);

                                if(obj.length() == 0) {

                                    par = "NumeroTelefono=" + U.getNumeroTelefono() + "&Nome=" + U.getNome() + "&Cognome=" + U.getCognome() + "&Mail=" + U.getMail() + "&Indirizzo=" + U.getIndirizzo() + "&Password=" + U.getPassword() + "&Confermato=" + U.getConfermato() + "&Amministratore=" + U.getAmministratore() +"&idLocale=" + U.getIdLocale() + "&Disabilitato=" + U.getDisabilitato();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Stuff that updates the UI
                                            showLoadingDialog();                                        }
                                    });

                                    InsertIntoDB(Connection.getURL(WebConnection.query.INSERTUSER, par));

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Stuff that updates the UI
                                            pd.dismiss();
                                            Toast.makeText(getApplicationContext(), "Conferma il tuo account tramite la mail ricevuta e inserisci le tue credenziali per accedere", Toast.LENGTH_LONG).show();
                                        }
                                    });

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Stuff that updates the UI
                                            Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                                            animation1.setDuration(1000);
                                            v.startAnimation(animation1);
                                        }
                                    });

                                    Intent I = new Intent(activity_signin.this, MainActivity.class);
                                    startActivity(I);
                                    activity_signin.this.finish();
                                }
                                else
                                {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Stuff that updates the UI
                                            Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                                            animation1.setDuration(1000);
                                            v.startAnimation(animation1);
                                            Toast.makeText(getApplicationContext(), "Utente già presente con il seguente numero di telefono: " + U.getNumeroTelefono(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }catch (Exception e){e.printStackTrace();}
                        }
                    }).start();


                }
                else{
                    if(mail.equals(getString(R.string.SampleMail)) || mail.equals(""))
                    {
                        Mail.setTextColor(Color.RED);
                        Toast.makeText(getApplicationContext(), "Inserire la Mail", Toast.LENGTH_SHORT).show();
                    }
                    else if(password.equals(getString(R.string.SamplePassword)) || password.equals(""))
                    {
                        Password.setTextColor(Color.RED);
                        Toast.makeText(getApplicationContext(), "Inserire una Password", Toast.LENGTH_SHORT).show();
                    }
                    else if(confermaPassword.equals(getString(R.string.SampleConfermaPassword)) || confermaPassword.equals(""))
                    {
                        ConfermaPassword.setTextColor(Color.RED);
                        Toast.makeText(getApplicationContext(), "Inserire di nuovo la Password", Toast.LENGTH_SHORT).show();
                    }
                    else if(!confermaPassword.equals(password))
                    {
                        Password.setTextColor(Color.RED);
                        ConfermaPassword.setTextColor(Color.RED);
                        Toast.makeText(getApplicationContext(), "Le Password non coincidono", Toast.LENGTH_SHORT).show();
                    }
                    else if(confermaPassword.equals(password))
                    {
                        Password.setTextColor(Color.WHITE);
                        ConfermaPassword.setTextColor(Color.WHITE);
                    }
                    else if(nome.equals(getString(R.string.SampleNome)) || nome.equals(""))
                    {
                        Nome.setTextColor(Color.RED);
                        Toast.makeText(getApplicationContext(), "Inserire il tuo Nome", Toast.LENGTH_SHORT).show();
                    }
                    else if(cognome.equals(getString(R.string.SampleCognome)) || cognome.equals(""))
                    {
                        Cognome.setTextColor(Color.RED);
                        Toast.makeText(getApplicationContext(), "Inserire il tuo Cognome", Toast.LENGTH_SHORT).show();
                    }
                    else if(indirizzo.equals(getString(R.string.SampleIndirizzo)) || indirizzo.equals(""))
                    {
                        Indirizzo.setTextColor(Color.RED);
                        Toast.makeText(getApplicationContext(), "Inserire il tuo indirizzo", Toast.LENGTH_SHORT).show();
                    }
                    else if(Citta.equals(getString(R.string.SampleCittà)) || indirizzo.equals(""))
                    {
                        Citta.setTextColor(Color.RED);
                        Toast.makeText(getApplicationContext(), "Inserire la tua città", Toast.LENGTH_SHORT).show();
                    }
                    else if(Cap.equals(getString(R.string.SampleCap)) || indirizzo.equals(""))
                    {
                        Cap.setTextColor(Color.RED);
                        Toast.makeText(getApplicationContext(), "Inserire il tuo cap", Toast.LENGTH_SHORT).show();
                    }
                    else if(numeroTelefono.equals(getString(R.string.SampleTelefono)) || numeroTelefono.equals(""))
                    {
                        NumeroTelefono.setTextColor(Color.RED);
                        Toast.makeText(getApplicationContext(), "Inserire il tuo Numero di Telefono", Toast.LENGTH_SHORT).show();
                    }
                    else if(numeroTelefono.length() != 10)
                    {
                        NumeroTelefono.setTextColor(Color.RED);
                        Toast.makeText(getApplicationContext(), "Inserire il tuo Numero di Telefono ex: 3280000000", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                        animation1.setDuration(1000);
                        v.startAnimation(animation1);
                    }
                });
                if(Setting.getDebug())
                    System.out.println("BACK <--");
                Intent I = new Intent(activity_signin.this, MainActivity.class);
                startActivity(I);
                activity_signin.this.finish();



            }
        });

        Mail.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!Mail.getText().toString().equals("") && Mail.getText().toString().equals(getString(R.string.SampleMail))) {
                    Mail.setText("");
                    Mail.setTextColor(Color.WHITE);
                }
                if (!hasFocus) {
                    if(Mail.getText().toString().equals(""))
                    {
                        Mail.setText(getString(R.string.SampleMail));
                        Mail.setTextColor(Color.WHITE);
                    }
                }
                else{
                    Mail.setTextColor(Color.WHITE);
                }

            }
        });
        Password.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!Password.getText().toString().equals("") && Password.getText().toString().equals(getString(R.string.SamplePassword))) {
                    Password.setText("");
                    Password.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    Password.setTextColor(Color.WHITE);
                }
                if (!hasFocus) {
                    if(Password.getText().toString().equals(""))
                    {
                        Password.setTransformationMethod(null);
                        Password.setText(getString(R.string.SamplePassword));
                        Password.setTextColor(Color.WHITE);
                    }
                }
                else
                {
                    Password.setTextColor(Color.WHITE);
                }

            }
        });
        ConfermaPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!ConfermaPassword.getText().toString().equals("") && ConfermaPassword.getText().toString().equals(getString(R.string.SampleConfermaPassword))) {
                    ConfermaPassword.setText("");
                    ConfermaPassword.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    ConfermaPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ConfermaPassword.setTextColor(Color.WHITE);
                }
                if (!hasFocus) {
                    if(ConfermaPassword.getText().toString().equals(""))
                    {
                        ConfermaPassword.setTransformationMethod(null);
                        ConfermaPassword.setText(getString(R.string.SampleConfermaPassword));
                        ConfermaPassword.setTextColor(Color.WHITE);
                    }
                }
                else{
                    ConfermaPassword.setTextColor(Color.WHITE);
                }

            }
        });
        Nome.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!Nome.getText().toString().equals("") && Nome.getText().toString().equals(getString(R.string.SampleNome))) {
                    Nome.setText("");
                    Nome.setTextColor(Color.WHITE);
                }
                if (!hasFocus) {
                    if(Nome.getText().toString().equals(""))
                    {
                        Nome.setText(getString(R.string.SampleNome));
                        Nome.setTextColor(Color.WHITE);
                    }
                }
                else
                {
                    Nome.setTextColor(Color.WHITE);
                }
            }
        });
        Cognome.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!Cognome.getText().toString().equals("") && Cognome.getText().toString().equals(getString(R.string.SampleCognome))) {
                    Cognome.setText("");
                    Cognome.setTextColor(Color.WHITE);
                }
                if (!hasFocus) {
                    if(Cognome.getText().toString().equals(""))
                    {
                        Cognome.setText(getString(R.string.SampleCognome));
                        Cognome.setTextColor(Color.WHITE);
                    }
                }
                else{
                    Cognome.setTextColor(Color.WHITE);

                }

            }
        });
        Indirizzo.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!Indirizzo.getText().toString().equals("") && Indirizzo.getText().toString().equals(getString(R.string.SampleIndirizzo))) {
                    Indirizzo.setText("");
                    Indirizzo.setTextColor(Color.WHITE);
                }
                if (!hasFocus) {
                    if(Indirizzo.getText().toString().equals(""))
                    {
                        Indirizzo.setText(getString(R.string.SampleIndirizzo));
                        Indirizzo.setTextColor(Color.WHITE);
                    }
                }
                else
                {
                    Indirizzo.setTextColor(Color.WHITE);
                }
            }
        });
        Citta.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!Citta.getText().toString().equals("") && Citta.getText().toString().equals(getString(R.string.SampleCittà))) {
                    Citta.setText("");
                    Citta.setTextColor(Color.WHITE);
                }
                if (!hasFocus) {
                    if(Citta.getText().toString().equals(""))
                    {
                        Citta.setText(getString(R.string.SampleCittà));
                        Citta.setTextColor(Color.WHITE);
                    }
                }
                else
                {
                    Citta.setTextColor(Color.WHITE);
                }
            }
        });
        Cap.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!Cap.getText().toString().equals("") && Cap.getText().toString().equals(getString(R.string.SampleCap))) {
                    Cap.setText("");
                    Cap.setTextColor(Color.WHITE);
                }
                if (!hasFocus) {
                    if(Cap.getText().toString().equals(""))
                    {
                        Cap.setText(getString(R.string.SampleCap));
                        Cap.setTextColor(Color.WHITE);
                    }
                }
                else
                {
                    Cap.setTextColor(Color.WHITE);
                }
            }
        });
        NumeroTelefono.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!NumeroTelefono.getText().toString().equals("") && NumeroTelefono.getText().toString().equals(getString(R.string.SampleTelefono))) {
                    NumeroTelefono.setText("");
                    NumeroTelefono.setTextColor(Color.WHITE);
                }
                if (!hasFocus) {
                    if(NumeroTelefono.getText().toString().equals(""))
                    {
                        NumeroTelefono.setText(getString(R.string.SampleTelefono));
                        NumeroTelefono.setTextColor(Color.WHITE);
                    }
                    else if(NumeroTelefono.getText().toString().length() != 10)
                    {
                        NumeroTelefono.setTextColor(Color.RED);
                        Toast.makeText(getApplicationContext(), "Inserire il tuo Numero di Telefono (10 cifre) ex: 3890000000", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    NumeroTelefono.setTextColor(Color.WHITE);
                }
            }
        });


    }


    private void showLoadingDialog() {
        pd = new ProgressDialog(this, R.style.DialogTheme);
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        pd.show();
    }
    private void InsertIntoDB(final String urlWebService) {

        try {

            URL url = new URL(urlWebService);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();


            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String json;
            while ((json = bufferedReader.readLine()) != null) {
                if (json.contains("Inserito")) {
                    sb.append("Inserito");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        } else if (id == R.id.nav_ordini) {

        } else if (id == R.id.nav_carrello) {

        } else if (id == R.id.nav_map) {

        } else if (id == R.id.nav_account) {

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
