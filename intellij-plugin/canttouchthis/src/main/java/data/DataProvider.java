package data;

import data.model.File;

interface DataProvider {
    void loadAll();
    File get(String file);
}
