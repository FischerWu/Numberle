import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class NumberleModelTest {
    INumberleModel instance;

    @Before
    public void setUp() {
        instance = new NumberleModel();
        instance.initialize();
    }

    @After
    public void tearDown() {
        instance = null;
    }

    // Scenario: Testing if the model initializes correctly with default settings.
    @Test
    public void testInitializeWithDefaultSettings() {
        instance.initialize();


        assertEquals(7, instance.getCurrentGuess().length());
        assertEquals(NumberleModel.MAX_ATTEMPTS, instance.getRemainingAttempts());
        assertFalse(instance.isGameWon());
    }

    // Scenario: Testing if the model correctly processes a valid guess and marks the game as won.
    @Test
    public void testProcessInputWithValidGuess() {
        String targetNumber = instance.getTargetNumber();

        boolean result = instance.processInput(targetNumber);
        assertTrue(result);
        assertTrue(instance.isGameWon());
    }

    // Scenario: Testing if the model handles an invalid guess correctly, reducing attempts but not marking the game as won.
    @Test
    public void testProcessInputWithInvalidGuess() {
        String invalidGuess = "1+1=3";

        boolean result = instance.processInput(invalidGuess);

        assertFalse(result);
        assertFalse(instance.isGameWon());
        assertTrue(instance.getRemainingAttempts() < NumberleModel.MAX_ATTEMPTS);
    }
}
