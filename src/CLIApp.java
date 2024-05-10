import java.util.Map;
import java.util.Scanner;

public class CLIApp {
    public static void main(String[] args) {
        INumberleModel model = new NumberleModel();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Numberle!");
        model.initialize();


        while (!model.isGameOver()) {
            if (model.getFlag3()) {
                model.startNewGame();
            }


            System.out.print("Enter your guess: ");
            String input = scanner.nextLine();

            if (!model.processInput(input) && (model.getFlag1())) {
                System.out.println("Invalid input!");
                continue;
            }

            System.out.println("Remaining attempts: " + model.getRemainingAttempts());

            printAvailableCharacters(model.getCharacterColorMap());
        }

        // 游戏结束，显示最终消息
        if (model.isGameWon()) {
            System.out.println("Congratulations! You've won the game!");
        } else {
            System.out.println("Game over! You've used all attempts.");
            System.out.println("The correct answer was: " + model.getTargetNumber());
        }

        scanner.close();
    }
    // 打印可用数字或符号
    private static void printAvailableCharacters(Map<String, Integer> characterColorMap) {
        System.out.println("Available characters:");
        String allCharacters = "123456789+-*/=";
        System.out.print("White: ");
        for (char c : allCharacters.toCharArray()) {
            if (!characterColorMap.containsKey(String.valueOf(c))) {
                System.out.print(c + " ");
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

