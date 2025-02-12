package codr7.tyred;

import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Context {
    private final Connection db;
    private final List<Transaction> transactions = new ArrayList<>();

    public Context(final String path, final String user, final String password) {
        try {
            db = DriverManager.getConnection(path, user, password);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        begin();
    }

    public Transaction begin() {
        String savePoint = null;

        if (transactions.isEmpty()) {
            exec("BEGIN");
        } else {
            savePoint = Integer.valueOf(transactions.size()).toString();
            exec("SAVEPOINT " + savePoint);
        }

        final var t = new Transaction(savePoint);
        transactions.add(t);
        return t;
    }

    public void commit() {
        final var t = transactions.remove(transactions.size()-1);

        if (transactions.isEmpty()) {
            transactions.add(t);
        }

        t.commit(this);
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

            final var s = db.prepareStatement(sql);

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

    public void rollback() {
        final var t = transactions.remove(transactions.size()-1);

        if (transactions.isEmpty()) {
            transactions.add(t);
        }

        t.rollback(this);
    }

    public void storeValue(final Pair<Record, Column> rc, final Object v) {
        final var t = transactions.get(transactions.size()-1);
        t.storeValue(rc, v);
    }

    public void storeValue(final Record r, final Column c, final Object v) {
        storeValue(new Pair<>(r, c), v);
    }

    public Object storedValue(final Record r, final Column c) {
        final var t = transactions.get(transactions.size()-1);
        return t.storedValue(r, c);
    }
}
