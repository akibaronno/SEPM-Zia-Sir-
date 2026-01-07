import java.util.*;

public class CollectionsProject {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // ---------- TASK 1: Find k-th smallest element in ArrayList ----------
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(5, 1, 9, 2, 6, 7));
        int k = 3; // Find 3rd smallest
        System.out.println("Task 1: " + kthSmallest(list, k));

        // ---------- TASK 2: Word Frequency using TreeMap ----------
        String text = "java java python java c++ python";
        TreeMap<String, Integer> wordFreq = new TreeMap<>();
        for (String word : text.split(" ")) {
            wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
        }
        System.out.println("Task 2: Word Frequencies: " + wordFreq);

        // ---------- TASK 3: Queue and Stack using PriorityQueue ----------
        PriorityQueue<Integer> pqQueue = new PriorityQueue<>(); // min-heap for queue
        PriorityQueue<Integer> pqStack = new PriorityQueue<>(Collections.reverseOrder()); // max-heap for stack
        pqQueue.addAll(Arrays.asList(5, 1, 7, 3));
        pqStack.addAll(Arrays.asList(5, 1, 7, 3));
        System.out.println("Task 3: Queue order: ");
        while (!pqQueue.isEmpty()) System.out.print(pqQueue.poll() + " ");
        System.out.println("\nTask 3: Stack order: ");
        while (!pqStack.isEmpty()) System.out.print(pqStack.poll() + " ");
        System.out.println();

        // ---------- TASK 4: TreeMap mapping Student IDs to details ----------
        TreeMap<Integer, String> students = new TreeMap<>();
        students.put(101, "Alice, CSE");
        students.put(102, "Bob, EEE");
        students.put(103, "Charlie, BBA");
        System.out.println("Task 4: Students TreeMap: " + students);

        // ---------- TASK 5: Check if two LinkedLists are equal ----------
        LinkedList<Integer> ll1 = new LinkedList<>(Arrays.asList(1, 2, 3, 4));
        LinkedList<Integer> ll2 = new LinkedList<>(Arrays.asList(1, 2, 3, 4));
        LinkedList<Integer> ll3 = new LinkedList<>(Arrays.asList(1, 2, 3, 5));
        System.out.println("Task 5: ll1 equals ll2? " + ll1.equals(ll2));
        System.out.println("Task 5: ll1 equals ll3? " + ll1.equals(ll3));

        // ---------- TASK 6: HashMap mapping Employee IDs to Departments ----------
        HashMap<Integer, String> employees = new HashMap<>();
        employees.put(201, "HR");
        employees.put(202, "Finance");
        employees.put(203, "IT");
        System.out.println("Task 6: Employees HashMap: " + employees);

        sc.close();
    }

    // --------- Method for Task 1 ----------
    public static int kthSmallest(ArrayList<Integer> list, int k) {
        Collections.sort(list); // Sort ArrayList in ascending order
        if (k <= 0 || k > list.size()) return -1; // Invalid k
        return list.get(k - 1); // Return k-th smallest
    }
}
