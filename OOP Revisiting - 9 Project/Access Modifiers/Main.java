class Person {
    private String name;  // private: accessible only within the class

    public void setName(String newName) {
        name = newName;
    }

    public String getName() {
        return name;
    }
}

public class Main {
    public static void main(String[] args) {
        Person p = new Person();
        p.setName("Alice");
        System.out.println(p.getName());
    }
}
