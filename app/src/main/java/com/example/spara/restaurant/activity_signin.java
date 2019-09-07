package com.example.spara.restaurant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;

import com.google.android.material.navigation.NavigationView;
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

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class activity_signin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String tmpJSON = null;
    Cart cartProducts;
    User UserLogged;
    WebConnection Connection;


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


        signin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                String mail = Mail.getText().toString();
                String password = Password.getText().toString();
                String confermaPassword = ConfermaPassword.getText().toString();
                String nome = Nome.getText().toString();
                String cognome = Cognome.getText().toString();
                String indirizzo = Indirizzo.getText().toString();
                String citta = Citta.getText().toString();
                String cap = Cap.getText().toString();
                String numeroTelefono = NumeroTelefono.getText().toString();

                /*
                System.out.println(mail);
                System.out.println(getString(R.string.SampleMail));
                System.out.println(password);
                System.out.println(R.string.SamplePassword);
                System.out.println(confermaPassword);
                System.out.println(R.string.SamplePassword);
                System.out.println(nome);
                System.out.println(R.string.SampleNome);
                System.out.println(cognome);
                System.out.println(R.string.SampleCognome);
                System.out.println(indirizzo);
                System.out.println(R.string.SampleIndirizzo);
                System.out.println(numeroTelefono);
                System.out.println(R.string.SampleTelefono);
                */


                if(!mail.equals(getString(R.string.SampleMail)) && confermaPassword.equals(password) && !password.equals(getString(R.string.SamplePassword)) && !confermaPassword.equals(getString(R.string.SampleConfermaPassword)) && !nome.equals(getString(R.string.SampleNome)) && !cognome.equals(getString(R.string.SampleCognome))
                        && !indirizzo.equals(getString(R.string.SampleIndirizzo)) && !numeroTelefono.equals(getString(R.string.SampleTelefono)) && numeroTelefono.length() == 10)
                {

                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                Connection = new WebConnection();

                                String IndirizzoCompleto = Indirizzo + ", " + cap + ", " + citta;

                                User U = new User(numeroTelefono, nome, cognome, mail, IndirizzoCompleto, password, false, false, (long)1, false);

                                String par = "NumeroTelefono=" + U.getNumeroTelefono() + "&Mail=null";
                                String tmpJSON = downloadJSON(Connection.getURL(WebConnection.query.SEARCHACCOUNT, par));
                                JSONArray jsonArray = new JSONArray(tmpJSON);

                                if(jsonArray.length() == 0) {

                                    String Via = U.getIndirizzo().replaceAll(" ", "%20");
                                    System.out.println(Via);
                                    par = "NumeroTelefono=" + U.getNumeroTelefono() + "&Nome=" + U.getNome() + "&Cognome=" + U.getCognome() + "&Mail=" + U.getMail() + "&Indirizzo=" + Via + "&Password=" + U.getPassword() + "&Confermato=" + U.getConfermato() + "&Amministratore=" + U.getAmministratore() +"&idLocale=" + U.getIdLocale() + "&Disabilitato=" + U.getDisabilitato();
                                    showLoadingDialog();
                                    InsertIntoDB(Connection.getURL(WebConnection.query.INSERTUSER, par));

                                    //par="idLocale=1&NumeroTelefono=" + U.getNumeroTelefono();
                                    //InsertIntoDB(Connection.getURL(WebConnection.query.INSERTLOCALUSER, par));

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
            return "";
        }

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

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
