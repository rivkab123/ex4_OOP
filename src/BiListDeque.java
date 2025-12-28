import java.util.ArrayList;

/**
 * A double-ended deque backed by two {@link ArrayList}s.
 * <p>
 * Elements with negative indices are stored in the {@code left} list in reversed order:
 * {@code left.get(0)} corresponds to index -1, {@code left.get(1)} to index -2, etc.
 * Elements with non-negative indices are stored in the {@code right} list in normal order:
 * {@code right.get(0)} corresponds to index 0, {@code right.get(1)} to index 1, etc.
 *
 * @param <T> the type of elements held in this deque
 */
public class BiListDeque<T> {

    // left is reversed: left.get(0) == index -1, left.get(1) == index -2 ...
    private final ArrayList<T> left = new ArrayList<>();

    // right is normal: right.get(0) == index 0, right.get(1) == index 1 ...
    private final ArrayList<T> right = new ArrayList<>();

    private static final int NEGATIVE_INDEX_OFFSET = 1;

    /** Constructs an empty deque */
    public BiListDeque() {}

    /**
     * Adds an element at the beginning (index -1) of the deque.
     *
     * @param value the element to add
     */
    public void addFirst(T value) {
        left.add(value); // becomes new -1
    }

    /**
     * Adds an element at the end (highest non-negative index) of the deque.
     *
     * @param value the element to add
     */
    public void addLast(T value) {
        right.add(value);
    }

    /**
     * Returns the element at the specified index.
     * Negative indices access the {@code left} list in reversed order.
     *
     * @param index the index of the element to return
     * @return the element at the specified index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public T get(int index) {
        if (index >= 0) {
            return right.get(index);
        } else {
            int k = -index - NEGATIVE_INDEX_OFFSET; // -1 -> 0, -2 -> 1, ...
            return left.get(k);
        }
    }

    /**
     * Checks if an index is valid for this deque.
     *
     * @param index the index to check
     * @return true if the index is valid, false otherwise
     */
    public boolean isValidIndex(int index) {
        return index >= -left.size() && index < right.size();
    }

    /**
     * Returns the total number of elements in the deque.
     *
     * @return total size of the deque
     */
    public int size() {
        return left.size() + right.size();
    }
}
