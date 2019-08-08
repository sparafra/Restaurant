package com.example.spara.restaurant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {

    int id;
    String Stato;
    boolean Asporto;
    String NumeroTelefono;
    float Costo;
    List<Product> listProducts;


    Date DateTime;

    public Order(){listProducts = new ArrayList<>(); Costo =0;}


    public void addProduct(Product P)
    {
        listProducts.add(P);
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getStato() {
        return Stato;
    }
    public void setStato(String stato) {
        Stato = stato;
    }
    public boolean getAsporto() {
        return Asporto;
    }
    public void setAsporto(boolean asporto) {
        Asporto = asporto;
    }
    public String getNumeroTelefono() {
        return NumeroTelefono;
    }
    public void setNumeroTelefono(String numeroTelefono) {
        NumeroTelefono = numeroTelefono;
    }
    public Date getDateTime() {
        return DateTime;
    }
    public void setDateTime(Date dateTime) {
        DateTime = dateTime;
    }
    public List<Product> getListProducts() {
        return listProducts;
    }
    public void setListProducts(List<Product> listProducts) {
        this.listProducts = listProducts;
    }
    public void setCosto(float Costo){this.Costo = Costo;}

    @Override
    public boolean equals(Object obj) {
        Order o = (Order) obj;
        return (this.id == o.getId());
    }

    public float getTotaleCosto()
    {
        if(Costo == 0)
        {
            float Tot=0;
            for(int k=0; k<listProducts.size(); k++)
            {
                Tot += listProducts.get(k).getPrezzo();
            }
            return  Tot;
        }
        else
            return Costo;


    }
}
