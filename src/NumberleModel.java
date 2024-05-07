import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * NumberleModel 类是游戏的模型，负责处理游戏逻辑和数据。
 */
public class NumberleModel extends Observable implements INumberleModel {
    private String targetNumber; // 目标数字
    private StringBuilder currentGuess; // 当前猜测
    private int remainingAttempts; // 剩余尝试次数
    private boolean gameWon; // 游戏是否胜利
    private List<String> answers; // 存储数学等式的列表

    private int[] colorState;
    private Map<String, Integer> characterColorMap;
    private boolean showInvalidEquationError = true; // 第一个标志
    private boolean showTargetEquation = true; // 第二个标志
    private boolean randomizeEquation = true; // 第三个标志
    private String defaultAnswer = "1+1+1=3";

    /**
     * NumberleModel 类的构造函数，初始化游戏模型。
     */
    public NumberleModel() {
        loadAnswerFromFile("equations.txt");
    }


    /**
     * 初始化游戏。
     */
    @Override
    public void initialize() {
        loadAnswerFromFile("equations.txt");
        currentGuess = new StringBuilder("       "); // 初始化当前猜测为空白
        remainingAttempts = MAX_ATTEMPTS; // 初始化剩余尝试次数
        gameWon = false; // 初始化游戏胜利状态为假
        if (randomizeEquation) {
            targetNumber = defaultAnswer;
        }
        setChanged(); // 设置数据已更改
        notifyObservers(); // 通知观察者
    }

    /**
     * 处理用户输入。
     *
     * @param input 用户输入的字符串
     * @return 输入是否有效
     */
    @Override
    public boolean processInput(String input) {
        currentGuess = new StringBuilder(input);
        boolean validInput = isValidInput(input);
        boolean validGuess = validInput && checkGuessValid();

        // 根据条件进行处理
        if (!validInput || (showInvalidEquationError && !validGuess)) {
            JOptionPane.showMessageDialog(null, "Invalid input!");
            return false;
        }

        setColorState();
        setColorState(input,targetNumber);

        remainingAttempts--;
        if (input.equals(targetNumber)) {
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
            double result = getResult(left);
            return result == getResult(right);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 计算表达式的结果。
     *
     * @param input 表达式字符串
     * @return 表达式的结果
     */
    private double getResult(String input) {
        String[] parts = input.split("(?=[+\\-×÷*/])|(?<=[+\\-×÷*/])");
        double result = 0;
        Stack<String> stack = new Stack<>();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            assert part != null && !part.isEmpty();
            switch (part) {
                case "+":
                    stack.push(part);
                    break;
                case "-":
                    stack.push(part);
                    break;
                case "*":
                    double b = Double.parseDouble(stack.pop());
                    assert (parts[i + 1] != null && parts[i + 1].matches("\\d+"));
                    double a = Double.parseDouble(parts[i + 1]);
                    i++;
                    stack.push(String.valueOf(a * b));
                    break;
                case "/":
                    double bDiv = Double.parseDouble(stack.pop());
                    assert (parts[i + 1] != null && parts[i + 1].matches("\\d+"));
                    double aDiv = Double.parseDouble(parts[i + 1]);
                    i++;
                    stack.push(String.valueOf(bDiv / aDiv));
                    break;
                default:
                    stack.push(part);
                    break;
            }
        }

        for (int i = 0; i < stack.size(); i++) {
            String s = stack.get(i);
            switch (s) {
                case "+":
                    double bAdd = Double.parseDouble(stack.get(i + 1));
                    result += bAdd;
                    i++;
                    break;
                case "-":
                    double bSub = Double.parseDouble(stack.get(i + 1));
                    result -= bSub;
                    i++;
                    break;
                default:
                    assert s.matches("\\d+");
                    result = Double.parseDouble(s);
                    break;
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

    private void loadAnswerFromFile(String filePath) {
        answers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                answers.add(line.trim()); // 将答案添加到列表中
            }
        } catch (IOException e) {
            System.err.println("Error reading the answers file: " + e.getMessage());
            return;
        }
        if (!answers.isEmpty()) {
            Random rand = new Random();
            targetNumber = answers.get(rand.nextInt(answers.size()));
        } else {
            System.err.println("No answers found in the file.");
        }
    }


    private void setColorState() {
        colorState = new int[currentGuess.length()];
        for (int i = 0; i < currentGuess.length(); i++) {
            char c = currentGuess.charAt(i);
            if (c == targetNumber.charAt(i)) {
                colorState[i] = 1; // 绿色
            } else if (targetNumber.contains(String.valueOf(c))) {
                colorState[i] = 2; // 橙色
            } else {
                colorState[i] = 3; // 深灰色
            }
        }
    }

    private void setColorState(String currentGuess, String targetNumber) {
        // 初始化颜色状态数组
        colorState = new int[currentGuess.length()];
        // 初始化字符颜色映射
        characterColorMap = new HashMap<>();
        // 遍历当前猜测的每个字符
        for (int i = 0; i < currentGuess.length(); i++) {
            char c = currentGuess.charAt(i);
            int colorIndex = 0; // 初始化颜色索引
            // 检查字符是否与目标数字的相应位置匹配
            if (c == targetNumber.charAt(i)) {
                colorIndex = 1; // 绿色
            } else if (targetNumber.contains(String.valueOf(c))) {
                colorIndex = 2; // 橙色
            } else {
                colorIndex = 3; // 深灰色
            }
            // 更新颜色状态数组
            colorState[i] = colorIndex;
            // 更新或添加字符颜色映射
            characterColorMap.put(String.valueOf(c), colorIndex);
        }
    }

    public int[] getColorState() {
        return colorState;
    }

    public Map<String, Integer> getCharacterColorMap() {
        return characterColorMap;
    }

    private boolean isValidInput(String input) {
        return input.length() == 7 && input.contains("=") && input.matches("[0-9+\\-×÷*/=]+");
    }

    // 获取标志状态的方法
    public boolean isShowInvalidEquationError() {
        return showInvalidEquationError;
    }

    public void setShowInvalidEquationError(boolean showInvalidEquationError) {
        this.showInvalidEquationError = showInvalidEquationError;
    }

    public boolean isShowTargetEquation() {
        return showTargetEquation;
    }

    public void setShowTargetEquation(boolean showTargetEquation) {
        this.showTargetEquation = showTargetEquation;
    }

    public boolean isRandomizeEquation() {
        return randomizeEquation;
    }

    public void setRandomizeEquation(boolean randomizeEquation) {
        this.randomizeEquation = randomizeEquation;
    }



}
