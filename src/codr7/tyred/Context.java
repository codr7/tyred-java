package codr7.tyred;

import java.nio.file.Paths;
import java.sql.*;
import java.util.Arrays;

public class Context {
    private final Connection h2;

    public Context(final String path, final String user, final String password) {
        try {
            h2 = DriverManager.getConnection("jdbc:h2:" + Paths.get(path).toAbsolutePath(), user, password);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void exec(final String sql, final Object...params) {
        try {
            prepare(sql, params).execute();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public PreparedStatement prepare(final String sql, final Object...params) {
        try {
            System.out.println(sql + '\n' + Arrays.toString(params));

            final var s = h2.prepareStatement(sql);

            for (var i = 0; i < params.length; i++) {
                s.setObject(i+1, params[i]);
            }

            return s;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet query(final String sql, final Object...params) {
        try {
            return prepare(sql, params).executeQuery();
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void storeValue(final Record r, final Column c, final Object v) {
        //TODO store in current transaction
    }

    public Object storedValue(final Record r, final Column c) {
        //TODO fetch from current transaction
        return r.get(c);
    }
}
