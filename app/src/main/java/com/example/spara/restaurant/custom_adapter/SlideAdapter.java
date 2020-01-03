package com.example.spara.restaurant.custom_adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.palette.graphics.Palette;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.spara.restaurant.R;
import com.example.spara.restaurant.object.JSONUtility;
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

    // list of images
    /*
    public int[] lst_images = {
            R.drawable.image_1,
            R.drawable.image_2,
            R.drawable.image_3,
            R.drawable.image_4
    };

     */

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

    public SlideAdapter(Context context, String[] listURLimg, String[] listTitle, String[] listDescription) {
        this.context = context;
        this.listURL_images = listURLimg;
        this.lst_title = listTitle;
        this.lst_description = listDescription;
        Connection = new WebConnection();
    }

    @Override
    public int getCount() {
        return lst_title.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide,container,false);
        LinearLayout layoutslide = (LinearLayout) view.findViewById(R.id.slidelinearlayout);
        ImageView imgslide = (ImageView)  view.findViewById(R.id.slideimg);
        TextView txttitle= (TextView) view.findViewById(R.id.txttitle);
        TextView description = (TextView) view.findViewById(R.id.txtdescription);

        try {
            //lst_backgroundcolor[position] = getDominantColor(Picasso.get().load(listURL_images[position]).get());
            System.out.println(Connection.getURL(WebConnection.query.PRODUCTIMAGE,listURL_images[position]));

            new Thread(new Runnable() {
                public void run() {
                    try {
                        layoutslide.setBackgroundColor(getDominantColor(Picasso.get().load(Connection.getURL(WebConnection.query.PRODUCTIMAGE, listURL_images[position])).get()));
                        System.out.println(getDominantColor(Picasso.get().load(Connection.getURL(WebConnection.query.PRODUCTIMAGE, listURL_images[position])).get()));
                    }catch (Exception e){e.printStackTrace();}

                }
            }).start();

        }catch (Exception e){e.printStackTrace();}

        //layoutslide.setBackgroundColor(getDominantColor(Picasso.get().load(listURL_images[position]).get()));

        //imgslide.setImageResource(lst_images[position]);
        Picasso.get().load(Connection.getURL(WebConnection.query.PRODUCTIMAGE,listURL_images[position])).into(imgslide);

        txttitle.setText(lst_title[position]);
        description.setText(lst_description[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
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
        return swatches.size() > 0 ? swatches.get(0).getRgb() : getRandomColor();
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
