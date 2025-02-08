package codr7.tyred;

public interface TypedColumn<T> extends TableColumn {
    default Condition EQ(String value) {
        return TableColumn.super.EQ(value);
    }
}
