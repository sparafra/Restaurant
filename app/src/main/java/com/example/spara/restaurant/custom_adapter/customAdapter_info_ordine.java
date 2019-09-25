package com.example.spara.restaurant.custom_adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.spara.restaurant.R;
import com.example.spara.restaurant.activity.activity_info_ordine;
import com.example.spara.restaurant.object.Order;
import com.example.spara.restaurant.object.Product;
import com.example.spara.restaurant.object.WebConnection;

import java.util.HashMap;
import java.util.List;

public class customAdapter_info_ordine extends SimpleAdapter {
    LayoutInflater inflater;
    Context context;
    List<HashMap<String, String>> arrayList;
    List<Product> listProducts;

    public customAdapter_info_ordine(Context context, List<HashMap<String, String>> data, int resource, String[] from, int[] to, List<Product> listProducts) {
        super(context, data, resource, from, to);
        this.context = context;
        this.arrayList = data;
        inflater.from(context);
        this.listProducts = listProducts;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        TextView Quantity = (TextView) view.findViewById(R.id.text1_2);
        Quantity.setText("x" + listProducts.get(position).getQuantity());

        ImageView remove = (ImageView) view.findViewById(R.id.icon2);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, arrayList.get(position).get("name"), Toast.LENGTH_SHORT).show();


                ListView list = (ListView) view.findViewById(R.id.listProdotti);


                if (position != -1) {
                    if (listProducts.size() > 0) {
                        new Thread(new Runnable() {
                            public void run() {
                                if(listProducts.get(position).getQuantity() == 1) {

                                    if (context instanceof activity_info_ordine) {
                                        Order ord = ((activity_info_ordine) context).getOrder();
                                        String par = "idOrdine=" + ord.getId() + "&idProdotto=" + listProducts.get(position).getId();
                                        ((activity_info_ordine) context).InsertIntoDB(((activity_info_ordine) context).getConnection().getURL(WebConnection.query.DELETEORDERPRODUCT, par));
                                    }
                                    listProducts.remove(listProducts.get(position));
                                    arrayList.remove(position);
                                }
                                else
                                {
                                    listProducts.get(position).setQuantity(listProducts.get(position).getQuantity()-1);
                                    if (context instanceof activity_info_ordine) {
                                        Order ord = ((activity_info_ordine) context).getOrder();
                                        String par = "idOrdine=" + ord.getId() + "&idProdotto=" + listProducts.get(position).getId() + "&Quantita=" + listProducts.get(position).getQuantity();
                                        ((activity_info_ordine) context).InsertIntoDB(((activity_info_ordine) context).getConnection().getURL(WebConnection.query.UPDATEORDERPRODUCT, par));
                                    }
                                }
                                //notifyDataSetChanged();
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyDataSetChanged();


                                    }
                                });

                            }

                        }).start();

                    }
                } else {
                    //Toast.makeText(getApplicationContext(), "Non sono presenti prodotti", Toast.LENGTH_SHORT).show();

                }

            }
        });

        ImageView add = (ImageView) view.findViewById(R.id.icon1);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, arrayList.get(position).get("name"), Toast.LENGTH_SHORT).show();


                ListView list = (ListView) view.findViewById(R.id.listProdotti);


                if (position != -1) {
                    if (listProducts.size() > 0) {
                        new Thread(new Runnable() {
                            public void run() {

                                listProducts.get(position).setQuantity(listProducts.get(position).getQuantity()+1);
                                if (context instanceof activity_info_ordine) {
                                    Order ord = ((activity_info_ordine) context).getOrder();
                                    String par = "idOrdine=" + ord.getId() + "&idProdotto=" + listProducts.get(position).getId() + "&Quantita=" + listProducts.get(position).getQuantity();
                                    ((activity_info_ordine) context).InsertIntoDB(((activity_info_ordine) context).getConnection().getURL(WebConnection.query.UPDATEORDERPRODUCT, par));
                                }
                                //notifyDataSetChanged();
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyDataSetChanged();

                                    }
                                });

                            }

                        }).start();

                    }
                } else {
                    //Toast.makeText(getApplicationContext(), "Non sono presenti prodotti", Toast.LENGTH_SHORT).show();

                }

            }
        });
        return view;
    }

}