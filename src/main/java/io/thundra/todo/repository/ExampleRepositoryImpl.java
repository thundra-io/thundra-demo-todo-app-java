package io.thundra.todo.repository;

/**
 * @author yusuferdem
 */
public class ExampleRepositoryImpl implements ExampleRepository {

    public static final int INT = 3;
    public static final int INT1 = 4;
    public static final int INT2 = 2;

    public int getSum() {
        int x = INT * INT1;
        int y = INT2 * INT2;
        return x * y;
    }
}
