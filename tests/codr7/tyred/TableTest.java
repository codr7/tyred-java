package codr7.tyred;

import codr7.tyred.columns.StringColumn;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

class TableTest extends BaseTest {
    static class S extends Schema {
        final Table users = add(new Table("TableTestUsers"));
        final StringColumn userName = new StringColumn(users, "Name", 100, Option.PrimaryKey);
        final StringColumn userEmail = new StringColumn(users, "Email", 100, Option.Nullable);
        final Index userEmailIndex = new Index(users, "UserEmail", true, Stream.of(userEmail));

        final Table admins = add(new Table("TableTestAdmins"));
        final ForeignKey adminUserKey = new ForeignKey(admins, "User", users, Option.PrimaryKey);
    }

    @Test
    public void testStore() {
        final var cx = newTestContext();
        final var db = new S();
        db.migrate(cx);

        final var u = new Record();
        u.set(db.userName, "foo");
        u.store(db.users, cx);

        u.set(db.userName, "bar");
        u.store(db.users, cx);
        cx.rollback();
    }
}