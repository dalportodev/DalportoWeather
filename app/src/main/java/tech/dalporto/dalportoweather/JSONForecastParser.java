package tech.dalporto.dalportoweather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tech.dalporto.dalportoweather.model.Weather;

public class JSONForecastParser {

    public static ArrayList<Weather> getWeather(String data) throws JSONException  {
        ArrayList<Weather> weather = new ArrayList<>();
        JSONObject jObj = new JSONObject();

        if (data != null) {
            jObj = new JSONObject(data);
        }

        //Location loc = new Location();


        JSONArray list = jObj.getJSONArray("list");
        JSONObject location = jObj.getJSONObject("city");


        for (int i = 0; i < list.length(); i++) {
            Weather temp = new Weather();
            //temp.location = loc;
            temp.setCity(getString("name", location));
            temp.setCountry(getString("country", location));

            JSONObject item = list.getJSONObject(i);

            temp.currentCondition.setTime(getString("dt_txt", item));
            JSONArray jArr = item.getJSONArray("weather");

            JSONObject JSONWeather = jArr.getJSONObject(0);
            temp.currentCondition.setWeatherId(getInt("id", JSONWeather));
            temp.currentCondition.setDescr(getString("description", JSONWeather));
            temp.currentCondition.setCondition(getString("main", JSONWeather));
            temp.currentCondition.setIcon(getString("icon", JSONWeather));

            JSONObject mainObj = getObject("main", item);
            temp.currentCondition.setHumidity(getInt("humidity", mainObj));
            temp.currentCondition.setPressure(getInt("pressure", mainObj));
            temp.temperature.setMaxTemp(getFloat("temp_max", mainObj));
            temp.temperature.setMinTemp(getFloat("temp_min", mainObj));
            temp.temperature.setTemp(getFloat("temp", mainObj));

            JSONObject wObj = getObject("wind", item);
            temp.wind.setSpeed(getFloat("speed", wObj));
            temp.wind.setDeg(getFloat("deg", wObj));

            JSONObject cObj = getObject("clouds", item);
            temp.clouds.setPerc(getInt("all", cObj));
            weather.add(temp);
        }
        return weather;
    }

    private static JSONObject getObject(String tagName, JSONObject jObj)  throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static float  getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    private static int  getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }
}