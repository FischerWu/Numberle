// 导入必要的包
import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Observer;


public class NumberleView implements Observer {
    private final INumberleModel model;
    private final NumberleController controller;
    private final JFrame frame = new JFrame("Numberle");
    private  int currentIndex = 0;
    private final JTextField[][] inputFields = new JTextField[6][7];
    private JButton[] numberButtons;
    private JButton[] operatorButtons;
    private final JLabel attemptsLabel = new JLabel();
    private final JLabel targetLabel = new JLabel();
    private JButton restartButton;
    private JPanel targetPanel;

    // 构造函数，初始化游戏视图。
    // @param model 游戏模型
    // @param controller 游戏控制器
    public NumberleView(INumberleModel model, NumberleController controller) {
        this.controller = controller;
        this.model = model;
        this.controller.startNewGame();
        ((NumberleModel)this.model).addObserver(this);
        initializeMenu();
        initializeFrame();
        this.controller.setView(this);
        update((NumberleModel)this.model, null);
    }

    private void initializeFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 750);
        frame.setLayout(new BorderLayout());

        initializeFirstPanel();
        initializeSecondPanel();
        initializeThirdPanel();

        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        currentIndex = 0;
    }

    private void initializeMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Menu");
        JMenuItem menuItem1 = new JMenuItem("Invalid Equation");
        menuItem1.setSelected(true);
        JMenuItem menuItem2 = new JMenuItem("Show Target Equation");
        menuItem2.setSelected(true);
        JMenuItem menuItem3 = new JMenuItem("Default Equation");
        menuItem3.setSelected(true);


        menuItem1.setBackground(controller.getFlag1() ? new Color(0, 200, 150) : Color.WHITE);
        menuItem2.setBackground(controller.getFlag2() ? new Color(0, 200, 150) : Color.WHITE);
        menuItem3.setBackground(controller.getFlag3() ? new Color(0, 200, 150) : Color.WHITE);


        menuItem1.addActionListener(e -> {
            controller.changeFlag1();
            boolean currentState = controller.getFlag1();
            menuItem1.setBackground(currentState ? new Color(0, 200, 150) : Color.WHITE);
        });

        menuItem2.addActionListener(e -> {
            controller.changeFlag2();
            boolean currentState = controller.getFlag2();
            menuItem2.setBackground(currentState ? new Color(0, 200, 150) : Color.WHITE);
            targetPanel.setVisible(currentState);
        });

        menuItem3.addActionListener(e -> {
            controller.changeFlag3();
            boolean currentState = controller.getFlag3();
            this.controller.startNewGame();
            menuItem3.setBackground(currentState ? new Color(0, 200, 150) : Color.WHITE);
        });
        menu.add(menuItem1);
        menu.add(menuItem2);
        menu.add(menuItem3);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
    }

    private void initializeFirstPanel() {
        JPanel firstPanel = new JPanel();

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 7, 10, 10));
        inputPanel.setPreferredSize(new Dimension(420, 360));


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

        firstPanel.add(inputPanel);
        frame.add(firstPanel, BorderLayout.NORTH);
    }

    private void initializeSecondPanel() {
        JPanel secondPanel = new JPanel();
        secondPanel.setLayout(new BoxLayout(secondPanel, BoxLayout.Y_AXIS));
        JPanel timesPanel = new JPanel();
        timesPanel.setLayout(new GridLayout(3, 1));
        timesPanel.setPreferredSize(new Dimension(580, 75));
        JPanel attemptsPanel = new JPanel();
        attemptsPanel.add(attemptsLabel);

        targetPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        targetPanel.add(targetLabel);
        targetPanel.setPreferredSize(new Dimension(targetPanel.getPreferredSize().width, targetLabel.getPreferredSize().height));
        targetPanel.setVisible(controller.getFlag2());

        restartButton = new JButton("Restart");
        restartButton.setBackground(new Color(230, 230, 230));
        restartButton.setEnabled(false);
        restartButton.addActionListener(e -> {
            controller.startNewGame();
            restartButton.setEnabled(false);
            currentIndex = 0;
        });
        attemptsPanel.add(restartButton);
        timesPanel.add(attemptsPanel);

        secondPanel.add(timesPanel);
        secondPanel.add(targetPanel);
        frame.add(secondPanel, BorderLayout.CENTER);
    }

    private void initializeThirdPanel() {
        JPanel thirdPanel = new JPanel();
        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new GridLayout(3, 1));
        keyboardPanel.setPreferredSize(new Dimension(580, 180));
        JPanel numberPanel = new JPanel();
        numberPanel.setLayout(new GridLayout(1, 10, 10, 10));

        numberButtons = new JButton[10];
        for (int i = 0; i < 10; i++) {
            JButton button = new JButton(Integer.toString(i));
            button.addActionListener(e -> {
                if (controller.isGameOver()){
                    return;
                }
                if (currentIndex < inputFields[getCurrentRow()].length) {
                    inputFields[getCurrentRow()][currentIndex].setText(button.getText());
                    currentIndex++;
                } else {
                    currentIndex = 7;
                }
            });
            button.setPreferredSize(new Dimension(50, 50));
            button.setBackground(Color.WHITE);
            numberButtons[i] = button;
            numberPanel.add(numberButtons[i]);
        }
        keyboardPanel.add(numberPanel);

        JPanel operatorPanel = new JPanel();
        JPanel deletePanel = new JPanel();
        JButton deleteButton = new JButton("Delete");
        deleteButton.setPreferredSize(new Dimension(120, 50));
        deleteButton.addActionListener(e -> {
            currentIndex--;
            if (controller.isGameOver()){
                return;
            }
            if (currentIndex < 0) {
                currentIndex = 0;
                return;
            }
            if (currentIndex == 7) {
                return;
            }
            inputFields[getCurrentRow()][currentIndex].setText("");
        });
        deleteButton.setBackground(new Color(230, 235, 230));
        deletePanel.add(deleteButton);
        operatorPanel.add(deletePanel);


        operatorButtons = new JButton[5];
        String[] operatorLabels = {"+", "-", "*", "/", "="};
        for (int i = 0; i < operatorLabels.length; i++) {
            operatorButtons[i] = new JButton(operatorLabels[i]);
            JButton button = new JButton(operatorLabels[i]);
            operatorButtons[i].addActionListener(e -> {
                if (controller.isGameOver()){
                    return;
                }
                if (currentIndex < inputFields[getCurrentRow()].length) {
                    inputFields[getCurrentRow()][currentIndex].setText(button.getText());
                    currentIndex++;
                } else {
                    currentIndex = 7;
                }
            });
            operatorButtons[i].setPreferredSize(new Dimension(50, 50));
            operatorButtons[i].setBackground(Color.WHITE);
            operatorPanel.add(operatorButtons[i]);
        }


        JPanel submitPanel = new JPanel();
        JButton submitButton = new JButton("Enter");
        submitButton.setPreferredSize(new Dimension(120, 50));
        submitButton.addActionListener(e -> {
            if (controller.isGameOver()){
                JOptionPane.showMessageDialog(frame, "Game over! The target number was " + controller.getTargetWord());
                return;
            }
            StringBuilder guess = new StringBuilder();
            for (JTextField inputField : inputFields[getCurrentRow()]) {
                guess.append(inputField.getText());
            }

            if (currentIndex != 7) {
                JOptionPane.showMessageDialog(frame, "Too short!");
                return;
            }
            currentIndex = 0;

            if (!controller.processInput(guess.toString())) {
                JOptionPane.showMessageDialog(frame, "Invalid input!");
                currentIndex = 7;
            }

            restartButton.setEnabled(true);
        });
        submitButton.setBackground(new Color(230, 230, 230));
        submitPanel.add(submitButton);
        operatorPanel.add(submitPanel);

        keyboardPanel.add(operatorPanel);

        thirdPanel.add(keyboardPanel);
        frame.add(thirdPanel, BorderLayout.SOUTH);
    }



    @Override
    public void update(java.util.Observable o, Object arg) {
        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
        targetLabel.setText("TargetNumber: " + controller.getTargetWord());
        String currentGuess = controller.getCurrentGuess().toString();
        if (getCurrentRow() <= 0){
            if (currentGuess.equals("       ")) {
                initInputFields();
                initKeyboard();
            }
            return;
        }
        updateInputFieldColors();
        updateKeyBoardColors();

        if (controller.isGameOver()) {
            if (controller.isGameWon()) {
                JOptionPane.showMessageDialog(frame, "Congratulations! You won!"); // 显示胜利消息对话框
            } else {
                JOptionPane.showMessageDialog(frame, "Game over! The target number was " + controller.getTargetWord()); // 显示失败消息对话框
            }
        }
    }



    private void initInputFields() {
        for (JTextField[] guessText : inputFields) {
            for (JTextField jTextField : guessText) {
                jTextField.setText("");
                jTextField.setBackground(Color.WHITE);
            }
        }
    }

    private void initKeyboard() {
        for (JButton button : numberButtons) {
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
            button.setOpaque(false);
        }

        for (JButton button : operatorButtons) {
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
            button.setOpaque(false);
        }
    }


    private int getCurrentRow() {
        return INumberleModel.MAX_ATTEMPTS - controller.getRemainingAttempts();
    }

    private void updateInputFieldColors() {
        int[] colorState = model.getColorState();
        int currentRow = getCurrentRow() - 1;
        if (currentRow >= 0 && currentRow < inputFields.length) {
            for (int i = 0; i < colorState.length; i++) {
                switch (colorState[i]) {
                    case 1 -> inputFields[currentRow][i].setBackground(new Color(0, 200, 150));
                    case 2 -> inputFields[currentRow][i].setBackground(new Color(255, 150, 0));
                    case 3 -> inputFields[currentRow][i].setBackground(new Color(160, 160, 180));
                    default -> inputFields[currentRow][i].setBackground(Color.WHITE);
                }
            }
        }
    }

    private void updateKeyBoardColors() {
        Map<String,Integer> characterColorMap = model.getCharacterColorMap();
        for (JButton button : operatorButtons) {
            String operator = button.getText();
            int colorIndex = characterColorMap.getOrDefault(operator, 0);
            setKeyBoardColor(button, colorIndex);
        }
        for (JButton button : numberButtons) {
            String operator = button.getText();
            int colorIndex = characterColorMap.getOrDefault(operator, 0);
            setKeyBoardColor(button, colorIndex);
        }
    }

    private void setKeyBoardColor(JButton button, int colorIndex) {
        switch (colorIndex) {
            case 1 -> {
                button.setForeground(new Color(0, 200, 150));
                button.setBackground(new Color(0, 200, 150));
                button.setOpaque(true);
            }
            case 2 -> {
                button.setForeground(new Color(255, 150, 0));
                button.setBackground(new Color(255, 150, 0));
                button.setOpaque(true);
            }
            case 3 -> {
                button.setForeground(new Color(160, 160, 180));
                button.setBackground(new Color(160, 160, 180));
                button.setOpaque(true);
            }
            default -> {
                button.setForeground(Color.BLACK);
                button.setBackground(Color.WHITE);
                button.setOpaque(false);
            }
        }
    }
}
