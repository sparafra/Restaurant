package com.example.spara.restaurant;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Background extends Service {

    WebConnection Connection;
    User UserLogged;
    Cart cartProducts;

    String UserNumber;
    String Mail;
    String Password;
    List<Notice> listNotice;

    public Background() {
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        /*
        if (intent != null && intent.getExtras() != null) {
            UserNumber = (String) intent.getExtras().get("UserNumber");
        }*/
        onTaskRemoved(intent);

        return START_STICKY;
    }

    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implement");
    }

    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Connection = new WebConnection();
            loadPreferences();
            String par ="NumeroTelefono=" + UserNumber ;
            String json = downloadJSON(Connection.getURL(WebConnection.query.SEARCHACCOUNTBYID, par));
            fillUser(json);

            while (true) {
                // put your socket-code here
                loadPreferences();
                if (!UserNumber.equals("") && !Mail.equals("") && !Password.equals("")) {
                    try {
                        par = "NumeroTelefono=" + UserNumber;
                        //Numero conviene prenderlo da shared preference quando si salveranno i dati di accesso sul telefono
                        json = downloadJSON(Connection.getURL(WebConnection.query.NOTICE, par));
                        listNotice = new ArrayList<>();
                        fillNoticeList(json, listNotice);
                        System.out.println(listNotice.size());
                        for (int k = 0; k < listNotice.size(); k++) {
                            //Switch notify message
                            addNotification("Notifica", listNotice.get(k).getMessaggio(), k, "NewOrdine");
                        }
                        for (int k = 0; k < listNotice.size(); k++) {
                            //Update notice on database by state=0 to state=1
                            String Mex = listNotice.get(k).getMessaggio().replace(" ", "%20");
                            par = "idAvviso=" + listNotice.get(k).getIdAvviso() + "&Stato=true&CreatoDa=" + listNotice.get(k).getCreatoDa() + "&Messaggio=" + Mex + "&idLocale=" + listNotice.get(k).getIdLocale() + "&RicevutoDa=" + listNotice.get(k).getRicevutoDa() ;
                            InsertIntoDB(Connection.getURL(WebConnection.query.NOTICESTATEUPDATE, par));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        //SystemClock.sleep(150000);
                        //
                        // Thread.sleep(150000); //sleep di 2.5 minuti
                        Thread.sleep(90000); //sleep di 1.5 minuti
                    } catch (Exception e) {
                    }
                }
            }
        }
    };

    private void fillUser(String json) {

        try {
            UserLogged = new User();
            JSONObject obj = new JSONObject(json);

            UserLogged.setNumeroTelefono(obj.getString("NumeroTelefono"));
            UserLogged.setMail(obj.getString("Mail"));
            UserLogged.setPassword(obj.getString("Password"));
            UserLogged.setNome(obj.getString("Nome"));
            UserLogged.setCognome(obj.getString("Cognome"));
            UserLogged.setIndirizzo(obj.getString("Indirizzo"));
            if(obj.getBoolean("Amministratore") == false)
            {
                UserLogged.setAmministratore(false);

            }
            else
            {
                UserLogged.setAmministratore(true);

            }
            if(obj.getBoolean("Confermato") == false)
            {
                UserLogged.setConfermato(false);

            }
            else
            {
                UserLogged.setConfermato(true);
            }


        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        new Thread(runnable).start();

        //start a separate thread and start listening to your network object

            //Log.i("PROVA SERVICE", "Evento n."+n++);
            System.out.println("BackgroundService");
            /*
            new Thread(new Runnable() {
                public void run() {
                    while(true) {
                        loadPreferences();
                        if(!UserNumber.equals("") && !Mail.equals("") && !Password.equals("")) {
                            try {
                                String par = "NumeroTelefono=" + UserNumber;
                                //Numero conviene prenderlo da shared preference quando si salveranno i dati di accesso sul telefono
                                String json = downloadJSON(Connection.getURL(WebConnection.query.NOTICE, par));
                                listNotice = new ArrayList<>();
                                fillNoticeList(json, listNotice);
                                System.out.println(listNotice.size());
                                for (int k = 0; k < listNotice.size(); k++) {
                                    addNotification("Notifica", listNotice.get(k).getMessaggio(), k);
                                }
                                for (int k = 0; k < listNotice.size(); k++) {
                                    //Update notice on database by state=0 to state=1
                                    par = "idAvviso=" + listNotice.get(k).getIdAvviso();
                                    InsertIntoDB(Connection.getURL(WebConnection.query.NOTICESTATEUPDATE, par));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                SystemClock.sleep(150000);
                                //Thread.sleep(150000); //sleep di 2.5 minuti
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                }
            }).start();
*/
    }
    private void loadPreferences() {

        SharedPreferences settings = getSharedPreferences("alPachino",
                Context.MODE_PRIVATE);

        // Get value
        UserNumber = settings.getString("NumeroTelefono", "");
        Mail = settings.getString("Mail", "");
        Password = settings.getString("Password", "");
    }
    private void addNotification(String Title, String Message, int id, String State) {

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(Title)
                        .setContentText(Message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setSound(alarmSound)
                        .setVibrate(new long[]{500L, 200L, 200L, 500L});

        //System.out.println(Message.substring(Message.indexOf(": ")+2));
        //System.out.println(Integer.parseInt(Message.substring(Message.indexOf(": ")+2)));
        Intent notificationIntent = new Intent(this, activity_info_ordine.class);
        notificationIntent.putExtra("Cart", cartProducts);
        notificationIntent.putExtra("User", UserLogged);
        notificationIntent.putExtra("WebConnection" ,Connection);
        notificationIntent.putExtra("idOrdine", Integer.parseInt(Message.substring(Message.indexOf(": ")+2)));

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
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
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, builder.build());
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
    private void fillNoticeList(String json, List<Notice> list)throws JSONException {

        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            Notice N = new Notice();
            N.setIdAvviso(obj.getInt("idAvviso"));
            N.setCreatoDa(obj.getString("CreatoDa"));
            N.setIdLocale(obj.getInt("idLocale"));
            N.setMessaggio(obj.getString("Messaggio"));
            N.setRicevutoDa(obj.getString("RicevutoDa"));
            if(obj.getBoolean("Stato") == false)
                N.setStato(false);
            else
                N.setStato(true);

            list.add(N);
        }

    }
}
