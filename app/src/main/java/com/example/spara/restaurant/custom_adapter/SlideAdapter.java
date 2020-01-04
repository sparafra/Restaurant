package com.example.spara.restaurant.custom_adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;
import androidx.viewpager.widget.PagerAdapter;

import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.spara.restaurant.R;
import com.example.spara.restaurant.activity.MainActivity;
import com.example.spara.restaurant.activity.activity_carrello;
import com.example.spara.restaurant.activity.activity_home;
import com.example.spara.restaurant.activity.activity_slider;
import com.example.spara.restaurant.object.JSONUtility;
import com.example.spara.restaurant.object.Restaurant;
import com.example.spara.restaurant.object.Setting;
import com.example.spara.restaurant.object.WebConnection;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static com.example.spara.restaurant.object.JSONUtility.fillProductsList;

public class SlideAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;
    WebConnection Connection;

    ArrayList<Restaurant> listRestaurant;

    // list of images
    /*
    public int[] lst_images = {
            R.drawable.image_1,
            R.drawable.image_2,
            R.drawable.image_3,
            R.drawable.image_4
    };

     */
    public String[] listBackgroundURL;

    public String[] listURL_images;

    // list of title

    public String[] lst_title;

    /*
    public String[] lst_title = {
            "COSMONAUT",
            "SATELITE",
            "GALAXY",
            "ROCKET"
    }   ;

     */
    // list of descriptions
    public String[] lst_description;
    /*
    public String[] lst_description = {
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,"
    };

     */
    // list of background colors
    public int[]  lst_backgroundcolor;
    /*
    public int[]  lst_backgroundcolor = {
            Color.rgb(55,55,55),
            Color.rgb(239,85,85),
            Color.rgb(110,49,89),
            Color.rgb(1,188,212)
    };
    */

    public SlideAdapter(Context context, String[] listURLimg, String[] listTitle, String[] listDescription, int[] list_background, String[] listBackgroundURL, ArrayList<Restaurant> listRestaurant) {
        this.context = context;
        this.listURL_images = listURLimg;
        this.lst_title = listTitle;
        this.lst_description = listDescription;
        this.lst_backgroundcolor = list_background;
        this.listBackgroundURL = listBackgroundURL;
        this.listRestaurant = listRestaurant;
        Connection = new WebConnection();



    }

    @Override
    public int getCount() {
        return lst_title.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(ConstraintLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide,container,false);
        //LinearLayout layoutslide = (LinearLayout) view.findViewById(R.id.slidelinearlayout);
        ConstraintLayout layoutslide = (ConstraintLayout) view.findViewById(R.id.slidelinearlayout);

        ImageView imgslide = (ImageView)  view.findViewById(R.id.slideimg);
        TextView txttitle= (TextView) view.findViewById(R.id.txttitle);
        TextView description = (TextView) view.findViewById(R.id.txtdescription);

        ImageView imgTransparent = (ImageView) view.findViewById(R.id.imageView);
        imgTransparent.setAlpha(200);

        ImageView imgBackground = (ImageView) view.findViewById(R.id.background);

        Picasso.get().load(Connection.getURL(WebConnection.query.PRODUCTIMAGE, listBackgroundURL[position])).fit().into(imgBackground);

        /*
        try {
            layoutslide.setBackground(new BitmapDrawable(Picasso.get().load(Connection.getURL(WebConnection.query.PRODUCTIMAGE, listBackgroundURL[position])).get()));
        }catch (Exception e){e.printStackTrace();}
        //layoutslide.setBackgroundColor(lst_backgroundcolor[position]);
        /*
        new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println(Connection.getURL(WebConnection.query.PRODUCTIMAGE, listBackgroundURL[position]));
                    layoutslide.setBackground(new BitmapDrawable(Picasso.get().load(Connection.getURL(WebConnection.query.PRODUCTIMAGE, listBackgroundURL[position])).get()));
                    //layoutslide.setBackgroundColor(lst_backgroundcolor[position]);
                    //layoutslide.setAlpha((float)0.2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

         */
        //imgslide.setImageResource(lst_images[position]);

        Picasso.get().load(Connection.getURL(WebConnection.query.PRODUCTIMAGE,listURL_images[position])).into(imgslide);

        txttitle.setText(lst_title[position]);
        description.setText(lst_description[position]);

        layoutslide.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                Intent I = new Intent(context, MainActivity.class);
                I.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //add this line

                I.putExtra("WebConnection" ,Connection);
                I.putExtra("Restaurant" , listRestaurant.get(position));

                context.startActivity(I);
                //context.this.finish();


                new Thread(new Runnable() {
                    public void run() {
                        if(Setting.getDebug())
                            System.out.println("FILTER PANINI");

                    }
                }).start();

            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout)object);
    }

    public void setURLImages(String[] list){listURL_images = list;}
    public void setlistTitle(String[] list){lst_title=list;}
    public void setlistDescription(String[] list){lst_description=list;}
    public void setListBackgroundColor(int[] list){lst_backgroundcolor = list;}

    public static int getDominantColor(Bitmap bitmap) {
        List<Palette.Swatch> swatchesTemp = Palette.from(bitmap).generate().getSwatches();
        List<Palette.Swatch> swatches = new ArrayList<Palette.Swatch>(swatchesTemp);
        Collections.sort(swatches, new Comparator<Palette.Swatch>() {
            @Override
            public int compare(Palette.Swatch swatch1, Palette.Swatch swatch2) {
                return swatch2.getPopulation() - swatch1.getPopulation();
            }
        });
        if(swatches.size()>0)
            return swatches.get(0).getRgb();
        else
            return getRandomColor();
        //return swatches.size() > 0 ? swatches.get(0).getRgb() : getRandomColor();
    }
    public static int getRandomColor()
    {
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        return Color.rgb(r,g,b);

    }
}
