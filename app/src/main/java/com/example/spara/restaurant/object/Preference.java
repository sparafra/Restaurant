package com.example.spara.restaurant.object;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preference {

    public Preference(){}

    public static void savePreferences(String NumeroTelefono, String Mail, String Password, Activity activity) {
        SharedPreferences settings = activity.getSharedPreferences("alPachino",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        // Edit and commit
        editor.putString("NumeroTelefono", NumeroTelefono);
        editor.putString("Mail", Mail);
        editor.putString("Password", Password);
        editor.commit();
    }


}
