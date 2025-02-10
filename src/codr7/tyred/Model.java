package codr7.tyred;

public interface Model {
    Context context();
    boolean isModified();
    boolean isStored();
    Record record();
    Table[] tables();
}
