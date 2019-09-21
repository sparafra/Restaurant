package com.example.spara.restaurant;

public class Restaurant {

    static final Long id = Long.valueOf(1);
    static final String Nome = "";
    static final String Indirizzo = "";
    static final String NumeroTelefono = "0981808098";
    static final String Mail = "";
    static final boolean Attivo = true;

    public Restaurant(){}

    public Long getId() {
        return id;
    }


    public String getNome() {
        return Nome;
    }


    public String getIndirizzo() {
        return Indirizzo;
    }


    public String getNumeroTelefono() {
        return NumeroTelefono;
    }


    public String getMail() {
        return Mail;
    }


    public boolean getAttivo() {
        return Attivo;
    }

}
