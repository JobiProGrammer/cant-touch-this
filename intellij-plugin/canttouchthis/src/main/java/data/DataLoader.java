package data;

import data.model.File;

public class DataLoader {
    private DataProvider provider;

    public DataLoader() {
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
