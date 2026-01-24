import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class GuessTheWordGame {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/word_game?serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Akib@akib31";

    private JFrame frame;
    private JTextField inputField;
    private JLabel titleLabel, wordLabel, hintLabel, attemptsLabel, scoreLabel, wordCountLabel, devLabel;
    private JButton guessButton, playAgainButton, exitButton;
    private JComboBox<String> difficultyBox;

    private Connection conn;
    private String playerName;
    private WordInfo currentWord;
    private char[] hiddenWord;
    private Set<Character> guessedLetters;
    private int attempts, totalScore, currentWordIndex;
    private final int WORDS_PER_LEVEL = 5;

    private final Map<String, Set<Integer>> usedWordIdsMap = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuessTheWordGame().createUI());
    }

    public void createUI() {
        frame = new JFrame("Guess The Word Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 550);
        frame.setLayout(new GridBagLayout());

        Color bgColor = UIManager.getColor("OptionPane.background");
        frame.getContentPane().setBackground(bgColor);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);

        titleLabel = new JLabel("Guess The Word Game", SwingConstants.CENTER);
        titleLabel.setForeground(Color.MAGENTA);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 32));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        frame.add(titleLabel, c);

        playerName = JOptionPane.showInputDialog(frame, "Enter your name:");
        if (playerName == null || playerName.isEmpty()) playerName = "Player";

        String[] difficulties = {"EASY", "MEDIUM", "HARD"};
        difficultyBox = new JComboBox<>(difficulties);
        difficultyBox.addActionListener(e -> resetLevel());
        c.gridx = 0; c.gridy = 1; c.gridwidth = 1;
        JLabel diffLabel = new JLabel("Select difficulty:");
        diffLabel.setForeground(Color.BLACK);
        frame.add(diffLabel, c);
        c.gridx = 1;
        frame.add(difficultyBox, c);

        wordCountLabel = new JLabel("Word: 0 / " + WORDS_PER_LEVEL, SwingConstants.CENTER);
        wordCountLabel.setForeground(Color.PINK);
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        frame.add(wordCountLabel, c);

        hintLabel = new JLabel("Hint: ", SwingConstants.CENTER);
        hintLabel.setForeground(Color.BLUE);
        c.gridy = 3;
        frame.add(hintLabel, c);

        wordLabel = new JLabel("Word: ", SwingConstants.CENTER);
        wordLabel.setForeground(Color.CYAN.darker());
        wordLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        c.gridy = 4;
        frame.add(wordLabel, c);

        attemptsLabel = new JLabel("Attempts: ", SwingConstants.CENTER);
        attemptsLabel.setForeground(Color.RED.darker());
        c.gridy = 5;
        frame.add(attemptsLabel, c);

        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreLabel.setForeground(Color.GREEN.darker());
        c.gridy = 6;
        frame.add(scoreLabel, c);

        inputField = new JTextField(5);
        inputField.setFont(new Font("Monospaced", Font.BOLD, 24));
        c.gridy = 7; c.gridwidth = 1; c.gridx = 0;
        frame.add(inputField, c);

        guessButton = new JButton("Guess");
        guessButton.setBackground(new Color(0, 120, 215));
        guessButton.setForeground(Color.WHITE);
        c.gridx = 1;
        frame.add(guessButton, c);

        playAgainButton = new JButton("Play Again");
        playAgainButton.setBackground(new Color(0, 180, 0));
        playAgainButton.setForeground(Color.WHITE);
        playAgainButton.setEnabled(false);
        c.gridx = 0; c.gridy = 8; c.gridwidth = 2;
        frame.add(playAgainButton, c);

        exitButton = new JButton("Exit");
        exitButton.setBackground(new Color(180, 0, 0));
        exitButton.setForeground(Color.WHITE);
        c.gridy = 9;
        frame.add(exitButton, c);

        devLabel = new JLabel("Devs: Akib & Sahed", SwingConstants.CENTER);
        devLabel.setForeground(Color.GRAY.darker());
        devLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        c.gridy = 10;
        frame.add(devLabel, c);

        exitButton.addActionListener(e -> {
            try { if (conn != null && !conn.isClosed()) conn.close(); }
            catch (SQLException ex) { ex.printStackTrace(); }
            frame.dispose(); System.exit(0);
        });

        guessButton.addActionListener(e -> guessWord());
        playAgainButton.addActionListener(e -> resetLevel());

        try { conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS); }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database connection failed!");
            e.printStackTrace(); System.exit(0);
        }

        for (String diff : difficulties) usedWordIdsMap.put(diff, new HashSet<>());

        resetLevel();
        frame.setVisible(true);
    }

    private void resetLevel() {
        totalScore = 0;
        currentWordIndex = 0;
        for (String key : usedWordIdsMap.keySet()) usedWordIdsMap.get(key).clear();
        loadNextWord();
    }

    private void loadNextWord() {
        if (currentWordIndex >= WORDS_PER_LEVEL) {
            JOptionPane.showMessageDialog(frame, "Level completed! Total Score: " + totalScore);
            playAgainButton.setEnabled(true);
            inputField.setEditable(false);
            guessButton.setEnabled(false);
            return;
        }

        String difficulty = (String) difficultyBox.getSelectedItem();
        try { currentWord = getRandomWord(difficulty); }
        catch (SQLException e) { e.printStackTrace(); return; }

        if (currentWord == null) {
            JOptionPane.showMessageDialog(frame, "No more new words for this difficulty!");
            return;
        }

        hiddenWord = new char[currentWord.word.length()];
        Arrays.fill(hiddenWord, '_');
        guessedLetters = new HashSet<>();

        attempts = switch (difficulty) {
            case "EASY" -> currentWord.word.length() + 4;
            case "MEDIUM" -> currentWord.word.length() + 2;
            case "HARD" -> currentWord.word.length();
            default -> currentWord.word.length() + 3;
        };

        inputField.setText("");
        inputField.setEditable(true);
        guessButton.setEnabled(true);
        playAgainButton.setEnabled(false);
        updateUI();
    }

    private void updateUI() {
        //
        // Show length of the word here
        wordLabel.setText("Word: " + String.valueOf(hiddenWord) +
                "  (Length: " + currentWord.word.length() + ")");

        hintLabel.setText("Hint: " + currentWord.hint);
        attemptsLabel.setText("Attempts left: " + attempts);
        scoreLabel.setText("Score: " + totalScore);
        wordCountLabel.setText("Word: " + (currentWordIndex + 1) + " / " + WORDS_PER_LEVEL);
    }

    private void guessWord() {
        String text = inputField.getText().toLowerCase().trim();
        if (text.isEmpty()) { JOptionPane.showMessageDialog(frame, "Enter a letter."); return; }

        inputField.setText("");

        boolean found = false;
        for (int i = 0; i < currentWord.word.length(); i++) {
            if (currentWord.word.charAt(i) == text.charAt(0)) {
                hiddenWord[i] = text.charAt(0);
                found = true;
            }
        }

        if (!found) attempts--;

        if (String.valueOf(hiddenWord).equals(currentWord.word)) {
            totalScore += 10;
            JOptionPane.showMessageDialog(frame, "Correct! Word: " + currentWord.word);
            saveScore(10);
            currentWordIndex++;
            loadNextWord();
        } else if (attempts <= 0) {
            totalScore -= 5;
            JOptionPane.showMessageDialog(frame, "Failed! Word was: " + currentWord.word);
            saveScore(-5);
            currentWordIndex++;
            loadNextWord();
        }

        updateUI();
    }

    private void saveScore(int score) {
        try {
            String sql = "INSERT INTO scores (player_name, score, difficulty) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, playerName);
                ps.setInt(2, score);
                ps.setString(3, (String) difficultyBox.getSelectedItem());
                ps.executeUpdate();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private WordInfo getRandomWord(String difficulty) throws SQLException {
        String sql = "SELECT id, word, hint FROM words WHERE difficulty = ? ORDER BY RAND()";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, difficulty);
            ResultSet rs = ps.executeQuery();
            Set<Integer> usedWordIds = usedWordIdsMap.get(difficulty);
            while (rs.next()) {
                int id = rs.getInt("id");
                if (!usedWordIds.contains(id)) {
                    usedWordIds.add(id);
                    return new WordInfo(id, rs.getString("word"), rs.getString("hint"));
                }
            }
        }
        return null;
    }

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
}
