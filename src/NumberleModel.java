import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class NumberleModel extends Observable implements INumberleModel {
    private String targetNumber;
    private StringBuilder currentGuess;
    private int remainingAttempts;
    private boolean gameWon;
    private int[] colorState;
    private Map<String, Integer> charColorMap;
    // Set Invalid Equation
    private boolean flag1 = true;
    // Set Show Target Equation
    private boolean flag2 = false;
    // Set Default Equation
    private boolean flag3 = true;
    private final String defaultTarget = "6*1-3=3";



    /*@ Initializes or resets the model to start a new game.
    @ invariant invariant();
    @ invariant remainingAttempts >= 0;
    @ requires fileReader != null && !targetNumberList.isEmpty();
    @ requires MAX_ATTEMPTS = 6;
    @ ensures targetNumber != null;
    @ ensures currentGuess != null;
    @ ensures remainingAttempts == MAX_ATTEMPTS;
    @ ensures gameWon == false;
    @*/
    @Override
    public void initialize() {
        assert MAX_ATTEMPTS == 6 : "MAX_ATTEMPTS must be 6";

        loadTargetNumber();
        currentGuess = new StringBuilder("       ");
        remainingAttempts = MAX_ATTEMPTS;
        gameWon = false;


        if (flag3) {
            targetNumber = defaultTarget;
        }
        if (flag2) {
            assert targetNumber != null : "targetNumber must not be null to print";
            System.out.println("The targetNumber is:" + targetNumber);
        }
        setChanged();
        notifyObservers();
    }



    /* Processes the user input expression.
    @ invariant invariant();
    @ requires expression is String;
    @ requires expression != null;
    @ ensures isValidInput(expression) && checkValid();
    @ param expression. The user input expression to process.
    @ return boolean. True if the input is valid and the guess is correct, false otherwise.
    @*/
    @Override
    public boolean processInput(String expression) {
        assert expression != null : "Input expression must not be null";

        currentGuess = new StringBuilder(expression);
        boolean validInput = isValidInput(expression);
        boolean validGuess = validInput && checkValid();

        if (!validGuess && flag1) {
            return false;
        }

        setColorState();
        setColorStateMap(expression, targetNumber);

        remainingAttempts--;
        assert remainingAttempts >= 0 : "remainingAttempts must not be negative";
        if (expression.equals(targetNumber)) {
            gameWon = true;
        }
        setChanged();
        notifyObservers();
        return true;
    }

    private boolean checkValid() {
        String[] parts = currentGuess.toString().split("=");
        // If the guess does not contain exactly one equals sign, return false
        if (parts.length != 2) {
            return false;
        }
        String left = parts[0];
        String right = parts[1];

        try {
            // Calculate the results of left and right expressions and compare
            double result = calculateResult(left);
            return result == calculateResult(right);
        } catch (Exception e) {
            return false;
        }
    }


    // Calculate the result of a mathematical expression
    private double calculateResult(String expression) {
        Stack<Double> stack = new Stack<>();
        char[] chars = expression.toCharArray();
        double number = 0;
        char operator = '+';
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isDigit(c)) {
                number = number * 10 + (c - '0');
            }
            if ((!Character.isDigit(c) && c != ' ') || i == chars.length - 1) {
                switch (operator) {
                    case '+' -> stack.push(number);
                    case '-' -> stack.push(-number);
                    case '*' -> stack.push(stack.pop() * number);
                    case '/' -> stack.push(stack.pop() / number);
                }
                operator = c;
                number = 0;
            }
        }
        double result = 0;
        while (!stack.isEmpty()) {
            result += stack.pop();
        }
        return result;
    }



    @Override
    public boolean isGameOver() {
        return remainingAttempts <= 0 || gameWon;
    }


    @Override
    public boolean isGameWon() {
        return gameWon;
    }


    @Override
    public String getTargetNumber() {
        return targetNumber;
    }


    @Override
    public StringBuilder getCurrentGuess() {
        return currentGuess;
    }


    @Override
    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    @Override
    public void startNewGame() {
        initialize();
    }

    /**
     * Load the target number from the file and randomly select one as the target number for the game.
     */
    private void loadTargetNumber() {
        Set<String> targetNumberSet = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("equations.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                targetNumberSet.add(trimmedLine);
            }
        } catch (IOException e) {
            System.err.println("Error reading the answers file: " + e.getMessage());
            return;
        }

        // Randomly select a target number from the list
        List<String> targetNumberList = new ArrayList<>(targetNumberSet);
        if (!targetNumberList.isEmpty()) {
            Random rand = new Random();
            targetNumber = targetNumberList.get(rand.nextInt(targetNumberList.size()));
        } else {
            System.err.println("No unique answers found in the file.");
        }
    }



    /**
     * Set the color state of each character of the current guess with the target digit.
     * The color state is used to indicate the accuracy of the guess:
     * 1 means the character is in the correct position, set Green.
     * 2 indicates that the character is present in the target digit, but in an incorrect position, set Orange.
     * 3 indicates that the character is neither in the target digit nor in a matching position, set Gray.
     */
    private void setColorState() {
        colorState = new int[currentGuess.length()];
        HashSet<Character> processedChars = new HashSet<>();
        int i = 0;

        while (i < currentGuess.length()) {
            char c = currentGuess.charAt(i);

            if (processedChars.contains(c)) {
                i++;
                continue;
            }

            if (c == targetNumber.charAt(i)) {
                colorState[i] = 1; // Green
            } else if (targetNumber.contains(String.valueOf(c))) {
                colorState[i] = 2; // Orange
            } else {
                colorState[i] = 3; // Gray
            }
            processedChars.add(c);
            i++;
        }
    }



    /*
     * Set the current guess color state map, which maps each character to a color state.
     * This method is similar to setColorState, but also updates a mapping that maps the character to its color state.
     */
    private void setColorStateMap(String currentGuess, String targetNumber) {
        colorState = new int[currentGuess.length()];
        charColorMap = new HashMap<>();
        HashSet<Character> targetChars = new HashSet<>();
        for (char c : targetNumber.toCharArray()) {
            targetChars.add(c);
        }

        int i = 0;
        while (i < currentGuess.length()) {
            char c = currentGuess.charAt(i);
            if (c == targetNumber.charAt(i)) {
                colorState[i] = 1; // Green
            } else if (targetChars.contains(c)) {
                colorState[i] = 2; // Orange
            } else {
                colorState[i] = 3; // Gray
            }
            charColorMap.put(String.valueOf(c), colorState[i]);
            i++;
        }
    }


    public int[] getColorState() {
        return colorState;
    }

    public Map<String, Integer> getCharColorMap() {
        return charColorMap;
    }

    private boolean isValidInput(String input) {
        return input.length() == 7 &&
                input.contains("=") &&
                input.matches("[0-9+\\-*/=]+");
    }

    public boolean getFlag1() {
        return flag1;
    }


    public boolean getFlag2() {
        return flag2;
    }


    public boolean getFlag3() {
        return flag3;
    }

    @Override
    public void setFlag1() {
        this.flag1 = !flag1;
    }

    @Override
    public void setFlag2() {
        this.flag2 = !flag2;
    }

    @Override
    public void setFlag3() {
        this.flag3 = !flag3;
    }

}
