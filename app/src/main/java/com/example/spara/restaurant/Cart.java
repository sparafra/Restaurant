package com.example.spara.restaurant;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Parcelable {

    List<Product> listProducts;
    public Cart(){
        listProducts = new ArrayList<>();
    }
    public Cart(Parcel in){
        listProducts = new ArrayList<>();
        in.readList(listProducts, getClass().getClassLoader());
    }
    public void addProduct(Product P )
    {
        listProducts.add(P);
    }
    public float getTotalCost()
    {
        float Total =0;

        for(int k=0; k<listProducts.size(); k++)
        {
            Total += listProducts.get(k).getPrezzo();
        }

        return Total;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeList(listProducts);
    }

    public static final Parcelable.Creator<Cart> CREATOR
            = new Parcelable.Creator<Cart>() {
        public Cart createFromParcel(Parcel in) {
            return new Cart(in);
        }

        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };

    public List<Product> getListProducts() {
        return listProducts;
    }

    public void clear()
    {
        listProducts.clear();
    }
    public boolean remove(Product P)
    {
        try {
            listProducts.remove(P);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public int size(){return listProducts.size();}
}
