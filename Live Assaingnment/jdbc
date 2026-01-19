import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class jdbc {

    static String url = "jdbc:mysql://localhost:3306/akib";
    static String user = "root";
    static String password = "Akib@akib31";

    public static void main(String[] args) {

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        Scanner sc = new Scanner(System.in);

        try {
            // Connect to database
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();
            System.out.println("Connected to database successfully");

            // Create table
            String createTable =
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INT PRIMARY KEY AUTO_INCREMENT, " +
                            "name VARCHAR(255), " +
                            "age INT)";
            stmt.executeUpdate(createTable);

            // Take input from user
            System.out.print("Enter name: ");
            String name = sc.nextLine();

            System.out.print("Enter age: ");
            int age = sc.nextInt();

            // Insert user input into database
            String insertSQL = "INSERT INTO users (name, age) VALUES (?, ?)";
            pstmt = conn.prepareStatement(insertSQL);
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.executeUpdate();

            System.out.println("Data inserted successfully\n");

            // Fetch and print all data
            rs = stmt.executeQuery("SELECT * FROM users");
            System.out.println("ID | Name | Age");
            System.out.println("----------------");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("id") + " | " +
                                rs.getString("name") + " | " +
                                rs.getInt("age")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
                sc.close();
                System.out.println("\nConnection closed successfully");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
