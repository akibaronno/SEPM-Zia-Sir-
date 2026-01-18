import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class StudentManager extends Application {

    private TextField nameField, emailField;
    private TableView<Student> table;
    private Connection conn;

    @Override
    public void start(Stage stage) {
        connectDB();

        Label title = new Label("Enter Student Details");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        nameField = new TextField();
        emailField = new TextField();

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(new Label("Name:"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(new Label("Email:"), 0, 1);
        form.add(emailField, 1, 1);

        Button insert = new Button("Insert");
        Button view = new Button("View");
        Button update = new Button("Update");
        Button delete = new Button("Delete");

        HBox buttons = new HBox(10, insert, view, update, delete);
        buttons.setAlignment(Pos.CENTER);

        table = new TableView<>();
        TableColumn<Student, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> c.getValue().idProperty().asObject());

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> c.getValue().nameProperty());

        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(c -> c.getValue().emailProperty());

        table.getColumns().addAll(idCol, nameCol, emailCol);

        insert.setOnAction(e -> insertData());
        view.setOnAction(e -> loadData());
        update.setOnAction(e -> updateData());
        delete.setOnAction(e -> deleteData());

        VBox root = new VBox(20, title, form, buttons, table);
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 600, 500));
        stage.setTitle("Student Record Manager");
        stage.show();
    }

    private void connectDB() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:student.db");
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS student(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "email TEXT)"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertData() {
        try {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO student(name,email) VALUES(?,?)");
            ps.setString(1, nameField.getText());
            ps.setString(2, emailField.getText());
            ps.executeUpdate();
            loadData();
        } catch (SQLException ignored) {}
    }

    private void loadData() {
        table.getItems().clear();
        try {
            ResultSet rs = conn.createStatement()
                .executeQuery("SELECT * FROM student");
            while (rs.next()) {
                table.getItems().add(
                    new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email")
                    )
                );
            }
        } catch (SQLException ignored) {}
    }

    private void updateData() {
        Student s = table.getSelectionModel().getSelectedItem();
        if (s == null) return;

        try {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE student SET name=?, email=? WHERE id=?");
            ps.setString(1, nameField.getText());
            ps.setString(2, emailField.getText());
            ps.setInt(3, s.getId());
            ps.executeUpdate();
            loadData();
        } catch (SQLException ignored) {}
    }

    private void deleteData() {
        Student s = table.getSelectionModel().getSelectedItem();
        if (s == null) return;

        try {
            PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM student WHERE id=?");
            ps.setInt(1, s.getId());
            ps.executeUpdate();
            loadData();
        } catch (SQLException ignored) {}
    }

    public static void main(String[] args) {
        launch();
    }
}
