package data.model;

public class Change {
    Line[] lines;
    User user;

    public Change(Line[] lines, User user) {
        this.lines = lines;
        this.user = user;
    }

    public Change() {
    }
}
