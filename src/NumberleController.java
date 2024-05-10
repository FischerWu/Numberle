// NumberleController.java
public class NumberleController {
    private INumberleModel model;
    private NumberleView view;

    public NumberleController(INumberleModel model) {
        this.model = model;
    }

    public void setView(NumberleView view) {
        this.view = view;
    }

    public boolean processInput(String input) {
        return model.processInput(input);
    }

    public boolean isGameOver() {
        return model.isGameOver();
    }

    public boolean isGameWon() {
        return model.isGameWon();
    }


    public String getTargetWord() {
        return model.getTargetNumber();
    }

    public StringBuilder getCurrentGuess() {

        return model.getCurrentGuess();
    }

    public int getRemainingAttempts() {
        return model.getRemainingAttempts();
    }

    public void startNewGame() {
        model.startNewGame();
    }

    public boolean getFlag1() {
        return model.getFlag1();
    }

    public boolean getFlag2() {
        return model.getFlag2();
    }

    public boolean getFlag3() {
        return model.getFlag3();
    }

    public void changeFlag1() {
        model.setFlag1();
    }

    public void changeFlag2() {
        model.setFlag2();
    }

    public void changeFlag3() {
        model.setFlag3();
    }


}