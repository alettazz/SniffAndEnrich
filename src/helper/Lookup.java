package src.helper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Lookup {

    private static final String baseURL = "http://api.macvendors.com/";


    public static String get(String macAddress) {
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(baseURL + macAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            return result.toString();
        } catch (FileNotFoundException e) {
            // MAC not found
            return "N/A";
        } catch (IOException e) {
            // Error during lookup, either network or API.
            return null;
        }
    }
}