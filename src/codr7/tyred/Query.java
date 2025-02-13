package codr7.tyred;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Query implements Source {
    public enum Order {Ascending, Descending}

    public static String toSql(final Order o) {
        return (o == Order.Ascending) ? "ASC" : "DESC";
    }

    private Source from;
    private long limit = -1;
    private long offset = -1;
    private final List<Pair<Column, Order>> orderBy = new ArrayList<>();
    private final List<Column> select = new ArrayList<>();
    private Condition where;

    public Query(final Source from) {
        this.from = from;
    }

    public Query limit(final long n) {
        limit = n;
        return this;
    }

    public Query offset(final long n) {
        offset = n;
        return this;
    }

    public Query orderBy(final Column c, final Order o) {
        orderBy.add(new Pair<>(c, o));
        return this;
    }

    public Query select(final Column...columns) {
        select.addAll(Arrays.stream(columns).toList());
        return this;
    }

    @Override
    public Stream<Object> sourceParams() {
        return Stream.concat(
                select.stream().flatMap(Column::columnParams),
                Stream.concat(
                        from.sourceParams(),
                        Stream.concat(
                            (where == null) ? Stream.empty() : where.params(),
                            orderBy.stream().flatMap(o -> o.left().columnParams()))));
    }

    @Override
    public String sourceSql() {
        return sql();
    }

    public String sql() {
        final var sql = new StringBuilder();

        sql.append("SELECT ").
                append(select.stream().map(Column::columnSql).collect(Collectors.joining(", ")));

        if (where != null) {
            sql.append(" WHERE ").append(where.sql());
        }

        if (!orderBy.isEmpty()) {
            sql.append(" ORDER BY ").
                    append(orderBy.stream().
                            map(o -> o.left().columnSql() + ' ' + toSql(o.right())).
                            collect(Collectors.joining(", ")));
        }

        if (offset > -1) {
            sql.append(" OFFSET ").append(offset);
        }

        if (limit > -1) {
            sql.append(" LIMIT ").append(limit);
        }

        return sql.toString();
    }
}
