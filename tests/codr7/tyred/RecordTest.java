package codr7.tyred;

import codr7.tyred.columns.StringColumn;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class RecordTest extends BaseTest {
    static Table t = new Table("RecordTestTable");
    static final StringColumn c1 = new StringColumn(t, "Column1", 100, Option.PrimaryKey);
    static final StringColumn c2 = new StringColumn(t, "Column2", 100, Option.Nullable);

    @Test
    public void testEquals() {
        final var r1 = new Record();
        final var r2 = new Record();
        assertEquals(r1, r2);

        r1.set(c1, "foo");
        r2.set(c1, "bar");
        assertNotEquals(r1, r2);

        r2.set(c1, "foo");
        assertEquals(r1, r2);

        r2.set(c2, "bar");
        assertNotEquals(r1, r2);
    }

    @Test
    public void testIsModified() {
        final var cx = newTestContext();
        final var r = new Record();
        assertFalse(r.isModified(t, cx));

        r.set(c1, "foo");
        assertTrue(r.isModified(t, cx));

        t.migrate(cx);
        t.insert(r, cx);
        assertFalse(r.isModified(t, cx));

        r.set(c1, "bar");
        assertTrue(r.isModified(t, cx));

        t.update(r, cx);
        assertFalse(r.isModified(t, cx));

        r.set(c2, "foo");
        assertTrue(r.isModified(t, cx));

        cx.rollback();
    }
}