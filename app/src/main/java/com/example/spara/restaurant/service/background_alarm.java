package com.example.spara.restaurant.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.spara.restaurant.R;
import com.example.spara.restaurant.activity.activity_home;
import com.example.spara.restaurant.activity.activity_info_ordine;
import com.example.spara.restaurant.object.Cart;
import com.example.spara.restaurant.object.Notice;
import com.example.spara.restaurant.object.User;
import com.example.spara.restaurant.object.WebConnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.spara.restaurant.object.JSONUtility.*;

public class background_alarm extends BroadcastReceiver {


    WebConnection Connection;
    User UserLogged;
    Cart cartProducts;

    String UserNumber;
    String Mail;
    String Password;
    List<Notice> listNotice;

    String CHANNEL_ID = "alpachino";

    @Override
    public void onReceive(Context context, Intent intent) {

        new Thread(new Runnable() {
            public void run() {

                System.out.println("Background Service Fire");
                if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

                    //Intent serviceIntent = new Intent(context, MyService.class);
                    //context.startService(serviceIntent);
                }

                Connection = new WebConnection();

                loadPreferences(context);
                String par = "NumeroTelefono=" + UserNumber;
                String json = downloadJSON(Connection.getURL(WebConnection.query.SEARCHACCOUNTBYID, par));
                UserLogged = fillUser(json);

                createNotificationChannel(context);


                if (!UserNumber.equals("") && !Mail.equals("") && !Password.equals("")) {
                    try {
                        par = "NumeroTelefono=" + UserNumber;
                        //Numero conviene prenderlo da shared preference quando si salveranno i dati di accesso sul telefono
                        json = downloadJSON(Connection.getURL(WebConnection.query.NOTICE, par));
                        System.out.println(Connection.getURL(WebConnection.query.NOTICE, par));
                        listNotice = new ArrayList<>();
                        listNotice = fillNoticeList(json);
                        System.out.println(listNotice.size());
                        for (int k = 0; k < listNotice.size(); k++) {
                            //Switch notify message

                            addNotification(listNotice.get(k).getTitolo(), listNotice.get(k).getMessaggio(), k, listNotice.get(k).getTipo(), context);
                        }
                        for (int k = 0; k < listNotice.size(); k++) {
                            //Update notice on database by state=0 to state=1
                            String Mex = listNotice.get(k).getMessaggio().replace(" ", "%20");
                            par = "idAvviso=" + listNotice.get(k).getIdAvviso() + "&Stato=true&CreatoDa=" + listNotice.get(k).getCreatoDa() + "&Messaggio=" + Mex + "&idLocale=" + listNotice.get(k).getIdLocale() + "&RicevutoDa=" + listNotice.get(k).getRicevutoDa() + "&Tipo=" + listNotice.get(k).getTipo() + "&Titolo=" + listNotice.get(k).getTitolo().replaceAll(" ", "%20");
                            InsertIntoDB(Connection.getURL(WebConnection.query.NOTICESTATEUPDATE, par));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }}).start();

    }

    private void loadPreferences(Context context) {

        SharedPreferences settings = context.getSharedPreferences("alPachino",
                Context.MODE_PRIVATE);

        // Get value
        UserNumber = settings.getString("NumeroTelefono", "");
        Mail = settings.getString("Mail", "");
        Password = settings.getString("Password", "");
    }
    private void addNotification(String Title, String Message, int id, String Tipo, Context context) {

        System.out.println("START NOTIFICATION");
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(Title)
                        .setContentText(Message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setSound(alarmSound)
                        .setVibrate(new long[]{500L, 200L, 200L, 500L});


        // Create an explicit intent for an Activity in your app


        //System.out.println(Message.substring(Message.indexOf(": ")+2));
        //System.out.println(Integer.parseInt(Message.substring(Message.indexOf(": ")+2)));
        Intent notificationIntent;
        try {
            notificationIntent = new Intent(context, activity_info_ordine.class);
            notificationIntent.putExtra("Cart", cartProducts);
            notificationIntent.putExtra("User", UserLogged);
            notificationIntent.putExtra("WebConnection", Connection);
            if(Tipo.equals("new_order"))
                notificationIntent.putExtra("idOrdine", Integer.parseInt(Message.substring(Message.indexOf(":") + 2)));


        }catch (Exception e){
            notificationIntent = new Intent(context, activity_home.class);
            notificationIntent.putExtra("Cart", cartProducts);
            notificationIntent.putExtra("User", UserLogged);
            notificationIntent.putExtra("WebConnection", Connection);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(contentIntent);

        /*
        PendingIntent i;
        if (State.equals("NewOrdine"))
        {
            i = PendingIntent.getActivity(this, 0,
                new Intent(this, activity_gestione_ordini.class),
                0);
        }
        */
        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, builder.build());
    }
    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
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
}
