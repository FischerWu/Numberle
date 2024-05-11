import java.util.Map;

public interface INumberleModel {
    int MAX_ATTEMPTS = 6;

    void initialize();
    boolean processInput(String expression);
    boolean isGameOver();
    boolean isGameWon();
    String getTargetNumber();
    StringBuilder getCurrentGuess();
    int getRemainingAttempts();
    void startNewGame();
    int[] getColorState();
    Map<String,Integer> getCharColorMap();
    boolean getFlag1();
    boolean getFlag2();
    boolean getFlag3();
    void setFlag1();
    void setFlag2();
    void setFlag3();
}