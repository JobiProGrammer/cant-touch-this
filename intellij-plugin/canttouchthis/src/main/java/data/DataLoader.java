package data;

import data.model.File;

public class DataLoader {
    private DataProvider provider;
    private Config config;

    public DataLoader(Config config) {
        this.config = config;
        provider = isOnline() ? new WebDataProvider(this.config) : new TestDataProvider();
    }

    private boolean isOnline() {
        return true;
    }

    public void reload() {
        provider.loadAll();
    }

    public File getFile(String file) {
        return provider.get(file);
    }
}
