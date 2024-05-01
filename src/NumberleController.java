// NumberleController.java
public class NumberleController {
    private INumberleModel model; // 游戏模型
    private NumberleView view; // 游戏视图

    public NumberleController(INumberleModel model) {
        this.model = model; // 设置游戏模型
    }

    public void setView(NumberleView view) {
        this.view = view;  // 设置游戏视图
    }

    public void processInput(String input) {
        model.processInput(input); // 处理用户输入
    }

    public boolean isGameOver() {
        return model.isGameOver(); // 检查游戏是否结束
    }

    public boolean isGameWon() {
        return model.isGameWon(); // 检查游戏是否获胜
    }


    public String getTargetWord() {
        return model.getTargetNumber(); // 获取目标数字
    }

    public StringBuilder getCurrentGuess() {

        return model.getCurrentGuess();  // 获取当前猜测
    }

    public int getRemainingAttempts() {
        return model.getRemainingAttempts(); // 获取剩余尝试次数
    }

    public void startNewGame() {
        model.startNewGame(); // 开始新游戏
    }


}