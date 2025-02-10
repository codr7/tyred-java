package codr7.tyred;

import org.junit.jupiter.api.Test;

class TableTest extends BaseTest {

    @Test
    public void testStore() {
        final var cx = newTestContext();
        final var db = new TestSchema();
        db.migrate(cx);

        final var u = new Record();
        u.set(db.userName, "foo");
        u.store(db.users, cx);

        u.set(db.userName, "bar");
        u.store(db.users, cx);
        cx.rollback();
    }
}