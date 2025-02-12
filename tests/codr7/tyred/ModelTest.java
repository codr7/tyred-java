package codr7.tyred;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest extends BaseTest {
    public static class User extends BaseModel {
        private final TestSchema db;

        public User(final TestSchema s, final Record r) {
            super(r);
            db = s;
        }

        public User(final TestSchema s, final Context cx) {
            this(s, new Record());
            record().set(db.userId, s.userIds.nextValue(cx));
        }


        public String name() {
            return record().get(db.userName);
        }

        public User setName(final String v) {
            record().set(db.userName, v);
            return this;
        }

        @Override
        public Table[] tables() {
            return new Table[]{db.users};
        }
    }

    @Test
    public void testStore() {
        final var s = new TestSchema();
        final var cx = newTestContext();
        s.migrate(cx);

        final var u = new User(s, cx);
        u.setName("foo");
        assertFalse(u.isStored(cx));
        assertTrue(u.isModified(cx));

        u.store(cx);
        assertTrue(u.isStored(cx));
        assertFalse(u.isModified(cx));

        u.setName("bar");
        assertTrue(u.isStored(cx));
        assertTrue(u.isModified(cx));

        u.store(cx);
        assertTrue(u.isStored(cx));
        assertFalse(u.isModified(cx));

        cx.rollback();
        assertFalse(u.isStored(cx));
        assertTrue(u.isModified(cx));
    }
}