package app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Birthday;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BirthdayManagerApp extends Application {

    private TableView<Birthday> table = new TableView<>();
    private TextField nameField = new TextField();
    private DatePicker birthDatePicker = new DatePicker();
    private TextField searchField = new TextField();

    @Override
    public void start(Stage stage) {

        // --- Labels in Bangla ---
        Label nameLabel = new Label("নাম");
        Label dateLabel = new Label("জন্মতারিখ");
        Label searchLabel = new Label("অনুসন্ধান");

        Button addBtn = new Button("যোগ করুন");
        Button updateBtn = new Button("হালনাগাদ");
        Button deleteBtn = new Button("মুছে দিন");
        Button searchBtn = new Button("অনুসন্ধান করুন");
        Button refreshBtn = new Button("রিফ্রেশ");

        HBox formBox = new HBox(10, nameLabel, nameField, dateLabel, birthDatePicker, addBtn, updateBtn, deleteBtn);
        HBox searchBox = new HBox(10, searchLabel, searchField, searchBtn, refreshBtn);
        VBox root = new VBox(10, formBox, searchBox, table);
        root.setPadding(new Insets(20));

        // --- Table Columns ---
        TableColumn<Birthday, String> nameCol = new TableColumn<>("নাম");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        TableColumn<Birthday, String> dateCol = new TableColumn<>("জন্মতারিখ");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getBirthDate().toString()));

        table.getColumns().addAll(nameCol, dateCol);

        // --- Button Actions ---
        addBtn.setOnAction(e -> addBirthday());
        updateBtn.setOnAction(e -> updateBirthday());
        deleteBtn.setOnAction(e -> deleteBirthday());
        searchBtn.setOnAction(e -> searchBirthday());
        refreshBtn.setOnAction(e -> loadBirthdays());

        loadBirthdays();
        checkTodayBirthdays();

        stage.setScene(new Scene(root, 900, 500));
        stage.setTitle("ক্লাসমেট জন্মদিন ম্যানেজার");
        stage.show();
    }

    // --- Load Birthdays ---
    private void loadBirthdays() {
        table.getItems().clear();
        String sql = "SELECT * FROM birthdays ORDER BY MONTH(birth_date), DAY(birth_date)";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                table.getItems().add(new Birthday(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDate("birth_date").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addBirthday() {
        String name = nameField.getText();
        LocalDate date = birthDatePicker.getValue();
        if (name.isEmpty() || date == null) {
            showAlert("ত্রুটি", "সব তথ্য পূরণ করুন!");
            return;
        }
        String sql = "INSERT INTO birthdays (name, birth_date) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDate(2, Date.valueOf(date));
            ps.executeUpdate();
            loadBirthdays();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBirthday() {
        Birthday selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("ত্রুটি", "একটি এন্ট্রি নির্বাচন করুন"); return; }
        String sql = "UPDATE birthdays SET name=?, birth_date=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nameField.getText());
            ps.setDate(2, Date.valueOf(birthDatePicker.getValue()));
            ps.setInt(3, selected.getId());
            ps.executeUpdate();
            loadBirthdays();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteBirthday() {
        Birthday selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("ত্রুটি", "একটি এন্ট্রি নির্বাচন করুন"); return; }
        String sql = "DELETE FROM birthdays WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, selected.getId());
            ps.executeUpdate();
            loadBirthdays();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchBirthday() {
        String keyword = searchField.getText();
        table.getItems().clear();
        String sql = "SELECT * FROM birthdays WHERE name LIKE ? OR MONTH(birth_date)=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            int month = 0;
            try { month = Integer.parseInt(keyword); } catch (Exception ignored) {}
            ps.setInt(2, month);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                table.getItems().add(new Birthday(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDate("birth_date").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkTodayBirthdays() {
        LocalDate today = LocalDate.now();
        String sql = "SELECT * FROM birthdays WHERE MONTH(birth_date)=? AND DAY(birth_date)=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, today.getMonthValue());
            ps.setInt(2, today.getDayOfMonth());
            ResultSet rs = ps.executeQuery();
            List<String> names = new ArrayList<>();
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
            if (!names.isEmpty()) {
                showAlert("আজ জন্মদিন", String.join(", ", names) + " এর জন্মদিন আজ!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
