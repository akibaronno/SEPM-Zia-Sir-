import java.util.LinkedList;
import java.util.Queue;

// Class representing a parking order
class RegistrarParking {
    private final int carId;

    public RegistrarParking(int carId) {
        this.carId = carId;
    }

    public int getCarId() {
        return carId;
    }
}

// Shared parking pool (thread-safe queue)
class ParkingPool {
    private final Queue<RegistrarParking> parkingQueue = new LinkedList<>();
    private final int capacity;

    public ParkingPool(int capacity) {
        this.capacity = capacity;
    }

    // Add a car to the queue
    public synchronized void addCar(RegistrarParking order) throws InterruptedException {
        while (parkingQueue.size() >= capacity) {
            wait(); // Wait if parking is full
        }
        parkingQueue.add(order);
        System.out.println("Car " + order.getCarId() + " is waiting to park.");
        notifyAll(); // Notify agents that a car is available
    }

    // Retrieve a car from the queue
    public synchronized RegistrarParking getCar() throws InterruptedException {
        while (parkingQueue.isEmpty()) {
            wait(); // Wait if no cars are waiting
        }
        RegistrarParking car = parkingQueue.poll();
        notifyAll(); // Notify that a slot is free
        return car;
    }
}

// Parking agent (thread) that parks cars
class ParkingAgent extends Thread {
    private final ParkingPool parkingPool;
    private final String agentName;

    public ParkingAgent(ParkingPool pool, String name) {
        this.parkingPool = pool;
        this.agentName = name;
    }

    @Override
    public void run() {
        try {
            while (true) {
                RegistrarParking car = parkingPool.getCar();
                System.out.println(agentName + " is parking Car " + car.getCarId());
                Thread.sleep(1000); // Simulate parking time
                System.out.println(agentName + " finished parking Car " + car.getCarId());
            }
        } catch (InterruptedException e) {
            System.out.println(agentName + " stopped.");
        }
    }
}

// Main class to demonstrate N cars parking
public class MainClass {
    public static void main(String[] args) throws InterruptedException {
        int numberOfCars = 10;
        int parkingCapacity = 5;
        int numberOfAgents = 3;

        ParkingPool parkingPool = new ParkingPool(parkingCapacity);

        // Start parking agent threads
        for (int i = 1; i <= numberOfAgents; i++) {
            new ParkingAgent(parkingPool, "Agent-" + i).start();
        }

        // Cars arriving to park
        for (int carId = 1; carId <= numberOfCars; carId++) {
            RegistrarParking carOrder = new RegistrarParking(carId);
            parkingPool.addCar(carOrder);
            Thread.sleep(300); // Simulate cars arriving at different times
        }
    }
}
