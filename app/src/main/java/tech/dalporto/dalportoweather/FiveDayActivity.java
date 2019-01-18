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
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_day);

        Toolbar myToolbar = findViewById(R.id.toolbar_forecast);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.my_recycler_view);

        Context context = this;
        sharedPref = context.getSharedPreferences("tech.dalporto.dalportoweather.PREFERENCES", Context.MODE_PRIVATE);

        readPreferences();
        //Intent intent = getIntent();
        //city = intent.getStringExtra("location");
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
                Intent intent = new Intent(FiveDayActivity.this, MainActivity.class);
                //intent.putExtra("location", city);
                startActivity(intent);
                return true;
            case R.id.changeZip:
                showAddItemDialog(this);
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
                        if (task.isCancelled()) {
                            task = new JSONWeatherTask();
                            task.execute(new String[]{city});
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Task not completed!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
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

            mAdapter = new MyAdapter(weather);
            mRecyclerView.setAdapter(mAdapter);
            task.cancel(true);
        }
    }
}