import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Observer;


public class NumberleView implements Observer {
    private final INumberleModel model;
    private final NumberleController controller;
    private final JFrame frame = new JFrame("Numberle");
    private int currentIndex = 0;
    private final JTextField[][] inputFields = new JTextField[6][7];
    private JButton[] numberButtons;
    private JButton[] operatorButtons;
    private final JLabel attemptsLabel = new JLabel();
    private final JLabel targetLabel = new JLabel();
    private JButton restartButton;
    private JPanel targetPanel;


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

    // Initializes the frame and menu of the game window
    private void initializeFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 750);
        frame.setLayout(new BorderLayout());

        initializeFirstPanel();
        initializeSecondPanel();
        initializeThirdPanel();

        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        currentIndex = 0;
    }

    // Initializes the menu that control the flag1 flag2 flag3
    private void initializeMenu() {
        JMenuBar menuBar = new JMenuBar();
        // Create a menu bar called menu
        JMenu menu = new JMenu("Menu");
        // Create menu items
        JMenuItem menuItem1 = new JMenuItem("Invalid Equation");
        menuItem1.setSelected(true);
        JMenuItem menuItem2 = new JMenuItem("Show Target Equation");
        menuItem2.setSelected(true);
        JMenuItem menuItem3 = new JMenuItem("Default Equation");
        menuItem3.setSelected(true);

        // Set initial background color for menu items based on controller flags
        menuItem1.setBackground(controller.getFlag1() ? new Color(0, 200, 150) : Color.WHITE);
        menuItem2.setBackground(controller.getFlag2() ? new Color(0, 200, 150) : Color.WHITE);
        menuItem3.setBackground(controller.getFlag3() ? new Color(0, 200, 150) : Color.WHITE);


        // Add action listeners to menu items
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
            controller.updateRestartButton();
            currentIndex = 0;
            menuItem3.setBackground(currentState ? new Color(0, 200, 150) : Color.WHITE);
        });
        // Add menu to frame
        menu.add(menuItem1);
        menu.add(menuItem2);
        menu.add(menuItem3);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
    }

    // Initializes the input panel and buttons where have 6*7 panel.
    private void initializeFirstPanel() {
        JPanel firstPanel = new JPanel();

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 7, 10, 10));
        Dimension panelSize = new Dimension(500,400);
        inputPanel.setPreferredSize(panelSize);

        initializeInputPanel(inputPanel);

        firstPanel.add(inputPanel);
        frame.add(firstPanel, BorderLayout.NORTH);
    }


    private void initializeInputPanel(JPanel inputPanel) {
        // Set the size, font, and background color of the text box
        Dimension textFieldSize = new Dimension(80, 80);
        Font textFieldFont = new Font("Arial", Font.PLAIN, 30);
        Color textFieldBackground = Color.WHITE;

        // Set the properties of each text box
        for (int j = 0; j < inputFields.length; j++) {
            for (int i = 0; i < inputFields[j].length; i++) {
                JTextField textField = new JTextField();
                textField.setPreferredSize(textFieldSize);
                textField.setFont(textFieldFont);
                textField.setEditable(false);
                textField.setBackground(textFieldBackground);
                textField.setHorizontalAlignment(JTextField.CENTER);
                inputFields[j][i] = textField;
                inputPanel.add(textField);
            }
        }
    }

    // Initializes the action panel and buttons that can see remaining attempts.
    // There also can see the target number if flag2 is true.
    // Set Restart button is enable/disable that controlled by controller.
    private void initializeSecondPanel() {
        JPanel secondPanel = new JPanel();
        secondPanel.setLayout(new BoxLayout(secondPanel, BoxLayout.Y_AXIS));
        JPanel attemptsPanel = new JPanel();
        attemptsPanel.add(attemptsLabel);
        // remaining attempts
        JPanel timesPanel = new JPanel();
        timesPanel.setLayout(new GridLayout(3, 1));
        Dimension panelSize = new Dimension(600,100);
        timesPanel.setPreferredSize(panelSize);
        timesPanel.add(attemptsPanel);

        // restart button
        setRestartButton(attemptsPanel);

        // target number, controlled by flag2
        setTargetNumber(secondPanel);

        secondPanel.add(timesPanel);
        frame.add(secondPanel, BorderLayout.CENTER);
    }

    private void setTargetNumber(JPanel secondPanel) {
        Dimension panelSize = new Dimension(600,100);
        targetPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        targetPanel.add(targetLabel);
        targetPanel.setPreferredSize(panelSize);
        targetPanel.setVisible(controller.getFlag2());
        secondPanel.add(targetPanel);
    }

    private void setRestartButton(JPanel attemptsPanel) {
        restartButton = new JButton("Restart");
        restartButton.setEnabled(false);
        restartButton.setBackground(new Color(230, 230, 230));
        restartButton.setEnabled(false);
        restartButton.addActionListener(e -> {
            controller.startNewGame();
            controller.updateRestartButton();
            currentIndex = 0;
        });
        attemptsPanel.add(restartButton);
    }

    // Initializes the number and operator input keyboard.
    // number and normal operator can add index.
    // Delete can subtract index and prevents index from crossing boundaries.
    // Enter checks the string length and processes the content of the string.
    private void initializeThirdPanel() {
        JPanel thirdPanel = new JPanel();
        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new GridLayout(3, 1));
        Dimension panelSize = new Dimension(600,200);
        keyboardPanel.setPreferredSize(panelSize);
        JPanel numberPanel = new JPanel();
        setNumberKeyboard(numberPanel);

        JPanel operatorPanel = setDeletePanel();

        // Initialize operator buttons: +, -, *, /, =
        setOperatorKeyboard(operatorPanel);

        // Initialize submit button
        setSubmitKeyboard(operatorPanel);
        keyboardPanel.add(numberPanel);

        keyboardPanel.add(operatorPanel);

        thirdPanel.add(keyboardPanel);
        frame.add(thirdPanel, BorderLayout.SOUTH);
    }


    private void setSubmitKeyboard(JPanel operatorPanel) {
        JPanel submitPanel = new JPanel();
        JButton submitButton = new JButton("Enter");
        submitButton.setPreferredSize(new Dimension(120, 50));
        submitButton.addActionListener(e -> {
            if (controller.isGameOver()){
                JOptionPane.showMessageDialog(frame, "Game over! The target number is " + controller.getTargetWord());
                return;
            }
            StringBuilder guess = new StringBuilder();
            for (JTextField inputField : inputFields[getCurrentRow()]) {
                guess.append(inputField.getText());
            }
            // 0 <= currentIndex <= 7, make index in correct
            if (currentIndex != 7) {
                JOptionPane.showMessageDialog(frame, "Should input full!");
                return;
            }
            currentIndex = 0;

            if (!controller.processInput(guess.toString())) {
                JOptionPane.showMessageDialog(frame, "Invalid input!");
                currentIndex = 7;
            }
            controller.updateRestartButton();
        });
        submitButton.setBackground(new Color(230, 230, 230));
        submitPanel.add(submitButton);
        operatorPanel.add(submitPanel);
    }

    private void setOperatorKeyboard(JPanel operatorPanel) {
        operatorButtons = new JButton[5];
        String[] operatorLabels = {"+", "-", "*", "/", "="};
        for (int i = 0; i < operatorLabels.length; i++) {
            operatorButtons[i] = new JButton(operatorLabels[i]);
            JButton button = new JButton(operatorLabels[i]);
            operatorButtons[i].addActionListener(e -> {
                if (controller.isGameOver()){
                    // remainingAttempts <= 0 || gameWon
                    return;
                }
                if (currentIndex < inputFields[getCurrentRow()].length) {
                    inputFields[getCurrentRow()][currentIndex].setText(button.getText());
                    currentIndex++;
                } else {
                    // 0 <= currentIndex <= 7
                    currentIndex = 7;
                }
            });
            operatorButtons[i].setPreferredSize(new Dimension(50, 50));
            operatorButtons[i].setBackground(Color.WHITE);
            operatorPanel.add(operatorButtons[i]);
        }
    }


    private void setNumberKeyboard(JPanel numberPanel) {
        numberPanel.setLayout(new GridLayout(1, 10, 10, 10));
        // Initialize numbers: 0, 1, 2, 3, 4, 5, 6, 7, 8, 9
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
    }

    private JPanel setDeletePanel() {
        // Initialize Delete button
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
        return operatorPanel;
    }


    @Override
    public void update(java.util.Observable o, Object arg) {
        // Update the label
        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
        targetLabel.setText("TargetNumber: " + controller.getTargetWord());
        String currentGuess = controller.getCurrentGuess().toString();
        // If it's the first row and the guess is empty, reinitialize input fields and keyboard
        if (getCurrentRow() <= 0){
            if (currentGuess.equals("       ")) {
                initInputFields();
                initKeyboard();
            }
            return;
        }
        updateColors();

        // If the game is over, show appropriate message
        if (controller.isGameOver()) {
            if (controller.isGameWon()) {
                JOptionPane.showMessageDialog(frame, "Congratulations! You won!");
            } else {
                JOptionPane.showMessageDialog(frame, "Game over! The target number is " + controller.getTargetWord());
            }
        }
    }


    // Initializes the input field
    private void initInputFields() {
        for (JTextField[] guessText : inputFields) {
            for (JTextField jTextField : guessText) {
                // Clear text and reset background color
                jTextField.setText("");
                jTextField.setBackground(Color.WHITE);
            }
        }
    }

    // Initialize keyboard (number and operator button)
    private void initKeyboard() {
        for (JButton button : numberButtons) {
            // Reset button appearance
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
            button.setOpaque(false);
        }

        for (JButton button : operatorButtons) {
            // Reset button appearance
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
            button.setOpaque(false);
        }
    }


    // Get the current row based on the current number of attempts
    private int getCurrentRow() {
        return INumberleModel.MAX_ATTEMPTS - controller.getRemainingAttempts();
    }

    // Update the color of the input field
    private void updateColors() {
        int[] colorState = model.getColorState();
        Map<String, Integer> characterColorMap = model.getCharColorMap();
        int currentRow = getCurrentRow() - 1;

        if (currentRow >= 0 && currentRow < inputFields.length) {
            for (int i = 0; i < colorState.length; i++) {
                int colorIndex = colorState[i];
                JTextField textField = inputFields[currentRow][i];
                if (textField != null) {
                    // Set background color based on feedback
                    if (colorIndex == 1) {
                        textField.setBackground(new Color(0, 200, 150));
                    } else if (colorIndex == 2) {
                        textField.setBackground(new Color(255, 150, 0));
                    } else if (colorIndex == 3) {
                        textField.setBackground(new Color(160, 160, 180));
                    } else {
                        textField.setBackground(Color.WHITE);
                    }
                }
            }
        }

        // Update button colors based on feedback
        for (JButton button : operatorButtons) {
            setButtonColor(button, characterColorMap.getOrDefault(button.getText(), 0));
        }
        for (JButton button : numberButtons) {
            setButtonColor(button, characterColorMap.getOrDefault(button.getText(), 0));
        }
    }

    // Set button colors based on feedback
    private void setButtonColor(JButton button, int colorIndex) {
        if (colorIndex == 1) {
            button.setForeground(new Color(0, 200, 150));
            button.setBackground(new Color(0, 200, 150));
            button.setOpaque(true);
        } else if (colorIndex == 2) {
            button.setForeground(new Color(255, 150, 0));
            button.setBackground(new Color(255, 150, 0));
            button.setOpaque(true);
        } else if (colorIndex == 3) {
            button.setForeground(new Color(160, 160, 180));
            button.setBackground(new Color(160, 160, 180));
            button.setOpaque(true);
        } else {
            button.setForeground(Color.BLACK);
            button.setBackground(Color.WHITE);
            button.setOpaque(false);
        }
    }

    // Enable or disable the restart button
    public void enableRestartButton(boolean bool) {
        restartButton.setEnabled(bool);
    }

}
