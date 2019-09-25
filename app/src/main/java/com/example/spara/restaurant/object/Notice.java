package com.example.spara.restaurant.object;

public class Notice {

    int idAvviso;
    Boolean Stato;
    String CreatoDa;
    String RicevutoDa;
    String Messaggio;
    Long idLocale;

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
    public Long getIdLocale() {
        return idLocale;
    }
    public void setIdLocale(Long idLocale) {
        this.idLocale = idLocale;
    }
}
