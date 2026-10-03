import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Random;

public class Benchmark {
    private static volatile long sink;

    private static class Result {
        long time;
        long steps;
        long moves;
        long comparisons;

        Result(long time, Metrics metrics) {
            this.time = time;
            steps = metrics.getSteps();
            moves = metrics.getMoves();
            comparisons = metrics.getComparisons();
        }
    }

    private static int[] createData(int n) {
        Random random = new Random(42);
        int[] values = new int[n];

        for (int i = 0; i < n; i++) {
            values[i] = random.nextInt(1_000_000);
        }

        return values;
    }

    private static IntList createList(String structure) {
        if (structure.equals("DynamicArray")) {
            return new DynamicArray();
        }

        if (structure.equals("MyLinkedList")) {
            return new MyLinkedList();
        }

        throw new IllegalArgumentException("Unknown structure");
    }

    private static Result runList(
        String workload, String variant, String structure, int[] values
    ) {
        IntList list = createList(structure);

        for (int value : values) {
            list.add(value);
        }

        Random random = new Random(42);
        int queryCount = 1000;

        if (workload.equals("W1")) {
            queryCount = 10000;
        }

        int[] queries = new int[queryCount];

        for (int i = 0; i < queryCount; i++) {
            if (workload.equals("W1")) {
                queries[i] = random.nextInt(values.length);
            } else if (workload.equals("W2")) {
                if (i % 2 == 0) {
                    queries[i] = values[random.nextInt(values.length)];
                } else {
                    // Stored values are non-negative: negative queries miss.
                    queries[i] = -1 - random.nextInt(1_000_000);
                }
            } else {
                queries[i] = random.nextInt(1_000_000);
            }
        }

        int index = 0;

        if (variant.equals("middle")) {
            index = values.length / 2;
        }

        list.metrics().reset();
        long checksum = 0;
        long start = System.nanoTime();

        if (workload.equals("W1")) {
            for (int query : queries) {
                checksum += list.get(query);
            }
        } else if (workload.equals("W2")) {
            for (int query : queries) {
                if (list.contains(query)) {
                    checksum++;
                }
            }
        } else if (workload.equals("W3")) {
            for (int query : queries) {
                list.add(index, query);
            }

            for (int i = 0; i < queryCount; i++) {
                checksum += list.remove(index);
            }
        } else {
            throw new IllegalArgumentException("Unknown workload");
        }

        long elapsed = System.nanoTime() - start;
        Result result = new Result(elapsed, list.metrics());
        sink = checksum;

        // Validation is outside the timer and saved counters.
        if (workload.equals("W1")) {
            long expected = 0;

            for (int query : queries) {
                expected += values[query];
            }

            if (checksum != expected) {
                throw new AssertionError("Incorrect random access");
            }
        }

        if (workload.equals("W2") && checksum != 500) {
            throw new AssertionError("Expected exactly 500 successful searches");
        }

        if (workload.equals("W3")) {
            if (list.size() != values.length) {
                throw new AssertionError("Incorrect size after W3");
            }

            // Remove the head repeatedly to validate in linear time.
            for (int value : values) {
                if (list.remove(0) != value) {
                    throw new AssertionError("W3 changed original values");
                }
            }
        }

        return result;
    }

    private static Result runHeap(int[] values) {
        MinHeap heap = new MinHeap();

        // Store output for verification after timing.
        int[] output = new int[values.length];
        long start = System.nanoTime();

        for (int value : values) {
            heap.insert(value);
        }

        for (int i = 0; i < output.length; i++) {
            output[i] = heap.extractMin();
        }

        long elapsed = System.nanoTime() - start;
        Result result = new Result(elapsed, heap.metrics());

        long checksum = 0;
        long expectedChecksum = 0;

        for (int i = 0; i < output.length; i++) {
            if (i > 0 && output[i] < output[i - 1]) {
                throw new AssertionError("Heap output is not sorted");
            }

            checksum += output[i];
            expectedChecksum += values[i];
        }

        if (heap.size() != 0 || checksum != expectedChecksum) {
            throw new AssertionError("Incorrect heap output");
        }

        sink = checksum;
        return result;
    }

    private static Result runCase(
        String workload, String variant, String structure, int[] values
    ) {
        if (workload.equals("W4")) {
            return runHeap(values);
        }

        return runList(workload, variant, structure, values);
    }

    private static void sortByTime(Result[] results) {
        for (int i = 1; i < results.length; i++) {
            Result current = results[i];
            int j = i - 1;

            while (j >= 0 && results[j].time > current.time) {
                results[j + 1] = results[j];
                j--;
            }

            results[j + 1] = current;
        }
    }

    private static void measure(
        PrintWriter writer, String workload, String variant,
        String structure, int n
    ) {
        int[] values = createData(n);

        System.out.println(
            "Running " + workload + "/" + variant
            + " " + structure + " n=" + n
        );

        // Discard two warm-up runs.
        for (int i = 0; i < 2; i++) {
            runCase(workload, variant, structure, values);
        }

        Result[] results = new Result[5];

        for (int i = 0; i < results.length; i++) {
            results[i] = runCase(workload, variant, structure, values);
        }

        sortByTime(results);
        Result median = results[2];

        for (Result result : results) {
            if (result.steps != median.steps
                || result.moves != median.moves
                || result.comparisons != median.comparisons) {
                throw new AssertionError("Counters differ between runs");
            }
        }

        double milliseconds = median.time / 1_000_000.0;

        writer.printf(
            Locale.ROOT,
            "%s,%s,%s,%d,%.6f,%d,%d,%d%n",
            workload, variant, structure, n, milliseconds,
            median.steps, median.moves, median.comparisons
        );

        writer.flush();

        if (writer.checkError()) {
            throw new IllegalStateException("Cannot write CSV");
        }

        System.out.printf(
            Locale.ROOT, "Median: %.3f ms%n", milliseconds
        );
    }

    public static void main(String[] args) throws Exception {
        Files.createDirectories(Path.of("results"));
        Path csv = Path.of("results/results.csv");

        try (PrintWriter writer = new PrintWriter(
            Files.newBufferedWriter(csv)
        )) {
            writer.println(
                "workload,variant,structure,n,time_ms,steps,moves,comparisons"
            );

            int[] sizes = {100, 1000, 10000, 100000};
            String[] structures = {"DynamicArray", "MyLinkedList"};

            for (int n : sizes) {
                for (String structure : structures) {
                    measure(writer, "W1", "-", structure, n);
                    measure(writer, "W2", "-", structure, n);
                    measure(writer, "W3", "head", structure, n);
                    measure(writer, "W3", "middle", structure, n);
                }

                measure(writer, "W4", "-", "MinHeap", n);
            }
        }

        System.out.println("Saved 36 rows to results/results.csv");
    }
}
