package tech.dalporto.dalportoweather;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import tech.dalporto.dalportoweather.model.Weather;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    //private String[] mDataset;
    private ArrayList<Weather> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mTextView;
        public ImageView imgView;
        public TextView descTextView;
        public TextView tempTextView;
        public TextView dateTimeTextView;
        public TextView windTextView;

        public MyViewHolder(View v) {
            super(v);
            mTextView = v;
            //descTextView = itemView.findViewById(R.id.descriptionTextView);
            tempTextView = itemView.findViewById(R.id.tempTextView);
            dateTimeTextView = itemView.findViewById(R.id.dateTextView);
            windTextView = itemView.findViewById(R.id.windTextView);
            imgView = itemView.findViewById(R.id.imageView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<Weather> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forecast_item, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        /* for use in landscape view only (separate layout to be implemented)
        holder.descTextView.setText(mDataset.get(position).currentCondition.getDescr().substring(0,1).toUpperCase() +
                mDataset.get(position).currentCondition.getDescr().substring(1) + ",");
        */

        if (mDataset.get(0).getCountry().equals("US")) {
            holder.tempTextView.setText(String.valueOf((int)mDataset.get(position).temperature.getTemp()) + "F°");
        } else {
            holder.tempTextView.setText(String.valueOf((int)mDataset.get(position).temperature.getTemp()) + "C°");
        }

        holder.windTextView.setText("Wind: " + mDataset.get(position).getWind() + "MPH");
        String iconUrl = "http://openweathermap.org/img/w/" + mDataset.get(position).currentCondition.getIcon() + ".png";
        Picasso.get().load(iconUrl).into(holder.imgView);

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(String.valueOf(mDataset.get(position).currentCondition.getTime().substring(0, 10)));
            //  holder.dateTimeTextView.setText(new SimpleDateFormat("EEE").format(date));
            String dayOfWeek = new SimpleDateFormat("EEE").format(date);
            format = new SimpleDateFormat("HH:mm:ss");
            date = format.parse(mDataset.get(position).currentCondition.getTime().substring(11, mDataset.get(position).currentCondition.getTime().length()));
            format = new SimpleDateFormat("hh:mm aa");
            holder.dateTimeTextView.setText(dayOfWeek + " " + format.format(date));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}