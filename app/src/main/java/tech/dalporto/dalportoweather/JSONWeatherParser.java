package tech.dalporto.dalportoweather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tech.dalporto.dalportoweather.model.Weather;

public class JSONWeatherParser {

    public static Weather getWeather(String data) throws JSONException  {
        Weather weather = new Weather();
        JSONObject jObj = new JSONObject();

        // We create out JSONObject from the data
        if (data != null) {
            jObj = new JSONObject(data);
        }

        JSONObject coordObj = getObject("coord", jObj);
        weather.setLatitude(getFloat("lat", coordObj));
        weather.setLongitude(getFloat("lon", coordObj));

        JSONObject sysObj = getObject("sys", jObj);
        weather.setCountry(getString("country", sysObj));
        weather.setSunrise(getInt("sunrise", sysObj));
        weather.setSunset(getInt("sunset", sysObj));
        weather.setCity(getString("name", jObj));


        // We get weather info (This is an array)
        JSONArray jArr = jObj.getJSONArray("weather");

        // We use only the first value
        JSONObject JSONWeather = jArr.getJSONObject(0); // 38 instances -> 0-37 array of weather objects
        weather.currentCondition.setWeatherId(getInt("id", JSONWeather));
        weather.currentCondition.setDescr(getString("description", JSONWeather));
        weather.currentCondition.setCondition(getString("main", JSONWeather));
        weather.currentCondition.setIcon(getString("icon", JSONWeather));

        JSONObject mainObj = getObject("main", jObj);
        weather.currentCondition.setHumidity(getInt("humidity", mainObj));
        weather.currentCondition.setPressure(getInt("pressure", mainObj));
        weather.temperature.setMaxTemp(getFloat("temp_max", mainObj));
        weather.temperature.setMinTemp(getFloat("temp_min", mainObj));
        weather.temperature.setTemp(getFloat("temp", mainObj));

        // Wind
        JSONObject wObj = getObject("wind", jObj);
        weather.wind.setSpeed(getFloat("speed", wObj));
        weather.wind.setDeg(getFloat("deg", wObj));

        // Clouds
        JSONObject cObj = getObject("clouds", jObj);
        weather.clouds.setPerc(getInt("all", cObj));

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