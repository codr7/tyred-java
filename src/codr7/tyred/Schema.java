package codr7.tyred;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Schema {
    private List<Definition> definitions = new ArrayList<>();

    public <T extends Definition> T add(final T d) {
        definitions.add(d);
        return d;
    }

    public void create(final Context cx) {
        for (final var d: definitions) {
            if (!d.exists(cx)) {
                d.create(cx);
            }
        }
    }

    public void drop(final Context cx) {
        final var rds = new ArrayList<>(definitions);
        Collections.reverse(rds);

        for (final var d: rds) {
            if (d.exists(cx)) {
                d.drop(cx);
            }
        }
    }

    public void migrate(final Context cx) {
        for (final var d: definitions) {
            d.migrate(cx);
        }
    }
}
