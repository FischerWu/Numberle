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
        System.out.println("You can enter your own equation, aiming to match the target equation");
        System.out.println("You have 6 attempts to guess the target equation");
        System.out.println("When calculating, you can use numbers (0-9) and arithmetic signs (+ - * / =)");
        System.out.println("You can enter 7 characters");
        System.out.println("If the number or sign is in the equation, but in the wrong place, it will be highlighted in orange");
        System.out.println("If it is in the exact place, then it will be highlighted in green");
        System.out.println("If there is no number or sign in the equation at all, the color will be gray");
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

            printAvailChar(model.getCharColorMap());
        }

        if (model.isGameWon()) {
            System.out.println("Congratulations! You've won the game!");
        } else {
            System.out.println("Game over! You've used all attempts.");
            System.out.println("The correct answer was: " + model.getTargetNumber());
        }
    }

    private static void printAvailChar(Map<String, Integer> charColorMap) {
        char[] allChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '*', '/', '='};

        for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
            String colorName = switch (colorIndex) {
                case 1 -> "Green";
                case 2 -> "Orange";
                case 3 -> "Gray";
                default -> "White";
            };
            System.out.println(colorName + " characters:");
            for (char key : allChar) {
                String character = String.valueOf(key);
                int charColorIndex = charColorMap.getOrDefault(character, 0);
                if (charColorIndex == colorIndex) {
                    System.out.print(character + " ");
                }
            }
            System.out.println();
        }
    }


}