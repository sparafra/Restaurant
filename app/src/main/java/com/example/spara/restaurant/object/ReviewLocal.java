package com.example.spara.restaurant.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ReviewLocal extends Review implements Parcelable{

	Long idLocale;
	String Recensione;
	
	public ReviewLocal(Long idLocale, String NumeroTelefono, int Voto, String Recensione, Date DataOra)
	{
		super(NumeroTelefono, Voto, DataOra);
		this.idLocale = idLocale;
		this.Recensione = Recensione;
	}
	public ReviewLocal() {}
	public ReviewLocal(Parcel in){
		this.idLocale = in.readLong();
		this.Recensione = in.readString();

	}
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(idLocale);
		out.writeString(Recensione);
	}

	public int describeContents() {
		return 0;
	}
	public static final Parcelable.Creator<ReviewLocal> CREATOR
			= new Parcelable.Creator<ReviewLocal>() {
		public ReviewLocal createFromParcel(Parcel in) {
			return new ReviewLocal(in);
		}

		public ReviewLocal[] newArray(int size) {
			return new ReviewLocal[size];
		}
	};
	public Long getIdLocale() {
		return idLocale;
	}

	public void setIdLocale(Long idLocale) {
		this.idLocale = idLocale;
	}

	public String getRecensione() {
		return Recensione;
	}

	public void setRecensione(String recensione) {
		Recensione = recensione;
	}
	
	
}
