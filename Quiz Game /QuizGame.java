import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.*;

public class QuizGameFX extends Application {

    // ---------- DATABASE CONFIG ----------
    private static final String JDBC_URL =
            "jdbc:mysql://localhost:3306/word_game?serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Akib@akib31";

    // ---------- GAME STATE ----------
    private Connection conn;
    private String playerName = "Player";
    private WordInfo currentWord;
    private char[] hiddenWord;
    private Set<Character> guessedLetters = new HashSet<>();
    private int attempts;
    private int totalScore = 0;
    private int wordCount = 0;

    private final int WORDS_PER_LEVEL = 5;
    private final Map<String, Set<Integer>> usedWordIdsMap = new HashMap<>();

    // ---------- UI CONTROLS ----------
    private Label wordLabel = new Label();
    private Label hintLabel = new Label();
    private Label attemptsLabel = new Label();
    private Label scoreLabel = new Label();
    private TextField letterField = new TextField();
    private Button guessButton = new Button("Guess");

    private String difficulty;

    @Override
    public void start(Stage stage) {
        connectDatabase();
        askPlayerName(stage);
    }

    // ---------- PLAYER NAME ----------
    private void askPlayerName(Stage stage) {
        TextField nameField = new TextField();
        ComboBox<String> difficultyBox = new ComboBox<>();
        difficultyBox.getItems().addAll("EASY", "MEDIUM", "HARD");
        difficultyBox.setValue("EASY");

        Button startBtn = new Button("Start Game");

        VBox root = new VBox(10,
                new Label("Enter Player Name:"),
                nameField,
                new Label("Select Difficulty:"),
                difficultyBox,
                startBtn
        );
        root.setPadding(new Insets(20));

        startBtn.setOnAction(e -> {
            playerName = nameField.getText().isEmpty() ? "Player" : nameField.getText();
            difficulty = difficultyBox.getValue();
            usedWordIdsMap.put(difficulty, new HashSet<>());
            showGameUI(stage);
            loadNextWord();
        });

        stage.setScene(new Scene(root, 350, 250));
        stage.setTitle("Quiz Game");
        stage.show();
    }

    // ---------- GAME UI ----------
    private void showGameUI(Stage stage) {
        letterField.setMaxWidth(60);

        guessButton.setOnAction(e -> processGuess());

        VBox root = new VBox(12,
                wordLabel,
                hintLabel,
                attemptsLabel,
                scoreLabel,
                new HBox(10, new Label("Letter:"), letterField, guessButton)
        );
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 400, 300));
    }

    // ---------- LOAD WORD ----------
    private void loadNextWord() {
        if (wordCount >= WORDS_PER_LEVEL) {
            showAlert("Level Completed",
                    "Final Score: " + totalScore);
            closeConnection();
            return;
        }

        try {
            currentWord = getRandomWord();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (currentWord == null) {
            showAlert("No Words", "No more words available.");
            return;
        }

        hiddenWord = new char[currentWord.word.length()];
        Arrays.fill(hiddenWord, '_');
        guessedLetters.clear();

        attempts = switch (difficulty) {
            case "EASY" -> currentWord.word.length() + 4;
            case "MEDIUM" -> currentWord.word.length() + 2;
            case "HARD" -> currentWord.word.length();
            default -> currentWord.word.length() + 3;
        };

        updateUI();
        wordCount++;
    }

    // ---------- PROCESS GUESS ----------
    private void processGuess() {
        String input = letterField.getText().toLowerCase().trim();
        letterField.clear();

        if (input.isEmpty()) return;

        char guess = input.charAt(0);
        if (guessedLetters.contains(guess)) return;

        guessedLetters.add(guess);
        boolean found = false;

        for (int i = 0; i < currentWord.word.length(); i++) {
            if (currentWord.word.charAt(i) == guess) {
                hiddenWord[i] = guess;
                found = true;
            }
        }

        if (!found) attempts--;

        if (!String.valueOf(hiddenWord).contains("_")) {
            totalScore += 10;
            saveScore(10);
            loadNextWord();
        } else if (attempts <= 0) {
            totalScore -= 5;
            saveScore(-5);
            loadNextWord();
        }

        updateUI();
    }

    // ---------- UI UPDATE ----------
    private void updateUI() {
        wordLabel.setText("Word: " + String.valueOf(hiddenWord));
        hintLabel.setText("Hint: " + currentWord.hint);
        attemptsLabel.setText("Attempts Left: " + attempts);
        scoreLabel.setText("Score: " + totalScore);
    }

    // ---------- DATABASE ----------
    private void connectDatabase() {
        try {
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
        } catch (SQLException e) {
            showAlert("Database Error", "Connection failed!");
        }
    }

    private WordInfo getRandomWord() throws SQLException {
        String sql =
                "SELECT id, word, hint FROM words WHERE difficulty=? ORDER BY RAND()";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, difficulty);
            ResultSet rs = ps.executeQuery();

            Set<Integer> used = usedWordIdsMap.get(difficulty);
            while (rs.next()) {
                int id = rs.getInt("id");
                if (!used.contains(id)) {
                    used.add(id);
                    return new WordInfo(id,
                            rs.getString("word"),
                            rs.getString("hint"));
                }
            }
        }
        return null;
    }

    private void saveScore(int score) {
        try {
            String sql =
                    "INSERT INTO scores (player_name, score, difficulty) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, playerName);
                ps.setInt(2, score);
                ps.setString(3, difficulty);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException ignored) {}
    }

    // ---------- UTIL ----------
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // ---------- INNER CLASS ----------
    static class WordInfo {
        int id;
        String word;
        String hint;

        WordInfo(int id, String word, String hint) {
            this.id = id;
            this.word = word.toLowerCase();
            this.hint = hint;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
