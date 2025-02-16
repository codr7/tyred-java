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

```
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