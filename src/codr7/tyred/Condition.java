package codr7.tyred;

import java.util.Arrays;
import java.util.stream.Stream;

public record Condition(String sql, Object..._params) {
    public static Condition AND(final Condition...parts) {
        return Arrays.stream(parts).reduce((x, y) -> x.AND(y)).get();
    }

    public Condition AND(final Condition c) {
        return new Condition(sql + " AND " + c.sql, Utils.concat(_params, c._params));
    }

    public Stream<Object> params() {
        return Arrays.stream(_params);
    }
}
