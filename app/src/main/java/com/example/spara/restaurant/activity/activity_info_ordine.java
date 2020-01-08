package com.example.spara.restaurant.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.example.spara.restaurant.object.AlertDialogFragment;
import com.example.spara.restaurant.object.Cart;
import com.example.spara.restaurant.object.ChangeDeliveryDialogFragment;
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
import com.example.spara.restaurant.custom_adapter.customAdapter_info_ordine;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

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
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

import lib.kingja.switchbutton.SwitchMultiButton;

import static com.example.spara.restaurant.object.JSONUtility.*;

public class activity_info_ordine extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ChangeDeliveryDialogFragment.EditNameDialogListener{

    Order O;
    User U;

    Cart cartProducts;
    User UserLogged;
    WebConnection Connection;
    Restaurant Rest;

    ProgressDialog pd;

    ListView listProducts;

    ImageView call;
    TextView Indirizzo;
    TextView Nominativo;
    TextView OrderId;
    TextView CostoTotale;
    LabeledSwitch Domicilio;
    ImageView deleteOrder;

    SwitchMultiButton status;

    boolean isRefreshing = false;

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


        //Background Image Declaration
        ImageView imgTransparent = findViewById(R.id.imageView);
        imgTransparent.setAlpha(230);

        //Status Image Button Declaration
        call = findViewById(R.id.call);
        status = (SwitchMultiButton) findViewById(R.id.status);

        Indirizzo = findViewById(R.id.Indirizzo);
        Nominativo = findViewById(R.id.Nominativo);
        Domicilio = findViewById(R.id.swDomicilio);
        OrderId = findViewById(R.id.OrderId);
        CostoTotale = findViewById(R.id.CostoTotale);
        deleteOrder = findViewById(R.id.deleteOrder);

        //ListView Declaration
        listProducts = (ListView) findViewById(R.id.listProducts);


        //Get INTENT Extra
        if(Setting.getDebug())
            System.out.println("INITIALIZE PREFERENCES");
        cartProducts = (Cart) getIntent().getParcelableExtra("Cart");
        UserLogged = (User) getIntent().getParcelableExtra("User");
        Connection = (WebConnection) getIntent().getParcelableExtra("WebConnection");
        Rest = (Restaurant) getIntent().getParcelableExtra("Restaurant");

        int idOrdine = getIntent().getIntExtra("idOrdine", -1);

        if(Setting.getDebug())
            System.out.println("ORDER ID: " + idOrdine);

        ConstraintLayout layoutslide = (ConstraintLayout) findViewById(R.id.content_info_ordini);

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


        if(UserLogged.getAmministratore())
        {
            if(Setting.getDebug())
                System.out.println("SETTING THE ADDITIONAL PANEL FOR ADMIN");
            navigationView.getMenu().findItem(R.id.nav_gestione_ordini).setVisible(true);
        }
        //Toast.makeText(getApplicationContext(), UserLogged.getNumeroTelefono(), Toast.LENGTH_SHORT).show();


        showLoadingDialog();
        refreshInfoOrder(idOrdine);

        status.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String tabText) {

                if (isRefreshing) {
                    if (Setting.getDebug())
                        System.out.println("CHANGING STATUS ORDER");

                    showLoadingDialog();
                    new Thread(new Runnable() {
                        public void run() {
                            String status = "";
                            switch (tabText) {
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
                                    status = "";
                            }


                            SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ITALIAN);
                            String date_time = datetime.format(O.getDateTime());

                            String par = "idOrdine=" + O.getId() + "&Stato=" + status + "&Asporto=" + O.getAsporto() + "&NumeroTelefono=" + O.getNumeroTelefono() + "&DataOra=" + date_time.replaceAll(" ", "%20") + "&Costo=" + O.getTotaleCosto();

                            new Thread(new Runnable() {
                                public void run() {
                                    InsertIntoDB(Connection.getURL(WebConnection.query.UPDATEORDER, par));

                                    refreshInfoOrder(O.getId());
                                }
                            }).start();
                        }
                    }).start();

                }
            }
        });



        //ListView Click on Item Event
        listProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);

            }
        });
        deleteOrder.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                if(Setting.getDebug())
                    System.out.println("DELETE ORDER");

                String par = "idOrdine="+O.getId();
                InsertIntoDB(Connection.getURL(WebConnection.query.DELETEORDER, par));
                Toast.makeText(getApplicationContext(), "Ordine cancellato", Toast.LENGTH_SHORT).show();

                Intent I = new Intent(activity_info_ordine.this, activity_gestione_ordini.class);
                I.putExtra("Cart", cartProducts);
                I.putExtra("User", UserLogged);
                I.putExtra("WebConnection" ,Connection);
                I.putExtra("Restaurant" ,Rest);

                startActivity(I);
                activity_info_ordine.this.finish();
            }
        });


        call.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                        animation1.setDuration(2000);
                        v.startAnimation(animation1);
                    }
                });

                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(activity_info_ordine.this,
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity_info_ordine.this,
                            Manifest.permission.CALL_PHONE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(activity_info_ordine.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                1);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                    if(Setting.getDebug())
                        System.out.println("CALL TO: " + U.getNumeroTelefono());

                    callPhone(U.getNumeroTelefono());
                }

            }
        });


        OnToggledListener x = new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(Setting.getDebug())
                    System.out.println("SWITCH EVENT");
                showAlertDialog();
            }
        };
        Domicilio.setOnToggledListener(x);




    }

    private void refreshInfoOrder(int idOrdine)
    {

        new Thread(new Runnable() {
            public void run() {
                isRefreshing = true;
                if(Setting.getDebug())
                    System.out.println("REFRESH INFO");



                String par = "idOrdine=" + idOrdine;
                String tmpJSON = downloadJSON(Connection.getURL(WebConnection.query.ORDER, par));
                O = fillOrder(tmpJSON);
                par = "NumeroTelefono=" + O.getNumeroTelefono() ;
                tmpJSON = downloadJSON(Connection.getURL(WebConnection.query.SEARCHACCOUNTBYID, par));
                U = fillUser(tmpJSON);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        loadIntoProductListView(O.getListProducts());
                        Domicilio.setOn(O.getAsporto());

                        Nominativo.setText(U.getNome() + " " + U.getCognome());
                        Indirizzo.setText(U.getIndirizzo());
                        OrderId.setText("Order ID: " + O.getId());
                        CostoTotale.setText("Costo Totale: " + String.valueOf(O.getTotaleCosto()) + " €");
                        System.out.println(O.getTotaleCosto());
                        switch (O.getStato())
                        {
                            case "Richiesto":
                                status.setSelectedTab(0);
                                break;
                            case "In Preparazione":
                                status.setSelectedTab(1);
                                break;
                            case "In Consegna":
                                status.setSelectedTab(2);
                                break;
                            case "Consegnato":
                                status.setSelectedTab(3);
                                break;
                            case "Tutto":
                                status.setSelectedTab(4);
                                break;
                        }

                    }
                });
                pd.dismiss();
                isRefreshing = false;
            }
        }).start();
    }

    private void showLoadingDialog() {
        pd = new ProgressDialog(this, R.style.DialogTheme);
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        pd.show();
    }
    private void showAlertDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ChangeDeliveryDialogFragment alertDialog = ChangeDeliveryDialogFragment.newInstance("Sei sicuro di voler cambiare?");
        alertDialog.show(fm, "fragment_alert");
    }
    @Override
    public void onFinishEditDialog(String inputText) {

        if(inputText.equals("Si"))
        {
            if(Setting.getDebug())
                System.out.println("CHANGING DOMICILIO OF ORDER " + O.getId());

            new Thread(new Runnable() {
                public void run() {

                    SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ITALIAN);
                    String date_time = datetime.format(O.getDateTime());

                    O.setAsporto(Domicilio.isOn());
                    if(!O.getAsporto())
                        O.setCosto(O.getTotaleCosto() - 1);
                    else
                        O.setCosto(O.getTotaleCosto() + 1);


                    String par = "idOrdine=" + O.getId() + "&Stato=" + O.getStato() + "&Asporto=" + O.getAsporto() +"&NumeroTelefono=" + O.getNumeroTelefono() + "&DataOra=" + date_time.replaceAll(" ", "%20") + "&Costo=" + O.getTotaleCosto();
                    InsertIntoDB(Connection.getURL(WebConnection.query.UPDATEORDER, par));

                    refreshInfoOrder(O.getId());

                    //CREATE NOTICE FOR CLIENT TO ADVISE OF CHANGE
                    if(Setting.getDebug())
                        System.out.println("INITIALIZE NOTICE FOR CLIENT");

                    Notice N = new Notice();
                    N.setStato(false);
                    N.setRicevutoDa(O.getNumeroTelefono());
                    N.setTitolo("Ordine " + O.getId());
                    if(!O.getAsporto())
                        N.setMessaggio("Cambiato da Domicilio a Asporto");
                    else
                        N.setMessaggio("Cambiato da Asporto a Domicilio");

                    N.setIdLocale(Rest.getId());
                    N.setCreatoDa(UserLogged.getNumeroTelefono());
                    N.setTipo("normal");

                    par = "Stato=" + N.getStato() + "&CreatoDa=" + N.getCreatoDa() + "&Messaggio=" + N.getMessaggio().replaceAll(" ", "%20") + "&idLocale=" + N.getIdLocale() +"&RicevutoDa=" + N.getRicevutoDa() + "&Tipo=" + N.getTipo() + "&Titolo=" + N.getTitolo().replaceAll(" ", "%20") ;
                    InsertIntoDB(Connection.getURL(WebConnection.query.INSERTNOTICE, par));


                    pd.dismiss();

                }
            }).start();
        }
        else
        {
            Domicilio.setOn(!Domicilio.isOn());
        }

    }


    public void InsertIntoDB(final String urlWebService) {

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




    public Order getOrder(){return O;}
    public WebConnection getConnection(){return Connection;}


    private void loadIntoProductListView(List<Product> listP)
    {
        if(Setting.getDebug())
            System.out.println("LOAD INTO LISTVIEW");

        List<HashMap<String, String>> listitems = new ArrayList<>();
        customAdapter_info_ordine adapter = new customAdapter_info_ordine(this, listitems, R.layout.list_item__with_2_icon, new String[]{"First Line", "Second Line"}, new int[]{R.id.text1, R.id.text2}, listP);

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
            I.putExtra("Restaurant" ,Rest);

            startActivity(I);
            activity_info_ordine.this.finish();
        }
        else if (id == R.id.nav_gestione_ordini)
        {
            Intent I = new Intent(activity_info_ordine.this, activity_gestione_ordini.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            I.putExtra("Restaurant" ,Rest);

            startActivity(I);
            activity_info_ordine.this.finish();
        }
        else if (id == R.id.nav_ordini)
        {
            Intent I = new Intent(activity_info_ordine.this, activity_ordini.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            I.putExtra("Restaurant" ,Rest);

            startActivity(I);
            activity_info_ordine.this.finish();
        }
        else if (id == R.id.nav_carrello)
        {
            Intent I = new Intent(activity_info_ordine.this, activity_carrello.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            I.putExtra("Restaurant" ,Rest);

            startActivity(I);
            activity_info_ordine.this.finish();
        }
        else if (id == R.id.nav_map)
        {
            Intent I = new Intent(activity_info_ordine.this, activity_map.class);
            I.putExtra("Cart", cartProducts);
            I.putExtra("User", UserLogged);
            I.putExtra("WebConnection" ,Connection);
            I.putExtra("Restaurant" ,Rest);

            startActivity(I);
            activity_info_ordine.this.finish();
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
            Intent I = new Intent(activity_info_ordine.this, MainActivity.class);
            I.putExtra("WebConnection" ,Connection);
            I.putExtra("Restaurant" ,Rest);
            startActivity(I);
            activity_info_ordine.this.finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_info_ordine);
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
                    startActivity(new Intent(activity_info_ordine.this, MainActivity.class));
                    activity_info_ordine.this.finish();
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
