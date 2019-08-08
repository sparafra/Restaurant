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
    List<Ingredient> listIngredienti;

    public Product(int id, String Nome, float Prezzo, String Tipo, String ImageURL, List<Ingredient> listIngredienti)
    {
        this.id = id;
        this.Nome = Nome;
        this.Prezzo = Prezzo;
        this.Tipo = Tipo;
        this.ImageURL = ImageURL;
        this.listIngredienti = listIngredienti;
    }
    public Product(Parcel in){
        listIngredienti = new ArrayList<>();
        this.id = in.readInt();
        this.Nome = in.readString();
        this.Prezzo = in.readFloat();
        this.Tipo = in.readString();
        this.ImageURL = in.readString();
        in.readList(listIngredienti, getClass().getClassLoader());
    }
    public  Product(){}
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(Nome);
        out.writeFloat(Prezzo);
        out.writeString(Tipo);
        out.writeString(ImageURL);
        out.writeList(listIngredienti);
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
    public void setImageURL(String ImageURL){this.ImageURL = ImageURL;}
    public String getImageURL(){return ImageURL;}
}
