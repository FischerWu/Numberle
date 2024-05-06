// 导入必要的包
import javax.swing.*;
import java.awt.*;
import java.util.Observer;

// NumberleView 类负责显示游戏界面并与用户交互。
public class NumberleView implements Observer {
    private final INumberleModel model; // 游戏模型
    private final NumberleController controller; // 游戏控制器
    private final JFrame frame = new JFrame("Numberle"); // 游戏窗口
    private  int currentInputIndex = 0;
    private final JTextField[][] inputFields = new JTextField[6][7];
    private final JLabel attemptsLabel = new JLabel(); // 剩余尝试次数标签

    // 构造函数，初始化游戏视图。
    // @param model 游戏模型
    // @param controller 游戏控制器
    public NumberleView(INumberleModel model, NumberleController controller) {
        this.controller = controller; // 设置游戏控制器
        this.model = model; // 设置游戏模型
        this.controller.startNewGame(); // 开始新游戏
        ((NumberleModel)this.model).addObserver(this); // 将视图添加为模型的观察者
        initializeFrame(); // 初始化游戏窗口
        this.controller.setView(this); // 设置视图
        update((NumberleModel)this.model, null); // 更新视图
    }

    // 初始化游戏窗口。
    private void initializeFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭操作
        frame.setSize(600, 700); // 设置窗口大小
        frame.setLayout(new BorderLayout()); // 设置布局

        JPanel topPanel = new JPanel();


        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 7, 10, 10));
        inputPanel.setPreferredSize(new Dimension(420, 360));

        /*
        初始化6*7面板
         */
        for (int j = 0; j < inputFields.length; j++) {
            for (int i = 0; i < inputFields[j].length; i++) {
                inputFields[j][i] = new JTextField();
                inputFields[j][i].setPreferredSize(new Dimension(60, 60));
                inputFields[j][i].setFont(new Font("Arial", Font.BOLD, 24));
                inputFields[j][i].setEditable(false);
                inputFields[j][i].setBackground(Color.WHITE);
                inputFields[j][i].setHorizontalAlignment(JTextField.CENTER);
                inputPanel.add(inputFields[j][i]);
            }
        }

        topPanel.add(inputPanel); // 将showInputPanel面板添加到panel1面板中
        frame.add(topPanel, BorderLayout.NORTH); // 将panel1面板添加到frame的北部位置

        /*
        中间标签，尝试次数和restart
         */
        JPanel middlePanel = new JPanel();
        JPanel timesPanel = new JPanel();
        timesPanel.setLayout(new GridLayout(3, 1)); // 设置布局
        timesPanel.setPreferredSize(new Dimension(580, 180)); // 设置首选大小
        JPanel attemptsPanel = new JPanel(); // 尝试次数面板
        attemptsPanel.add(attemptsLabel); // 将尝试次数标签添加到尝试次数面板

        JButton restartButton = new JButton("Restart");
        restartButton.setBackground(new Color(220, 225, 237));
        restartButton.setEnabled(false);
        restartButton.addActionListener(e -> {
            controller.startNewGame();
            restartButton.setEnabled(false); // 设置重启按钮为不可用状态
            currentInputIndex = 0;
        });
        attemptsPanel.add(restartButton);
        timesPanel.add(attemptsPanel);
        middlePanel.add(timesPanel);
        frame.add(middlePanel, BorderLayout.CENTER); // 将中间面板添加到frame的中间位置);


        /*
        数字键盘
         */
        JPanel bottomPanel = new JPanel();
        JPanel keyboardPanel = new JPanel(); // 键盘面板
        keyboardPanel.setLayout(new GridLayout(3, 1)); // 设置布局
        keyboardPanel.setPreferredSize(new Dimension(580, 180)); // 设置首选大小
        JPanel numberPanel = new JPanel(); // 数字面板
        numberPanel.setLayout(new GridLayout(1, 10, 10, 10)); // 设置布局

        // 创建一个包含10个按钮的数组
        for (int i = 0; i < 10; i++) { // 遍历数字按钮
            JButton button = new JButton(Integer.toString(i)); // 创建一个数字按钮
            button.addActionListener(e -> { // 添加按钮点击事件监听器
                if (controller.isGameOver()){ // 如果游戏已结束
                    return; // 退出方法
                }
                if (currentInputIndex < inputFields[getCurrentRow()].length) {
                    inputFields[getCurrentRow()][currentInputIndex].setText(button.getText()); // 设置文本框文本为按钮文本
                    currentInputIndex++;
                } else {
                    currentInputIndex = 7;
                }
            });
            button.setPreferredSize(new Dimension(50, 50)); // 设置按钮首选大小
            button.setBackground(Color.WHITE); // 设置背景颜色
            numberPanel.add(button);
        }
        keyboardPanel.add(numberPanel);

        /*
        清除键
         */
        JPanel operatorPanel = new JPanel();
        JPanel deletePanel = new JPanel();
        JButton deleteButton = new JButton("Delete"); // 删除按钮
        deleteButton.setPreferredSize(new Dimension(120, 50)); // 设置按钮首选大小
        deleteButton.addActionListener(e -> { // 添加按钮点击事件监听器
            if (controller.isGameOver()){ // 如果游戏已结束
                return; // 退出方法
            }
            if (getCurrentColumn() == -1) { // 如果当前列为空
                return; // 退出方法
            }
            inputFields[getCurrentRow()][getCurrentColumn()].setText(""); // 设置文本框文本为空
            currentInputIndex--;
        });
        deleteButton.setBackground(new Color(220, 225, 237)); // 设置背景颜色
        deletePanel.add(deleteButton); // 将删除按钮添加到删除按钮面板
        operatorPanel.add(deletePanel); // 将删除按钮面板添加到操作符面板

        /*
        操作符按钮
         */
        // operatorButtons = new JButton[5]; // 创建一个包含5个按钮的数组
        String[] operatorLabels = {"+", "-", "*", "/", "="}; // 定义操作符数组
        JButton[] operateButtons = new JButton[operatorLabels.length + 1]; // 加一是为了包括等号按钮
        for (int i = 0; i < operatorLabels.length; i++) { // 遍历操作符按钮
            operateButtons[i] = new JButton(operatorLabels[i]);
            JButton button = new JButton(operatorLabels[i]); // 创建一个操作符按钮
            operateButtons[i].addActionListener(e -> { // 添加按钮点击事件监听器
                if (controller.isGameOver()){ // 如果游戏已结束
                    return; // 退出方法
                }
                if (currentInputIndex < inputFields[getCurrentRow()].length) {
                    inputFields[getCurrentRow()][currentInputIndex].setText(button.getText()); // 设置文本框文本为按钮文本
                    currentInputIndex++;
                }
            });
            operateButtons[i].setPreferredSize(new Dimension(50, 50)); // 设置按钮首选大小
            operateButtons[i].setBackground(Color.WHITE); // 设置背景颜色
            operatorPanel.add(operateButtons[i]); // 将按钮添加到操作符面板
        }

        /*
        提交功能
         */
        JPanel submitPanel = new JPanel(); // 提交按钮面板
        JButton submitButton = new JButton("Enter"); // 提交按钮
        submitButton.setPreferredSize(new Dimension(120, 50)); // 设置按钮首选大小
        submitButton.addActionListener(e -> { // 添加按钮点击事件监听器
            if (controller.isGameOver()){ // 如果游戏已结束
                JOptionPane.showMessageDialog(frame, "Game over! The target number was " + controller.getTargetWord()); // 显示游戏结束消息对话框
                return;
            }

            StringBuilder guess = new StringBuilder(); // 创建一个字符串构建器
            for (JTextField inputField : inputFields[getCurrentRow()]) {
                guess.append(inputField.getText());
            }

            if (currentInputIndex != 7) { // 如果输入长度不为7
                JOptionPane.showMessageDialog(frame, "Too short!"); // 显示提示消息对话框
                return;
            }

            if (!controller.processInput(guess.toString())) { // 如果输入不合法
                JOptionPane.showMessageDialog(frame, "Invalid input!"); // 显示提示消息对话框

            }

            currentInputIndex = 0;
            restartButton.setEnabled(true); // 启用重启按钮
        });
        submitButton.setBackground(new Color(220, 225, 237)); // 设置背景颜色
        submitPanel.add(submitButton); // 将提交按钮添加到提交按钮面板
        operatorPanel.add(submitPanel); // 将提交按钮面板添加到操作符面板

        keyboardPanel.add(operatorPanel); // 将操作符面板添加到键盘面板

        bottomPanel.add(keyboardPanel); // 将键盘面板添加到面板2
        frame.add(bottomPanel, BorderLayout.SOUTH); // 将面板2添加到frame的中心位置
        frame.setVisible(true); // 设置窗口可见
        frame.setLocationRelativeTo(null); // 设置窗口居中显示


        currentInputIndex = 0;
    }

    // 视图更新方法
    @Override
    public void update(java.util.Observable o, Object arg) {
        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts()); // 更新尝试次数标签文本
        String currentGuess = controller.getCurrentGuess().toString(); // 获取当前猜测
        String targetNumber = controller.getTargetWord(); // 获取目标数字
        if (getCurrentRow() <= 0){ // 如果当前行小于等于0
            if (currentGuess.equals("       ")) { // 如果当前猜测为空
                initButtons(); // 初始化按钮
            }
            return; // 退出方法
        }
        for (int i = 0; i < currentGuess.toCharArray().length; i++) { // 遍历当前猜测
            char c = currentGuess.charAt(i); // 获取当前字符

            if (c == targetNumber.charAt(i)) { // 如果猜测正确
                inputFields[getCurrentRow() -1][i].setBackground(Color.GREEN); // 设置文本框背景颜色为绿色

            } else if (targetNumber.contains(String.valueOf(c))) { // 如果猜测包含在目标数字中
                inputFields[getCurrentRow()-1][i].setBackground(Color.ORANGE); // 设置文本框背景颜色为橙色
            } else { // 其他情况
                inputFields[getCurrentRow()-1][i].setBackground(Color.DARK_GRAY); // 设置文本框背景颜色为深灰色
            }
        }

        if (model.isGameOver()) { // 如果游戏已结束
            if (model.isGameWon()) { // 如果游戏胜利
                JOptionPane.showMessageDialog(frame, "Congratulations! You won!"); // 显示胜利消息对话框
            } else { // 如果游戏失败
                JOptionPane.showMessageDialog(frame, "Game over! The target number was " + controller.getTargetWord()); // 显示失败消息对话框
            }
        }
    }

    // 初始化按钮方法
    private void initButtons() {
        for (JTextField[] guessText : inputFields) { // 遍历文本框数组
            for (JTextField jTextField : guessText) { // 遍历文本框
                jTextField.setText(""); // 设置文本为空
                jTextField.setBackground(Color.WHITE); // 设置背景颜色为白色
            }
        }
    }


    // 获取当前行方法
    private int getCurrentRow() {
        return INumberleModel.MAX_ATTEMPTS - model.getRemainingAttempts(); // 返回当前行数
    }

    // 获取当前列方法
    private int getCurrentColumn() {
        for (int i = 0; i < 7; i++) { // 遍历列
            if (inputFields[getCurrentRow()][i].getText().equals("")) { // 如果文本框文本为空
                return i-1; // 返回当前列
            }
        }
        return 6; // 返回6
    }
}
