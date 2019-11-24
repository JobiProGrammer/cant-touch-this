package data;

import com.google.gson.Gson;
import data.model.Change;
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
    private File[] files;
    private WebThread thread;

    public WebDataProvider(Config config) {
        this.config = config;
        this.thread = new WebThread(config);
        thread.start();
    }

    @Override
    public File get(String file) {
        if (files == null) return null;
        for (File f : this.files) {
            if (f.path.equals(file)) return f;
        }
        return null;
    }

    @Override
    public void loadAll() {
        this.files = thread.getResults();
    }

}
