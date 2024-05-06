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

    boolean flag1 = true; // 标志位1
    boolean flag2 = true; // 标志位2
    boolean flag3 = true; // 标志位3
    String defaultNumber = "2+3*2=8"; // 默认数学等式

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
        currentGuess = new StringBuilder(input); // 更新当前猜测为用户输入
        try {
            if (input.length() != 7 // 如果输入长度不为7
                    || !input.contains("=") // 或不包含等号
                    || !input.matches("[0-9+\\-×÷*/=]+")) { // 或包含非法字符
                if (flag1){ // 如果标志位1为真
                    return false; // 返回输入无效
                }
            }
            if (!checkGuessValid() && flag1) { // 如果猜测无效且标志位1为真
                return false; // 返回输入无效
            }
        } catch (NumberFormatException e) {
            return false; // 返回输入无效
        }

        remainingAttempts--; // 剩余尝试次数减一
        if (input.equals(targetNumber)) { // 如果猜测等于目标数字
            gameWon = true; // 设置游戏胜利状态为真
        }
        setChanged(); // 设置数据已更改
        notifyObservers(); // 通知观察者
        return true; // 返回输入有效
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
        String[] parts = input.split("(?=[+\\-×÷*/])|(?<=[+\\-×÷*/])"); // 将表达式按运算符分割成部分
        double result = 0; // 初始化结果为0
        Stack<String> stack = new Stack<>(); // 创建字符串栈
        for (int i = 0; i < parts.length; i++) { // 遍历表达式的部分
            String part = parts[i]; // 获取当前部分
            assert part != null && !part.isEmpty();
            if (part.matches("\\d+") || part.equals("+") || part.equals("-")) { // 如果是数字或者加减号
                stack.push(part); // 入栈
            } else { // 如果是乘除号
                double b = Double.parseDouble(stack.pop()); // 弹出栈顶元素作为第二操作数
                assert (parts[i + 1] != null && parts[i + 1].matches("\\d+"));
                double a = Double.parseDouble(parts[i + 1]); // 获取下一个数字作为第一操作数
                i++;
                if (part.equals("×") || part.equals("*") || part.equals("x")) { // 如果是乘法
                    stack.push(String.valueOf(a * b)); // 计算并入栈
                } else if (part.equals("÷") || part.equals("/")) { // 如果是除法
                    stack.push(String.valueOf(b / a)); // 计算并入栈
                }
            }
        }

        for (int i = 0; i < stack.size(); i++) { // 遍历栈中的元素
            String s = stack.get(i); // 获取当前元素
            if (s.equals("+") || s.equals("-")) { // 如果是加减号
                assert i > 0 && i < stack.size() - 1;
                double b = Double.parseDouble(stack.get(i + 1)); // 获取下一个数字作为第二操作数
                if (s.equals("+")) { // 如果是加号
                    result = result + b; // 执行加法
                } else { // 如果是减号
                    result = result - b; // 执行减法
                }
                i++;
            } else { // 如果是数字
                assert s.matches("\\d+");
                result = Double.parseDouble(s); // 解析数字并赋值给结果
            }
        }
        return result; // 返回结果
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
            return; // 在错误的情况下提前返回
        }
        if (!answers.isEmpty()) {
            // 如果列表不为空，随机选择一个答案作为目标数字
            Random rand = new Random();
            targetNumber = answers.get(rand.nextInt(answers.size()));
        } else {
            // 如果没有找到答案，可以设置一个默认值或者抛出一个异常
            System.err.println("No answers found in the file.");
        }
    }
}
