package com.example.spara.restaurant.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.example.spara.restaurant.object.AlertDialogFragment;
import com.example.spara.restaurant.object.Cart;
import com.example.spara.restaurant.object.Ingredient;
import com.example.spara.restaurant.object.JSONUtility;
import com.example.spara.restaurant.object.Notice;
import com.example.spara.restaurant.object.Order;
import com.example.spara.restaurant.object.Preference;
import com.example.spara.restaurant.object.Product;
import com.example.spara.restaurant.R;
import com.example.spara.restaurant.object.Restaurant;
import com.example.spara.restaurant.object.Setting;
import com.example.spara.restaurant.object.User;
import com.example.spara.restaurant.object.WebConnection;
import com.example.spara.restaurant.custom_adapter.customAdapter_carrello;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class activity_carrello extends AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener, AlertDialogFragment.EditNameDialogListener {


    ProgressDialog pd;

    Cart cartProducts;
    User UserLogged;
    WebConnection Connection;
    Restaurant Rest;

    ListView list;
    int posSelected = -1;

    int idOrderInserted = -1;
    int idProductEdited = -1;

    Order O;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrello);

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_carrello);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //MY CODE

        //ListView Declaration
        list = (ListView) findViewById(R.id.listProdotti);

        //Background Image Declaration
        ImageView imgTransparent = findViewById(R.id.imageView);
        imgTransparent.setAlpha(230);

        //Image and Button Declaration
        ImageView btnClearCart = findViewById(R.id.svuotaCarrello);
        Button btnOrder = findViewById(R.id.prenota);
        ImageView imgProduct = findViewById(R.id.imgProduct);

        if(Setting.getDebug())
            System.out.println("INITIALIZE PREFERENCES");

        //Get INTENT Extra
        cartProducts = (Cart) getIntent().getParcelableExtra("Cart");
        UserLogged = (User) getIntent().getParcelableExtra("User");
        Connection = (WebConnection) getIntent().getParcelableExtra("WebConnection");
        Rest = (Restaurant) getIntent().getParcelableExtra("Restaurant");

        if(UserLogged.getAmministratore())
        {
            if(Setting.getDebug())
                System.out.println("SETTING THE ADDITIONAL PANEL FOR ADMIN");

            navigationView.getMenu().findItem(R.id.nav_gestione_ordini).setVisible(true);
        }

        //Setting Visibility of Button
        if(cartProducts.getListProducts().size() > 0) {
            loadIntoListView(cartProducts.getListProducts());
        }
        else{
            if(Setting.getDebug())
                System.out.println("BUTTON HIDE ORDER AND CLEARCART");

            btnClearCart.setVisibility(View.INVISIBLE);
            btnOrder.setVisibility(View.INVISIBLE);
        }


        //List Click on Item Event
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                //Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_SHORT).show();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                        animation1.setDuration(2000);
                        view.startAnimation(animation1);                    }
                });

                new Thread(new Runnable() {
                    public void run() {
                        posSelected = position;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Stuff that updates the UI
                                //btnRemoveProduct.setVisibility(View.VISIBLE);
                            }
                        });

                        if (cartProducts.getListProducts().get(position).getImageURL().equals("nd"))
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imgProduct.setImageResource(R.drawable.logo);
                                    Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                                    animation1.setDuration(2000);
                                    imgProduct.startAnimation(animation1);
                                }
                            });
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Stuff that updates the UI
                                    Picasso.get().load(Connection.getURL(WebConnection.query.PRODUCTIMAGE, cartProducts.getListProducts().get(position).getImageURL())).into(imgProduct);
                                    Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                                    animation1.setDuration(2000);
                                    imgProduct.startAnimation(animation1);
                                }
                            });
                        }
                    }
                }).start();

            }
        });

        //Button Click Event
        btnClearCart.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        if(Setting.getDebug())
                            System.out.println("CLEAR CART");

                        cartProducts.clear();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Stuff that updates the UI
                                loadIntoListView(cartProducts.getListProducts());

                                Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                                animation1.setDuration(1000);
                                v.startAnimation(animation1);
                                animation1.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        btnClearCart.setVisibility(View.INVISIBLE);
                                        btnOrder.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });

                            }
                        });

                    }
                }).start();
            }
        });



        btnOrder.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                if(Setting.getDebug())
                    System.out.println("PREPARE ORDER");

                //INSERT INTO DATABASE ORDER WITH THIS CART
                if (UserLogged.getConfermato()) {
                    if(Setting.getDebug())
                        System.out.println("INITIALIZE ORDER");

                    O = new Order();
                    O.setListProducts(cartProducts.getListProducts());
                    O.setStato("Richiesto");
                    O.setAsporto(false);
                    O.setNumeroTelefono(UserLogged.getNumeroTelefono());

                    Date currentTime = Calendar.getInstance().getTime();
                    SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ITALIAN);
                    String date_time = datetime.format(currentTime);


                    O.setDateTime(currentTime);
                    O.setCosto(cartProducts.getTotalCost());
                    if(Setting.getDebug())
                        System.out.println("CART COSTO: " + cartProducts.getTotalCost());

                    try {
                        showLoadingDialog();
                        new Thread(new Runnable() {
                            public void run() {
                                InsertOrderProduct(O);

                                if(Setting.getDebug())
                                    System.out.println("SEND NOTICE TO ALL ADMIN");

                                String par = "idLocale=" + Rest.getId() + "&Amministratore=true";
                                String tmpJSON = JSONUtility.downloadJSON(Connection.getURL(WebConnection.query.ADMINLIST, par));
                                List<User> AdminUsers = JSONUtility.fillUsers(tmpJSON);

                                for(int k=0; k<AdminUsers.size(); k++)
                                {
                                    if(Setting.getDebug())
                                        System.out.println("PREPARE NOTICE FOR: " + AdminUsers.get(k).getNumeroTelefono());

                                    Notice N = new Notice();
                                    N.setStato(false);
                                    N.setRicevutoDa(AdminUsers.get(k).getNumeroTelefono());
                                    N.setMessaggio("Ricevuto nuovo ordine con id: " + O.getId());
                                    N.setIdLocale(Rest.getId());
                                    N.setCreatoDa(O.getNumeroTelefono());
                                    N.setTitolo("Nuovo Ordine");
                                    N.setTipo("new_order");

                                    par = "Stato=" + N.getStato() + "&CreatoDa=" + N.getCreatoDa() + "&Messaggio=" + N.getMessaggio().replaceAll(" ", "%20") + "&idLocale=" + N.getIdLocale() +"&RicevutoDa=" + N.getRicevutoDa() + "&Tipo=" + N.getTipo() + "&Titolo=" + N.getTitolo().replaceAll(" ", "%20");
                                    InsertIntoDB(Connection.getURL(WebConnection.query.INSERTNOTICE, par));

                                }

                                pd.dismiss();
                                showAlertDialog();
                                cartProducts.clear();
                            }
                        }).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                                animation1.setDuration(1000);
                                v.startAnimation(animation1);
                            }
                        });
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Conferma il tuo account tramite la mail ricevuta per poter effettuare un ordine", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showAlertDialog() {
        FragmentManager fm = getSupportFragmentManager();
        AlertDialogFragment alertDialog = AlertDialogFragment.newInstance("Ordine a domicilio?");
        alertDialog.show(fm, "fragment_alert");
    }
    private void showLoadingDialog() {
        pd = new ProgressDialog(this, R.style.DialogTheme);
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        pd.show();
    }
    @Override
    public void onFinishEditDialog(String inputText) {

        if(inputText.equals("Si"))
        {
            if(Setting.getDebug())
                System.out.println("UPDATE ORDER TO INSERT DOMICILIO");

            O.setAsporto(true);
            O.setCosto(O.getTotaleCosto() + 1);

            SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ITALIAN);
            String date_time = datetime.format(O.getDateTime());

            String par = "idOrdine=" + O.getId() + "&Stato=" + O.getStato() + "&Asporto=" + O.getAsporto() + "&Costo=" + O.getTotaleCosto() +"&NumeroTelefono=" + UserLogged.getNumeroTelefono() + "&DataOra=" + date_time.replaceAll(" ", "%20");

            new Thread(new Runnable() {
                public void run() {
                    InsertIntoDB(Connection.getURL(WebConnection.query.UPDATEORDER, par));
                }
            }).start();
        }
        Intent I = new Intent(activity_carrello.this, activity_ordini.class);
        I.putExtra("Cart", cartProducts);
        I.putExtra("User", UserLogged);
        I.putExtra("WebConnection", Connection);
        startActivity(I);
        activity_carrello.this.finish();
    }


    private void InsertOrderProduct(Order O)
    {

        if(Setting.getDebug())
            System.out.println("INSERT ORDER");

        SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ITALIAN);
        String date_time = datetime.format(O.getDateTime());
        String par = "Asporto=" + O.getAsporto() + "&Pagato=false" +"&NumeroTelefono=" + UserLogged.getNumeroTelefono() + "&idLocale=" + UserLogged.getIdLocale() + "&Costo=" + O.getTotaleCosto() + "&DataOra="+ date_time.replaceAll(" ", "%20") ;

        try {
            idOrderInserted = InsertIntoDBWithId(Connection.getURL(WebConnection.query.INSERTORDER, par));
            O.setId(idOrderInserted);
            if(Setting.getDebug())
                System.out.println("ORDER INSERTED CORRECTLY");

        }catch (Exception e){}



        for (int k = 0; k < O.getListProducts().size(); k++) {
            if (O.getListProducts().get(k).getNome().equals("Personalizzata")) {

                if(Setting.getDebug())
                    System.out.println("INSERT CUSTOM PRODUCT FOR ORDER: " + O.getId());

                par = "Nome=" + O.getListProducts().get(k).getNome() + "&Prezzo=" + O.getListProducts().get(k).getPrezzo() + "&Tipo=" + O.getListProducts().get(k).getTipo() +"&idLocale=" + Rest.getId() + "&ImageURL="+ O.getListProducts().get(k).getImageURL() ;
                idProductEdited = InsertIntoDBWithId(Connection.getURL(WebConnection.query.INSERTPRODUCT, par));

                for(int i=0; i<O.getListProducts().get(k).getListIngredienti().size(); i++)
                {
                    Ingredient I = O.getListProducts().get(k).getListIngredienti().get(i);
                    par = "idProdotto=" + String.valueOf(idProductEdited) + "&idIngrediente=" + I.getId();
                    InsertIntoDB(Connection.getURL(WebConnection.query.INSERTPRODUCTINGREDIENT, par));
                }

                par = "idOrdine=" + idOrderInserted + "&idProdotto=" + idProductEdited + "&Quantita=" + O.getListProducts().get(k).getQuantity();
                InsertIntoDB(Connection.getURL(WebConnection.query.INSERTORDERPRODUCT, par)); // InsertOrderProduct
            }
            else{
                if(Setting.getDebug())
                    System.out.println("INSERT PRODUCT FOR ORDER: " + O.getId());

                par = "idOrdine=" + idOrderInserted + "&idProdotto=" + O.getListProducts().get(k).getId() + "&Quantita=" + O.getListProducts().get(k).getQuantity();
                InsertIntoDB(Connection.getURL(WebConnection.query.INSERTORDERPRODUCT, par)); // InsertOrderProduct
            }

        }
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
            if(Setting.getDebug())
                System.out.println("INSERT INTO DATABASE URL REQUEST SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            if(Setting.getDebug())
                System.out.println("INSERT INTO DATABASE URL REQUEST FAILED");
        }
    }
    private int InsertIntoDBWithId(final String urlWebService) {

        try {

            URL url = new URL(urlWebService);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();


            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String json;
            while ((json = bufferedReader.readLine()) != null) {
                if (json.contains("Inserito")) {
                    sb.append(bufferedReader.readLine());
                }
            }
            if(Setting.getDebug())
                System.out.println("INSERT INTO DATABASE URL REQUEST SUCCESS");

            return Integer.parseInt(sb.toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
            if(Setting.getDebug())
                System.out.println("INSERT INTO DATABASE URL REQUEST FAILED");
            return -1;
        }
    }

    private void loadIntoListView(List<Product> listP)
    {
        if(Setting.getDebug())
            System.out.println("LOAD INTO LISTVIEW");

        List<HashMap<String, String>> listitems = new ArrayList<>();
        //SimpleAdapter adapter = new SimpleAdapter(this, listitems, R.layout.list_item, new String[]{"First Line", "Second Line"}, new int[]{R.id.text1, R.id.text2});
        customAdapter_carrello adapter = new customAdapter_carrello(this, listitems, R.layout.list_item__with_2_icon, new String[]{"First Line", "Icon", "Second Line"}, new int[]{R.id.text1, R.id.icon, R.id.text2});


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
            System.out.println(Ingredienti);
            resultMap.put("Second Line", Ingredienti);
            listitems.add(resultMap);
        }
        list.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_carrello);
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
            Intent I = new Intent(activity_carrello.this, activity_home.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            I.putExtra("Restaurant" ,Rest);
            startActivity(I);
            activity_carrello.this.finish();
        }
        else if (id == R.id.nav_ordini)
        {
            Intent I = new Intent(activity_carrello.this, activity_ordini.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            I.putExtra("Restaurant" ,Rest);

            startActivity(I);
            activity_carrello.this.finish();
        }
        else if (id == R.id.nav_gestione_ordini)
        {
            Intent I = new Intent(activity_carrello.this, activity_gestione_ordini.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            I.putExtra("Restaurant" ,Rest);

            startActivity(I);
            activity_carrello.this.finish();
        }
        else if (id == R.id.nav_carrello)
        {
            //NOTHING
        }
        else if (id == R.id.nav_map)
        {
            Intent I = new Intent(activity_carrello.this, activity_map.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            I.putExtra("Restaurant" ,Rest);

            startActivity(I);
            activity_carrello.this.finish();
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
                callPhone(Rest.getNumeroTelefono());

            }
        }
        else if (id == R.id.nav_exit)
        {
            Preference.savePreferences("", "", "", this);
            startActivity(new Intent(activity_carrello.this, MainActivity.class));
            activity_carrello.this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_carrello);
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
                    startActivity(new Intent(activity_carrello.this, MainActivity.class));
                    activity_carrello.this.finish();
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
