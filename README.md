# tyred-java

## Introduction
This project implements the [tyred](https://github.com/codr7/tyred) framework in Java.

## Database Support
The framework is built on top of JDBC and uses standard SQL, 
which makes it relatively database agnostic. Many features are optional,
if you don't use them, the database isn't required to support them.

So far, it has been tested with [H2](https://www.h2database.com/).

## Schemas
The following example defines a simple database schema consisting of resources and calendars
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

## Definitions
All definitions support the following operations.

### create
`create` attempts to recursively create the definition and signals an error if it already exists.

```java
var db = new Database();
var cx = new Context("test", "test", "test");
db.create(cx);
cx.commit();
```

### drop
`drop` attempts to recursively drop the definition and signals an error if it doesn't exist.

```java
var db = new Database();
var cx = new Context("test", "test", "test");
db.drop(cx);
cx.commit();
```

### exists
`exists` returns `true` if the definition already exists, otherwise `false`.

```java
var db = new Database();
var cx = new Context("test", "test", "test");

if (!db.users.exists(cx)) {
  db.users.create(cx);
}
```

### migrate
`migrate` creates the definition if it doesn't already exist;
otherwise it drills down and calls migrate recursively,
adding missing columns/constraints/indexes to tables etc.

```java
var db = new Database();
var cx = new Context("test", "test", "test");
db.migrate(cx);
cx.commit();
```

## Records
A record maps columns to values, columns may belong to different tables.

```java
var db = new Database();
var cx = new Context("test", "test", "test");
var r = new Record();
r.set(db.resourceName, "foo");
r.store(db.resources, cx);
```

### State
Records support interrogation of their state.

`exists` returns true if the record already exists in the specified table.

```java
var db = new Database();
var cx = new Context("test", "test", "test");

var r = new Record();
r.set(db.resourceName, "foo");

if (!r.exists(db.resources, cx)) {
    r.store(db.resources, cx);
}
```

`isModified` returns true if the record contains modifications for the specified table.

```java
var db = new Database();
var cx = new Context("test", "test", "test");

var r = new Record();
r.set(db.resourceName, "foo");
r.store();

if (r.set(db.resourceName, "bar").isModified(db.resources, cx)) {
    r.store(db.resources, cx);
}
```

## Models
A model encapsulates a record.

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

### State
Like records, models support interrogation of their state.

`exists` returns `true` if the model already exists in all dependent tables, otherwise `false`.

```java
var db = new Database();
var cx = new Context("test", "test", "test");
var r = new Resource(db).setName("foo");

if (!r.exists(cx)) {
  store(cx);
}
```

`isModified` returns `true` if the model contains modifications for any dependent table, otherwise `false`.

```java
var db = new Database();
var cx = new Context("test", "test", "test");
var r = new Resource(db).setName("foo");

if (r.setName("bar").isModified(cx)) {
  store(cx);
}
```

## Transactions
Transactions support nesting to unlimited depth. Nested transactions
establish save points. The outer transaction is automatically started when
a context is created, and restarted after commit/rollback.

```java
var db = new Database();
var cx = new Context("test", "test", "test");
var r = new Resource(db).setName("foo").store(cx);

// Commit record to database
cx.commit();

// Start nested transaction
cx.begin();
r.setName("bar").store(cx);

// Commit save point
cx.commit();

// Undo entire transaction including changes
cx.rollback();
```

```java
var db = new Database();
var cx = new Context("test", "test", "test");
var r = new Resource(db).setName("foo").store(cx);

// Commit record to database
cx.commit();

// Start nested transaction
cx.begin();
r.setName("bar").store(cx);

// Undo save point changes
cx.rollback();

// Commit transaction excluding changes
cx.commit();
```