package codr7.tyred;

import codr7.tyred.columns.IntegerColumn;
import codr7.tyred.columns.StringColumn;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest extends BaseTest {
    static Table t = new Table("TransactionTestTable");
    static final IntegerColumn c = new IntegerColumn(t, "Column", Option.PrimaryKey);

    @Test
    void testCommitRolledBackSavePoint() {
        final var cx = newTestContext();
        final var r = new Record();
        t.migrate(cx);
        cx.commit();

        cx.begin();
        r.set(c, 1).store(t, cx);
        assertTrue(t.exists(r, cx));
        cx.rollback();
        cx.commit();
        assertFalse(t.exists(r, cx));

        t.drop(cx);
        cx.commit();
    }

    @Test
    void testRollbackCommittedSavePoint() {
        final var cx = newTestContext();
        final var r = new Record();
        t.migrate(cx);
        cx.commit();

        cx.begin();
        r.set(c, 1).store(t, cx);
        assertTrue(t.exists(r, cx));
        cx.commit();
        cx.rollback();
        assertFalse(t.exists(r, cx));

        t.drop(cx);
        cx.commit();
    }
}