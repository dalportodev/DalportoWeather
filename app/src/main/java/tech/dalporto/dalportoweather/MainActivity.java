package tech.dalporto.dalportoweather;

import org.json.JSONException;
import tech.dalporto.dalportoweather.model.Weather;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private TextView cityText;
    private TextView condDescr;
    private TextView temp;
    private TextView windSpeed;
    private TextView hum;
    private ImageView imgView;
    private String city = "";
    private JSONWeatherTask task;
    private SharedPreferences sharedPref;
    private Toolbar myToolbar;
    private RecyclerView recyclerViewCountries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // added this because in alertdialog in CountryListAdapter sets to permitAll()
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().detectAll().build();
        StrictMode.setThreadPolicy(policy);

        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Context context = this;
        sharedPref = context.getSharedPreferences("tech.dalporto.dalportoweather.PREFERENCES", Context.MODE_PRIVATE);

        cityText = findViewById(R.id.cityText);
        condDescr = findViewById(R.id.condDescr);
        temp = findViewById(R.id.temp);
        hum = findViewById(R.id.hum);
        windSpeed = findViewById(R.id.windSpeed);
        imgView = findViewById(R.id.condIcon);

        readPreferences();
        if (city.equals("")) {
            showAddItemDialog(this);
        } else {
            task = new JSONWeatherTask();
            task.execute(new String[]{city});
        }

    }
    public void writePreferences() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("location", city);
        editor.commit(); // test editor.apply() later
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
                Intent intent = new Intent(MainActivity.this, FiveDayActivity.class);
                startActivity(intent);
                return true;
            case R.id.changeZip:
                showAddItemDialog(this);
                return true;
            case R.id.changeCountry:
                showCountryChangeDialog(this);
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

    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ((new WeatherHttpClient()).getWeatherData(params[0], "weather", city.substring(city.length() - 2, city.length())));

            try {
                weather = JSONWeatherParser.getWeather(data);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);
            String iconUrl;

            if (weather.currentCondition.getIcon() != null) {
                iconUrl = "http://openweathermap.org/img/w/" + weather.currentCondition.getIcon() + ".png";
                Picasso.get().load(iconUrl).into(imgView);

                cityText.setText(weather.getCity() + ", " + weather.getCountry());
                condDescr.setText(weather.currentCondition.getDescr().substring(0,1).toUpperCase() +
                        weather.currentCondition.getDescr().substring(1));
                if (city.substring(city.length() - 2, city.length()).equals("US")) {
                    temp.setText("Temp: " + Math.round((weather.temperature.getTemp())) + "F");
                } else {
                    temp.setText("Temp: " + Math.round((weather.temperature.getTemp())) + "C");
                }
                hum.setText("Humidity: " + weather.currentCondition.getHumidity() + "%");
                windSpeed.setText("Wind: " + weather.getWind() + " mph");
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), city + " invalid, try again", Toast.LENGTH_LONG);
                toast.show();
                showAddItemDialog(MainActivity.this);
            }
        }
    }
}