package tech.dalporto.dalportoweather;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

import tech.dalporto.dalportoweather.model.Weather;

public class FiveDayActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String city = "";
    private JSONWeatherTask task;
    private SharedPreferences sharedPref;
    private Toolbar myToolbar;
    private RecyclerView recyclerViewCountries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_day);

        myToolbar = findViewById(R.id.toolbar_forecast);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.my_recycler_view);

        Context context = this;
        sharedPref = context.getSharedPreferences("tech.dalporto.dalportoweather.PREFERENCES", Context.MODE_PRIVATE);

        readPreferences();
        task = new JSONWeatherTask();
        task.execute(new String[]{city});

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public void writePreferences() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("location", city);
        editor.commit();
    }
    public void readPreferences() {
        city = sharedPref.getString("location", "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.changeZip:
                showAddItemDialog(this);
                return true;
            case R.id.changeCountry:
                showCountryChangeDialog(this);
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void showCountryChangeDialog(Context c) {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.country_list, null);

        recyclerViewCountries = dialogView.findViewById(R.id.recyclerviewCountryList);
        recyclerViewCountries.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerViewCountries.setLayoutManager(mLayoutManager);
        RecyclerView.Adapter mAdapter = new CountryListAdapter(Util.countryList.getCountries());
        recyclerViewCountries.setAdapter(mAdapter);

        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Enter Co code")
                .setView(dialogView)
                .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //String input = String.valueOf(taskEditText.getText());
                        //city = input + "," + getApplicationContext().getResources().getConfiguration().locale.getCountry();
                        writePreferences();
                        task = new JSONWeatherTask();
                        task.execute(new String[]{city});
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void showAddItemDialog(Context c) {
        final EditText taskEditText = new EditText(c);
        String dialogText;
        if (!city.equals("")) {
            if (city.substring(city.length() - 2, city.length()).equals("US")) {
                dialogText = "Enter zip code";
            } else {
                dialogText = "Enter full city name";
            }
        } else {
            if (getApplicationContext().getResources().getConfiguration().locale.getCountry().equals("US")) {
                dialogText = "Enter zip code";
            } else {
                dialogText = "Enter full city name";
            }
        }
        final String temp = dialogText;
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle(dialogText)
                .setView(taskEditText)
                .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = String.valueOf(taskEditText.getText()).replaceAll("\\s","");
                        if (temp.equals("Enter zip code")) {
                            city = input + "," + "US";
                        } else {
                            if (city.equals("")) {
                                city = input + "," + getApplicationContext().getResources().getConfiguration().locale.getCountry();
                            } else {
                                city = input + "," + city.substring(city.length() - 2, city.length());
                            }
                        }
                        writePreferences();
                        task = new JSONWeatherTask();
                        task.execute(new String[]{city});
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private class JSONWeatherTask extends AsyncTask<String, Void, ArrayList<Weather>> {

        @Override
        protected ArrayList<Weather> doInBackground(String... params) {
            ArrayList<Weather> weather = new ArrayList<>();
            String data = ((new WeatherHttpClient()).getWeatherData(params[0], "forecast", city.substring(city.length() - 2, city.length())));
            try {
                    weather = JSONForecastParser.getWeather(data);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;
        }

        @Override
        protected void onPostExecute(ArrayList<Weather> weather) {
            super.onPostExecute(weather);

            mAdapter = new MyAdapter(weather);
            mRecyclerView.setAdapter(mAdapter);
            task.cancel(true);
            myToolbar.setTitle(weather.get(0).getCity() + ", " + weather.get(0).getCountry() + " Forecast");
        }
    }
}