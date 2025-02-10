package codr7.tyred;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Index extends BaseDefinition implements Definition {
    private final Table table;
    private final List<TableColumn> columns;
    private final boolean isUnique;

    public Index(final Table table, final String name, boolean isUnique, final Stream<TableColumn> columns) {
        super(name);
        this.table = table;
        this.columns = new ArrayList<>(columns.toList());
        this.isUnique = isUnique;
        table.add(this);
    }

    @Override
    public String definitionType() {
        return isUnique ? "UNIQUE INDEX" : "INDEX";
    }

    @Override
    public String createSql() {
        return super.createSql() + " ON " +
                Utils.quote(table.name()) + " (" +
                columns.stream().
                        map(TableColumn::nameSql).
                        collect(Collectors.joining( ", ")) +
                ')';
    }

    @Override
    public boolean exists(final Context cx) {
        try (final var q = cx.query("""
                SELECT EXISTS
                (SELECT FROM INFORMATION_SCHEMA.INDEXES WHERE table_name = ? AND index_name = ?)
                """, table.name(), name())) {
            q.next();
            return q.getBoolean(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
