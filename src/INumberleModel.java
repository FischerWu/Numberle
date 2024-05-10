import java.util.Map;

public interface INumberleModel {
    int MAX_ATTEMPTS = 6;

    void initialize();
    boolean processInput(String input);
    boolean isGameOver();
    boolean isGameWon();
    String getTargetNumber();
    StringBuilder getCurrentGuess();
    int getRemainingAttempts();
    void startNewGame();
    int[] getColorState();
    Map<String,Integer> getCharacterColorMap();
    boolean getFlag1();
    boolean getFlag2();
    boolean getFlag3();
    void setFlag1();
    void setFlag2();
    void setFlag3();
}