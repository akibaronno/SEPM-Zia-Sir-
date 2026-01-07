import java.util.*;

public class CollectionDemo {
    public static void main(String[] args) {
        // --- LinkedList ---
        LinkedList<String> linkedList = new LinkedList<>();
        linkedList.add("A");
        linkedList.add("C");
        linkedList.add(1, "B"); // Insert at position
        System.out.println("LinkedList: " + linkedList);

        // --- PriorityQueue ---
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        pq.add(5);
        pq.add(2);
        pq.add(8);
        System.out.println("PriorityQueue (min at head): " + pq);
        System.out.println("Polling from PriorityQueue:");
        while (!pq.isEmpty()) {
            System.out.print(pq.poll() + " "); // Pops in sorted order
        }
        System.out.println();

        // --- Deque ---
        Deque<String> deque = new ArrayDeque<>();
        deque.add("First");
        deque.addFirst("Zero"); // Add at front
        deque.addLast("Second"); // Add at end
        System.out.println("Deque: " + deque);
        System.out.println("Removing from Deque:");
        System.out.println("Remove First: " + deque.removeFirst());
        System.out.println("Remove Last: " + deque.removeLast());

        // --- HashMap ---
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("Alice", 30);
        hashMap.put("Bob", 25);
        hashMap.put("Charlie", 35);
        System.out.println("HashMap: " + hashMap); // No guaranteed order

        // --- TreeMap ---
        Map<String, Integer> treeMap = new TreeMap<>();
        treeMap.put("Alice", 30);
        treeMap.put("Bob", 25);
        treeMap.put("Charlie", 35);
        System.out.println("TreeMap (sorted by key): " + treeMap);

        // --- HashSet ---
        Set<String> hashSet = new HashSet<>();
        hashSet.add("Apple");
        hashSet.add("Banana");
        hashSet.add("Apple"); // Duplicate ignored
        System.out.println("HashSet (unique elements, no order): " + hashSet);
    }
}
