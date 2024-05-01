// NumberleView.java

import javax.swing.*;
import java.awt.*;
import java.util.Observer;

/**
 * NumberleView 类负责显示游戏界面并与用户交互。
 */
public class NumberleView implements Observer {
    private final INumberleModel model; // 游戏模型
    private final NumberleController controller; // 游戏控制器
    private final JFrame frame = new JFrame("Numberle"); // 游戏窗口
    private int currentInputIndex = 0;
    private final JTextField[] inputFields = new JTextField[7];
    private final JLabel attemptsLabel = new JLabel("Attempts remaining: "); // 剩余尝试次数标签
    private JLabel gameStatusLabel = new JLabel("The gameStatus: "); // 用于显示游戏状态的标签



    /**
     * 构造函数，初始化游戏视图。
     * @param model 游戏模型
     * @param controller 游戏控制器
     */
    public NumberleView(INumberleModel model, NumberleController controller) {
        this.controller = controller; // 设置游戏控制器
        this.model = model; // 设置游戏模型
        this.controller.startNewGame(); // 开始新游戏
        ((NumberleModel) this.model).addObserver(this); // 将视图添加为模型的观察者
        initializeFrame(); // 初始化游戏窗口
        this.controller.setView(this); // 设置视图
        update((NumberleModel) this.model, null); // 更新视图
    }

    /**
     * 初始化游戏窗口。
     */
    public void initializeFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭操作
        frame.setSize(600, 200); // 设置窗口大小
        frame.setLayout(new BorderLayout()); // 设置布局

        JPanel center = new JPanel(); // 中心面板
        center.setLayout(new BoxLayout(center, BoxLayout.X_AXIS)); // 设置布局
        center.add(new JPanel());

        JPanel inputPanel = new JPanel(); // 输入面板

        inputPanel.setLayout(new FlowLayout()); // 设置布局

        for (int i = 0; i < inputFields.length; i++) {
            inputFields[i] = new JTextField(3); // 每个字段宽度为 1
            inputFields[i].setHorizontalAlignment(JTextField.CENTER);
            inputFields[i].setFont(new Font("Arial", Font.BOLD, 24));
            inputFields[i].setPreferredSize(new Dimension(50, 50)); // 设置首选大小以匹配你的设计

            inputPanel.add(inputFields[i]);
        }




        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts()); // 设置剩余尝试次数标签
        inputPanel.add(attemptsLabel); // 添加剩余尝试次数标签
        gameStatusLabel.setText("The gameStatus: " + controller.isGameWon());
        inputPanel.add(gameStatusLabel); // 添加游戏状态标签
        center.add(inputPanel);
        center.add(new JPanel());
        frame.add(center, BorderLayout.NORTH); // 添加中心面板


        JPanel keyboardPanel = new JPanel(); // 键盘面板
        keyboardPanel.setLayout(new BoxLayout(keyboardPanel, BoxLayout.Y_AXIS)); // 设置布局
        keyboardPanel.add(new JPanel());

        JPanel numberPanel = new JPanel(); // 数字面板
        numberPanel.setLayout(new GridLayout(1, 10, 5, 5)); // 设置布局
        keyboardPanel.add(numberPanel);

        for (int i = 0; i < 10; i++) {
            JButton button = new JButton(Integer.toString(i)); // 创建数字按钮
            button.setEnabled(true); // 设置按钮状态
            button.addActionListener(e -> {
                if (currentInputIndex < inputFields.length) {
                    inputFields[currentInputIndex].setText(button.getText());
                    currentInputIndex++;
                }
            });
            button.setPreferredSize(new Dimension(50, 50)); // 设置按钮大小
            numberPanel.add(button); // 添加数字按钮
        }

        JPanel operatonPanel = new JPanel();
        operatonPanel.setLayout(new GridLayout(1,7, 5, 5));

        String[] operationLabels = {"+", "-", "*", "/", "="};
        JButton clearButton = new JButton("Delete");
        JButton enterButton = new JButton("Enter");

        clearButton.setEnabled(true);
        clearButton.addActionListener(e -> {
            if (currentInputIndex > 0) { // 只有当至少有一个输入时才继续
                // 因为currentInputIndex指向下一个要输入的位置，所以我们减去2以找到最后一个输入的位置
                int lastIndex = currentInputIndex - 1;

                // 清空最后一个非空输入框的内容
                if (!inputFields[lastIndex].getText().isEmpty()) {
                    inputFields[lastIndex].setText("");
                    currentInputIndex = lastIndex; // 更新currentInputIndex以反映删除操作
                } else {
                    // 如果最后一个输入框为空，则向前检查前一个非空输入框
                    for (int i = lastIndex; i >= 0; i--) {
                        if (!inputFields[i].getText().isEmpty()) {
                            inputFields[i].setText("");
                            currentInputIndex = i;
                            break; // 找到并清空了一个非空输入框，退出循环
                        }
                    }
                }
            }
        });
        operatonPanel.add(clearButton);


        JButton[] operatonButtons = new JButton[operationLabels.length + 1]; // 加一是为了包括等号按钮
        for (int i = 0; i < operationLabels.length; i++) {
            int index = i;
            operatonButtons[i] = new JButton(operationLabels[i]);
            operatonButtons[i].setEnabled(true); // 设置按钮状态
            operatonButtons[i].addActionListener(e -> {
                if (currentInputIndex < inputFields.length) {
                    inputFields[currentInputIndex].setText(operatonButtons[index].getText());
                    currentInputIndex++;
                }
            });
            operatonButtons[i].setPreferredSize(new Dimension(50, 50)); // 设置按钮大小
            operatonPanel.add(operatonButtons[i]);

        }


        enterButton.addActionListener(e -> {
            StringBuilder inputBuilder = new StringBuilder();

            // 遍历所有的输入框，收集输入的数字和操作符
            for (JTextField inputField : inputFields) {
                inputBuilder.append(inputField.getText());
                inputField.setText(""); // 清空每个输入框
            }

            String inputString = inputBuilder.toString();
            controller.processInput(inputString); // 把整合后的字符串发送给控制器处理
            currentInputIndex = 0; // 重置当前输入位置
        });
        operatonPanel.add(enterButton);

        keyboardPanel.add(new JPanel());
        keyboardPanel.add(numberPanel);
        keyboardPanel.add(operatonPanel);


        frame.add(keyboardPanel, BorderLayout.CENTER); // 添加键盘面板
        frame.setVisible(true); // 设置窗口可见
    }

    /**
     * 更新视图。
     * @param o 被观察对象
     * @param arg 参数
     */
    @Override
    public void update(java.util.Observable o, Object arg) {
        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
        gameStatusLabel.setText("The gameStatus: " + controller.isGameWon());
    }


}
