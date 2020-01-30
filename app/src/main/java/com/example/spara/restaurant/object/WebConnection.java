package com.example.spara.restaurant.object;

import android.os.Parcel;
import android.os.Parcelable;

public class WebConnection extends Thread implements Parcelable  {

    //final String ipWebServer = "212.237.3.25";
    final String ipWebServer = "192.168.1.2";


    public enum query {
        LISTACCOUNTS, INGREDIENTS, ORDERPRODUCTSUSER, BURGERFRIESINGREDIENTS, PIZZEINGREDIENTS, PRODUCTSINGREDIENTS, SALADSINGREDIENTS, INSERTUSER, INSERTORDER, INSERTORDERPRODUCT, INSERTPRODUCT,
        INSERTPRODUCTTIPOLOGY, INSERTPRODUCTINGREDIENT, UPDATEORDER, NOTICE, NOTICESTATEUPDATE, PRODUCTIMAGE, SEARCHACCOUNT, ORDERPRODUCTSUSERSTATE,
        ORDER, ADMINLIST, INSERTLOCALUSER, INSERTNOTICE, SEARCHACCOUNTBYID, UPDATEORDERPRODUCT, DELETEORDERPRODUCT, DELETEORDER, ALLLOCALS
    }

    public  WebConnection(){ }
    public WebConnection(Parcel in){ }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(ipWebServer);
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Parcelable.Creator<WebConnection> CREATOR
            = new Parcelable.Creator<WebConnection>() {
        public WebConnection createFromParcel(Parcel in) {
            return new WebConnection(in);
        }

        public WebConnection[] newArray(int size) {
            return new WebConnection[size];
        }
    };

    public String getURL(query Q, String parameters)
    {
        switch(Q) {
            case ORDERPRODUCTSUSER:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/OrdersByUser?" + parameters;
            case ORDER:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/OrdersById?" + parameters;
            case ORDERPRODUCTSUSERSTATE:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/OrdersByState?" + parameters;
            case INSERTUSER:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/SaveUser?" + parameters;
            case INSERTORDER:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/SaveOrder?" + parameters;
            case INSERTORDERPRODUCT:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/SaveOrderProduct?" + parameters;
            case INSERTPRODUCT:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/SaveProduct?" + parameters;
            case INSERTPRODUCTTIPOLOGY:
                return "http://" + ipWebServer + "/API/InserisciProdottoTipologia.php?" + parameters; //NON UTILIZZATA
            case INSERTPRODUCTINGREDIENT:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/SaveProductIngredient?" + parameters;
            case UPDATEORDER:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/UpdateOrder?" + parameters;
            case NOTICE:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/AllNoticeByRicevutoDa?" + parameters;
            case NOTICESTATEUPDATE:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/UpdateNotice?" + parameters;
            case PRODUCTIMAGE:
                return "http://" + ipWebServer + ":8080/Restaurant/" + parameters.replaceAll(" ", "%20");
            case SEARCHACCOUNT:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/UserByMail?" + parameters;
            case SEARCHACCOUNTBYID:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/UserById?" + parameters;
            case ADMINLIST:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/AllUsersByAdmin?" + parameters;
            case INSERTNOTICE:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/SaveNotice?" + parameters;
            case UPDATEORDERPRODUCT:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/UpdateOrderProduct?" + parameters;
            case DELETEORDERPRODUCT:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/DeleteOrderProduct?" + parameters;
            case DELETEORDER:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/DeleteOrder?" + parameters;
            case LISTACCOUNTS:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/AllUsers?idLocale=" + parameters;
            case INGREDIENTS:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/AllIngredients?idLocale=" + parameters;
            case BURGERFRIESINGREDIENTS:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/ProductsByType?Type=Fritti&idLocale=" + parameters;
            case PIZZEINGREDIENTS:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/ProductsByType?Type=Pizza&idLocale=" + parameters;
            case PRODUCTSINGREDIENTS:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/AllProducts?idLocale=" + parameters;
            case SALADSINGREDIENTS:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/app/ProductsByType?Type=Panino&idLocale=" +parameters;
            default:
                return "";
        }
    }
    public String getURL(query Q)
    {
        switch(Q) {

            case ALLLOCALS:
                return "http://" + ipWebServer + ":8080/Restaurant/servlet/AllLocals";
            default:
                return "";
        }
    }
}
