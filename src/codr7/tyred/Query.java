package codr7.tyred;

import java.sql.SQLException;
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
    private List<Condition> where = new ArrayList<>();

    public Query(final Source from) {
        this.from = from;
    }

    public Record[] findAll(final Context cx) {
        try (final var q = cx.query(sourceSql(), sourceParams().toArray(Object[]::new))) {
            final var rs = new ArrayList<Record>();

            while (q.next()) {
                rs.add(new Record(q, select.toArray(Column[]::new)));
            }

            return rs.toArray(Record[]::new);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Query join(final ForeignKey k) {
        final var c = Condition.AND(
                k.foreignColumns().
                        map(fc -> fc.left().EQ(fc.right())).
                        toArray(Condition[]::new));

        from = new Join(from, k.foreignTable(), c);
        return this;
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

    public Query select(final Stream<Column> columns) {
        select.addAll(columns.toList());
        return this;
    }

    public Query select(final Column...columns) {
        return select(Arrays.stream(columns));
    }

    @Override
    public Stream<Object> sourceParams() {
        return Stream.concat(
                select.stream().flatMap(Column::columnParams),
                Stream.concat(
                        from.sourceParams(),
                        Stream.concat(
                            (where.isEmpty()) ? Stream.empty() : where().params(),
                            orderBy.stream().flatMap(o -> o.left().columnParams()))));
    }

    @Override
    public String sourceSql() {
        final var sql = new StringBuilder();

        sql.append("SELECT ")
                .append(select.stream().map(Column::columnSql).collect(Collectors.joining(", ")))
                .append(" FROM ").append(from.sourceSql());

        if (!where.isEmpty()) {
            sql.append(" WHERE ").append(where().sql());
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

    public Query where(final Stream<Condition> cs) {
        where.addAll(cs.toList());
        return this;
    }

    public Query where(final Condition...cs) {
        return where(Arrays.stream(cs));
    }

    public Condition where() {
        return Condition.AND(where.toArray(Condition[]::new));

    }
}
