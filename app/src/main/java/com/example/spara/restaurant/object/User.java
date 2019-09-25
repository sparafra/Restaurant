package com.example.spara.restaurant.object;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {



    String NumeroTelefono;
    String Nome;
    String Cognome;
    String Mail;
    String Indirizzo;
    String Password;
    boolean Confermato;
    boolean Amministratore;
    Long idLocale;
    boolean Disabilitato;


    public User(){ }
    public User(String NumeroTelefono, String Nome, String Cognome, String Mail, String Indirizzo, String Password, boolean Confermato, boolean Amministratore, Long idLocale, boolean Disabilitato)
    {
        this.NumeroTelefono = NumeroTelefono;
        this.Nome = Nome;
        this.Cognome = Cognome;
        this.Mail = Mail;
        this.Indirizzo = Indirizzo;
        this.Password = Password;
        this.Confermato = Confermato;
        this.Amministratore = Amministratore;
        this.idLocale = idLocale;
        this.Disabilitato = Disabilitato;
    }

    public User(Parcel in){
        this.NumeroTelefono = in.readString();
        this.Nome = in.readString();
        this.Cognome = in.readString();
        this.Mail = in.readString();
        this.Indirizzo = in.readString();
        this.Password = in.readString();
        this.Confermato = Boolean.valueOf(in.readString());
        this.Amministratore = Boolean.valueOf(in.readString());
        this.idLocale = Long.valueOf(in.readString());
        this.Disabilitato = Boolean.valueOf(in.readString());
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(NumeroTelefono);
        out.writeString(Nome);
        out.writeString(Cognome);
        out.writeString(Mail);
        out.writeString(Indirizzo);
        out.writeString(Password);
        out.writeString(String.valueOf(Confermato));
        out.writeString(String.valueOf(Amministratore));
        out.writeString(String.valueOf(idLocale));
        out.writeString(String.valueOf(Disabilitato));
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public boolean equals(Object object) {
        User product = (User) object;
        return (this.NumeroTelefono == product.getNumeroTelefono());
    }
    public String getNumeroTelefono() {
        return NumeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        NumeroTelefono = numeroTelefono;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getCognome() {
        return Cognome;
    }

    public void setCognome(String cognome) {
        Cognome = cognome;
    }

    public String getMail() {
        return Mail;
    }

    public void setMail(String mail) {
        Mail = mail;
    }

    public String getIndirizzo() {
        return Indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        Indirizzo = indirizzo;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
    public boolean getConfermato() {
        return Confermato;
    }

    public void setConfermato(boolean confermato) {
        Confermato = confermato;
    }
    public boolean getAmministratore() {
        return Amministratore;
    }

    public void setAmministratore(boolean amministratore) {
        Amministratore = amministratore;
    }

    public Long getIdLocale() {
        return idLocale;
    }

    public void setIdLocale(Long idLocale) {
        this.idLocale = idLocale;
    }

    public boolean getDisabilitato() {
        return Disabilitato;
    }

    public void setDisabilitato(boolean disabilitato) {
        Disabilitato = disabilitato;
    }
}
