package codr7.tyred;

import codr7.tyred.columns.StringColumn;

import java.util.stream.Stream;

public class TestSchema extends Schema {
    public final Table users = add(new Table("TestUsers"));
    public final StringColumn userName = new StringColumn(users, "Name", 100, Option.PrimaryKey);
    public final StringColumn userEmail = new StringColumn(users, "Email", 100, Option.Nullable);
    public final Index userEmailIndex = new Index(users, "UserEmail", true, Stream.of(userEmail));

    public final Table admins = add(new Table("TestAdmins"));
    public final ForeignKey adminUserKey = new ForeignKey(admins, "User", users, Option.PrimaryKey);
}
