package tech.dalporto.dalportoweather;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
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

        public MyViewHolder(View v) {
            super(v);
            mTextView = v;
            descTextView = itemView.findViewById(R.id.descriptionTextView);
            tempTextView = itemView.findViewById(R.id.tempTextView);
            dateTimeTextView = itemView.findViewById(R.id.dateTextView);
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
        holder.descTextView.setText(mDataset.get(position).currentCondition.getDescr());
        holder.tempTextView.setText(String.valueOf(mDataset.get(position).temperature.getTemp()));
        holder.dateTimeTextView.setText(String.valueOf(mDataset.get(position).currentCondition.getTime()));
        String iconUrl = "http://openweathermap.org/img/w/" + mDataset.get(position).currentCondition.getIcon() + ".png";
        Picasso.get().load(iconUrl).into(holder.imgView);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}