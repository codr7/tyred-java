package codr7.tyred;

import codr7.tyred.columns.*;

import java.util.stream.Stream;

public class TestSchema extends Schema {
    public final Sequence userIds = add(new Sequence("UserIds", 1));

    public final Table users = add(new Table("TestUsers"));
    public final LongColumn userId = new LongColumn(users, "id", Option.PrimaryKey);

    public final DateColumn userDate = new DateColumn(users, "Date", Option.Nullable);
    public final DateTimeColumn userDateTime = new DateTimeColumn(users, "DateTime", Option.Nullable);
    public final DateTimezColumn userDateTimez = new DateTimezColumn(users, "DateTimez", Option.Nullable);
    public final IntegerColumn userInteger = new IntegerColumn(users, "Integer", Option.Nullable);
    public final StringColumn userString = new StringColumn(users, "String", 100, Option.Nullable);
    public final TimeColumn userTime = new TimeColumn(users, "Time", Option.Nullable);
    public final TimezColumn userTimez = new TimezColumn(users, "Timez", Option.Nullable);

    public final Index userStringIndex = new Index(users, "UserString", true, Stream.of(userString));

    public final Table admins = add(new Table("TestAdmins"));
    public final ForeignKey adminUserKey = new ForeignKey(admins, "User", users, Option.PrimaryKey);
}
