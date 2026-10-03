public final class MinHeap {
    private int[] data = new int[8];
    private int size;
    private final Metrics metrics = new Metrics();

    public int size() {
        return size;
    }

    public Metrics metrics() {
        return metrics;
    }

    // Every array-cell read counts as one step.
    private int read(int index) {
        metrics.step();
        return data[index];
    }

    // Only element-value comparisons are counted.
    private boolean less(int first, int second) {
        metrics.comparison();
        return first < second;
    }

    private void swap(int first, int second) {
        int firstValue = read(first);
        int secondValue = read(second);

        data[first] = secondValue;
        metrics.move();

        data[second] = firstValue;
        metrics.move();
    }

    private void grow() {
        if (size < data.length) {
            return;
        }

        int[] larger = new int[data.length * 2];

        for (int i = 0; i < size; i++) {
            larger[i] = read(i);
            metrics.move();
        }

        data = larger;
    }

    public void insert(int value) {
        grow();

        int index = size;
        data[index] = value;
        size++;

        while (index > 0) {
            int parent = (index - 1) / 2;

            if (!less(read(index), read(parent))) {
                break;
            }

            swap(index, parent);
            index = parent;
        }
    }

    public int peekMin() {
        if (size == 0) {
            throw new IllegalStateException("Empty heap");
        }

        return read(0);
    }

    public int extractMin() {
        if (size == 0) {
            throw new IllegalStateException("Empty heap");
        }

        int minimum = read(0);
        size--;

        if (size > 0) {
            data[0] = read(size);
            metrics.move();
            bubbleDown(0);
        }

        return minimum;
    }

    private void bubbleDown(int index) {
        // Indices below size / 2 have at least one child.
        while (index < size / 2) {
            int left = 2 * index + 1;
            int right = left + 1;
            int smallest = left;

            if (right < size) {
                if (less(read(right), read(left))) {
                    smallest = right;
                }
            }

            if (!less(read(smallest), read(index))) {
                break;
            }

            swap(index, smallest);
            index = smallest;
        }
    }

    // Floyd's algorithm: build a heap in linear time.
    public void buildHeap(int[] values) {
        int capacity = Math.max(8, values.length);
        data = new int[capacity];
        size = values.length;

        for (int i = 0; i < size; i++) {
            metrics.step();
            data[i] = values[i];
            metrics.move();
        }

        for (int i = size / 2 - 1; i >= 0; i--) {
            bubbleDown(i);
        }
    }

    // Used by tests. Does not change measured counters.
    public boolean hasHeapProperty() {
        for (int i = 1; i < size; i++) {
            int parent = (i - 1) / 2;

            if (data[parent] > data[i]) {
                return false;
            }
        }

        return true;
    }
}
