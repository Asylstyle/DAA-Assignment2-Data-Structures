public final class DynamicArray implements IntList {
    private int[] data = new int[8];
    private int size;
    private final Metrics metrics = new Metrics();

    public int size() {
        return size;
    }

    public Metrics metrics() {
        return metrics;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
    }

    private void grow() {
        if (size < data.length) {
            return;
        }

        int[] larger = new int[data.length * 2];

        for (int i = 0; i < size; i++) {
            metrics.step();
            larger[i] = data[i];
            metrics.move();
        }

        data = larger;
    }

    public void add(int value) {
        add(size, value);
    }

    public void add(int index, int value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        grow();

        for (int i = size; i > index; i--) {
            metrics.step();
            data[i] = data[i - 1];
            metrics.move();
        }

        data[index] = value;
        size++;
    }

    public int get(int index) {
        checkIndex(index);
        metrics.step();
        return data[index];
    }

    public int remove(int index) {
        checkIndex(index);
        metrics.step();
        int removed = data[index];

        for (int i = index; i < size - 1; i++) {
            metrics.step();
            data[i] = data[i + 1];
            metrics.move();
        }

        size--;
        return removed;
    }

    public boolean contains(int value) {
        for (int i = 0; i < size; i++) {
            metrics.step();
            metrics.comparison();

            if (data[i] == value) {
                return true;
            }
        }

        return false;
    }
}
