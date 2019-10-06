package com.example.spara.restaurant.activity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;

import com.example.spara.restaurant.object.JSONUtility;
import com.example.spara.restaurant.object.Preference;
import com.example.spara.restaurant.object.Setting;
import com.example.spara.restaurant.object.Cart;
import com.example.spara.restaurant.object.Ingredient;
import com.example.spara.restaurant.object.Product;
import com.example.spara.restaurant.R;
import com.example.spara.restaurant.object.Restaurant;
import com.example.spara.restaurant.object.User;
import com.example.spara.restaurant.object.WebConnection;
import com.example.spara.restaurant.service.background_alarm;
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
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import static com.example.spara.restaurant.object.JSONUtility.fillProductsList;

public class activity_home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //PERMISSION CODE
    //CALL - 1
    //INTERNET - 2

    Long TimeToAlarm = 5L * 60L * 1000L; //5 minutes

    public static final int REQUEST_CODE=101;

    ListView list;

    ArrayList<Product> listProducts;
    int posSelected = -1;
    boolean pizzeSelected = false;
    Cart cartProducts;
    User UserLogged;
    WebConnection Connection;
    ProgressDialog pd;

    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //MY CODE

        if(Setting.getDebug())
            System.out.println("INITIALIZE PREFERENCES");

        //Get INTENT Extra
        cartProducts = (Cart) getIntent().getParcelableExtra("Cart");
        UserLogged = (User) getIntent().getParcelableExtra("User");
        Connection = (WebConnection) getIntent().getParcelableExtra("WebConnection");

        //Listview declaration
        list = (ListView) findViewById(R.id.list);

        //Background Image
        ImageView imgTransparent = findViewById(R.id.imageView);
        imgTransparent.setAlpha(200);

        //Image and Button declaration
        ImageView imgProduct = findViewById(R.id.imgProdotto);
        ImageView btnFritti = findViewById(R.id.Fritti);
        //ImageView btnPizze = findViewById(R.id.Pizze);
        ImageView btnPanini = findViewById(R.id.Panini);
        ImageView btnAddCart = findViewById(R.id.addCart);

        //Starting background service

        if(Setting.getDebug())
            System.out.println("INITIALIZE ALARM");

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, background_alarm.class);
        intent.putExtra("UserNumber", UserLogged.getNumeroTelefono());

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        /*
        Alarm will be triggered approximately after 5 Min and will be repeated every hour after that
        */

        if(Setting.getDebug())
            System.out.println("CALL START ALARM METHOD");

        startAlarm(TimeToAlarm);


        /*
        if(Restaurant.getId() == 1) {
            ConstraintLayout content_main = (ConstraintLayout) findViewById(R.id.content_home);
            //TextView indirizzo_main = findViewById(R.id.indirizzo_main);
            //ImageView imgLogo = findViewById(R.id.logo);

            content_main.setBackgroundResource(R.drawable.mi_ndujo);
            //imgLogo.setImageResource(R.drawable.logo_panino_genuino);
            //indirizzo_main.setText(Restaurant.getIndirizzo());
            imgProduct.setImageResource(R.drawable.bisignano);
            setTitle("Panino Genuino");
        }

         */




        //User Confirm Control
        if(!UserLogged.getConfermato())
        {
            Toast.makeText(getApplicationContext(), "ATTENZIONE: Utente non confermato. Effettua la conferma tramite la mail ricevuta per poter creare i tuoi ordini", Toast.LENGTH_LONG).show();
        }
        if(UserLogged.getAmministratore())
        {
            if(Setting.getDebug())
                System.out.println("SETTING THE ADDITIONAL PANEL FOR ADMIN");

            navigationView.getMenu().findItem(R.id.nav_gestione_ordini).setVisible(true);
        }


        //ListView click on item event
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                        animation1.setDuration(2000);
                        view.startAnimation(animation1);                    }
                });

                posSelected = position;
                if(posSelected == 0 && pizzeSelected)
                {
                    Intent I = new Intent(activity_home.this, activity_personalizza.class);
                    I.putExtra("Cart", cartProducts);
                    I.putExtra("User", UserLogged);
                    I.putExtra("WebConnection" ,Connection);
                    startActivity(I);
                    activity_home.this.finish();
                }
                else {
                    if (pizzeSelected) {

                        if(Setting.getDebug())
                            System.out.println("SETTING THE IMAGE PRODUCT: " + listProducts.get(position - 1).getImageURL());

                        if (listProducts.get(position - 1).getImageURL().equals("nd"))
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Stuff that updates the UI
                                    imgProduct.setImageResource(R.drawable.logo);
                                    Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                                    animation1.setDuration(2000);
                                    imgProduct.startAnimation(animation1);                    }
                            });
                        }
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Stuff that updates the UI
                                    System.out.println(Connection.getURL(WebConnection.query.PRODUCTIMAGE, listProducts.get(position - 1).getImageURL()));
                                    Picasso.get().load(Connection.getURL(WebConnection.query.PRODUCTIMAGE, listProducts.get(position - 1).getImageURL())).into(imgProduct);
                                    Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                                    animation1.setDuration(2000);
                                    imgProduct.startAnimation(animation1);                    }
                            });
                        }
                    } else {
                        if (listProducts.get(position).getImageURL().equals("null")) {
                            if(Setting.getDebug())
                                System.out.println("IMAGE OF PRODUCT NOT DETECTED, SETTING THE DEFAULT IMAGE PRODUCT" );

                            imgProduct.setImageResource(R.drawable.logo);

                        }
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Stuff that updates the UI
                                    Picasso.get().load(Connection.getURL(WebConnection.query.PRODUCTIMAGE, listProducts.get(position).getImageURL())).into(imgProduct);
                                    Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                                    animation1.setDuration(2000);
                                    imgProduct.startAnimation(animation1);                    }
                            });
                        }
                    }

                    HashMap<String, String> Map = (HashMap) parent.getItemAtPosition(position);
                    String selectedItem = Map.get("First Line");
                }
            }
        });

        //Buttons click event
        btnFritti.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                        animation1.setDuration(2000);
                        v.startAnimation(animation1);                    }
                });

                pizzeSelected = false;

                showLoadingDialog();
                new Thread(new Runnable() {
                    public void run() {
                        if(Setting.getDebug())
                            System.out.println("FILTER PANINI");
                        String tmpJSON = JSONUtility.downloadJSON(Connection.getURL(WebConnection.query.BURGERFRIESINGREDIENTS));
                        listProducts = fillProductsList(tmpJSON);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Stuff that updates the UI
                                loadIntoListView(listProducts);
                                pd.dismiss();
                            }
                        });
                    }
                }).start();

            }
        });

        /*
        btnPizze.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                        animation1.setDuration(2000);
                        v.startAnimation(animation1);                    }
                });
                pizzeSelected = true;
                showLoadingDialog();
                new Thread(new Runnable() {
                    public void run() {
                        if(Setting.getDebug())
                            System.out.println("FILTER PIZZE");
                        String tmpJSON = JSONUtility.downloadJSON(Connection.getURL(WebConnection.query.PIZZEINGREDIENTS));
                        listProducts = fillProductsList(tmpJSON);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Stuff that updates the UI
                                loadIntoListView(listProducts);
                                pd.dismiss();
                            }
                        });
                    }
                }).start();

            }
        });

         */

        btnPanini.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                        animation1.setDuration(2000);
                        v.startAnimation(animation1);                    }
                });

                pizzeSelected = false;
                showLoadingDialog();
                new Thread(new Runnable() {
                    public void run() {
                        if(Setting.getDebug())
                            System.out.println("FILTER SALAD");
                        String tmpJSON = JSONUtility.downloadJSON(Connection.getURL(WebConnection.query.SALADSINGREDIENTS));
                        listProducts = fillProductsList(tmpJSON);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // Stuff that updates the UI
                                loadIntoListView(listProducts);
                                pd.dismiss();
                            }
                        });
                    }
                }).start();
                //loadIntoListView(listProducts);
            }
        });

        btnAddCart.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                        animation1.setDuration(1000);

                        btnAddCart.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
                        v.startAnimation(animation1);
                        animation1.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                btnAddCart.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                });

                if(posSelected != -1) {

                    if(Setting.getDebug())
                        System.out.println("ADD PRODUCT TO CART");

                    if(pizzeSelected)
                    {

                        boolean trovato = false;
                        for(int k=0; k<cartProducts.getListProducts().size() && !trovato; k++)
                        {
                            if(cartProducts.getListProducts().get(k).getId() == listProducts.get(posSelected-1).getId()) {
                                cartProducts.getListProducts().get(k).setQuantity(cartProducts.getListProducts().get(k).getQuantity()+1);
                                trovato = true;
                            }
                        }
                        if(!trovato)
                            cartProducts.addProduct(listProducts.get(posSelected - 1));

                        Toast.makeText(getApplicationContext(), listProducts.get(posSelected-1).getNome() + " aggiunto al carrello", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        boolean trovato = false;
                        for(int k=0; k<cartProducts.getListProducts().size() && !trovato; k++)
                        {
                            if(cartProducts.getListProducts().get(k).getId() == listProducts.get(posSelected).getId()) {
                                cartProducts.getListProducts().get(k).setQuantity(cartProducts.getListProducts().get(k).getQuantity()+1);
                                trovato = true;
                            }
                        }
                        if(!trovato)
                            cartProducts.addProduct(listProducts.get(posSelected));
                        Toast.makeText(getApplicationContext(), listProducts.get(posSelected).getNome() + " aggiunto al carrello", Toast.LENGTH_SHORT).show();
                    }
                    posSelected = -1;
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Seleziona un Prodotto", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPanini.callOnClick();

    }

    private void showLoadingDialog() {
        pd = new ProgressDialog(this, R.style.DialogTheme);
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        pd.show();
    }
    private void loadIntoListView(ArrayList<Product> listP)
    {
        if(Setting.getDebug())
            System.out.println("LOAD PRODUCT INTO LISTVIEW");

        List<HashMap<String, String>> listitems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, listitems, R.layout.list_item, new String[]{"First Line", "Second Line"}, new int[]{R.id.text1, R.id.text2});

        if(listP.size() > 0 && listP.get(0).getTipo().equals("Pizza")) {
            HashMap<String, String> tmp = new HashMap<>();
            tmp.put("First Line", "Personalizza la tua pizza!");
            listitems.add(tmp);
        }

        List<Ingredient> listI;
        for(int k=0; k<listP.size(); k++)
        {
            HashMap<String, String> resultMap = new HashMap<>();
            listI = listP.get(k).getListIngredienti();
            resultMap.put("First Line", listP.get(k).getNome());
            String Ingredienti = "";
            for(int i=0; i<listI.size(); i++)
            {
                if(!listI.get(i).getNome().equals("null")) {
                    if (Ingredienti.equals("")) {
                        Ingredienti += listI.get(i).getNome();
                    } else {
                        Ingredienti += ", " + listI.get(i).getNome();
                    }
                }
            }
            Ingredienti += " €" + listP.get(k).getPrezzo();
            resultMap.put("Second Line", Ingredienti);
            listitems.add(resultMap);
        }
        list.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home);
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
            // Handle the camera action

        }
        else if (id == R.id.nav_ordini)
        {
            Intent I = new Intent(activity_home.this, activity_ordini.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            startActivity(I);
            activity_home.this.finish();
        }
        else if (id == R.id.nav_gestione_ordini)
        {
            Intent I = new Intent(activity_home.this, activity_gestione_ordini.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            startActivity(I);
            activity_home.this.finish();
        }
        else if (id == R.id.nav_carrello)
        {
            Intent I = new Intent(activity_home.this, activity_carrello.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            startActivity(I);
            activity_home.this.finish();
        }
        else if (id == R.id.nav_map)
        {
            Intent I = new Intent(activity_home.this, activity_map.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            startActivity(I);
            activity_home.this.finish();
        }
        else if (id == R.id.nav_account)
        {

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
                callPhone(Restaurant.getNumeroTelefono());

            }

        }
        else if (id == R.id.nav_exit)
        {
            Preference.savePreferences("", "", "", this);
            startActivity(new Intent(activity_home.this, MainActivity.class));
            activity_home.this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home);
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
                    callPhone(Restaurant.getNumeroTelefono());
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
                    startActivity(new Intent(activity_home.this, MainActivity.class));
                    activity_home.this.finish();
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



    private void startAlarm(Long interval) {

        if(Setting.getDebug())
            System.out.println("ALARM SETUP INITIALING");

        Long triggerAtMillis = System.currentTimeMillis() + interval;

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, interval, pendingIntent);
        /*
        1st Param : Type of the Alarm

        2nd Param : Time in milliseconds when the alarm will be triggered first

        3rd Param : Interval after which alarm will be repeated . You can only use any one of the AlarmManager constants

        4th Param :Pending Intent

        */

        if(Setting.getDebug())
            System.out.println("ALARM SETUP COMPLETED");

    }
}
