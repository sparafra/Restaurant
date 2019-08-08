package com.example.spara.restaurant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

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

public class activity_info_ordine extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    Order O;
    User U;

    Cart cartProducts;
    User UserLogged;
    WebConnection Connection;

    ProgressDialog pd;

    ListView listProducts;

    Button changeStatus;
    TextView Indirizzo;
    TextView Nominativo;
    TextView OrderId;
    TextView CostoTotale;
    Switch Domicilio;

    int posSelected = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_ordine);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         //Mail Floating Action
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_info_ordine);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //MY CODE


        Spinner spChangeStatus = (Spinner) findViewById(R.id.sp_change_status);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterChangeStatus = ArrayAdapter.createFromResource(this,
                R.array.order_filter_array, R.layout.row);
// Specify the layout to use when the list of choices appears
        adapterChangeStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spChangeStatus.setAdapter(adapterChangeStatus);


        //Background Image Declaration
        ImageView imgTransparent = findViewById(R.id.imageView);
        imgTransparent.setAlpha(230);

        //Status Image Button Declaration
        changeStatus = findViewById(R.id.btn_change_status);
        Indirizzo = findViewById(R.id.Indirizzo);
        Nominativo = findViewById(R.id.Nominativo);
        Domicilio = findViewById(R.id.swDomicilio);
        OrderId = findViewById(R.id.OrderId);
        CostoTotale = findViewById(R.id.CostoTotale);

        //ListView Declaration
        listProducts = (ListView) findViewById(R.id.listProducts);

        //Get INTENT Extra
        cartProducts = (Cart) getIntent().getParcelableExtra("Cart");
        UserLogged = (User) getIntent().getParcelableExtra("User");
        Connection = (WebConnection) getIntent().getParcelableExtra("WebConnection");
        int idOrdine = getIntent().getIntExtra("idOrdine", -1);
        System.out.println(idOrdine);

        if(UserLogged.getAmministratore())
        {
            navigationView.getMenu().findItem(R.id.nav_gestione_ordini).setVisible(true);
        }
        //Toast.makeText(getApplicationContext(), UserLogged.getNumeroTelefono(), Toast.LENGTH_SHORT).show();

        showLoadingDialog();
        new Thread(new Runnable() {
            public void run() {
                String par = "idOrdine=" + idOrdine;
                String tmpJSON = downloadJSON(Connection.getURL(WebConnection.query.ORDER, par));
                fillOrder(tmpJSON);
                par = "NumeroTelefono=" + O.getNumeroTelefono() ;
                tmpJSON = downloadJSON(Connection.getURL(WebConnection.query.SEARCHACCOUNTBYID, par));
                fillUser(tmpJSON);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        loadIntoProductListView(O.getListProducts());
                        Domicilio.setChecked(O.getAsporto());
                        Nominativo.setText(U.getNome() + " " + U.getCognome());
                        Indirizzo.setText(U.getIndirizzo());
                        OrderId.setText("Order ID: " + O.getId());
                        CostoTotale.setText("Costo Totale: " + O.getTotaleCosto() + " €");
                        switch (O.getStato())
                        {
                            case "Richiesto":
                                spChangeStatus.setSelection(0);
                                break;
                            case "In Preparazione":
                                spChangeStatus.setSelection(1);
                                break;
                            case "In Consegna":
                                spChangeStatus.setSelection(2);
                                break;
                            case "Consegnato":
                                spChangeStatus.setSelection(3);
                                break;
                            case "Tutto":
                                spChangeStatus.setSelection(4);
                                break;
                        }

                    }
                });
                pd.dismiss();
            }
        }).start();

        //ListView Click on Item Event
        listProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);

            }
        });

        spChangeStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)

                //Toast.makeText(getApplicationContext(), parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();

            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        changeStatus.setOnClickListener(new View.OnClickListener() {
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

                showLoadingDialog();
                new Thread(new Runnable() {
                    public void run() {
                        String status="";
                        switch (spChangeStatus.getSelectedItem().toString())
                        {
                            case "Richiesto":
                                O.setStato("Richiesto");
                                status = "Richiesto";
                                break;
                            case "In Preparazione":
                                O.setStato("In Preparazione");
                                status = "In%20Preparazione";
                                break;
                            case "In Consegna":
                                O.setStato("In Consegna");
                                status = "In%20Consegna";
                                break;
                            case "Consegnato":
                                O.setStato("Consegnato");
                                status = "Consegnato";
                                break;
                            default:
                                status="";
                        }


                        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALIAN);
                        SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.ITALIAN);
                        String dateStr = date.format(O.getDateTime());
                        String timeStr = time.format(O.getDateTime());

                        String par = "idOrdine=" + O.getId() + "&Stato=" + status + "&Asporto=" + O.getAsporto() +"&NumeroTelefono=" + O.getNumeroTelefono() + "&DataOra=" + dateStr +"%20"+ timeStr + "&Costo=" + O.getTotaleCosto();

                        new Thread(new Runnable() {
                            public void run() {
                                InsertIntoDB(Connection.getURL(WebConnection.query.UPDATEORDER, par));

                                String par = "idOrdine=" + idOrdine;
                                String tmpJSON = downloadJSON(Connection.getURL(WebConnection.query.ORDER, par));
                                fillOrder(tmpJSON);
                                par = "NumeroTelefono=" + O.getNumeroTelefono();
                                System.out.println(Connection.getURL(WebConnection.query.SEARCHACCOUNTBYID, par));
                                tmpJSON = downloadJSON(Connection.getURL(WebConnection.query.SEARCHACCOUNTBYID, par));
                                fillUser(tmpJSON);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Stuff that updates the UI
                                        loadIntoProductListView(O.getListProducts());
                                        Domicilio.setChecked(O.getAsporto());
                                        Nominativo.setText(U.getNome() + " " + U.getCognome());
                                        Indirizzo.setText(U.getIndirizzo());
                                        OrderId.setText("Order ID: " + O.getId());
                                        CostoTotale.setText("Costo Totale: " + O.getTotaleCosto() + " €");
                                        switch (O.getStato())
                                        {
                                            case "Richiesto":
                                                spChangeStatus.setSelection(0);
                                                break;
                                            case "In Preparazione":
                                                spChangeStatus.setSelection(1);
                                                break;
                                            case "In Consegna":
                                                spChangeStatus.setSelection(2);
                                                break;
                                            case "Consegnato":
                                                spChangeStatus.setSelection(3);
                                                break;
                                            case "Tutto":
                                                spChangeStatus.setSelection(4);
                                                break;
                                        }

                                    }
                                });
                                pd.dismiss();
                            }
                        }).start();
                    }
                }).start();
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

    private void fillOrder(String json) {

        try {
            O = new Order();
            //JSONArray jsonArray = new JSONArray(json);
            JSONObject obj = new JSONObject(json);

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
            O.setCosto(Float.valueOf(obj.getString("Costo")));
            JSONArray products = obj.getJSONArray("Products");

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


        }catch (Exception e){e.printStackTrace();}
    }
    private void fillUser(String json) {

        try {
            U = new User();

            JSONObject obj = new JSONObject(json);

            U.setNumeroTelefono(obj.getString("NumeroTelefono"));
            U.setMail(obj.getString("Mail"));
            U.setPassword(obj.getString("Password"));
            U.setNome(obj.getString("Nome"));
            U.setCognome(obj.getString("Cognome"));
            U.setIndirizzo(obj.getString("Indirizzo"));
            U.setAmministratore(obj.getBoolean("Amministratore"));
            U.setConfermato(obj.getBoolean("Confermato"));



        }catch (Exception e){e.printStackTrace();}
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_info_ordine);
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
            Intent I = new Intent(activity_info_ordine.this, activity_home.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            startActivity(I);
            activity_info_ordine.this.finish();
        }
        else if (id == R.id.nav_gestione_ordini)
        {
            Intent I = new Intent(activity_info_ordine.this, activity_gestione_ordini.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            startActivity(I);
            activity_info_ordine.this.finish();
        }
        else if (id == R.id.nav_ordini)
        {
            Intent I = new Intent(activity_info_ordine.this, activity_ordini.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            startActivity(I);
            activity_info_ordine.this.finish();
        }
        else if (id == R.id.nav_carrello)
        {
            Intent I = new Intent(activity_info_ordine.this, activity_carrello.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            startActivity(I);
            activity_info_ordine.this.finish();
        }
        else if (id == R.id.nav_map)
        {
            Intent I = new Intent(activity_info_ordine.this, activity_map.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            startActivity(I);
            activity_info_ordine.this.finish();
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
            startActivity(new Intent(activity_info_ordine.this, MainActivity.class));
            activity_info_ordine.this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_info_ordine);
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
