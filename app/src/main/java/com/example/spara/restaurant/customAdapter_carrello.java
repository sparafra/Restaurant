package com.example.spara.restaurant;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class customAdapter_carrello extends SimpleAdapter {
    LayoutInflater inflater;
    Context context;
    List<HashMap<String, String>> arrayList;


    public customAdapter_carrello(Context context, List<HashMap<String, String>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
        this.arrayList = data;
        inflater.from(context);


    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        Cart cartProducts = (Cart) ((Activity) context).getIntent().getParcelableExtra("Cart");

        TextView Quantity = (TextView) view.findViewById(R.id.text1_2);
        Quantity.setText("x" + cartProducts.getListProducts().get(position).Quantity);

        ImageView remove = (ImageView) view.findViewById(R.id.icon2);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, arrayList.get(position).get("name"), Toast.LENGTH_SHORT).show();


                ListView list = (ListView) view.findViewById(R.id.listProdotti);


                if (position != -1) {
                    if (cartProducts.size() > 0) {
                        new Thread(new Runnable() {
                            public void run() {
                                if(cartProducts.getListProducts().get(position).getQuantity() == 1) {
                                    cartProducts.remove(cartProducts.getListProducts().get(position));
                                    arrayList.remove(position);
                                }
                                else
                                {
                                    cartProducts.getListProducts().get(position).setQuantity(cartProducts.getListProducts().get(position).getQuantity()-1);
                                }
                                //notifyDataSetChanged();
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyDataSetChanged();
                                        ImageView btnClearCart = ((Activity) context).findViewById(R.id.svuotaCarrello);
                                        Button btnOrder = ((Activity) context).findViewById(R.id.prenota);
                                        if(cartProducts.size() == 0)
                                        {
                                            btnClearCart.setVisibility(View.INVISIBLE);
                                            btnOrder.setVisibility(View.INVISIBLE);
                                            Animation animation2 = new AlphaAnimation(1.0f, 0.0f);
                                            animation2.setDuration(300);
                                            btnClearCart.startAnimation(animation2);
                                            btnOrder.startAnimation(animation2);
                                        }
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
                    if (cartProducts.size() > 0) {
                        new Thread(new Runnable() {
                            public void run() {

                                cartProducts.getListProducts().get(position).setQuantity(cartProducts.getListProducts().get(position).getQuantity()+1);

                                //notifyDataSetChanged();
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyDataSetChanged();
                                        ImageView btnClearCart = ((Activity) context).findViewById(R.id.svuotaCarrello);
                                        Button btnOrder = ((Activity) context).findViewById(R.id.prenota);
                                        if(cartProducts.size() == 0)
                                        {
                                            btnClearCart.setVisibility(View.INVISIBLE);
                                            btnOrder.setVisibility(View.INVISIBLE);
                                            Animation animation2 = new AlphaAnimation(1.0f, 0.0f);
                                            animation2.setDuration(300);
                                            btnClearCart.startAnimation(animation2);
                                            btnOrder.startAnimation(animation2);
                                        }
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