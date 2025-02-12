package codr7.tyred;

import java.sql.SQLException;

public class Sequence extends BaseDefinition implements Definition {
    private long startValue;

    public Sequence(final String name, final long startValue) {
        super(name);
        this.startValue = startValue;
    }

    @Override
    public String createSql() {
        return super.createSql() + " MINVALUE " + startValue;
    }

    @Override
    public String definitionType() {
        return "SEQUENCE";
    }

    @Override
    public boolean exists(final Context cx) {
        try (final var q = cx.query("""
                SELECT EXISTS
                (SELECT FROM INFORMATION_SCHEMA.SEQUENCES WHERE sequence_name = ?)
                """, name())) {
            q.next();
            return q.getBoolean(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long nextValue(final Context cx) {
        try (final var q = cx.query("SELECT NEXTVAL('" + name() + "')")) {
            q.next();
            return q.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
