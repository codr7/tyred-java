package codr7.tyred;

import codr7.tyred.columns.StringColumn;
import org.junit.jupiter.api.Test;

class TableTest extends BaseTest {
    static class S extends Schema {
        final Table users = add(new Table("users"));
        final StringColumn userName = new StringColumn(users, "name", 100, Option.PrimaryKey);

        final Table admins = add(new Table("admins"));
        final ForeignKey adminUserKey = new ForeignKey(admins, "user", users, Option.PrimaryKey);
    }

    @Test
    public void testUpdate() {
        final var cx = newTestContext();
        final var db = new S();
        db.migrate(cx);

        final var u = new Record();
        u.set(db.userName, "foo");
        db.users.insert(u, cx);

        u.set(db.userName, "bar");
        db.users.update(u, cx);
        cx.rollback();
    }
}