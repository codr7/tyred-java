import codr7.frihda.db.Context;
import codr7.frihda.db.Schema;
import codr7.frihda.db.Table;
import codr7.frihda.db.columns.StringColumn;

public class Main {
    static class DB extends Schema {
        final Table users = add(new Table("users"));
        final StringColumn userName = new StringColumn(users, "name", 100);
    }

    public static void main(final String[] args) {
        try {
            Class.forName("org.h2.Driver");
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        final var cx = new Context("frihda", "frihda", "frihda");
        final var db = new DB();

        db.migrate(cx);
    }
}