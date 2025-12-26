import java.util.ArrayList;

public class BiListDeque<T> {

    // left is reversed: left.get(0) == index -1, left.get(1) == index -2 ...
    private final ArrayList<T> left = new ArrayList<>();

    // right is normal: right.get(0) == index 0, right.get(1) == index 1 ...
    private final ArrayList<T> right = new ArrayList<>();

    public BiListDeque() {}

    public void addFirst(T value) {
        left.add(value); // becomes new -1
    }

    public void addLast(T value) {
        right.add(value);
    }

    public T get(int index) {
        if (index >= 0) {
            return right.get(index);
        } else {
            int k = -index - 1; // -1 -> 0, -2 -> 1, ...
            return left.get(k);
        }
    }

    // Valid index range: [-leftSize .. rightSize-1]
    public boolean isValidIndex(int index) {
        return index >= -left.size() && index < right.size();
    }

    public int size() {
        return left.size() + right.size();
    }
}
