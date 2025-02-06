package codr7.tyred;

public final class StringUtils {
    public static String toNameCase(final String in) {
        return in.substring(0, 1).toUpperCase() + in.substring(1);
    }
}
