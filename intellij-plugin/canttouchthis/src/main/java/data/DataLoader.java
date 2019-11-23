package data;

import data.model.File;

public class DataLoader {
    private DataProvider provider;
    private Config config;

    public DataLoader(Config config) {
        this.config = config;
        provider = isOnline() ? new WebDataProvider() : new TestDataProvider();
        provider.loadAll();
    }

    private boolean isOnline() {
        return false;
    }

    public File getFile(String file){
        return provider.get(file);
    }
}
