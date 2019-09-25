package com.example.spara.restaurant.object;

public class Restaurant {

    static final Long id = Long.valueOf(1);
    static final String Nome = "";
    static final String Indirizzo = "";
    static final String NumeroTelefono = "0981808098";
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
