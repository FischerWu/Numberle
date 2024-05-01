// NumberleModel.java

import java.io.BufferedReader;
import java.util.Observable;
import java.util.Random;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NumberleModel extends Observable implements INumberleModel {
    private String targetNumber; // 玩家需要猜测的数字
    private StringBuilder currentGuess; // 玩家当前猜测的数字
    private int remainingAttempts; // 剩余尝试次数
    private boolean gameWon; // 标志，指示游戏是否获胜

    /**
     * 初始化游戏状态，生成随机目标数字并重置其他变量。
     */
    @Override
    public void initialize() {
        loadAnswerFromFile();
        currentGuess = new StringBuilder("       ");// 初始化当前猜测
        remainingAttempts = MAX_ATTEMPTS;// 将剩余尝试次数设置为允许的最大值
        gameWon = false; // 重置游戏获胜标志
        setChanged(); // 表示状态已更改
        notifyObservers(); // 通知观察者（例如视图）状态已更改
    }

    /**
     * 处理玩家输入（猜测）。减少剩余尝试次数并通知观察者。
     * @param input 玩家输入（猜测）
     * @return 现在总是返回 true（占位符）
     */
    @Override
    public boolean processInput(String input) {

        currentGuess.setLength(0);
        currentGuess.append(input);
        remainingAttempts--; // 减少剩余尝试次数
        setChanged(); // 表示状态已更改
        notifyObservers(); // 通知观察者状态已更改

        return true; // 占位符返回值
    }

    /**
     * 检查游戏是否结束（由于没有剩余尝试次数或游戏获胜）。
     * @return 如果游戏结束，则返回 true，否则返回 false
     */
    @Override
    public boolean isGameOver() {
        return remainingAttempts <= 0 || gameWon;
    }

    /**
     * 检查游戏是否获胜。
     * @return 如果游戏获胜，则返回 true，否则返回 false
     */
    @Override
    public boolean isGameWon() {
        String checkNumber = currentGuess.toString();
        gameWon = targetNumber.equals(checkNumber);
        return gameWon;
    }

    /**
     * 获取玩家需要猜测的目标数字。
     * @return 目标数字
     */
    @Override
    public String getTargetNumber() {
        return targetNumber;
    }

    /**
     * 获取玩家当前的猜测。
     * @return 当前猜测
     */
    @Override
    public StringBuilder getCurrentGuess() {
        return currentGuess;
    }

    
    /**
     * 获取剩余尝试次数。
     * @return 剩余尝试次数
     */
    @Override
    public int getRemainingAttempts() {
        return remainingAttempts;
    }


    /**
     * 开始新游戏，重新初始化游戏状态。
     */
    @Override
    public void startNewGame() {
        initialize();
    }

    // 设置答案来源

    private static final String ANSWERS_FILE_PATH = "/Users/fischerwu/IdeaProjects/Numberle/src/equations.txt"; // 答案文件的路径

    /**
     * 从文件中加载答案并设置目标数字。
     */
    private void loadAnswerFromFile() {
        List<String> answers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ANSWERS_FILE_PATH))) {
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
            targetNumber = "8888888";
        } else {
            // 如果没有找到答案，可以设置一个默认值或者抛出一个异常
            System.err.println("No answers found in the file.");
        }
    }

    private void validator() {
        String checkNumber = currentGuess.toString();
        gameWon = targetNumber.equals(checkNumber);
    }




}
