import java.sql.*;
import java.util.*;

public class QuizGame {

    // Database configuration (encapsulated using private constants)
    private static final String JDBC_URL =
            "jdbc:mysql://localhost:3306/word_game?serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Akib@akib31";

    // Game state variables (encapsulation: private access)
    private Connection conn;
    private String playerName;
    private WordInfo currentWord;
    private char[] hiddenWord;
    private Set<Character> guessedLetters;
    private int attempts, totalScore, currentWordIndex;

    private final int WORDS_PER_LEVEL = 5;
    private final Map<String, Set<Integer>> usedWordIdsMap = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new QuizGame().startGame();
    }

    // Starts the game and manages database connection
    private void startGame() {
        try {
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
            return;
        }

        System.out.print("Enter your name: ");
        playerName = scanner.nextLine().trim();
        if (playerName.isEmpty()) playerName = "Player";

        boolean playAgain;
        do {
            String[] difficulties = {"EASY", "MEDIUM", "HARD"};
            for (String diff : difficulties) {
                usedWordIdsMap.put(diff, new HashSet<>());
            }

            String difficulty = chooseDifficulty(difficulties);
            resetLevel(difficulty);

            playAgain = askPlayAgain();
        } while (playAgain);

        System.out.println("Thanks for playing, " + playerName + "!");
        closeConnection();
    }

    // Allows player to select difficulty
    private String chooseDifficulty(String[] difficulties) {
        System.out.println("Select difficulty:");
        for (int i = 0; i < difficulties.length; i++) {
            System.out.println((i + 1) + ". " + difficulties[i]);
        }

        int choice = 0;
        while (choice < 1 || choice > difficulties.length) {
            System.out.print("Enter choice (1-" + difficulties.length + "): ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception ignored) {}
        }
        return difficulties[choice - 1];
    }

    // Resets score and loads words for a level
    private void resetLevel(String difficulty) {
        totalScore = 0;
        currentWordIndex = 0;
        usedWordIdsMap.get(difficulty).clear();

        while (currentWordIndex < WORDS_PER_LEVEL) {
            loadNextWord(difficulty);
        }

        System.out.println("Level completed! Total Score: " + totalScore);
    }

    // Loads a new word from database
    private void loadNextWord(String difficulty) {
        try {
            currentWord = getRandomWord(difficulty);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (currentWord == null) {
            System.out.println("No more new words for this difficulty!");
            currentWordIndex = WORDS_PER_LEVEL;
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

        System.out.println("\nWord " + (currentWordIndex + 1) +
                " / " + WORDS_PER_LEVEL);
        System.out.println("Hint: " + currentWord.hint);

        playCurrentWord(difficulty);
    }

    // Core gameplay logic
    private void playCurrentWord(String difficulty) {
        while (attempts > 0 && String.valueOf(hiddenWord).contains("_")) {
            System.out.println("Word: " + String.valueOf(hiddenWord));
            System.out.println("Attempts left: " + attempts);
            System.out.print("Enter a letter: ");

            String input = scanner.nextLine().toLowerCase().trim();
            if (input.isEmpty()) {
                System.out.println("Enter a valid letter.");
                continue;
            }

            char guess = input.charAt(0);
            if (guessedLetters.contains(guess)) {
                System.out.println("Already guessed!");
                continue;
            }
            guessedLetters.add(guess);

            boolean found = false;
            for (int i = 0; i < currentWord.word.length(); i++) {
                if (currentWord.word.charAt(i) == guess) {
                    hiddenWord[i] = guess;
                    found = true;
                }
            }

            if (!found) attempts--;

            if (String.valueOf(hiddenWord).equals(currentWord.word)) {
                totalScore += 10;
                System.out.println("Correct! Word: " + currentWord.word);
                saveScore(10, difficulty);
                break;
            } else if (attempts <= 0) {
                totalScore -= 5;
                System.out.println("Failed! Word was: " + currentWord.word);
                saveScore(-5, difficulty);
                break;
            }
        }
        currentWordIndex++;
    }

    // Asks user if they want to play again
    private boolean askPlayAgain() {
        System.out.print("Do you want to play again? (y/n): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        return answer.equals("y") || answer.equals("yes");
    }

    // Saves score into database
    private void saveScore(int score, String difficulty) {
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

    // Fetches a random unused word
    private WordInfo getRandomWord(String difficulty) throws SQLException {
        String sql =
                "SELECT id, word, hint FROM words WHERE difficulty = ? ORDER BY RAND()";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, difficulty);
            ResultSet rs = ps.executeQuery();

            Set<Integer> usedWordIds = usedWordIdsMap.get(difficulty);
            while (rs.next()) {
                int id = rs.getInt("id");
                if (!usedWordIds.contains(id)) {
                    usedWordIds.add(id);
                    return new WordInfo(
                            id,
                            rs.getString("word"),
                            rs.getString("hint")
                    );
                }
            }
        }
        return null;
    }

    // Closes database connection safely
    private void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Inner class representing a word (data encapsulation)
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
