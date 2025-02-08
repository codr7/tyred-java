package codr7.tyred;

import java.util.Arrays;

public record Condition(String sql, Object...params) {
    public static Condition AND(final Condition...parts) {
        return Arrays.stream(parts).reduce((x, y) -> x.AND(y)).get();
    }

    public Condition AND(final Condition c) {
        return new Condition(sql + " AND " + c.sql, Utils.concat(params, c.params));
    }
}
