import codr7.frihda.Context;
import codr7.frihda.Schema;
import codr7.frihda.Table;
import codr7.frihda.TableDefinition;
import codr7.frihda.StringColumn;

import java.util.stream.Stream;

public class Main {
    static class DB extends Schema {
        final Table users = add(new Table("users"));
        final StringColumn userName = new StringColumn(users, "name", 100, Stream.of(TableDefinition.Option.PrimaryKey));
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