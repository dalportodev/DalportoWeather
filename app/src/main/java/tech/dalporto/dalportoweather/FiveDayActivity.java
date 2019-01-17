package tech.dalporto.dalportoweather;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

import tech.dalporto.dalportoweather.model.Weather;

public class FiveDayActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String city = "94089, US";
    private JSONWeatherTask task;
    private ArrayList<Weather> weatherList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_day);

        mRecyclerView = findViewById(R.id.my_recycler_view);
        Intent intent = getIntent();
        city = intent.getStringExtra("location");
        task = new JSONWeatherTask();
        task.execute(new String[]{city});
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(weatherList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private class JSONWeatherTask extends AsyncTask<String, Void, ArrayList<Weather>> {

        @Override
        protected ArrayList<Weather> doInBackground(String... params) {
            ArrayList<Weather> weather = new ArrayList<>();
            String data = ((new WeatherHttpClient()).getWeatherData(params[0], "forecast"));

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

            //mAdapter = new MyAdapter(weatherList);
            //mRecyclerView.setAdapter(mAdapter);
            weatherList = weather;
        }
    }
}