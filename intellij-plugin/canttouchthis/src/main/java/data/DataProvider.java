package data;

import data.model.File;

interface DataProvider {
    File get(String file);
    void loadAll();
}
