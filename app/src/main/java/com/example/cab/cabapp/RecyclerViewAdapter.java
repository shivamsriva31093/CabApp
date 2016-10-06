package com.example.cab.cabapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by shashank on 2/22/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {


    ArrayList<String> data = new ArrayList<String>();
    ArrayList<Integer> imagedata = new ArrayList<>();
    ArrayList<Integer> imageids = new ArrayList<>();
    String name;
    String email;
    Bitmap Bm;
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;
    private final static String TAG = "RecyclerviewAdapter";

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView menuName;
        private ImageView icon;
        private TextView email;
        private TextView name;
        private ImageView profile;
        private int Holder_Id;


        public ViewHolder(View v, int viewType) {
            super(v);

            if (viewType == TYPE_ITEM) {
                menuName = (TextView) v.findViewById(R.id.text1);
                icon = (ImageView) v.findViewById(R.id.image1);
                Holder_Id = 1;


            } else {
                name = (TextView) v.findViewById(R.id.name);
                email = (TextView) v.findViewById(R.id.email);
                profile = (ImageView) v.findViewById(R.id.profile);

                Holder_Id = 0;
            }


            v.setClickable(true);


        }


    }

    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view

        // set the view's size, margins, paddings and layout parameters

        if (viewType == TYPE_ITEM) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler, parent, false);
            ViewHolder vh = new ViewHolder(v, TYPE_ITEM);
            return vh;
        }
        if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
            ViewHolder vh = new ViewHolder(v, TYPE_HEADER);
            return vh;
        }

        return null;
    }

    public RecyclerViewAdapter(ArrayList<String> dataset, ArrayList<Integer> imageset, String Name, String Email, Bitmap bm)  {
        data = dataset;
        imagedata = imageset;
        Bm = bm;
        email = Email;
        name = Name;


    }





    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    public void onBindViewHolder(ViewHolder v, int position) {
        if (v.Holder_Id == 1) {                              // as the list view is going to be called after the header view so we decrement the
            // position by 1 and pass it to the holder while setting the text and image
            v.menuName.setText(data.get(position - 1)); // Setting the Text with the array of our Titles
            v.icon.setImageResource(imagedata.get(position - 1));// Settimg the image with array of our icons
        } else {

            v.name.setText(name);           // Similarly we set the resources for header view
            v.profile.setImageBitmap(getCircleBitmap(Bm));
            v.email.setText(email);
        }

        // v.menuName.setText(data.get(position1));
        //v.icon.setImageResource(imagedata.get(position));


    }

    public int getItemCount() {
        return imagedata.size() + 1;

    }


    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }



}