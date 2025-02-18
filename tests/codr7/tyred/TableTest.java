package codr7.tyred;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TableTest extends BaseTest {
    @Test
    void load() {
        final var db = new TestSchema();
        final var cx = newTestContext();
        db.migrate(cx);

        final var r = new Record()
                .set(db.userId, 1L)
                .set(db.userDate, Utils.currentDate())
                .set(db.userDateTime, Utils.currentDateTime())
                .set(db.userDateTimez, Utils.currentDateTimez())
                .store(db.users, cx);

        final var lr = new Record().set(db.userId, 1L);
        db.users.load(lr, cx);

        assertEquals(lr, r);
        cx.rollback();
    }
}