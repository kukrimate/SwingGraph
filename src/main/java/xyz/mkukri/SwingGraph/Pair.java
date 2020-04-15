package xyz.mkukri.SwingGraph;

/**
 * Pair of two object
 * @param <T> type of objects in this pair
 */
public class Pair<T> {
    public T obj1;
    public T obj2;

    public Pair(T obj1, T obj2) {
        this.obj1 = obj1;
        this.obj2 = obj2;
    }
}
