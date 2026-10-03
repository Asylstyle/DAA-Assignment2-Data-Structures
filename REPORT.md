# Assignment 2: Data Structures

**Author:** Assylzhan Amangeldi  
**Group:** SE-2526

## 1. Implementation

DynamicArray stores primitive int values in an array. Its initial
capacity is 8, and its capacity doubles when the array becomes full.
It does not shrink after removal.

MyLinkedList is a singly linked list. Each node stores an int value
and a next reference. Head and tail references allow constant-time
insertion at either end. Removing the last node requires finding
its predecessor.

MinHeap stores primitive int values in an array. Insertion uses
bubble-up, and extraction uses bubble-down. The optional buildHeap
method uses Floyd's bottom-up algorithm and copies the input array.

DynamicArray and MyLinkedList implement the same IntList interface.
Production structures do not use Java collection classes.
Tests use ArrayList and PriorityQueue as reference implementations.

## 2. Complexity analysis

Let n be the number of stored elements before an operation, and i
be a valid index. Bounds describe valid operations for large n.
An invalid index or an empty-heap operation throws in constant time.

Average positional bounds assume uniformly distributed valid indices.
For contains, the average assumes a fixed nonzero proportion of
unsuccessful queries or uniformly distributed successful positions.
Heap averages depend on the input distribution; conservative upper
bounds are shown instead of claiming an unsupported tight average.

Auxiliary space means temporary space used by one operation,
excluding the stored structure.

| Structure | Operation | Best time | Average time | Worst time | Worst auxiliary space | Justification |
|---|---|---|---|---|---|---|
| DynamicArray | add(x) | Theta(1) | Theta(1) amortized | Theta(n) | Theta(n) | Most appends write one cell; a full array copies n values. |
| DynamicArray | add(i,x) | Theta(1) | Theta(n) | Theta(n) | Theta(n) | It shifts n-i values and may allocate a larger array. |
| DynamicArray | remove(i) | Theta(1) | Theta(n) | Theta(n) | Theta(1) | Removing the last value needs no shifts; other positions shift the suffix. |
| DynamicArray | get(i) | Theta(1) | Theta(1) | Theta(1) | Theta(1) | Direct indexing reads one cell. |
| DynamicArray | contains(x) | Theta(1) | Theta(n) | Theta(n) | Theta(1) | A first-position match stops immediately; a miss reads every value. |
| MyLinkedList | add(x) | Theta(1) | Theta(1) | Theta(1) | Theta(1) | The tail reference gives immediate access to the insertion point. |
| MyLinkedList | add(i,x) | Theta(1) | Theta(n) | Theta(n) | Theta(1) | Head and append cases are constant; an interior position requires traversal. |
| MyLinkedList | remove(i) | Theta(1) | Theta(n) | Theta(n) | Theta(1) | Removing the head is constant; other positions require the predecessor. |
| MyLinkedList | get(i) | Theta(1) | Theta(n) | Theta(n) | Theta(1) | It follows exactly i next references from the head. |
| MyLinkedList | contains(x) | Theta(1) | Theta(n) | Theta(n) | Theta(1) | A first-node match is immediate; a miss visits all nodes. |
| MinHeap | insert(x) | Theta(1) | O(log n) amortized | Theta(n) | Theta(n) | Bubble-up takes at most logarithmic time; occasional resizing copies n values. |
| MinHeap | peekMin() | Theta(1) | Theta(1) | Theta(1) | Theta(1) | The minimum is stored at index 0. |
| MinHeap | extractMin() | Theta(1) | O(log n) | Theta(log n) | Theta(1) | Bubble-down may stop immediately or follow the heap height. |
| MinHeap | buildHeap(values) | Theta(n) | Theta(n) | Theta(n) | Theta(n) | Copying costs linear time; the sum of bottom-up sift heights is linear. |

For a fixed index, linked-list get costs Theta(i+1).
Interior linked-list insertion and removal also cost Theta(i+1).
Array removal costs Theta(n-i), and insertion without resizing costs
Theta(n-i+1).

Array append has amortized Theta(1) cost because doubling produces
a geometric sequence of copied capacities: 8 + 16 + 32 + ... .
Across N appends, the total number of copied values is O(N).

Heap insertion without resizing has worst-case Theta(log n).
Including resizing, a single insertion can take Theta(n).
Across a sequence of insertions, resizing contributes amortized O(1)
per operation, so insertion has an amortized O(log n) upper bound.
Its best case gives an Omega(1) lower bound; the average is not
claimed to be Theta(log n) without specifying an input model.

Floyd's algorithm is linear because many nodes are near the leaves.
The number of nodes with height h is O(n / 2^h), so total sift work
is bounded by O(n * sum(h / 2^h)) = O(n).

Total retained storage is Theta(n) for MyLinkedList.
For DynamicArray and MinHeap, it is Theta(capacity): after extensive
removal, unused capacity remains allocated because neither shrinks.

## 3. Measurement method

The benchmark uses n = 100, 1000, 10000 and 100000.
Each case regenerates identical non-negative input using Random(42).
Query generation and initial list filling happen before timing.

W1 performs 10000 random get calls.
W2 performs 1000 contains calls: 500 stored values and 500 guaranteed
absent negative values.
W3 performs 1000 insertions followed by 1000 removals at index 0 or
at the fixed original index n/2.
W4 times n heap insertions and n extractions.

Each case discards two warm-up runs and measures five fresh runs.
The median elapsed time is exported to results/results.csv.
The benchmark also checks that all measured runs have identical
operation counts.

Steps count array-cell reads or traversal to the next node.
Moves count relocation of array elements or structural reference
updates, including head and tail.
Comparisons count comparisons between element values.
Index checks and loop conditions do not count as element comparisons.
A fresh array insertion is not a relocation.
Traversal into a null next reference counts as a traversal step.
Reading a node's value does not count as an array-cell read.

W4 includes writing extracted values into an output array.
Output validation occurs after timing.
W1-W3 validation also occurs after timing and after saving counters.
Instrumentation adds overhead, and two warm-ups cannot guarantee
complete JVM stabilization. These results are classroom measurements
of instrumented implementations.
