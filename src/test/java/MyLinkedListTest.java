import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MyLinkedListTest {
    @Test
    void emptyAndInvalidIndices() {
        MyLinkedList list = new MyLinkedList();

        assertEquals(0, list.size());
        assertFalse(list.contains(10));

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 10));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(1, 10));

        list.add(10);

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(2, 10));
    }

    @Test
    void oneElementAndDuplicates() {
        MyLinkedList list = new MyLinkedList();

        list.add(7);
        assertEquals(7, list.get(0));
        assertTrue(list.contains(7));
        assertEquals(7, list.remove(0));
        assertEquals(0, list.size());
        assertFalse(list.contains(7));

        list.add(7);
        list.add(7);
        list.add(7);

        assertEquals(7, list.remove(1));
        assertEquals(2, list.size());
        assertEquals(7, list.get(0));
        assertEquals(7, list.get(1));
    }

    @Test
    void firstMiddleAndLastPositions() {
        MyLinkedList list = new MyLinkedList();

        list.add(20);
        list.add(0, 10);
        list.add(list.size(), 40);
        list.add(2, 30);

        for (int i = 0; i < 4; i++) {
            assertEquals((i + 1) * 10, list.get(i));
        }

        assertEquals(10, list.remove(0));
        assertEquals(40, list.remove(list.size() - 1));
        assertEquals(30, list.remove(1));
        assertEquals(20, list.remove(0));
        assertEquals(0, list.size());
    }

    @Test
    void tailRemainsValidAfterRemovingLastNode() {
        MyLinkedList list = new MyLinkedList();

        list.add(10);
        list.add(20);
        list.add(30);

        assertEquals(30, list.remove(2));
        list.add(40);

        assertEquals(3, list.size());
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
        assertEquals(40, list.get(2));
    }

    @Test
    void canBeFilledAgainAfterClearing() {
        MyLinkedList list = new MyLinkedList();

        for (int i = 0; i < 100; i++) {
            list.add(i);
        }

        for (int i = 0; i < 100; i++) {
            assertEquals(i, list.remove(0));
        }

        assertEquals(0, list.size());

        list.add(0, 50);
        list.add(60);
        list.add(0, 40);

        assertEquals(40, list.get(0));
        assertEquals(50, list.get(1));
        assertEquals(60, list.get(2));
    }

    @Test
    void randomOperationsMatchArrayList() {
        MyLinkedList actual = new MyLinkedList();
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
    void countersTrackTraversalAndLinkUpdates() {
        MyLinkedList list = new MyLinkedList();
        list.add(10);
        list.add(20);
        list.add(30);

        list.metrics().reset();
        assertEquals(30, list.get(2));
        assertEquals(2L, list.metrics().getSteps());
        assertEquals(0L, list.metrics().getMoves());

        list.metrics().reset();
        list.add(0, 5);
        assertEquals(0L, list.metrics().getSteps());
        assertEquals(2L, list.metrics().getMoves());

        list.metrics().reset();
        assertEquals(5, list.remove(0));
        assertEquals(0L, list.metrics().getSteps());
        assertEquals(1L, list.metrics().getMoves());

        list.metrics().reset();
        assertFalse(list.contains(99));
        assertEquals(3L, list.metrics().getSteps());
        assertEquals(3L, list.metrics().getComparisons());

        list.metrics().reset();
        list.add(2, 25);
        assertEquals(1L, list.metrics().getSteps());
        assertEquals(2L, list.metrics().getMoves());
    }

    @Test
    void appendUsesTailWithoutTraversal() {
        MyLinkedList list = new MyLinkedList();

        for (int i = 0; i < 100; i++) {
            list.add(i);
        }

        list.metrics().reset();
        list.add(100);

        assertEquals(0L, list.metrics().getSteps());
        assertEquals(2L, list.metrics().getMoves());
        assertEquals(0L, list.metrics().getComparisons());
        assertEquals(101, list.size());
    }
}
