public final class MyLinkedList implements IntList {
    private static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    private Node head;
    private Node tail;
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

    // Walk from the head to the requested node.
    private Node nodeAt(int index) {
        Node current = head;

        for (int i = 0; i < index; i++) {
            current = current.next;
            metrics.step();
        }

        return current;
    }

    // Append using tail: no traversal is needed.
    public void add(int value) {
        Node newNode = new Node(value);

        if (size == 0) {
            head = newNode;
            metrics.move();
        } else {
            tail.next = newNode;
            metrics.move();
        }

        tail = newNode;
        metrics.move();
        size++;
    }

    public void add(int index, int value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        if (index == size) {
            add(value);
            return;
        }

        Node newNode = new Node(value);

        if (index == 0) {
            newNode.next = head;
            metrics.move();

            head = newNode;
            metrics.move();
        } else {
            Node previous = nodeAt(index - 1);

            newNode.next = previous.next;
            metrics.move();

            previous.next = newNode;
            metrics.move();
        }

        size++;
    }

    public int get(int index) {
        checkIndex(index);
        Node current = nodeAt(index);
        return current.value;
    }

    public int remove(int index) {
        checkIndex(index);
        Node removed;

        if (index == 0) {
            removed = head;

            head = head.next;
            metrics.move();

            if (size == 1) {
                tail = null;
                metrics.move();
            }
        } else {
            Node previous = nodeAt(index - 1);
            removed = previous.next;

            previous.next = removed.next;
            metrics.move();

            if (index == size - 1) {
                tail = previous;
                metrics.move();
            }
        }

        size--;
        return removed.value;
    }

    public boolean contains(int value) {
        Node current = head;

        while (current != null) {
            metrics.comparison();

            if (current.value == value) {
                return true;
            }

            current = current.next;
            metrics.step();
        }

        return false;
    }
}
