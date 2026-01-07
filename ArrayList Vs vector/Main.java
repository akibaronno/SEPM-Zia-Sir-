import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Main {
    // Utility method to get internal capacity of ArrayList or Vector
    private static int getCapacity(Object list) {
        try {
            Field field = list.getClass().getDeclaredField("elementData");
            field.setAccessible(true);
            Object[] elementData = (Object[]) field.get(list);
            return elementData.length;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void main(String[] args) {
        // ----------------- ArrayList Example -----------------
        ArrayList<String> arrayList = new ArrayList<>(10);
        System.out.println("ArrayList Initial Capacity: " + getCapacity(arrayList));

        // Add 11 elements to exceed initial capacity
        for (int i = 1; i <= 11; i++) {
            arrayList.add("IT2300" + i);
        }

        System.out.println("ArrayList Size after adding 11 elements: " + arrayList.size());
        System.out.println("ArrayList Capacity after exceeding initial capacity: " + getCapacity(arrayList));
        System.out.println("---------------------------------------------------");

        // ----------------- Vector Example -----------------
        Vector<String> vector = new Vector<>(10);
        System.out.println("Vector Initial Capacity: " + vector.capacity());

        // Add 11 elements to exceed initial capacity
        for (int i = 1; i <= 11; i++) {
            vector.add("IT2300" + i);
        }

        System.out.println("Vector Size after adding 11 elements: " + vector.size());
        System.out.println("Vector Capacity after exceeding initial capacity: " + vector.capacity());
    }
}
