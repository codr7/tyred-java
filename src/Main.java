import codr7.frihda.db.Context;
import codr7.frihda.db.Table;

public class Main {
    public static void main(final String[] args) {
        try {
            Class.forName("org.h2.Driver");
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        final var cx = new Context("frihda", "frihda", "frihda");
        final var t = new Table("users");

        if (!t.exists(cx)) {
            t.create(cx);
        }
    }
}