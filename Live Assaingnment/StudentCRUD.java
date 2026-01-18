import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class StudentCRUD extends Application {

    private TextField nameField;
    private TextArea output;
    private Connection conn;

    // ---------- DB CONNECTION ----------
    private void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/testdb",
                    "root",
                    "root"
            );
        } catch (Exception e) {
            showAlert("Database Error", e.getMessage());
        }
    }

    @Override
    public void start(Stage stage) {
        connectDB();

        Label title = new Label("Enter Student Details");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        nameField = new TextField();
        nameField.setPromptText("Student Name");

        Button insertBtn = new Button("Insert");
        Button viewBtn = new Button("View");
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");

        HBox buttons = new HBox(10, insertBtn, viewBtn, updateBtn, deleteBtn);
        buttons.setAlignment(Pos.CENTER);

        output = new TextArea();
        output.setEditable(false);

        insertBtn.setOnAction(e -> insertStudent());
        viewBtn.setOnAction(e -> viewStudents());
        updateBtn.setOnAction(e -> updateStudent());
        deleteBtn.setOnAction(e -> deleteStudent());

        VBox root = new VBox(15, title, nameField, buttons, output);
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 450, 400));
        stage.setTitle("Student CRUD - JDBC");
        stage.show();
    }

    // ---------- INSERT ----------
    private void insertStudent() {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO student(name) VALUES(?)");
            ps.setString(1, nameField.getText());
            ps.executeUpdate();
            output.setText("Student inserted successfully");
        } catch (SQLException e) {
            output.setText(e.getMessage());
        }
    }

    // ---------- VIEW ----------
    private void viewStudents() {
        output.clear();
        try {
            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT * FROM student");

            while (rs.next()) {
                output.appendText(
                        "ID: " + rs.getInt("id") +
                        " | Name: " + rs.getString("name") + "\n"
                );
            }
        } catch (SQLException e) {
            output.setText(e.getMessage());
        }
    }

    // ---------- UPDATE ----------
    private void updateStudent() {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE student SET name=? WHERE id=1");
            ps.setString(1, nameField.getText());
            ps.executeUpdate();
            output.setText("Student updated (ID = 1)");
        } catch (SQLException e) {
            output.setText(e.getMessage());
        }
    }

    // ---------- DELETE ----------
    private void deleteStudent() {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM student WHERE id=1");
            ps.executeUpdate();
            output.setText("Student deleted (ID = 1)");
        } catch (SQLException e) {
            output.setText(e.getMessage());
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
