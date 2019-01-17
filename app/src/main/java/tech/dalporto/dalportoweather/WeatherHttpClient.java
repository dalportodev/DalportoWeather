package tech.dalporto.dalportoweather;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherHttpClient {

    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/";


    public String getWeatherData(String location, String duration) {

        HttpURLConnection con = null ;
        InputStream is = null;
        String returnVal;
        try {
            if (duration.equals("forecast")) {

                con = (HttpURLConnection) (new URL(BASE_URL + "forecast?zip="  + location + "&units=imperial" + "API KEY HERE")).openConnection();
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.connect();

                StringBuffer buffer = new StringBuffer();
                is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while (  (line = br.readLine()) != null )
                    buffer.append(line + "\r\n");
                returnVal = buffer.toString();

            } else {
                con = (HttpURLConnection) (new URL(BASE_URL + "weather?zip=" + location + "&units=imperial" + "API KEY HERE")).openConnection();
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.connect();

                StringBuffer buffer = new StringBuffer();
                is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while (  (line = br.readLine()) != null )
                    buffer.append(line + "\r\n");
                returnVal = buffer.toString();
            }

            is.close();
            con.disconnect();
            return returnVal;
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }
        return null;
    }
}