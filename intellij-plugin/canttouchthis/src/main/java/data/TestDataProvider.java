package data;

import data.model.Change;
import data.model.File;
import data.model.Line;
import data.model.User;

public class TestDataProvider implements DataProvider {
    @Override
    public void loadAll() {

    }

    @Override
    public File get(String file) {
        return new File(file,
                new Change[]{
                        new Change(new Line[]{new Line(1), new Line(3)}, new User("1@test.com")),
                        new Change(new Line[]{new Line(4), new Line(9)}, new User("2@test.com")),
                        new Change(new Line[]{new Line(13), new Line(13)}, new User("3@test.com"))
                });
    }
}
