## Typed Relational Database access in Java

### Introduction
This project is part of the [tyred](https://github.com/codr7/tyred) family of frameworks.

### Database Support
The framework is built on top of JDBC and uses standard SQL, 
which makes it relatively database agnostic. Many features are optional,
if you don't use them, the database isn't required to support them.

So far, it has been tested with [H2](https://www.h2database.com/).

### Definition
The following example defines a simple database consisting of users, resources and calendars
to track availability.

```java
public class Database extends Schema {
    public final Table users = add(new Table("users"));
    public final StringColumn userName = new StringColumn(users, "name", 100, Option.PrimaryKey);

    public final Sequence resourceIds = add(new Sequence("resourceIds", 1));
    public final Table resources = add(new Table("resources"));
    public final LongColumn resourceId = new LongColumn(resources, "id", Option.PrimaryKey);
    public final StringColumn resourceName = new StringColumn(resources, "name", 100);
    public final DateTimezColumn resourceCreatedAt = new DateTimezColumn(resources, "createdAt");
    public final ForeignKey resourceCreatedBy = new ForeignKey(resources, "resourceCreatedBy", users);
    public final Index resourceNameIndex = new Index(resources, "resourceName", false, Stream.of(resourceName));

    public final Table calendars = add(new Table("calendars"));
    public final DateTimeColumn calendarStart = new DateTimeColumn(calendars, "start", Option.PrimaryKey);
    public final DateTimeColumn calendarEnd = new DateTimeColumn(calendars, "end");
    public final IntegerColumn calendarTotal = new IntegerColumn(calendars, "total");
    public final IntegerColumn calendarUsed = new IntegerColumn(calendars, "used");
    public final ForeignKey calendarResource = new ForeignKey(calendars, "calendarResource", resources, Option.PrimaryKey);

    public Database() {
        resources.afterInsert.add((r, cx) -> {
            new Calendar(new Resource(this, r)).store(cx);
        });
    }
}
```

### Records
A record maps columns to values, columns may belong to different tables.

### Models
A model encapsulates a record, which may contain columns from multiple tables.

```java
public class User extends Model {
    public User(final Context cx) {
        super(cx.db, new Record());
    }

    public User(final Database db, final Record r) {
        super(db, r);
    }

    public String name() {
        return record().get(db.userName);
    }

    public User setName(final String v) {
        record().set(db.userName, v);
        return this;
    }

    @Override
    public Stream<Table> tables() {
        return Stream.of(db.users);
    }
}
```
```java
public class Resource extends Model {
    public Resource(final Context cx) {
        super(cx.db, new Record());

        record()
                .set(db.resourceId, db.resourceIds.nextValue(cx.dbContext))
                .set(db.resourceCreatedAt, OffsetDateTime.now())
                .set(db.resourceCreatedBy, cx.currentUser().record());
    }

    public Resource(final Database db, final Record r) {
        super(db, r);
    }

    public OffsetDateTime createdAt() {
        return record().get(db.resourceCreatedAt);
    }

    public User createdBy() {
        return new User(db, record().get(db.resourceCreatedBy));
    }

    public long id() {
        return record().get(db.resourceId);
    }

    public String name() {
        return record().get(db.resourceName);
    }

    public Resource setName(final String v) {
        record().set(db.resourceName, v);
        return this;
    }

    @Override
    public Stream<Table> tables() {
        return Stream.of(db.resources);
    }
}
```
```java
public class Calendar extends Model {
    public Calendar(final Resource rc) {
        super(rc.db, new codr7.tyred.Record());

        record()
                .set(db.calendarResource, rc.record())
                .set(db.calendarStart, LocalDateTime.MIN)
                .set(db.calendarEnd, LocalDateTime.MAX)
                .set(db.calendarTotal, 0)
                .set(db.calendarUsed, 0);
    }

    public Calendar(final Database db, final Record r) {
        super(db, r);
    }

    public Calendar add(final int q) {
        record().set(db.calendarTotal, total() + q);
        return this;
    }

    public LocalDateTime end() {
        return record().get(db.calendarEnd);
    }

    public Resource resource() {
        return new Resource(db, record().get(db.calendarResource));
    }

    public LocalDateTime start() {
        return record().get(db.calendarStart);
    }

    public int total() {
        return record().get(db.calendarTotal);
    }

    public Calendar use(final int q) {
        record().set(db.calendarTotal, total() - q)
                .set(db.calendarUsed, used() + q);

        return this;
    }

    public int used() {
        return record().get(db.calendarUsed);
    }

    @Override
    public Stream<Table> tables() {
        return Stream.of(db.calendars);
    }
}
```

### License
MIT, except for members of the following loser organizations:

- 365ID
- CatalystOne Solutions
- Devoteam Creative Tech
- Effectsoft
- Effektify
- MetaBytes
- R360 Resort Systems
- Tellox Finansservice