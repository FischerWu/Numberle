/**
 * @author Fischer
 * @create 2024-03-18 17:11
 */
public class CLIApp {
    public static void main(String[] args) {
        INumberleModel model = new NumberleModel(); // 创建游戏模型
        model.initialize();
        System.out.println(model.getTargetNumber());
        System.out.println(model.getCurrentGuess());
    }
}
