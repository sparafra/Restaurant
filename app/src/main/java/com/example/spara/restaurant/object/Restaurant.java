package com.example.spara.restaurant.object;

public class Restaurant {

    static final Long id = Long.valueOf(1);
    static final String Nome = "AlPachino";
    static final String Indirizzo = "Via G. Marconi, 82, 87036 Rende CS";
    static final String NumeroTelefono = "0981808080";
    static final String Mail = "";
    static final boolean Attivo = true;

    public Restaurant(){}

    public static Long getId() {
        return id;
    }


    public static String getNome() {
        return Nome;
    }


    public static String getIndirizzo() {
        return Indirizzo;
    }


    public static String getNumeroTelefono() {
        return NumeroTelefono;
    }


    public static String getMail() {
        return Mail;
    }


    public static boolean getAttivo() {
        return Attivo;
    }

}
