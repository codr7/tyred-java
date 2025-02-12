package codr7.tyred;

import java.nio.file.Paths;

public class BaseTest {
    public static Context newTestContext() {
        return new Context("jdbc:h2:" + Paths.get("tyred").toAbsolutePath(), "tyred", "tyred");
    }
}
