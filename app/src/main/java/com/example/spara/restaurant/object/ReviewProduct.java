package com.example.spara.restaurant.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ReviewProduct extends Review implements Parcelable{

	Long idProduct;
	
	public ReviewProduct(Long idProduct, String NumeroTelefono, int Voto, Date DataOra)
	{
		super(NumeroTelefono, Voto, DataOra);
		this.idProduct = idProduct;
	}
	public ReviewProduct()
	{
		super();
	}
	public ReviewProduct(Parcel in){
		this.idProduct = in.readLong();
		this.NumeroTelefono = in.readString();
		this.Voto = in.readInt();
		this.DataOra = (java.util.Date) in.readSerializable();
	}
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(idProduct);
		out.writeString(NumeroTelefono);
		out.writeInt(Voto);
		out.writeSerializable(DataOra);
	}

	public int describeContents() {
		return 0;
	}
	public static final Parcelable.Creator<ReviewProduct> CREATOR
			= new Parcelable.Creator<ReviewProduct>() {
		public ReviewProduct createFromParcel(Parcel in) {
			return new ReviewProduct(in);
		}

		public ReviewProduct[] newArray(int size) {
			return new ReviewProduct[size];
		}
	};

	public Long getIdProduct() {
		return idProduct;
	}

	public void setIdProduct(Long idProduct) {
		this.idProduct = idProduct;
	}
	
}
