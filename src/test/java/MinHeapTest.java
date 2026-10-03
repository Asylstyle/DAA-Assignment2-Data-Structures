import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MinHeapTest {
    @Test
    void emptyHeapThrowsExceptions() {
        MinHeap heap = new MinHeap();

        assertEquals(0, heap.size());
        assertTrue(heap.hasHeapProperty());
        assertThrows(IllegalStateException.class, () -> heap.peekMin());
        assertThrows(IllegalStateException.class, () -> heap.extractMin());
    }

    @Test
    void oneElementAndReuse() {
        MinHeap heap = new MinHeap();

        heap.insert(7);
        assertTrue(heap.hasHeapProperty());
        assertEquals(1, heap.size());
        assertEquals(7, heap.peekMin());
        assertEquals(1, heap.size());

        assertEquals(7, heap.extractMin());
        assertTrue(heap.hasHeapProperty());
        assertEquals(0, heap.size());

        heap.insert(-5);
        assertTrue(heap.hasHeapProperty());
        assertEquals(-5, heap.extractMin());
        assertTrue(heap.hasHeapProperty());
        assertThrows(IllegalStateException.class, () -> heap.extractMin());
    }

    @Test
    void duplicatesAndExtremeValues() {
        MinHeap heap = new MinHeap();
        int[] values = {
            7, 7, -2, 0, Integer.MAX_VALUE, Integer.MIN_VALUE, 7
        };
        int[] expected = values.clone();
        Arrays.sort(expected);

        for (int value : values) {
            heap.insert(value);
            assertTrue(heap.hasHeapProperty());
        }

        for (int value : expected) {
            assertEquals(value, heap.extractMin());
            assertTrue(heap.hasHeapProperty());
        }

        assertEquals(0, heap.size());
    }

    @Test
    void randomOperationsMatchPriorityQueue() {
        MinHeap actual = new MinHeap();
        PriorityQueue<Integer> expected = new PriorityQueue<>();
        Random random = new Random(42);

        for (int operation = 0; operation < 3000; operation++) {
            if (expected.isEmpty() || random.nextBoolean()) {
                int value = random.nextInt(200) - 100;
                actual.insert(value);
                expected.add(value);
            } else {
                assertEquals(expected.remove().intValue(),
                             actual.extractMin());
            }

            assertEquals(expected.size(), actual.size());
            assertTrue(actual.hasHeapProperty());

            if (!expected.isEmpty()) {
                assertEquals(expected.peek().intValue(), actual.peekMin());
            }
        }

        while (!expected.isEmpty()) {
            assertEquals(expected.remove().intValue(), actual.extractMin());
            assertTrue(actual.hasHeapProperty());
        }

        assertEquals(0, actual.size());
    }

    @Test
    void extractsAllValuesInSortedOrder() {
        MinHeap heap = new MinHeap();
        Random random = new Random(42);
        int[] values = new int[2000];

        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextInt(1000) - 500;
            heap.insert(values[i]);
            assertTrue(heap.hasHeapProperty());
        }

        Arrays.sort(values);

        for (int value : values) {
            assertEquals(value, heap.extractMin());
            assertTrue(heap.hasHeapProperty());
        }

        assertEquals(0, heap.size());
    }

    @Test
    void buildHeapCopiesInputAndProducesSortedOutput() {
        MinHeap heap = new MinHeap();
        int[] values = {9, 4, 7, 1, 3, 1, -5, 20, 0};
        int[] original = values.clone();
        int[] expected = values.clone();
        Arrays.sort(expected);

        heap.buildHeap(values);

        assertArrayEquals(original, values);
        assertEquals(values.length, heap.size());
        assertTrue(heap.hasHeapProperty());

        // Changing the original array must not change the heap.
        values[0] = Integer.MIN_VALUE;

        for (int value : expected) {
            assertEquals(value, heap.extractMin());
            assertTrue(heap.hasHeapProperty());
        }
    }

    @Test
    void buildHeapHandlesDifferentSizesAndAllowsInsertion() {
        Random random = new Random(42);

        for (int n = 0; n <= 100; n++) {
            MinHeap heap = new MinHeap();
            int[] values = new int[n];

            for (int i = 0; i < n; i++) {
                values[i] = random.nextInt(100);
            }

            int[] expected = values.clone();
            Arrays.sort(expected);

            heap.buildHeap(values);
            assertTrue(heap.hasHeapProperty());

            heap.insert(-1);
            assertTrue(heap.hasHeapProperty());
            assertEquals(-1, heap.extractMin());
            assertTrue(heap.hasHeapProperty());

            for (int value : expected) {
                assertEquals(value, heap.extractMin());
                assertTrue(heap.hasHeapProperty());
            }

            assertEquals(0, heap.size());
        }
    }

    @Test
    void buildHeapReplacesPreviousContents() {
        MinHeap heap = new MinHeap();
        heap.insert(-100);
        heap.insert(100);

        heap.buildHeap(new int[] {3, 2});
        assertTrue(heap.hasHeapProperty());
        assertEquals(2, heap.size());
        assertEquals(2, heap.extractMin());
        assertTrue(heap.hasHeapProperty());
        assertEquals(3, heap.extractMin());
        assertTrue(heap.hasHeapProperty());

        heap.buildHeap(new int[0]);
        assertEquals(0, heap.size());
        assertThrows(IllegalStateException.class, () -> heap.peekMin());
    }

    @Test
    void countersIncludeReadsSwapsAndComparisons() {
        MinHeap heap = new MinHeap();
        heap.insert(10);

        heap.metrics().reset();
        assertEquals(10, heap.peekMin());
        assertEquals(1L, heap.metrics().getSteps());
        assertEquals(0L, heap.metrics().getMoves());
        assertEquals(0L, heap.metrics().getComparisons());

        heap.metrics().reset();
        heap.insert(5);

        assertEquals(4L, heap.metrics().getSteps());
        assertEquals(2L, heap.metrics().getMoves());
        assertEquals(1L, heap.metrics().getComparisons());

        heap.metrics().reset();
        assertEquals(5, heap.extractMin());

        assertEquals(2L, heap.metrics().getSteps());
        assertEquals(1L, heap.metrics().getMoves());
        assertEquals(0L, heap.metrics().getComparisons());
        assertTrue(heap.hasHeapProperty());
    }
}
