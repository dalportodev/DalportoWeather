package tech.dalporto.dalportoweather;

import org.json.JSONException;
import tech.dalporto.dalportoweather.model.Weather;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

public class MainActivity extends Activity {


    private TextView cityText;
    private TextView condDescr;
    private TextView temp;
    private TextView press;
    private TextView windSpeed;
    private TextView windDeg;
    private TextView hum;
    private ImageView imgView;
    private String city = "94089,US";
    private JSONWeatherTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityText = findViewById(R.id.cityText);
        condDescr = findViewById(R.id.condDescr);
        temp = findViewById(R.id.temp);
        hum = findViewById(R.id.hum);
        press = findViewById(R.id.press);
        windSpeed = findViewById(R.id.windSpeed);
        windDeg = findViewById(R.id.windDeg);
        imgView = findViewById(R.id.condIcon);

        showAddItemDialog(this);
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
                        pickView(MainActivity.this);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
    private void pickView(Context c) {
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("1 day or 5 day forecast?")
                .setCancelable(false)
                .setPositiveButton("5 day forecast", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast toast = Toast.makeText(getApplicationContext(), "To be implemented", Toast.LENGTH_LONG);
                        toast.show();
                        Intent intent = new Intent(MainActivity.this, FiveDayActivity.class);
                        intent.putExtra("location", city);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("1 day forecast", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        task = new JSONWeatherTask();
                        task.execute(new String[]{city});
                        Toast toast3 = Toast.makeText(getApplicationContext(), city, Toast.LENGTH_LONG);
                        toast3.show();
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

                cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
                condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
                temp.setText("" + Math.round((weather.temperature.getTemp())) + "f");
                hum.setText("" + weather.currentCondition.getHumidity() + "%");
                press.setText(getApplicationContext().getResources().getConfiguration().locale.getCountry());
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