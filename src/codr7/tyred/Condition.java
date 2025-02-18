package codr7.tyred;

import java.util.Arrays;
import java.util.stream.Stream;

public record Condition(String sql, Object... params) {
    public static Condition fromColumnValues(final Stream<Pair<Column, Object>> csv) {
        return Condition.AND(csv.map(cv -> {
            final var v = cv.right();

            if (v == null) {
                throw new RuntimeException("Missing key: " + cv.left());
            }

            return cv.left().EQ(cv.right());
        }).toArray(Condition[]::new));
    }

    public static Condition fromColumns(final Stream<TableColumn> cs, final Record r) {
        final var csv = cs.
                map(c -> new Pair<Column, Object>(c, c.encode(r.getObject((c))))).
                toList();
        return fromColumnValues(csv.stream());
    }

    public static Condition fromColumnPairs(final Stream<Pair<TableColumn , TableColumn>> cs, final Record r) {
        final var csv = cs.
                map(c -> new Pair<Column, Object>(c.left(), c.right().encode(r.getObject(c.right())))).
                toList();

        return fromColumnValues(csv.stream());
    }

    public static Condition AND(final Condition...parts) {
        return Arrays.stream(parts).reduce((x, y) -> x.AND(y)).get();
    }

    public Condition AND(final Condition c) {
        return new Condition(sql + " AND " + c.sql, Utils.concat(params, c.params));
    }

    public Stream<Object> paramStream() {
        return Arrays.stream(params);
    }
}
