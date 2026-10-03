import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicArrayTest {
    @Test
    void emptyAndInvalidIndices() {
        DynamicArray array = new DynamicArray();

        assertEquals(0, array.size());
        assertFalse(array.contains(10));

        assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(-1, 10));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(1, 10));

        array.add(10);

        assertThrows(IndexOutOfBoundsException.class, () -> array.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(2, 10));
    }

    @Test
    void oneElementAndDuplicates() {
        DynamicArray array = new DynamicArray();

        array.add(7);
        assertEquals(7, array.get(0));
        assertTrue(array.contains(7));
        assertEquals(7, array.remove(0));
        assertEquals(0, array.size());

        array.add(7);
        array.add(7);
        array.add(7);

        assertEquals(7, array.remove(1));
        assertEquals(2, array.size());
        assertEquals(7, array.get(0));
        assertEquals(7, array.get(1));
    }

    @Test
    void firstMiddleAndLastPositions() {
        DynamicArray array = new DynamicArray();

        array.add(20);
        array.add(0, 10);
        array.add(array.size(), 40);
        array.add(2, 30);

        for (int i = 0; i < 4; i++) {
            assertEquals((i + 1) * 10, array.get(i));
        }

        assertEquals(10, array.remove(0));
        assertEquals(40, array.remove(array.size() - 1));
        assertEquals(30, array.remove(1));
        assertEquals(20, array.remove(0));
        assertEquals(0, array.size());
    }

    @Test
    void resizingPreservesValues() {
        DynamicArray array = new DynamicArray();

        for (int i = 0; i < 10000; i++) {
            array.add(i);
        }

        assertEquals(10000, array.size());

        for (int i = 0; i < 10000; i++) {
            assertEquals(i, array.get(i));
        }
    }

    @Test
    void randomOperationsMatchArrayList() {
        DynamicArray actual = new DynamicArray();
        ArrayList<Integer> expected = new ArrayList<>();
        Random random = new Random(42);

        for (int operation = 0; operation < 3000; operation++) {
            int choice = random.nextInt(5);

            if (expected.isEmpty() || choice == 0) {
                int value = random.nextInt(100) - 50;
                expected.add(value);
                actual.add(value);
            } else if (choice == 1) {
                int index = random.nextInt(expected.size() + 1);
                int value = random.nextInt(100) - 50;
                expected.add(index, value);
                actual.add(index, value);
            } else if (choice == 2) {
                int index = random.nextInt(expected.size());
                assertEquals(expected.remove(index).intValue(),
                             actual.remove(index));
            } else if (choice == 3) {
                int index = random.nextInt(expected.size());
                assertEquals(expected.get(index).intValue(),
                             actual.get(index));
            } else {
                int value = random.nextInt(150) - 75;
                assertEquals(expected.contains(value),
                             actual.contains(value));
            }

            assertEquals(expected.size(), actual.size());

            for (int i = 0; i < expected.size(); i++) {
                assertEquals(expected.get(i).intValue(), actual.get(i));
            }
        }
    }

    @Test
    void countersIncludeReadsShiftsAndComparisons() {
        DynamicArray array = new DynamicArray();
        array.add(10);
        array.add(20);
        array.add(30);

        array.metrics().reset();
        assertEquals(20, array.get(1));
        assertEquals(1L, array.metrics().getSteps());
        assertEquals(0L, array.metrics().getMoves());

        array.metrics().reset();
        array.add(0, 5);
        assertEquals(3L, array.metrics().getSteps());
        assertEquals(3L, array.metrics().getMoves());

        array.metrics().reset();
        assertEquals(5, array.remove(0));
        assertEquals(4L, array.metrics().getSteps());
        assertEquals(3L, array.metrics().getMoves());

        array.metrics().reset();
        assertFalse(array.contains(99));
        assertEquals(3L, array.metrics().getSteps());
        assertEquals(3L, array.metrics().getComparisons());
    }

    @Test
    void growthCopiesExactlyTheExistingElements() {
        DynamicArray array = new DynamicArray();

        for (int i = 0; i < 8; i++) {
            array.add(i);
        }

        array.metrics().reset();
        array.add(8);

        assertEquals(8L, array.metrics().getSteps());
        assertEquals(8L, array.metrics().getMoves());
        assertEquals(0L, array.metrics().getComparisons());
        assertEquals(9, array.size());
    }
}
