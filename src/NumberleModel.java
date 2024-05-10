import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * NumberleModel 类是游戏的模型，负责处理游戏逻辑和数据。
 */
public class NumberleModel extends Observable implements INumberleModel {
    private String targetNumber;
    private StringBuilder currentGuess;
    private int remainingAttempts;
    private boolean gameWon;
    private int[] colorState;
    private Map<String, Integer> charColorMap;
    private boolean flag1 = true;
    private boolean flag2 = false;
    private boolean flag3 = false;
    private final String defaultTarget = "1+1+1=3";

    /**
     * NumberleModel 类的构造函数，初始化游戏模型。
     */
    public NumberleModel() {
        loadAnswerFromFile();
    }

    /**
     * 初始化游戏。
     */
    @Override
    public void initialize() {
        loadAnswerFromFile();
        currentGuess = new StringBuilder("       "); // 初始化当前猜测为空白
        remainingAttempts = MAX_ATTEMPTS; // 初始化剩余尝试次数
        gameWon = false; // 初始化游戏胜利状态为假
        if (flag3) {
            targetNumber = defaultTarget;
        }
        if (flag2) {
            System.out.println("The answer is:" + targetNumber);
        }
        setChanged(); // 设置数据已更改
        notifyObservers(); // 通知观察者
    }



    /**
     * 处理用户输入。
     *
     * @param expression 用户输入的字符串
     * @return 输入是否有效
     */
    @Override
    public boolean processInput(String expression) {
        currentGuess = new StringBuilder(expression);
        boolean validInput = isValidInput(expression);
        boolean validGuess = validInput && checkGuessValid();

        // 根据条件进行处理
        if (!validInput || (flag1 && !validGuess)) {
            return false;
        }

        setColorState();
        setColorStateMap(expression, targetNumber);

        remainingAttempts--;
        if (expression.equals(targetNumber)) {
            gameWon = true;
        }
        setChanged();
        notifyObservers();
        return true;
    }

    /**
     * 检查猜测是否有效。
     *
     * @return 猜测是否有效
     */
    private boolean checkGuessValid() {
        String[] parts = currentGuess.toString().split("="); // 将当前猜测按等号分割成两部分
        if (parts.length != 2) { // 如果分割后不是两部分
            return false; // 返回无效
        }
        String left = parts[0];
        String right = parts[1];

        try {
            double result = calculateResult(left);
            return result == calculateResult(right);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 计算表达式的结果。
     *

     * @return 表达式的结果
     */
    private double calculateResult(String expression) {
        String[] parts = expression.split("(?=[+\\-×÷*/])|(?<=[+\\-×÷*/])");
        double result = 0;
        Stack<String> stack = new Stack<>();
        for (int i = 0; i < parts.length; i++) {
            String currentPart = parts[i];
            assert currentPart != null && !currentPart.isEmpty();
            switch (currentPart) {
                case "*", "/" -> {
                    double b = Double.parseDouble(stack.pop());
                    assert (parts[i + 1] != null && parts[i + 1].matches("\\d+"));
                    double a = Double.parseDouble(parts[i + 1]);
                    i++;
                    stack.push(currentPart.equals("*") ? String.valueOf(a * b) : String.valueOf(b / a));
                }
                default -> stack.push(currentPart);
            }
        }

        for (int i = 0; i < stack.size(); i++) {
            String currentStackItem = stack.get(i);
            switch (currentStackItem) {
                case "+", "-" -> {
                    double nextStackItem = Double.parseDouble(stack.get(i + 1));
                    result = currentStackItem.equals("+") ? result + nextStackItem : result - nextStackItem;
                    i++;
                }
                default -> {
                    assert currentStackItem.matches("\\d+");
                    result = Double.parseDouble(currentStackItem);
                }
            }
        }
        return result;
    }


    /**
     * 判断游戏是否结束。
     *
     * @return 游戏是否结束
     */
    @Override
    public boolean isGameOver() {
        return remainingAttempts <= 0 || gameWon; // 剩余尝试次数为0或游戏胜利时游戏结束
    }

    /**
     * 判断游戏是否胜利。
     *
     * @return 游戏是否胜利
     */
    @Override
    public boolean isGameWon() {
        return gameWon; // 返回游戏胜利状态
    }

    /**
     * 获取目标数字。
     *
     * @return 目标数字
     */
    @Override
    public String getTargetNumber() {
        return targetNumber; // 返回目标数字
    }

    /**
     * 获取当前猜测。
     *
     * @return 当前猜测
     */
    @Override
    public StringBuilder getCurrentGuess() {
        return currentGuess; // 返回当前猜测
    }

    /**
     * 获取剩余尝试次数。
     *
     * @return 剩余尝试次数
     */
    @Override
    public int getRemainingAttempts() {
        return remainingAttempts; // 返回剩余尝试次数
    }

    /**
     * 开始新游戏。
     */
    @Override
    public void startNewGame() {
        initialize(); // 调用初始化方法
    }

    private void loadAnswerFromFile() {
        List<String> targetNumberList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("equations.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                targetNumberList.add(line.trim()); // 将答案添加到列表中
            }
        } catch (IOException e) {
            System.err.println("Error reading the answers file: " + e.getMessage());
            return;
        }
        if (!targetNumberList.isEmpty()) {
            Random rand = new Random();
            targetNumber = targetNumberList.get(rand.nextInt(targetNumberList.size()));
        } else {
            System.err.println("No answers found in the file.");
        }
    }


    private void setColorState() {
        colorState = new int[currentGuess.length()];
        for (int i = 0; i < currentGuess.length(); i++) {
            char c = currentGuess.charAt(i);
            if (c == targetNumber.charAt(i)) {
                colorState[i] = 1;
            } else if (targetNumber.contains(String.valueOf(c))) {
                colorState[i] = 2;
            } else {
                colorState[i] = 3;
            }
        }
    }

    private void setColorStateMap(String currentGuess, String targetNumber) {
        colorState = new int[currentGuess.length()];
        charColorMap = new HashMap<>();
        for (int i = 0; i < currentGuess.length(); i++) {
            char c = currentGuess.charAt(i);
            if (c == targetNumber.charAt(i)) {
                colorState[i] = 1;
            } else if (targetNumber.contains(String.valueOf(c))) {
                colorState[i] = 2;
            } else {
                colorState[i] = 3;
            }
            // colorState[i] = colorIndex;
            charColorMap.put(String.valueOf(c), colorState[i]);
        }
    }

    public int[] getColorState() {
        return colorState;
    }

    public Map<String, Integer> getCharacterColorMap() {
        return charColorMap;
    }

    private boolean isValidInput(String input) {
        return input.length() == 7 && input.contains("=") && input.matches("[0-9+\\-×÷*/=]+");
    }

    // 获取标志状态的方法
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
