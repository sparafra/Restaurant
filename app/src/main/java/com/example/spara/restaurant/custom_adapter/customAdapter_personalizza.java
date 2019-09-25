package com.example.spara.restaurant.custom_adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.example.spara.restaurant.R;
import com.example.spara.restaurant.activity.activity_personalizza;
import com.example.spara.restaurant.object.Ingredient;

import java.util.HashMap;
import java.util.List;

public class customAdapter_personalizza extends SimpleAdapter {
    LayoutInflater inflater;
    Context context;
    List<HashMap<String, String>> arrayList;
    List<Ingredient> listIngredients;
    List<Ingredient> listChoosedIngredients;

    public customAdapter_personalizza(Context context, List<HashMap<String, String>> data, int resource, String[] from, int[] to, List<Ingredient> listIngredients, List<Ingredient> listChoosedIngredients) {
        super(context, data, resource, from, to);
        this.context = context;
        this.arrayList = data;
        inflater.from(context);

        this.listIngredients = listIngredients;
        this.listChoosedIngredients = listChoosedIngredients;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);


        if(arrayList.get(position).get("Icon1").equals("add"))
        {
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setImageResource(R.drawable.baseline_add_circle_outline_24);

            ImageView add = (ImageView) view.findViewById(R.id.icon);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(context, arrayList.get(position).get("name"), Toast.LENGTH_SHORT).show();
                    listChoosedIngredients.add(listIngredients.get(position));
                    listIngredients.remove(position);

                    if (position != -1) {

                            new Thread(new Runnable() {
                                public void run() {
                                    arrayList.remove(position);

                                    ((Activity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (context instanceof activity_personalizza) {
                                                ((activity_personalizza) context).loadIntoIngredientListView();
                                                ((activity_personalizza) context).loadIntoChoosedIngredientListView();

                                                if(listChoosedIngredients.size() == 1) {
                                                    Button save = ((Activity) context).findViewById(R.id.salva);
                                                    save.setVisibility(View.VISIBLE);
                                                    Animation animation2 = new AlphaAnimation(0.3f, 1.0f);
                                                    animation2.setDuration(1000);
                                                    save.startAnimation(animation2);
                                                }
                                            }
                                            notifyDataSetChanged();


                                        }
                                    });
                                }

                            }).start();

                    } else {
                        //Toast.makeText(getApplicationContext(), "Non sono presenti prodotti", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
        if(arrayList.get(position).get("Icon1").equals("remove"))
        {
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setImageResource(R.drawable.baseline_remove_circle_outline_white_24);

            ImageView remove = (ImageView) view.findViewById(R.id.icon);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(context, arrayList.get(position).get("name"), Toast.LENGTH_SHORT).show();
                    listIngredients.add(listChoosedIngredients.get(position));
                    listChoosedIngredients.remove(position);

                    if (position != -1) {

                        new Thread(new Runnable() {
                            public void run() {


                                arrayList.remove(position);
                                //notifyDataSetChanged();
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (context instanceof activity_personalizza) {
                                            ((activity_personalizza) context).loadIntoIngredientListView();
                                            ((activity_personalizza) context).loadIntoChoosedIngredientListView();
                                        }
                                        notifyDataSetChanged();
                                        if(listChoosedIngredients.size() == 0) {
                                            Button save = ((Activity) context).findViewById(R.id.salva);
                                            save.setVisibility(View.INVISIBLE);
                                            Animation animation2 = new AlphaAnimation(1.0f, 0.0f);
                                            animation2.setDuration(500);
                                            save.startAnimation(animation2);
                                        }

                                    }
                                });
                            }

                        }).start();

                    } else {
                        //Toast.makeText(getApplicationContext(), "Non sono presenti prodotti", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }


        return view;
    }

}