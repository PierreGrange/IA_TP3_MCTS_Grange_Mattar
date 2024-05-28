package player.ia;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IADuelParser {

    public static void main(String[] args) {
        String filePath = "duels.txt";
        try {
            List<List<Object>> result = parseAIFile(filePath);
            for (List<Object> entry : result) {
                System.out.println(entry);
            }
            // Exemple d'appel pour incrémenter les victoires
            updateWins(filePath, 1, 1);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<List<Object>> parseAIFile(String filePath) throws IOException {
        List<List<Object>> result = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            if (parts.length != 7) continue;  // Skip malformed lines

            List<Object> entry = new ArrayList<>();

            // Parse player types
            entry.add(parsePlayerType(parts[0]));  // Type for player 1
            entry.add(parseEvaluationFunctions(parts[1]));  // Evaluation functions for player 1
            entry.add(parsePlayerType(parts[2]));  // Type for player 2
            entry.add(parseEvaluationFunctions(parts[3]));  // Evaluation functions for player 2

            // Parse number of games and wins
            entry.add(Integer.parseInt(parts[4]));  // Number of games to play with these settings
            entry.add(Integer.parseInt(parts[5]));  // Number of wins for player 1
            entry.add(Integer.parseInt(parts[6]));  // Number of wins for player 2

            result.add(entry);
        }

        reader.close();
        return result;
    }

    private static int parsePlayerType(String playerType) {
        switch (playerType.toLowerCase()) {
            case "minimax":
                return 1;
            case "alphabeta":
                return 2;
            case "mcts":
                return 3;
            default:
                throw new IllegalArgumentException("Unknown player type: " + playerType);
        }
    }

    private static List<String> parseEvaluationFunctions(String evalFunctions) {
        if (evalFunctions.length() != 5) {
            throw new IllegalArgumentException("Evaluation functions string must be 5 characters long.");
        }

        List<String> functions = Arrays.asList("mobility", "discDiff", "corner", "boardMap", "parity");
        List<String> usedFunctions = new ArrayList<>();

        for (int i = 0; i < evalFunctions.length(); i++) {
            if (evalFunctions.charAt(i) == '1') {
                usedFunctions.add(functions.get(i));
            }
        }

        return usedFunctions;
    }

    public static void updateWins(String filePath, int lineNumber, int winner) throws IOException {
        File inputFile = new File(filePath);
        File tempFile = new File("tempFile.txt");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String currentLine;
        int currentLineNumber = 1;

        while ((currentLine = reader.readLine()) != null) {
            if (currentLineNumber == lineNumber) {
                String[] parts = currentLine.split(" ");
                if (parts.length == 7) {
                    int player1Wins = Integer.parseInt(parts[5]);
                    int player2Wins = Integer.parseInt(parts[6]);

                    if (winner == 1) {
                        player1Wins++;
                    } else if (winner == 2) {
                        player2Wins++;
                    }

                    parts[5] = String.valueOf(player1Wins);
                    parts[6] = String.valueOf(player2Wins);
                }

                currentLine = String.join(" ", parts);
            }

            writer.write(currentLine + System.lineSeparator());
            currentLineNumber++;
        }

        writer.close();
        reader.close();

        // Replace the original file with the updated one
        if (!inputFile.delete()) {
            System.out.println("Could not delete original file");
            return;
        }
        if (!tempFile.renameTo(inputFile)) {
            System.out.println("Could not rename temp file");
        }
    }
}
