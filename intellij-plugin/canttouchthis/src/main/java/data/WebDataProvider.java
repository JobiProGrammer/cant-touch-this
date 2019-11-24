package data;

import data.model.File;
import data.model.Project;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class WebDataProvider implements DataProvider {
    private Config config;
    private Project project;

    public WebDataProvider(Config config) {
        this.config = config;
    }

    @Override
    public File get(String file) {
        if (project == null) return null;
        for (File f : this.project.files) {
            if (f.path.equals(file)) return f;
        }
        return null;
    }

    @Override
    public void loadAll() {
        Map<String, String> params = new HashMap<>();
        params.put("project", this.config.projectName);
        String result;
        try {
            result = getHTML(this.config.host + "/change/", params, "POST");
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }

        if (result == null) {
            System.out.println("HTTP Result: null");
            return;
        } else {
            System.out.println("HTTP Result:\n\t" + result);
        }
    }


    private String getHTML(String urlIn, Map<String, String> parameters, String requestMethod) throws Exception {
        System.out.println("Outgoing HTTP Request: " + requestMethod + " " + urlIn);
        URL url = new URL(urlIn);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(requestMethod);

        System.out.println("RequestType: " + con.getRequestMethod());

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(getParamsString(parameters));
        out.flush();
        out.close();

        int status = con.getResponseCode();
        Reader streamReader = null;

        if (status > 299) {
            streamReader = new InputStreamReader(con.getErrorStream());
        } else {
            streamReader = new InputStreamReader(con.getInputStream());
        }
        BufferedReader in = new BufferedReader(streamReader);
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        con.disconnect();
        return content.toString();
    }

    public static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;

    }
}
