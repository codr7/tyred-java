## Typed Relational Database access in Java

### Introduction
This project is part of the [tyred](https://github.com/codr7/tyred) family of frameworks.

### Database Support
The framework is built on top of JDBC and uses standard SQL, 
which makes it relatively database agnostic. Many features are optional,
if you don't use them, the database isn't required to support them.

So far, it has been tested with [H2](https://www.h2database.com/).

### Definition
The following example defines a simple database consisting of resources and calendars
to track availability in time.

```java
public class Database extends Schema {
    public final Table resources = add(new Table("resources"));
    public final StringColumn resourceName = new StringColumn(resources, "name", 100, Option.PrimaryKey);
    public final DateTimezColumn resourceCreatedAt = new DateTimezColumn(resources, "createdAt");

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

```java
var db = new Database();
var cx = new Context("test", "test", "test");
var r = new Record();
r.set(db.resourceName, "foo");
r.store(db.resources, cx);
```

### Models
A model encapsulates a record, which may contain columns from multiple tables.

```java
public class Resource extends Model {
    public Resource(final Database db) {
        super(db, new Record());
        record().set(db.resourceCreatedAt, OffsetDateTime.now());
    }

    public Resource(final Database db, final Record r) {
        super(db, r);
    }

    public OffsetDateTime createdAt() {
        return record().get(db.resourceCreatedAt);
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
        super(rc.db, new Record());

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

### Transactions
Transactions support nesting to unlimited depth. Nested transactions
establish save points. The outer transaction is automatically started when
a context is created, and restarted when committed/rolled back.

```java
var db = new Database();
var cx = new Context("test", "test", "test");

var r = new Resource(db);
r.setName("foo").store(cx);
cx.commit(); // Record committed to database

cx.begin(); // Start nested transaction
r.setName("bar").store(cx);
cx.commit(); // Save point released
cx.rollback(); // Changes rolled back
```

```java
var db = new Database();
var cx = new Context("test", "test", "test");

var r = new Resource(db);
r.setName("foo").store(cx);
cx.commit(); // Record committed to database

cx.begin(); // Start nested transaction
r.setName("bar").store(cx);
cx.rollback(); // Save point rolled back
cx.commit(); // No changes are committed
```