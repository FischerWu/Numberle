// 导入必要的包
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

// NumberleView 类负责显示游戏界面并与用户交互。
public class NumberleView implements Observer {
    private final INumberleModel model;
    private final NumberleController controller;
    private final JFrame frame = new JFrame("Numberle");
    private  int currentInputIndex = 0;
    private final JTextField[][] inputFields = new JTextField[6][7];
    private JButton[] numberButtons;
    private JButton[] operatorButtons;
    private final JLabel attemptsLabel = new JLabel();
    private final JLabel targetLabel = new JLabel();
    private JPanel targetPanel;

    // 构造函数，初始化游戏视图。
    // @param model 游戏模型
    // @param controller 游戏控制器
    public NumberleView(INumberleModel model, NumberleController controller) {
        this.controller = controller;
        this.model = model;
        this.controller.startNewGame();
        ((NumberleModel)this.model).addObserver(this);
        initializeFrame();
        this.controller.setView(this);
        update((NumberleModel)this.model, null);
    }

    // 初始化游戏窗口。
    private void initializeFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 750);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();


        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 7, 10, 10));
        inputPanel.setPreferredSize(new Dimension(420, 360));


        JMenuBar menuBar = new JMenuBar(); // 创建菜单栏

        JMenu menu = new JMenu("Menu"); // 创建菜单
        JMenuItem menuItem1 = new JMenuItem("Error on Invalid Equation");
        menuItem1.setSelected(true);
        JMenuItem menuItem2 = new JMenuItem("Show Target Equation");
        menuItem2.setSelected(true);
        JMenuItem menuItem3 = new JMenuItem("Randomize Equation");
        menuItem3.setSelected(true);

        // 设置菜单项背景颜色
        menuItem1.setBackground(model.getFlag1() ? Color.BLUE : Color.WHITE);
        menuItem2.setBackground(model.getFlag2() ? Color.BLUE : Color.WHITE);
        menuItem3.setBackground(model.getFlag3() ? Color.BLUE : Color.WHITE);

        // 为菜单项按钮添加点击事件监听器
        menuItem1.addActionListener(e -> {
            controller.changeFlag1();
            boolean currentState = model.getFlag1();
            menuItem1.setBackground(currentState ? Color.BLUE : Color.WHITE);
        });

        menuItem2.addActionListener(e -> {
            controller.changeFlag2();
            boolean currentState = model.getFlag2();
            menuItem2.setBackground(currentState ? Color.BLUE : Color.WHITE);
            targetPanel.setVisible(currentState);
        });

        menuItem3.addActionListener(e -> {
            controller.changeFlag3();
            boolean currentState = model.getFlag3();
            model.initialize();
            menuItem3.setBackground(currentState ? Color.BLUE : Color.WHITE);
        });
        menu.add(menuItem1);
        menu.add(menuItem2);
        menu.add(menuItem3);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

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

        topPanel.add(inputPanel);
        frame.add(topPanel, BorderLayout.NORTH);

        /*
        中间标签，尝试次数和restart
         */
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        JPanel timesPanel = new JPanel();
        timesPanel.setLayout(new GridLayout(3, 1));
        timesPanel.setPreferredSize(new Dimension(580, 75));
        JPanel attemptsPanel = new JPanel();
        attemptsPanel.add(attemptsLabel);

        targetPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        targetPanel.add(targetLabel);
        targetPanel.setPreferredSize(new Dimension(targetPanel.getPreferredSize().width, targetLabel.getPreferredSize().height));
        targetPanel.setVisible(model.getFlag2());

        JButton restartButton = new JButton("Restart");
        restartButton.setBackground(new Color(220, 225, 237));
        restartButton.setEnabled(false);
        restartButton.addActionListener(e -> {
            controller.startNewGame();
            restartButton.setEnabled(false);
            currentInputIndex = 0;
        });
        attemptsPanel.add(restartButton);
        timesPanel.add(attemptsPanel);

        middlePanel.add(timesPanel);
        middlePanel.add(targetPanel);
        frame.add(middlePanel, BorderLayout.CENTER);


        /*
        数字键盘
         */
        JPanel bottomPanel = new JPanel();
        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new GridLayout(3, 1));
        keyboardPanel.setPreferredSize(new Dimension(580, 180));
        JPanel numberPanel = new JPanel();
        numberPanel.setLayout(new GridLayout(1, 10, 10, 10));

        // 创建一个包含10个按钮的数组
        numberButtons = new JButton[10];
        for (int i = 0; i < 10; i++) {
            JButton button = new JButton(Integer.toString(i));
            button.addActionListener(e -> {
                if (controller.isGameOver()){
                    return;
                }
                if (currentInputIndex < inputFields[getCurrentRow()].length) {
                    inputFields[getCurrentRow()][currentInputIndex].setText(button.getText());
                    currentInputIndex++;
                } else {
                    currentInputIndex = 7;
                }
            });
            button.setPreferredSize(new Dimension(50, 50));
            button.setBackground(Color.WHITE);
            numberButtons[i] = button;
            numberPanel.add(numberButtons[i]);
        }
        keyboardPanel.add(numberPanel);

        /*
        清除键
         */
        JPanel operatorPanel = new JPanel();
        JPanel deletePanel = new JPanel();
        JButton deleteButton = new JButton("Delete");
        deleteButton.setPreferredSize(new Dimension(120, 50));
        deleteButton.addActionListener(e -> {
            currentInputIndex--;
            if (controller.isGameOver()){
                return;
            }
            if (currentInputIndex < 0) {
                return;
            }
            if (currentInputIndex == 7) {
                return;
            }
            inputFields[getCurrentRow()][currentInputIndex].setText("");
        });
        deleteButton.setBackground(new Color(220, 225, 237));
        deletePanel.add(deleteButton);
        operatorPanel.add(deletePanel);

        /*
        操作符按钮
         */
        operatorButtons = new JButton[5];
        String[] operatorLabels = {"+", "-", "*", "/", "="};
        for (int i = 0; i < operatorLabels.length; i++) {
            operatorButtons[i] = new JButton(operatorLabels[i]);
            JButton button = new JButton(operatorLabels[i]);
            operatorButtons[i].addActionListener(e -> {
                if (controller.isGameOver()){
                    return;
                }
                if (currentInputIndex < inputFields[getCurrentRow()].length) {
                    inputFields[getCurrentRow()][currentInputIndex].setText(button.getText());
                    currentInputIndex++;
                } else {
                    currentInputIndex = 7;
                }
            });
            operatorButtons[i].setPreferredSize(new Dimension(50, 50));
            operatorButtons[i].setBackground(Color.WHITE);
            operatorPanel.add(operatorButtons[i]);
        }

        /*
        提交功能
         */
        JPanel submitPanel = new JPanel();
        JButton submitButton = new JButton("Enter");
        submitButton.setPreferredSize(new Dimension(120, 50));
        submitButton.addActionListener(e -> {
            if (controller.isGameOver()){
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
                // currentInputIndex =0;
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
        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
        targetLabel.setText("TargetNumber: " + controller.getTargetWord());
        String currentGuess = controller.getCurrentGuess().toString();
        if (getCurrentRow() <= 0){ // 如果当前行小于等于0
            if (currentGuess.equals("       ")) {
                initButtons(); // 初始化按钮
            }
            return;
        }
        updateInputFieldColors();
        updateButtonColors();

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
                    button.setBackground(Color.GREEN);
                    button.setOpaque(true);
                    break;
                case 2: // 橙色
                    button.setForeground(Color.ORANGE);
                    button.setBackground(Color.ORANGE);
                    button.setOpaque(true);
                    break;
                case 3: // 深灰色
                    button.setForeground(Color.DARK_GRAY);
                    button.setBackground(Color.DARK_GRAY);
                    button.setOpaque(true);
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
                    button.setBackground(Color.GREEN);
                    button.setOpaque(true);
                    break;
                case 2: // 橙色
                    button.setForeground(Color.ORANGE);
                    button.setBackground(Color.ORANGE);
                    button.setOpaque(true);
                    break;
                case 3: // 深灰色
                    button.setForeground(Color.DARK_GRAY);
                    button.setBackground(Color.ORANGE);
                    button.setOpaque(true);
                    break;
                default: // 默认颜色
                    button.setForeground(Color.BLACK);
                    break;
            }
        }
    }
}
