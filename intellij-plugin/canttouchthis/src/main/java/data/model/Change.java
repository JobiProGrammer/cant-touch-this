package data.model;

public class Change {
    public Line[] lines;
    public User user;

    public Change(Line[] lines, User user) {
        this.lines = lines;
        this.user = user;
    }

    public Change() {
    }
}
