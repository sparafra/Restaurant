package com.example.spara.restaurant;

public class Notice {

    int idAvviso;
    Boolean Stato;
    String CreatoDa;
    String RicevutoDa;
    String Messaggio;
    int idLocale;

    public Notice(){}

    public int getIdAvviso() {
        return idAvviso;
    }
    public void setIdAvviso(int idAvviso) {
        this.idAvviso = idAvviso;
    }
    public Boolean getStato() {
        return Stato;
    }
    public void setStato(Boolean stato) {
        Stato = stato;
    }
    public String getCreatoDa() {
        return CreatoDa;
    }
    public void setCreatoDa(String creatoDa) {
        CreatoDa = creatoDa;
    }
    public String getRicevutoDa() {
        return RicevutoDa;
    }
    public void setRicevutoDa(String ricevutoDa) {
        RicevutoDa = ricevutoDa;
    }
    public String getMessaggio() {
        return Messaggio;
    }
    public void setMessaggio(String messaggio) {
        Messaggio = messaggio;
    }
    public int getIdLocale() {
        return idLocale;
    }
    public void setIdLocale(int idLocale) {
        this.idLocale = idLocale;
    }
}
