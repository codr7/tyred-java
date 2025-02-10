package codr7.tyred_tests;

import codr7.tyred.Table;
import codr7.tyred.columns.StringColumn;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import codr7.tyred.Record;

class RecordTest {
    @Test
    public void testEquals() {
        final var r1 = new Record();
        final var r2 = new Record();

        assertEquals(r1, r2);

        final var t = new Table("t");
        final var c1 = new StringColumn(t, "c1", 100);

        r1.set(c1, "foo");
        r2.set(c1, "bar");
        assertNotEquals(r1, r2);

        r2.set(c1, "foo");
        assertEquals(r1, r2);

        final var c2 = new StringColumn(t, "c2", 100);
        r2.set(c2, "bar");
        assertNotEquals(r1, r2);
    }
}