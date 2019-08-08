package com.example.spara.restaurant;

import android.os.Parcel;
import android.os.Parcelable;

public class Ingredient implements Parcelable {

    int id;
    String Nome;
    float Prezzo;

    public Ingredient(int id, String Nome, float Prezzo)
    {
        this.id = id;
        this.Nome = Nome;
        this.Prezzo = Prezzo;
    }
    public Ingredient(Parcel in){
        this.id = in.readInt();
        this.Nome = in.readString();
        this.Prezzo = in.readFloat();
    }
    public Ingredient(){}

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(Nome);
        out.writeFloat(Prezzo);
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Parcelable.Creator<Ingredient> CREATOR
            = new Parcelable.Creator<Ingredient>() {
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNome() {
        return Nome;
    }
    public void setNome(String nome) {
        Nome = nome;
    }
    public float getPrezzo() {
        return Prezzo;
    }
    public void setPrezzo(float prezzo) {
        Prezzo = prezzo;
    }
}
