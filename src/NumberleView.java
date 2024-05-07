// 导入必要的包
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

// NumberleView 类负责显示游戏界面并与用户交互。
public class NumberleView implements Observer {
    private final INumberleModel model; // 游戏模型
    private final NumberleController controller; // 游戏控制器
    private final JFrame frame = new JFrame("Numberle"); // 游戏窗口
    private  int currentInputIndex = 0;
    private final JTextField[][] inputFields = new JTextField[6][7];
    private JButton[] numberButtons;
    private JButton[] operatorButtons;
    private final Map<String, JButton> buttonMap = new HashMap<>();
    private final JLabel attemptsLabel = new JLabel(); // 剩余尝试次数标签
    private String defaultAnswer = "1+1+1=3";

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


        JMenuBar menuBar = new JMenuBar(); // 创建菜单栏

        JMenu menu = new JMenu("Menu"); // 创建菜单
        JMenuItem menuItem1 = new JMenuItem("Error on Invalid Equation"); // 创建菜单项
        menuItem1.setSelected(true);
        JMenuItem menuItem2 = new JMenuItem("Show Target Equation"); // 创建菜单项
        menuItem2.setSelected(true);
        JMenuItem menuItem3 = new JMenuItem("Randomize Equation"); // 创建菜单项
        menuItem3.setSelected(true);

        // 设置菜单项背景颜色
        menuItem1.setBackground(((NumberleModel) model).isShowInvalidEquationError() ? Color.BLUE : Color.WHITE);
        menuItem2.setBackground(((NumberleModel) model).isShowTargetEquation() ? Color.BLUE : Color.WHITE);
        menuItem3.setBackground(((NumberleModel) model).isRandomizeEquation() ? Color.BLUE : Color.WHITE);

        // 为菜单项按钮添加点击事件监听器
        menuItem1.addActionListener(e -> {
            boolean currentState = !((NumberleModel) model).isShowInvalidEquationError();
            ((NumberleModel) model).setShowInvalidEquationError(currentState);
            menuItem1.setBackground(currentState ? Color.BLUE : Color.WHITE);
        });

        menuItem2.addActionListener(e -> {
            boolean currentState = !((NumberleModel) model).isShowTargetEquation();
            ((NumberleModel) model).setShowTargetEquation(currentState);
            menuItem2.setBackground(currentState ? Color.BLUE : Color.WHITE);
            JOptionPane.showMessageDialog(null, "Target Equation: " + controller.getTargetWord());
        });

        menuItem3.addActionListener(e -> {
            boolean currentState = !((NumberleModel) model).isRandomizeEquation();
            ((NumberleModel) model).setRandomizeEquation(currentState);
            menuItem3.setBackground(currentState ? Color.BLUE : Color.WHITE);
        });

        menu.add(menuItem1); // 将菜单项添加到菜单中
        menu.add(menuItem2);
        menu.add(menuItem3);

        menuBar.add(menu); // 将菜单添加到菜单栏中

        frame.setJMenuBar(menuBar); // 将菜单栏设置到 JFrame 中

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
        numberButtons = new JButton[10];
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
            numberButtons[i] = button;
            numberPanel.add(numberButtons[i]);
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
            currentInputIndex--;
            if (controller.isGameOver()){
                return; // 退出方法
            }
            if (currentInputIndex < 0) {
                return;
            }
            inputFields[getCurrentRow()][currentInputIndex].setText(""); // 设置文本框文本为空

        });
        deleteButton.setBackground(new Color(220, 225, 237)); // 设置背景颜色
        deletePanel.add(deleteButton); // 将删除按钮添加到删除按钮面板
        operatorPanel.add(deletePanel); // 将删除按钮面板添加到操作符面板

        /*
        操作符按钮
         */
        operatorButtons = new JButton[5]; // 创建一个包含5个按钮的数组
        String[] operatorLabels = {"+", "-", "*", "/", "="}; // 定义操作符数组
        // JButton[] operateButtons = new JButton[operatorLabels.length];
        for (int i = 0; i < operatorLabels.length; i++) { // 遍历操作符按钮
            operatorButtons[i] = new JButton(operatorLabels[i]);
            JButton button = new JButton(operatorLabels[i]); // 创建一个操作符按钮
            operatorButtons[i].addActionListener(e -> { // 添加按钮点击事件监听器
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
            operatorButtons[i].setPreferredSize(new Dimension(50, 50)); // 设置按钮首选大小
            operatorButtons[i].setBackground(Color.WHITE); // 设置背景颜色
            operatorPanel.add(operatorButtons[i]); // 将按钮添加到操作符面板
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
            StringBuilder guess = new StringBuilder();
            for (JTextField inputField : inputFields[getCurrentRow()]) {
                guess.append(inputField.getText());
            }

            if (currentInputIndex != 7) {
                JOptionPane.showMessageDialog(frame, "Too short!");
                return;
            }
            currentInputIndex = 0;

            if (!controller.processInput(guess.toString())) {
                JOptionPane.showMessageDialog(frame, "Invalid input!");
                currentInputIndex = 7;
            }

            restartButton.setEnabled(true);
        });
        submitButton.setBackground(new Color(220, 225, 237));
        submitPanel.add(submitButton);
        operatorPanel.add(submitPanel);

        keyboardPanel.add(operatorPanel);

        bottomPanel.add(keyboardPanel);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);


        currentInputIndex = 0;
    }

    // 视图更新方法
    @Override
    public void update(java.util.Observable o, Object arg) {
        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts()); // 更新尝试次数标签文本
        String currentGuess = controller.getCurrentGuess().toString(); // 获取当前猜测
        if (getCurrentRow() <= 0){ // 如果当前行小于等于0
            if (currentGuess.equals("       ")) { // 如果当前猜测为空
                initButtons(); // 初始化按钮
            }
            return; // 退出方法
        }

        updateInputFieldColors();
        updateButtonColors();



        if (((NumberleModel) model).isShowTargetEquation()) {
            String targetEquation = controller.getTargetWord(); // 获取目标等式
            System.out.println("Target Equation: " + targetEquation); // 打印目标等式
        }


        if (controller.isGameOver()) {
            if (controller.isGameWon()) {
                JOptionPane.showMessageDialog(frame, "Congratulations! You won!"); // 显示胜利消息对话框
            } else { // 如果游戏失败
                JOptionPane.showMessageDialog(frame, "Game over! The target number was " + controller.getTargetWord()); // 显示失败消息对话框
            }
        }
    }


    private void initButtons() {
        for (JTextField[] guessText : inputFields) { // 遍历文本框数组
            for (JTextField jTextField : guessText) { // 遍历文本框
                jTextField.setText(""); // 设置文本为空
                jTextField.setBackground(Color.WHITE); // 设置背景颜色为白色
            }
        }
    }



    private int getCurrentRow() {
        return INumberleModel.MAX_ATTEMPTS - model.getRemainingAttempts(); // 返回当前行数
    }


    private void updateInputFieldColors() {
        int[] colorState = model.getColorState();
        int currentRow = getCurrentRow() - 1;
        if (currentRow >= 0 && currentRow < inputFields.length) {
            for (int i = 0; i < colorState.length; i++) { // 遍历颜色状态数组
                switch (colorState[i]) { // 根据颜色状态设置文本框背景颜色
                    case 1: // 绿色
                        inputFields[currentRow][i].setBackground(Color.GREEN);
                        break;
                    case 2: // 橙色
                        inputFields[currentRow][i].setBackground(Color.ORANGE);
                        break;
                    case 3: // 深灰色
                        inputFields[currentRow][i].setBackground(Color.DARK_GRAY);
                        break;
                    default:
                        inputFields[currentRow][i].setBackground(Color.WHITE);
                        break;
                }
            }
        }
    }

    private void updateButtonColors() {
        Map<String,Integer> characterColorMap = model.getCharacterColorMap();
        // 遍历操作符按钮并更新颜色
        for (JButton button : operatorButtons) {
            String operator = button.getText(); // 获取按钮上的操作符
            int colorIndex = 0; // 默认颜色索引

            // 检查characterColorMap中是否存在该操作符的颜色索引
            if (characterColorMap.containsKey(operator)) {
                colorIndex = characterColorMap.get(operator);
            }
            // 根据colorIndex更新按钮颜色
            switch (colorIndex) {
                case 1: // 绿色
                    button.setForeground(Color.GREEN);
                    break;
                case 2: // 橙色
                    button.setForeground(Color.ORANGE);
                    break;
                case 3: // 深灰色
                    button.setForeground(Color.DARK_GRAY);
                    break;
                default: // 默认颜色
                    button.setForeground(Color.BLACK);
                    break;
            }
        }
        for (JButton button : numberButtons) {
            String operator = button.getText(); // 获取按钮上的操作符
            int colorIndex = 0; // 默认颜色索引

            // 检查characterColorMap中是否存在该操作符的颜色索引
            if (characterColorMap.containsKey(operator)) {
                colorIndex = characterColorMap.get(operator);
            }
            // 根据colorIndex更新按钮颜色
            switch (colorIndex) {
                case 1: // 绿色
                    button.setForeground(Color.GREEN);
                    break;
                case 2: // 橙色
                    button.setForeground(Color.ORANGE);
                    break;
                case 3: // 深灰色
                    button.setForeground(Color.DARK_GRAY);
                    break;
                default: // 默认颜色
                    button.setForeground(Color.BLACK);
                    break;
            }
        }
    }
}
