import javax.swing.*;

public class GUIApp {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        createAndShowGUI(); // 创建并显示游戏界面
                    }
                }
        );
    }

    public static void createAndShowGUI() {
        INumberleModel model = new NumberleModel(); // 创建游戏模型
        NumberleController controller = new NumberleController(model); // 创建游戏控制器
        NumberleView view = new NumberleView(model, controller); // 创建游戏视图
    }
}