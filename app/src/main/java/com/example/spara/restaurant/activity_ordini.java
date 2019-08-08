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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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

public class activity_ordini extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    List<Order> OrderList;

    Cart cartProducts;
    User UserLogged;
    WebConnection Connection;

    ProgressDialog pd;

    ListView listOrder;
    ListView listProducts;

    Button imgStatus;
    int posSelected = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordini);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_ordini);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //MY CODE

        //Background Image Declaration
        ImageView imgTransparent = findViewById(R.id.imageView);
        imgTransparent.setAlpha(230);

        //Status Image Button Declaration
        imgStatus = (Button)findViewById(R.id.imgStatus);
        imgStatus.setVisibility(View.INVISIBLE);

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

        String par = "NumeroTelefono=" + UserLogged.getNumeroTelefono() + "&idLocale=1";
        showLoadingDialog();
        new Thread(new Runnable() {
            public void run() {
                String tmpJSON = downloadJSON(Connection.getURL(WebConnection.query.ORDERPRODUCTSUSER, par));
                fillOrderList(tmpJSON);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        // Stuff that updates the UI
                        loadIntoOrderListView();
                    }
                });
                pd.dismiss();
            }
        }).start();

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
                                imgStatus.setVisibility(View.VISIBLE);
                                loadIntoProductListView(OrderList.get(posSelected).getListProducts());
                                imgStatus.setText(OrderList.get(posSelected).getStato());
                                imgStatus.startAnimation(animation1);
                                view.startAnimation(animation1);
                            }
                        });

                    }
                }).start();

            }
        });
        listProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                        animation1.setDuration(1000);
                        view.startAnimation(animation1);
                    }
                });
                //posSelected = position;
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
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                boolean presente = false;
                for (int k = 0; k < OrderList.size() && !presente; k++) {
                    if (OrderList.get(k).getId() == obj.getInt("idOrdine"))
                        presente = true;
                }

                if (!presente) {
                    Order O = new Order();
                    O.setId(obj.getInt("idOrdine"));
                    O.setStato(obj.getString("Stato"));
                    if (obj.getInt("Asporto") == 0) {
                        O.setAsporto(false);
                    } else {
                        O.setAsporto(true);
                    }
                    O.setNumeroTelefono(obj.getString("NumeroTelefono"));

                    SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALIAN);
                    Date d1 = null;
                    try {
                        d1 = sdf3.parse(obj.getString("DataOra"));
                    } catch (Exception e) {
                    }
                    O.setDateTime(d1);

                    List<Product> listProductsOrder = new ArrayList<>();
                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject tmpobj = jsonArray.getJSONObject(k);
                        boolean presentetmp = false;
                        for (int j = 0; j < listProductsOrder.size() && !presentetmp; j++) {
                            if (listProductsOrder.get(j).getId() == tmpobj.getInt("idProdotto"))
                                presentetmp = true;
                        }

                        if (!presentetmp) {
                            if (tmpobj.getInt("idOrdine") == O.getId()) {
                                Product P = new Product();
                                P.setId(Integer.parseInt(tmpobj.getString("idProdotto")));
                                P.setPrezzo(Float.parseFloat(tmpobj.getString("PrezzoProdotto")));
                                P.setImageURL(tmpobj.getString("ImageURL"));
                                P.setTipo(tmpobj.getString("TipoProdotto"));
                                P.setNome(tmpobj.getString("NomeProdotto"));

                                List<Ingredient> listIngredient = new ArrayList<>();
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject tmpobj1 = jsonArray.getJSONObject(j);
                                    if (P.getId() == tmpobj1.getInt("idProdotto") && O.getId() == tmpobj1.getInt("idOrdine")) {
                                        Ingredient I = new Ingredient();
                                        I.setId(Integer.parseInt(tmpobj1.getString("idIngrediente")));
                                        I.setNome(tmpobj1.getString("NomeIngrediente"));
                                        I.setPrezzo(Float.parseFloat(tmpobj1.getString("PrezzoIngrediente")));
                                        listIngredient.add(I);
                                    }
                                }
                                P.setListIngredienti(listIngredient);
                                listProductsOrder.add(P);
                            }
                        }

                    }
                    O.setListProducts(listProductsOrder);
                    OrderList.add(O);

                }

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
    private void loadIntoProductListView(List<Product> listP)
    {
        List<HashMap<String, String>> listitems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, listitems, R.layout.list_item, new String[]{"First Line", "Second Line"}, new int[]{R.id.text1, R.id.text2});

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
        listProducts.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_ordini);
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
            Intent I = new Intent(activity_ordini.this, activity_home.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            startActivity(I);
            activity_ordini.this.finish();
        }
        else if (id == R.id.nav_ordini)
        {

        }
        else if (id == R.id.nav_gestione_ordini)
        {
            Intent I = new Intent(activity_ordini.this, activity_gestione_ordini.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            startActivity(I);
            activity_ordini.this.finish();
        }
        else if (id == R.id.nav_carrello)
        {
            Intent I = new Intent(activity_ordini.this, activity_carrello.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            startActivity(I);
            activity_ordini.this.finish();
        }
        else if (id == R.id.nav_map)
        {
            Intent I = new Intent(activity_ordini.this, activity_map.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            startActivity(I);
            activity_ordini.this.finish();
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
            startActivity(new Intent(activity_ordini.this, MainActivity.class));
            activity_ordini.this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_ordini);
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
