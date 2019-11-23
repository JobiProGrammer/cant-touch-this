package data;

import data.model.File;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebDataProvider implements DataProvider {
    Config config;

    public WebDataProvider(Config config) {
        this.config = config;
    }

    private static String getHTML(String urlIn) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlIn);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }

    @Override
    public File get(String file) {
        return null;
    }
}
