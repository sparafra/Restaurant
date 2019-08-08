package com.example.spara.restaurant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

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
import android.widget.SimpleAdapter;
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
        ImageView btnRemoveProduct = findViewById(R.id.cancellaProdotto);
        ImageView imgProduct = findViewById(R.id.imgProduct);


        //Get INTENT Extra
        cartProducts = (Cart) getIntent().getParcelableExtra("Cart");
        UserLogged = (User) getIntent().getParcelableExtra("User");
        Connection = (WebConnection) getIntent().getParcelableExtra("WebConnection");

        if(UserLogged.getAmministratore())
        {
            navigationView.getMenu().findItem(R.id.nav_gestione_ordini).setVisible(true);
        }

        //Setting Visibility of Button
        if(cartProducts.getListProducts().size() > 0) {
            loadIntoListView(cartProducts.getListProducts());
            btnRemoveProduct.setVisibility(View.INVISIBLE);
        }
        else{
            btnClearCart.setVisibility(View.INVISIBLE);
            btnOrder.setVisibility(View.INVISIBLE);
            btnRemoveProduct.setVisibility(View.INVISIBLE);
        }


        //List Click on Item Event
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

                new Thread(new Runnable() {
                    public void run() {
                        posSelected = position;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Stuff that updates the UI
                                btnRemoveProduct.setVisibility(View.VISIBLE);
                            }
                        });

                        if (cartProducts.getListProducts().get(position).getImageURL().equals("null"))
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
                                        btnRemoveProduct.setVisibility(View.INVISIBLE);
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

        btnRemoveProduct.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                //Product P = cartProducts.getListProducts().get(posSelected);
                //Toast.makeText(getApplicationContext(), P.getNome() + " " + P.getId(), Toast.LENGTH_SHORT).show();
                if (posSelected != -1) {
                    if (cartProducts.size() > 0) {
                        new Thread(new Runnable() {
                            public void run() {
                                cartProducts.remove(cartProducts.getListProducts().get(posSelected));
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
                                                btnRemoveProduct.setVisibility(View.INVISIBLE);
                                                if(cartProducts.size() == 0)
                                                {
                                                    btnClearCart.setVisibility(View.INVISIBLE);
                                                    btnOrder.setVisibility(View.INVISIBLE);
                                                }
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {

                                            }
                                        });
                                    }
                                });
                                posSelected = -1;

                            }
                        }).start();

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Non sono presenti prodotti", Toast.LENGTH_SHORT).show();

                }
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                //INSERT INTO DATABASE ORDER WITH THIS CART
                if (UserLogged.getConfermato()) {
                    O = new Order();
                    O.setListProducts(cartProducts.getListProducts());
                    O.setStato("Richiesto");
                    O.setAsporto(false);
                    O.setNumeroTelefono(UserLogged.getNumeroTelefono());
                    Date currentTime = Calendar.getInstance().getTime();
                    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALIAN);
                    SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.ITALIAN);
                    String dateStr = date.format(currentTime);
                    String timeStr = time.format(currentTime);
                    O.setDateTime(currentTime);

                    try {
                        showLoadingDialog();
                        new Thread(new Runnable() {
                            public void run() {
                                InsertOrderProduct(O);


                                String par = "idLocale=1&Amministratore=true";
                                String tmpJSON = downloadJSON(Connection.getURL(WebConnection.query.ADMINLIST, par));
                                List<User> AdminUsers = fillUsers(tmpJSON);

                                for(int k=0; k<AdminUsers.size(); k++)
                                {
                                    Notice N = new Notice();
                                    N.setStato(false);
                                    N.setRicevutoDa(AdminUsers.get(k).getNumeroTelefono());
                                    N.setMessaggio("Ricevuto nuovo ordine con id: " + O.getId());
                                    N.setIdLocale(1);
                                    N.setCreatoDa(O.getNumeroTelefono());

                                    par = "Stato=" + N.getStato() + "&CreatoDa=" + N.getCreatoDa() + "&Messaggio=" + N.getMessaggio().replaceAll(" ", "%20") + "&idLocale=1&RicevutoDa=" + N.getRicevutoDa();
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
        //Toast.makeText(this, "Hi, " + inputText, Toast.LENGTH_SHORT).show();
        if(inputText.equals("Si"))
        {
            O.setAsporto(true);
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALIAN);
            SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.ITALIAN);
            String dateStr = date.format(O.getDateTime());
            String timeStr = time.format(O.getDateTime());
            String par = "idOrdine=" + O.getId() + "&Stato=" + O.getStato() + "&Asporto=" + O.getAsporto() +"&NumeroTelefono=" + UserLogged.getNumeroTelefono() + "&DataOra=" + dateStr +"%20"+ timeStr;

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

        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALIAN);
        SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.ITALIAN);
        String dateStr = date.format(O.getDateTime());
        String timeStr = time.format(O.getDateTime());
        System.out.println(O.getTotaleCosto());
        String par = "Asporto=" + O.getAsporto() + "&Pagato=false" +"&NumeroTelefono=" + UserLogged.getNumeroTelefono() + "&idLocale=1&Costo=" + O.getTotaleCosto() ;

        try {
            idOrderInserted = InsertIntoDBWithId(Connection.getURL(WebConnection.query.INSERTORDER, par));
            //downloadJSON(Connection.getURL(WebConnection.query.INSERTORDER, par)); // Insert Order
            O.setId(idOrderInserted);
            System.out.println("Order Inserito");
        }catch (Exception e){}



        for (int k = 0; k < O.getListProducts().size(); k++) {
            System.out.println(O.getListProducts().get(k).getNome());
            if (O.getListProducts().get(k).getNome().equals("Personalizzata")) {

                par = "Nome=" + O.getListProducts().get(k).getNome() + "&Prezzo=" + O.getListProducts().get(k).getPrezzo() + "&idLocale=1&ImageURL=null";
                idProductEdited = InsertIntoDBWithId(Connection.getURL(WebConnection.query.INSERTPRODUCT, par));

                //par = "idProdotto=" + String.valueOf(idProductEdited) + "&Tipo=" + O.getListProducts().get(k).getTipo();
                //InsertIntoDB(Connection.getURL(WebConnection.query.INSERTPRODUCTTIPOLOGY, par));

                for(int i=0; i<O.getListProducts().get(k).getListIngredienti().size(); i++)
                {
                    Ingredient I = O.getListProducts().get(k).getListIngredienti().get(i);
                    par = "idProdotto=" + String.valueOf(idProductEdited) + "&idIngrediente=" + I.getId();
                    InsertIntoDB(Connection.getURL(WebConnection.query.INSERTPRODUCTINGREDIENT, par));
                }
                System.out.println(String.valueOf(idProductEdited));

                par = "idOrdine=" + idOrderInserted + "&idProdotto=" + idProductEdited;
                InsertIntoDB(Connection.getURL(WebConnection.query.INSERTORDERPRODUCT, par)); // InsertOrderProduct
            }
            else{
                par = "idOrdine=" + idOrderInserted + "&idProdotto=" + O.getListProducts().get(k).getId() + "&Quantita=1";
                InsertIntoDB(Connection.getURL(WebConnection.query.INSERTORDERPRODUCT, par)); // InsertOrderProduct
            }

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
    private List<User> fillUsers(String json)
    {
        List<User> list = new ArrayList<>();
        try {

            JSONArray jsonArray = new JSONArray(json);
            for(int k=0; k< jsonArray.length(); k++) {
                JSONObject obj = jsonArray.getJSONObject(k);
                User U = new User();
                U.setNumeroTelefono(obj.getString("NumeroTelefono"));
                U.setMail(obj.getString("Mail"));
                U.setPassword(obj.getString("Password"));
                U.setNome(obj.getString("Nome"));
                U.setCognome(obj.getString("Cognome"));
                U.setIndirizzo(obj.getString("Indirizzo"));
                U.setAmministratore(obj.getBoolean("Amministratore"));
                U.setConfermato(obj.getBoolean("Confermato"));


                list.add(U);
            }
        }catch (Exception e){e.printStackTrace();}
        finally {
            return list;
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
        } catch (Exception e) {
            e.printStackTrace();
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
            return Integer.parseInt(sb.toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void loadIntoListView(List<Product> listP)
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
            startActivity(I);
            activity_carrello.this.finish();
        }
        else if (id == R.id.nav_ordini)
        {
            Intent I = new Intent(activity_carrello.this, activity_ordini.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
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
            startActivity(I);
            activity_carrello.this.finish();
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
            startActivity(new Intent(activity_carrello.this, MainActivity.class));
            activity_carrello.this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_carrello);
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
