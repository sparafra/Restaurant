package com.example.spara.restaurant.object;

import android.os.Parcel;
import android.os.Parcelable;

public class Restaurant implements Parcelable{

    Long id = Long.valueOf(6);
    String Nome = "Panino Genuino";
    String Indirizzo = "Via G. Marconi, 82, 87036 Rende CS";
    String NumeroTelefono = "0981808080";
    String Mail = "";
    boolean Attivo = true;
    String LogoURL;
    String BackgroundURL;

    public Restaurant(){}
    public Restaurant(Long id, String Nome, String Indirizzo, String NumeroTelefono, String Mail, String LogoURL, String BackgroundURL, boolean Attivo)
    {
        this.id = id;
        this.Nome = Nome;
        this.Indirizzo = Indirizzo;
        this.NumeroTelefono = NumeroTelefono;
        this.Mail = Mail;
        this.LogoURL = LogoURL;
        this.BackgroundURL = BackgroundURL;
        this.Attivo = Attivo;
    }
    public Restaurant(Parcel in){
        this.id = in.readLong();
        this.Nome = in.readString();
        this.Indirizzo = in.readString();
        this.NumeroTelefono = in.readString();
        this.Mail = in.readString();
        this.LogoURL = in.readString();
        this.BackgroundURL = in.readString();
        this.Attivo = Boolean.valueOf(in.readString());

    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(String.valueOf(id));
        out.writeString(Nome);
        out.writeString(Indirizzo);
        out.writeString(NumeroTelefono);
        out.writeString(Mail);
        out.writeString(LogoURL);
        out.writeString(BackgroundURL);
        out.writeString(String.valueOf(Attivo));

    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Parcelable.Creator<Restaurant> CREATOR
            = new Parcelable.Creator<Restaurant>() {
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public boolean equals(Object object) {
        Restaurant restaurant = (Restaurant) object;
        return (this.id == restaurant.getId());
    }

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

    public void setId(Long id){this.id = id;}
    public void setNome(String nome) {
        Nome = nome;
    }
    public void setIndirizzo(String indirizzo) {
        Indirizzo = indirizzo;
    }
    public void setNumeroTelefono(String numeroTelefono) {
        NumeroTelefono = numeroTelefono;
    }
    public void setMail(String mail) {
        Mail = mail;
    }
    public void setAttivo(boolean attivo) {
        Attivo = attivo;
    }
    public String getLogoURL() {
        return LogoURL;
    }
    public void setLogoURL(String logoURL) {
        LogoURL = logoURL;
    }

    public String getBackgroundURL() {
        return BackgroundURL;
    }

    public void setBackgroundURL(String backgroundURL) {
        BackgroundURL = backgroundURL;
    }
}
