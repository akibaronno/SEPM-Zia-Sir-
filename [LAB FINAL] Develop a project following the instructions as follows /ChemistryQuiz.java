package com.example.chemistryquiz;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.*;

public class ChemistryQuiz extends Application {

    private Connection conn;
    private String studentId;
    private int currentQuestion = 0;
    private int score = 0;

    private final String[][] questions = {
            {"পরমাণুর কেন্দ্রকে কী বলা হয়?", "ইলেকট্রন", "প্রোটন", "নিউক্লিয়াস", "নিউট্রন", "3"},
            {"H₂O কোন যৌগ?", "অ্যাসিড", "ক্ষার", "লবণ", "পানি", "4"},
            {"পর্যায় সারণিতে মোট কতটি পর্যায় আছে?", "৫", "৬", "৭", "৮", "3"},
            {"NaCl কী ধরনের যৌগ?", "সহযোজী", "আয়নিক", "ধাতব", "অম্লীয়", "2"},
            {"কার্বনের পারমাণবিক সংখ্যা কত?", "৪", "৬", "৮", "১২", "2"}
    };

    private ToggleGroup answerGroup;
    private Label questionLabel;
    private RadioButton[] options = new RadioButton[4];

    @Override
    public void start(Stage stage) {
        initDatabase();
        stage.setTitle("রসায়ন কুইজ");
        stage.setScene(createLoginScene(stage));
        stage.show();
    }

    private void initDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:quiz.db");

            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS student(id INTEGER PRIMARY KEY, student_id TEXT UNIQUE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS quiz_scores(id INTEGER PRIMARY KEY, student_id TEXT, score INTEGER)");

            for (int i = 1; i <= 54; i++) {
                try {
                    stmt.execute("INSERT INTO student(student_id) VALUES('IT-23" + String.format("%03d", i) + "')");
                } catch (SQLException ignored) {}
            }
        } catch (Exception e) {
            showAlert("DB Error", e.getMessage());
        }
    }

    private Scene createLoginScene(Stage stage) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        TextField id = new TextField();
        id.setPromptText("স্টুডেন্ট আইডি");

        Button start = new Button("শুরু");
        start.setOnAction(e -> {
            studentId = id.getText();
            stage.setScene(createQuizScene(stage));
        });

        root.getChildren().addAll(new Label("রসায়ন কুইজ"), id, start);
        return new Scene(root, 400, 300);
    }

    private Scene createQuizScene(Stage stage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        questionLabel = new Label();
        questionLabel.setFont(new Font(18));

        answerGroup = new ToggleGroup();
        for (int i = 0; i < 4; i++) {
            options[i] = new RadioButton();
            options[i].setToggleGroup(answerGroup);
            root.getChildren().add(options[i]);
        }

        Button next = new Button("Next");
        next.setOnAction(e -> {
            checkAnswer();
            currentQuestion++;
            if (currentQuestion < questions.length)
                loadQuestion();
            else
                stage.setScene(createResultScene(stage));
        });

        root.getChildren().addAll(questionLabel, next);
        loadQuestion();
        return new Scene(root, 500, 350);
    }

    private void loadQuestion() {
        questionLabel.setText(questions[currentQuestion][0]);
        for (int i = 0; i < 4; i++)
            options[i].setText(questions[currentQuestion][i + 1]);
    }

    private void checkAnswer() {
        RadioButton selected = (RadioButton) answerGroup.getSelectedToggle();
        for (int i = 0; i < 4; i++)
            if (options[i] == selected &&
                    Integer.parseInt(questions[currentQuestion][5]) == i + 1)
                score++;
    }

    private Scene createResultScene(Stage stage) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getChildren().add(new Label("স্কোর: " + score));
        return new Scene(root, 300, 200);
    }

    private void showAlert(String t, String m) {
        new Alert(Alert.AlertType.INFORMATION, m).showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
