package tech.dalporto.dalportoweather;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CountryListAdapter extends RecyclerView.Adapter<CountryListAdapter.MyViewHolder> {
    public static ArrayList<String> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public static View mView;
        public TextView country;

        public MyViewHolder(View v) {
            super(v);
            mView = v;
            country = itemView.findViewById(R.id.textViewCountryRview);
            country.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //Toast toast = Toast.makeText(v.getContext(), mDataset.get(getAdapterPosition()), Toast.LENGTH_LONG);
            //toast.show();
            showAddItemDialog(v.getContext(), mDataset.get(getAdapterPosition()));
        }
    }

    public static void showAddItemDialog(final Context c, final String newCountry) {
        final EditText taskEditText = new EditText(c);
        String dialogText;

            if (newCountry.equals("US")) {
                dialogText = "Enter zip code";
            } else {
                dialogText = "Enter full city name";
            }
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle(dialogText)
                .setView(taskEditText)
                .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = String.valueOf(taskEditText.getText() + "," + newCountry);
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                            if (((new WeatherHttpClient()).getWeatherData(input, "weather", newCountry)) != null) {
                                SharedPreferences sharedPref = c.getSharedPreferences("tech.dalporto.dalportoweather.PREFERENCES", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("location", input);
                                editor.commit();
                                dialog.dismiss();// added to fix MainActivity has leaked window error
                                Intent intent = new Intent(c, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                c.startActivity(intent);
                            } else {
                                Toast toast = Toast.makeText(c, input + " invalid, try again", Toast.LENGTH_SHORT);
                                toast.show();
                                showAddItemDialog(c, newCountry);
                            }
                        }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CountryListAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CountryListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.country_item, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.country.setText(mDataset.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}