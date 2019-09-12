package com.example.spara.restaurant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class activity_gestione_ordini extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    List<Order> OrderList;

    Cart cartProducts;
    User UserLogged;
    WebConnection Connection;

    ProgressDialog pd;

    ListView listOrder;
    ListView listProducts;

    int posSelected = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestione_ordini);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_gestione_ordini);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //MY CODE

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.order_filter_array, R.layout.row);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);



        //Background Image Declaration
        ImageView imgTransparent = findViewById(R.id.imageView);
        imgTransparent.setAlpha(230);


        //ListView Declaration
        listOrder = (ListView) findViewById(R.id.listOrdini);
        listProducts = (ListView) findViewById(R.id.listProdotti);

        //Get INTENT Extra
        cartProducts = (Cart) getIntent().getParcelableExtra("Cart");
        UserLogged = (User) getIntent().getParcelableExtra("User");
        Connection = (WebConnection) getIntent().getParcelableExtra("WebConnection");


        if(UserLogged.getAmministratore())
        {
            navigationView.getMenu().findItem(R.id.nav_gestione_ordini).setVisible(true);
        }
        //Toast.makeText(getApplicationContext(), UserLogged.getNumeroTelefono(), Toast.LENGTH_SHORT).show();

        /*
        String par = "Stato=Richiesto";
        showLoadingDialog();
        new Thread(new Runnable() {
            public void run() {
                String tmpJSON = downloadJSON(Connection.getURL(WebConnection.query.ORDERPRODUCTSUSERSTATE, par));
                fillOrderList(tmpJSON);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        loadIntoOrderListView();
                        pd.dismiss();
                    }
                });
            }
        }).start();
        */

        //ListView Click on Item Event
        listOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);

                new Thread(new Runnable() {
                    public void run() {
                        posSelected = position;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Stuff that updates the UI
                                Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                                animation1.setDuration(2000);
                                view.startAnimation(animation1);
                            }
                        });
                        Intent I = new Intent(activity_gestione_ordini.this, activity_info_ordine.class);
                        I.putExtra("Cart", cartProducts);
                        I.putExtra("User", UserLogged);
                        I.putExtra("WebConnection" ,Connection);
                        I.putExtra("idOrdine", OrderList.get(position).getId());
                        startActivity(I);
                        activity_gestione_ordini.this.finish();

                    }
                }).start();

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                String par;
                switch (parent.getItemAtPosition(pos).toString())
                {
                    case "Richiesto":
                        par = "idLocale=1&Stato=Richiesto";
                        break;
                    case "In Preparazione":
                        par = "idLocale=1&Stato=In%20Preparazione";
                        break;
                    case "In Consegna":
                        par = "idLocale=1&Stato=In%20Preparazione";
                        break;
                    case "Consegnato":
                        par = "idLocale=1&Stato=In%20Preparazione";
                        break;
                    case "Tutto":
                        par = "idLocale=1&Stato=all";
                        break;
                    default:
                        par="";
                }
                showLoadingDialog();
                new Thread(new Runnable() {
                    public void run() {
                        System.out.println(Connection.getURL(WebConnection.query.ORDERPRODUCTSUSERSTATE, par));
                        String tmpJSON = downloadJSON(Connection.getURL(WebConnection.query.ORDERPRODUCTSUSERSTATE, par));
                        fillOrderList(tmpJSON);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Stuff that updates the UI
                                loadIntoOrderListView();
                                pd.dismiss();
                            }
                        });
                    }
                }).start();
                //Toast.makeText(getApplicationContext(), parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();

            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
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
            return sb.toString().trim();
        } catch (Exception e) {
            return null;
        }
    }

    private void fillOrderList(String json) {
        try {
            OrderList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(json);
            System.out.println(jsonArray.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                System.out.println(obj.toString());

                Order O = new Order();
                O.setId(obj.getInt("idOrdine"));
                O.setStato(obj.getString("Stato"));
                if (obj.getBoolean("Asporto") == false) {
                    O.setAsporto(false);
                } else {
                    O.setAsporto(true);
                }
                O.setNumeroTelefono(obj.getString("NumeroTelefono"));

                SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALIAN);
                SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALIAN);
                Date d1 = null;
                try {
                    d1 = sdf3.parse(obj.getString("DataOra"));
                } catch (Exception e) {
                    d1 = sdf4.parse(obj.getString("DataOra"));
                }
                O.setDateTime(d1);
                System.out.println(O.getDateTime());
                System.out.println(obj.getString("DataOra"));
                JSONArray products = obj.getJSONArray("Products");
                System.out.println(products.toString());

                List<Product> listProductsOrder = new ArrayList<>();
                for (int k = 0; k < products.length(); k++) {
                    JSONObject product = products.getJSONObject(k);

                    System.out.println(products.toString());
                    Product P = new Product();
                    P.setId(product.getInt("idProdotto"));
                    P.setPrezzo(Float.parseFloat(product.getString("Price")));
                    P.setImageURL(product.getString("ImageURL"));
                    P.setTipo(product.getString("Type"));
                    P.setNome(product.getString("Name"));
                    P.setQuantity(product.getInt("Quantity"));

                    JSONArray ingredients = product.getJSONArray("Ingredients");
                    List<Ingredient> listIngredient = new ArrayList<>();
                    for (int j = 0; j < ingredients.length(); j++) {
                        JSONObject ingredient = ingredients.getJSONObject(j);

                        Ingredient I = new Ingredient();
                        I.setId(Integer.parseInt(ingredient.getString("idIngredient")));
                        I.setNome(ingredient.getString("Name"));
                        I.setPrezzo(Float.parseFloat(ingredient.getString("Price")));
                        listIngredient.add(I);

                    }
                    P.setListIngredienti(listIngredient);
                    listProductsOrder.add(P);
                }

                O.setListProducts(listProductsOrder);
                OrderList.add(O);

            }
        }catch (Exception e){e.printStackTrace();}
    }

    private void loadIntoOrderListView()
    {
        List<HashMap<String, String>> listitems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, listitems, R.layout.list_item, new String[]{"First Line", "Second Line"}, new int[]{R.id.text1, R.id.text2});


        for(int k=0; k<OrderList.size(); k++)
        {
            HashMap<String, String> resultMap = new HashMap<>();
            Date D = OrderList.get(k).getDateTime();
            SimpleDateFormat sdf3 = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ITALIAN);
            String s = sdf3.format(D);
            //String s = String.valueOf(D.getDay()) + "/" + String.valueOf(D.getMonth()) + "/" + String.valueOf(D.getYear()) + " " + String.valueOf(D.getHours()) + ":" + String.valueOf(D.getMinutes());
            resultMap.put("First Line", "Id: " + String.valueOf(OrderList.get(k).getId()) + " Data: " + s + " Costo: €" + String.valueOf(OrderList.get(k).getTotaleCosto()));

            resultMap.put("Second Line", "");
            listitems.add(resultMap);
        }
        if(OrderList.size() == 0)
        {
            HashMap<String, String> resultMap = new HashMap<>();
            resultMap.put("First Line", "NESSUN ORDINE EFFETTUATO");

        }
        listOrder.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_gestione_ordini);
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
            Intent I = new Intent(activity_gestione_ordini.this, activity_home.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            startActivity(I);
            activity_gestione_ordini.this.finish();
        }
        else if (id == R.id.nav_ordini)
        {
            Intent I = new Intent(activity_gestione_ordini.this, activity_ordini.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            startActivity(I);
            activity_gestione_ordini.this.finish();
        }
        else if (id == R.id.nav_carrello)
        {
            Intent I = new Intent(activity_gestione_ordini.this, activity_carrello.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            startActivity(I);
            activity_gestione_ordini.this.finish();
        }
        else if (id == R.id.nav_map)
        {
            Intent I = new Intent(activity_gestione_ordini.this, activity_map.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            startActivity(I);
            activity_gestione_ordini.this.finish();
        }
        else if (id == R.id.nav_account)
        {

        }
        else if (id == R.id.nav_chiamaci)
        {

        }
        else if (id == R.id.nav_exit)
        {
            savePreferences("", "", "");
            startActivity(new Intent(activity_gestione_ordini.this, MainActivity.class));
            activity_gestione_ordini.this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_gestione_ordini);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
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

}
