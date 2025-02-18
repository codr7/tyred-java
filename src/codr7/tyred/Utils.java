package codr7.tyred;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

public final class Utils {
    public static <T> T[] concat(T[] a, T[] b) {
        T[] both = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, both, a.length, b.length);
        return both;
    }

    public static LocalDate currentDate() {
        return LocalDate.now();
    }

    public static LocalDateTime currentDateTime() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

    public static OffsetDateTime currentDateTimez() {
        return OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

    public static String quote(final String name) {
        return '"' + name + '"';
    }

    public static String toNameCase(final String in) {
        return in.substring(0, 1).toUpperCase() + in.substring(1);
    }
}
