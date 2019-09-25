package com.example.spara.restaurant.activity;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;

import com.example.spara.restaurant.object.JSONUtility;
import com.example.spara.restaurant.object.Preference;
import com.example.spara.restaurant.service.Background;
import com.example.spara.restaurant.object.Cart;
import com.example.spara.restaurant.object.Ingredient;
import com.example.spara.restaurant.object.Product;
import com.example.spara.restaurant.R;
import com.example.spara.restaurant.object.Restaurant;
import com.example.spara.restaurant.object.ReviewProduct;
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
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.example.spara.restaurant.object.JSONUtility.fillProductsList;

public class activity_home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //PERMISSION CODE
    //CALL - 1
    //INTERNET -2

    ListView list;

    ArrayList<Product> listProducts;
    int posSelected = -1;
    boolean pizzeSelected = false;
    Cart cartProducts;
    User UserLogged;
    WebConnection Connection;
    ProgressDialog pd;

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

        //Get INTENT Extra
        cartProducts = (Cart) getIntent().getParcelableExtra("Cart");
        UserLogged = (User) getIntent().getParcelableExtra("User");
        Connection = (WebConnection) getIntent().getParcelableExtra("WebConnection");

        //Listview declaration
        list = (ListView) findViewById(R.id.list);

        //Background Image
        ImageView imgTransparent = findViewById(R.id.imageView);
        imgTransparent.setAlpha(230);

        //Image and Button declaration
        ImageView imgProduct = findViewById(R.id.imgProdotto);
        ImageView btnPaniniFritti = findViewById(R.id.Panini_Fritti);
        ImageView btnPizze = findViewById(R.id.Pizze);
        ImageView btnSalad = findViewById(R.id.Salad);
        ImageView btnAddCart = findViewById(R.id.addCart);

        //Starting background service
        Intent I1 = new Intent(activity_home.this, Background.class);
        I1.putExtra("UserNumber", UserLogged.getNumeroTelefono());
        //I.putExtra("WebConnection" ,Connection);
        startService(I1);

        //User Confirm Control
        if(!UserLogged.getConfermato())
        {
            Toast.makeText(getApplicationContext(), "ATTENZIONE: Utente non confermato. Effettua la conferma tramite la mail ricevuta per poter creare i tuoi ordini", Toast.LENGTH_LONG).show();
        }
        if(UserLogged.getAmministratore())
        {
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
                        System.out.println(listProducts.get(position - 1).getImageURL());
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
                        if (listProducts.get(position).getImageURL().equals("null"))
                            imgProduct.setImageResource(R.drawable.logo);
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
                    //Toast.makeText(getApplicationContext(), selectedItem, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Buttons click event
        btnPaniniFritti.setOnClickListener(new View.OnClickListener() {
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

        btnSalad.setOnClickListener(new View.OnClickListener() {
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

        btnPizze.callOnClick();

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

    /*
    private void fillProductsList(String json)
    {
        try {
            System.out.println(json);
            listProducts = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(json);
            String[][] stocks = new String[jsonArray.length()][2];
            StringBuilder Ingredienti;
            //int nProdotti = 0;

            for (int i = 0; i < jsonArray.length(); i++) {
                boolean presente = false;
                JSONObject obj = jsonArray.getJSONObject(i);
                Product P = new Product();
                Ingredienti = new StringBuilder();
                /*
                for (int k = 0; k < stocks.length && !presente; k++) {
                    if (obj.getString("Name").equals(stocks[k][0])) {
                        presente = true;
                    }
                }
                /

                if (!presente) {
                    P.setId(Integer.parseInt(obj.getString("id")));
                    P.setPrezzo(Float.parseFloat(obj.getString("Price")));
                    P.setTipo(obj.getString("Type"));
                    P.setNome(obj.getString("Name"));
                    P.setImageURL(obj.getString("ImageURL"));
                    P.setQuantity(obj.getInt("Quantity"));
                    P.setIdLocale(obj.getLong("idLocal"));

                    //stocks[nProdotti][0] = obj.getString("Name");
                    String prezzo = obj.getString("Price");

                    int nIngredienti = 0;
                    List<Ingredient> listIngredients = new ArrayList<>();
                    List<ReviewProduct> listReview = new ArrayList<>();

                    JSONArray jsonArrayIngredientsOfProduct = obj.getJSONArray("Ingredients");

                    for (int k = 0; k < jsonArrayIngredientsOfProduct.length(); k++) {
                        JSONObject tmpobj = jsonArrayIngredientsOfProduct.getJSONObject(k);
                        Ingredient I = new Ingredient();
                        //if (tmpobj.getString("Name").equals(stocks[nProdotti][0])) {
                            if (nIngredienti == 0) {
                                Ingredienti.append(tmpobj.getString("Name"));
                            } else {
                                Ingredienti.append(", " + tmpobj.getString("Name"));
                            }
                            I.setId(Integer.parseInt(tmpobj.getString("id")));
                            I.setNome(tmpobj.getString("Name"));
                            I.setPrezzo(Float.parseFloat(tmpobj.getString("Price")));
                            listIngredients.add(I);
                            nIngredienti++;
                        //}
                    }
                    JSONArray jsonArrayReviewOfProduct = obj.getJSONArray("Reviews");

                    for (int k = 0; k < jsonArrayReviewOfProduct.length(); k++) {
                        JSONObject tmpobj = jsonArrayReviewOfProduct.getJSONObject(k);
                        ReviewProduct RP = new ReviewProduct();

                        RP.setVoto(Integer.parseInt(tmpobj.getString("Voto")));
                        RP.setNumeroTelefono(tmpobj.getString("NumeroTelefono"));
                        Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tmpobj.getString("DataOra"));
                        RP.setDataOra(date1);
                        RP.setIdProduct(tmpobj.getLong("idProdotto"));
                        listReview.add(RP);
                        nIngredienti++;
                        //}
                    }
                    if (nIngredienti == 1 && Ingredienti.toString().equals("null")) {
                        Ingredienti.delete(0, Ingredienti.length());
                        Ingredienti.append("€" + prezzo);
                    } else {
                        Ingredienti.append(" €" + prezzo);
                    }
                    //stocks[nProdotti][1] = Ingredienti.toString().trim();
                    //nProdotti++;
                    P.setListIngredienti(listIngredients);
                    P.setListReview(listReview);
                    listProducts.add(P);
                }
            }

        }catch (Exception e){e.printStackTrace();}
    }
*/

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
            //startActivity(new Intent(activity_home.this, activity_carrello.class));
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
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+ Restaurant.getNumeroTelefono()));
                startActivity(callIntent);
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
                    startActivity(new Intent(activity_home.this, MainActivity.class));
                    activity_home.this.finish();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
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
}
