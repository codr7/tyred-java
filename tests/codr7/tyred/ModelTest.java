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

        public User(final TestSchema s) {
            this(s, new Record());
        }

        public String email() {
            return record().get(db.userEmail);
        }

        public String name() {
            return record().get(db.userName);
        }

        public User setEmail(final String v) {
            record().set(db.userEmail, v);
            return this;
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
        final var u = new User(s);
        final var cx = newTestContext();
        assertFalse(u.isStored(cx));
        assertFalse(u.isModified(cx));

        u.setEmail("foo").setName("bar");
        assertFalse(u.isStored(cx));
        assertTrue(u.isModified(cx));

        s.migrate(cx);
        u.store(cx);
        assertTrue(u.isStored(cx));
        assertFalse(u.isModified(cx));

        u.setEmail("baz").setName("qux");
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