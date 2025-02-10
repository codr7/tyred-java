package codr7.tyred;

public abstract class BaseDefinition implements Definition {
    private final String name;

    public BaseDefinition(final String name) {
        this.name = name;
    }

    public final String name() { return name; }
}
