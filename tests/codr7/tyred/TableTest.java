package codr7.tyred;

import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class TableTest extends BaseTest {
    @Test
    void load() {
        final var db = new TestSchema();
        final var cx = newTestContext();
        db.migrate(cx);

        final var r = new Record()
                .set(db.userId, 1L)
                .set(db.userDate, LocalDate.now())
                .set(db.userDateTime, LocalDateTime.now())
                .set(db.userDateTimez, OffsetDateTime.now())
                .set(db.userInteger, 42)
                .set(db.userString, "abc")
                .store(db.users, cx);

        final var lr = new Record().set(db.userId, 1L);
        db.users.load(lr, cx);

        assertEquals(lr, r);
        cx.rollback();
    }
}