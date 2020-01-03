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
import com.example.spara.restaurant.object.Ingredient;
import com.example.spara.restaurant.object.Product;
import com.example.spara.restaurant.R;
import com.example.spara.restaurant.object.Restaurant;
import com.example.spara.restaurant.object.Setting;
import com.example.spara.restaurant.object.User;
import com.example.spara.restaurant.object.WebConnection;
import com.example.spara.restaurant.custom_adapter.customAdapter_personalizza;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.spara.restaurant.object.JSONUtility.downloadJSON;
import static com.example.spara.restaurant.object.JSONUtility.fillIngredientList;
import static com.example.spara.restaurant.object.Preference.savePreferences;

public class activity_personalizza extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    ListView listviewIngredients;
    ListView listviewChoosedIngredients;
    ImageView addIngredient;
    ImageView deleteIngredient;
    ImageView back;
    Button save;

    Cart cartProducts;
    User UserLogged;
    WebConnection Connection;
    Restaurant Rest;

    List<Ingredient> listIngredients;
    List<Ingredient> listChoosedIngredients;

    ProgressDialog pd;

    int posSelected = -1;
    int posSelectedChoosed = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalizza);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_personalizza);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //MY CODE

        //ListView Declaration
        listviewIngredients = findViewById(R.id.listIngredienti);
        listviewChoosedIngredients = findViewById(R.id.listIngredientiScelti);

        //Button Declaration
        back = findViewById(R.id.indietro);
        save = findViewById(R.id.salva);

        //Background Image Declaration
        ImageView imgTransparent = findViewById(R.id.imageView);
        imgTransparent.setAlpha(230);

        //Get Intent Extra
        if(Setting.getDebug())
            System.out.println("INITIALIZE PREFERENCES");
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

        //Initialize List
        listChoosedIngredients = new ArrayList<>();

        //Setting visibility Button
        save.setVisibility(View.INVISIBLE);
        showLoadingDialog();
        new Thread(new Runnable() {
            public void run() {
                String tmpJSON = downloadJSON(Connection.getURL(WebConnection.query.INGREDIENTS));
                listIngredients = fillIngredientList(tmpJSON);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        // Stuff that updates the UI
                        loadIntoIngredientListView();
                    }
                });
                pd.dismiss();
            }
        }).start();

        listviewIngredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        Animation animation1 = new AlphaAnimation(0.0f, 1.0f);
                        animation1.setDuration(500);
                        view.startAnimation(animation1);
                    }
                });
                posSelected = position;
                posSelectedChoosed = -1;
            }
        });
        listviewChoosedIngredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        //Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                        //animation1.setDuration(1000);
                        //deleteIngredient.setVisibility(View.VISIBLE);
                        //view.startAnimation(animation1);
                        //deleteIngredient.startAnimation(animation1);

                    }
                });
                posSelectedChoosed = position;
                posSelected = -1;
            }
        });

        save.setOnClickListener(new View.OnClickListener() {

            //@Override
            public void onClick(View v) {

                if(listChoosedIngredients.size() > 0) {
                    if(Setting.getDebug())
                        System.out.println("INITIALIZE PRODUCT");

                    Product P = new Product();
                    P.setNome("Personalizzata");
                    float prezzo = 0;
                    for(int k=0; k<listChoosedIngredients.size(); k++)
                    {
                        prezzo += listChoosedIngredients.get(k).getPrezzo();
                    }
                    P.setImageURL("nd");
                    P.setPrezzo(prezzo);
                    P.setTipo("Pizza");
                    P.setListIngredienti(listChoosedIngredients);
                    P.setIdLocale(UserLogged.getIdLocale());
                    P.setQuantity(1);
                    cartProducts.addProduct(P);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                            animation1.setDuration(1000);
                            v.startAnimation(animation1);
                        }
                    });

                    Intent I = new Intent(activity_personalizza.this, activity_home.class);
                    I.putExtra("Cart", cartProducts);
                    I.putExtra("User", UserLogged);
                    I.putExtra("WebConnection" ,Connection);
                    startActivity(I);
                    activity_personalizza.this.finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Seleziona un Ingrediente", Toast.LENGTH_SHORT).show();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
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
                Intent I = new Intent(activity_personalizza.this, activity_home.class);
                I.putExtra("Cart", cartProducts);
                I.putExtra("User", UserLogged);
                I.putExtra("WebConnection" ,Connection);
                startActivity(I);
                activity_personalizza.this.finish();

            }
        });

    }


    public void loadIntoIngredientListView()
    {
        List<HashMap<String, String>> listitems = new ArrayList<>();
        customAdapter_personalizza adapter = new customAdapter_personalizza(this, listitems, R.layout.list_item_icon, new String[]{"First Line", "Icon1", "Second Line"}, new int[]{R.id.text1, R.id.icon1, R.id.text2}, listIngredients, listChoosedIngredients);


        for(int k=0; k<listIngredients.size(); k++)
        {
            HashMap<String, String> resultMap = new HashMap<>();
            resultMap.put("First Line", listIngredients.get(k).getNome() + " €" + String.valueOf(listIngredients.get(k).getPrezzo()));
            resultMap.put("Icon1", "add");
            resultMap.put("Second Line", "");
            listitems.add(resultMap);
        }
        listviewIngredients.setAdapter(adapter);
    }

    public void loadIntoChoosedIngredientListView()
    {
        if(Setting.getDebug())
            System.out.println("LOAD INTO LISTVIEW");

        List<HashMap<String, String>> listitems = new ArrayList<>();
        customAdapter_personalizza adapter = new customAdapter_personalizza(this, listitems, R.layout.list_item_icon, new String[]{"First Line", "Icon1", "Second Line"}, new int[]{R.id.text1, R.id.icon1, R.id.text2}, listIngredients, listChoosedIngredients);


        for(int k=0; k<listChoosedIngredients.size(); k++)
        {
            HashMap<String, String> resultMap = new HashMap<>();
            resultMap.put("First Line", listChoosedIngredients.get(k).getNome() + " €" + String.valueOf(listChoosedIngredients.get(k).getPrezzo()));
            resultMap.put("Icon1", "remove");
            resultMap.put("Second Line", "");
            listitems.add(resultMap);
        }
        listviewChoosedIngredients.setAdapter(adapter);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_personalizza);
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
            Intent I = new Intent(activity_personalizza.this, activity_home.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            I.putExtra("Restaurant" ,Rest);

            startActivity(I);
            //startActivity(new Intent(activity_home.this, activity_carrello.class));
            activity_personalizza.this.finish();
        }
        else if (id == R.id.nav_ordini)
        {
            Intent I = new Intent(activity_personalizza.this, activity_ordini.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            I.putExtra("Restaurant" ,Rest);

            startActivity(I);
            activity_personalizza.this.finish();
        }
        else if (id == R.id.nav_carrello)
        {
            Intent I = new Intent(activity_personalizza.this, activity_carrello.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            I.putExtra("Restaurant" ,Rest);

            startActivity(I);
            //startActivity(new Intent(activity_home.this, activity_carrello.class));
            activity_personalizza.this.finish();
        }
        else if (id == R.id.nav_map)
        {
            Intent I = new Intent(activity_personalizza.this, activity_map.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            I.putExtra("Restaurant" ,Rest);

            startActivity(I);
            activity_personalizza.this.finish();
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
            savePreferences("", "", "", this);
            startActivity(new Intent(activity_personalizza.this, MainActivity.class));
            activity_personalizza.this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_personalizza);
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
                    startActivity(new Intent(activity_personalizza.this, MainActivity.class));
                    activity_personalizza.this.finish();
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
