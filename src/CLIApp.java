import java.util.Map;
import java.util.Scanner;

public class CLIApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean playAgain = true;
        while (playAgain) {
            playGame();
            System.out.print("Do you want to play again? (yes/no): ");
            String choice = scanner.nextLine().toLowerCase();
            playAgain = choice.equals("yes");
        }

        System.out.println("Thank you for playing Numberle!");
        scanner.close();
    }

    private static void playGame() {
        INumberleModel model = new NumberleModel();
        model.initialize();
        System.out.println("Welcome to Numberle!");
        System.out.println("Remaining attempts: " + model.getRemainingAttempts());

        while (!model.isGameOver()) {
            if (model.getFlag3()) {
                model.startNewGame();
            }

            System.out.print("Enter your guess: ");
            String input = new Scanner(System.in).nextLine();

            if (!model.processInput(input) && (model.getFlag1())) {
                System.out.println("Invalid input! Please enter the correct equation");
                continue;
            }

            System.out.println("Remaining attempts: " + model.getRemainingAttempts());

            printAvailableCharacters(model.getCharacterColorMap());
        }

        if (model.isGameWon()) {
            System.out.println("Congratulations! You've won the game!");
        } else {
            System.out.println("Game over! You've used all attempts.");
            System.out.println("The correct answer was: " + model.getTargetNumber());
        }
    }

    private static void printAvailableCharacters(Map<String, Integer> characterColorMap) {
        System.out.println("Available characters:");
        String allChar = "123456789+-*/=";
        System.out.print("White: ");
        for (char key : allChar.toCharArray()) {
            if (!characterColorMap.containsKey(String.valueOf(key))) {
                System.out.print(key + " ");
            }
        }
        System.out.println();
        System.out.print("Green: ");
        characterColorMap.forEach((character, colorIndex) -> {
            if (colorIndex == 1) {
                System.out.print(character + " ");
            }
        });
        System.out.println();
        System.out.print("Orange: ");
        characterColorMap.forEach((character, colorIndex) -> {
            if (colorIndex == 2) {
                System.out.print(character + " ");
            }
        });
        System.out.println();
        System.out.print("Dark Gray: ");
        characterColorMap.forEach((character, colorIndex) -> {
            if (colorIndex == 3) {
                System.out.print(character + " ");
            }
        });
        System.out.println();
    }
}