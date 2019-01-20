package tech.dalporto.dalportoweather;

import org.json.JSONException;
import tech.dalporto.dalportoweather.model.Weather;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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
    private TextView press;
    private TextView windSpeed;
    private TextView windDeg;
    private TextView hum;
    private ImageView imgView;
    private String city = "";
    private JSONWeatherTask task;
    private SharedPreferences sharedPref;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Context context = this;
        sharedPref = context.getSharedPreferences("tech.dalporto.dalportoweather.PREFERENCES", Context.MODE_PRIVATE);

        cityText = findViewById(R.id.cityText);
        condDescr = findViewById(R.id.condDescr);
        temp = findViewById(R.id.temp);
        hum = findViewById(R.id.hum);
        windSpeed = findViewById(R.id.windSpeed);
        windDeg = findViewById(R.id.windDeg);
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

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void showAddItemDialog(Context c) {
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Enter zip code")
                .setView(taskEditText)
                .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = String.valueOf(taskEditText.getText());
                        city = input + "," + getApplicationContext().getResources().getConfiguration().locale.getCountry();
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
            String data = ((new WeatherHttpClient()).getWeatherData(params[0], "weather"));

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

                myToolbar.setTitle(weather.getCity() + ", " + weather.getCountry());
                cityText.setText(weather.getCity() + ", " + weather.getCountry());
                condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
                temp.setText("" + Math.round((weather.temperature.getTemp())) + "f");
                hum.setText("" + weather.currentCondition.getHumidity() + "%");
                windSpeed.setText("" + weather.wind.getSpeed() + " mps");
                windDeg.setText("" + weather.wind.getDeg() + "�");
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Invalid postal code", Toast.LENGTH_LONG);
                toast.show();
                showAddItemDialog(MainActivity.this);
            }
        }
    }
}