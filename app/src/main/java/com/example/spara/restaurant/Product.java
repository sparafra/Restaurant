package com.example.spara.restaurant;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class Product implements Parcelable {

    int id;
    String Nome;
    float Prezzo;
    String Tipo;
    String ImageURL;
    int Quantity;
    List<Ingredient> listIngredienti;
    List<ReviewProduct> listReview;
    Long idLocale;


    public Product(int id, String Nome, float Prezzo, String Tipo, String ImageURL, int Quantity, List<Ingredient> listIngredienti, List<ReviewProduct> listReview, Long idLocale)
    {
        this.id = id;
        this.Nome = Nome;
        this.Prezzo = Prezzo;
        this.Tipo = Tipo;
        this.ImageURL = ImageURL;
        this.Quantity = Quantity;
        this.listIngredienti = listIngredienti;
        this.listReview = listReview;
        this.idLocale = idLocale;

    }
    public Product(Parcel in){
        listIngredienti = new ArrayList<>();
        this.id = in.readInt();
        this.Nome = in.readString();
        this.Prezzo = in.readFloat();
        this.Tipo = in.readString();
        this.ImageURL = in.readString();
        this.Quantity = in.readInt();
        if(listIngredienti == null)
            listIngredienti = new ArrayList<>();
        if(listReview == null)
            listReview = new ArrayList<>();
        in.readList(listIngredienti, getClass().getClassLoader());
        in.readList(listReview, getClass().getClassLoader());
        this.idLocale = in.readLong();
    }
    public  Product(){
        this.listIngredienti = new ArrayList<>();
        this.listReview = new ArrayList<>();
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(Nome);
        out.writeFloat(Prezzo);
        out.writeString(Tipo);
        out.writeString(ImageURL);
        out.writeInt(Quantity);
        out.writeList(listIngredienti);
        out.writeList(listReview);
        out.writeLong(idLocale);
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Parcelable.Creator<Product> CREATOR
            = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public boolean equals(Object object) {
        Product product = (Product) object;
        return (this.getId() == product.getId());
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setNome(String nome) {
        Nome = nome;
    }
    public void setPrezzo(float prezzo) {
        Prezzo = prezzo;
    }
    public int getId() {
        return id;
    }
    public String getNome() {
        return Nome;
    }
    public float getPrezzo() {
        return Prezzo;
    }
    public String getTipo() {
        return Tipo;
    }
    public void setTipo(String tipo) {
        Tipo = tipo;
    }
    public List<Ingredient> getListIngredienti() {
        return listIngredienti;
    }
    public void setListIngredienti(List<Ingredient> listIngredienti) {
        this.listIngredienti = listIngredienti;
    }
    public void setQuantity(int Quantity){this.Quantity=Quantity;}
    public int getQuantity(){return this.Quantity;}
    public void setImageURL(String ImageURL){this.ImageURL = ImageURL;}
    public String getImageURL(){return ImageURL;}
    public List<ReviewProduct> getListReview() {
        return listReview;
    }

    public void setListReview(List<ReviewProduct> listReview) {
        this.listReview = listReview;
    }

    public Long getIdLocale() {
        return idLocale;
    }

    public void setIdLocale(Long idLocale) {
        this.idLocale = idLocale;
    }
}
