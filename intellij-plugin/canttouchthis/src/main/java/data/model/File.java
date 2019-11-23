package data.model;

public class File {
    public String path;
    public Change[] changes;

    public File(String path, Change[] changes) {
        this.path = path;
        this.changes = changes;
    }

    public File() {
    }
}
