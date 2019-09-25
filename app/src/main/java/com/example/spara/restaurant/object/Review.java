package com.example.spara.restaurant.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Review implements Parcelable{

	//Long idProdotto;
	
	String NumeroTelefono;
	
	int Voto;
		
	Date DataOra;
	
    public Review(String NumeroTelefono, int Voto, Date DataOra)
    {
    
        this.NumeroTelefono = NumeroTelefono;
        this.Voto = Voto;
        this.DataOra = DataOra;
    }
	public Review(Parcel in){
		this.NumeroTelefono = in.readString();
		this.Voto = in.readInt();
		this.DataOra = (java.util.Date) in.readSerializable();
	}
    public Review()
    {
    	
    }
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(NumeroTelefono);
    	out.writeInt(Voto);
		out.writeSerializable(DataOra);
	}

	public int describeContents() {
		return 0;
	}
	public static final Parcelable.Creator<Review> CREATOR
			= new Parcelable.Creator<Review>() {
		public Review createFromParcel(Parcel in) {
			return new Review(in);
		}

		public Review[] newArray(int size) {
			return new Review[size];
		}
	};
	public String getNumeroTelefono() {
		return NumeroTelefono;
	}

	public void setNumeroTelefono(String numeroTelefono) {
		NumeroTelefono = numeroTelefono;
	}

	public int getVoto() {
		return Voto;
	}

	public void setVoto(int voto) {
		Voto = voto;
	}

	public Date getDataOra() {
		return DataOra;
	}

	public void setDataOra(Date dataOra) {
		DataOra = dataOra;
	}
    
    
}
    