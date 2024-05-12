import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NumberleModelTest {
    INumberleModel instance;

    @Before
    public void setUp() {
        instance = new NumberleModel();
        instance.startNewGame();
    }

    @After
    public void tearDown() {
        instance = null;
    }

    /*@ Scenario: Start the game, the player enters the wrong answer four times,
    @   input the correct answer once, the game wins.
    @ requires MAX_ATTEMPTS == 6;
    @ requires flag3 = true;
    @ ensures targetNumber is not null;
    @ ensures currentGuess.equals("       ");
    @ ensures remainingAttempts = 1;
    @ ensures gameWon = true;
    @*/
    @Test
    public void testCase1() {
        instance.initialize();
        String errorExpression = "6+6-3=9";
        String correctExpression = "6*1-3=3";
        int attemptsAfter = 1;
        assertNotNull("Target number should not be null", instance.getTargetNumber());
        assertEquals("Current guess should be a string of three spaces", "       ", instance.getCurrentGuess().toString());
        assertEquals("Remaining attempts should be equal to MAX_ATTEMPTS", INumberleModel.MAX_ATTEMPTS, instance.getRemainingAttempts());
        assertFalse("Game should not be won upon initialization", instance.isGameWon());

        assertTrue(instance.processInput(errorExpression));
        assertTrue(instance.processInput(errorExpression));
        assertTrue(instance.processInput(errorExpression));
        assertTrue(instance.processInput(errorExpression));
        assertTrue(instance.processInput(correctExpression));
        assertEquals("Remaining attempts should not change", attemptsAfter, instance.getRemainingAttempts());
        assertTrue(instance.isGameWon());
    }


    /*@ Start the game, the player enters the wrong answer 6 times, the game over.
    @ requires flag3 is true;
    @ requires expression != targetNumber;
    @ ensures gameOver = true;
    @*/
    @Test
    public void testCase2() {
        String errorExpression = "6+6-3=9";
        for(int i = 0; i < 6; i++) {
            assertTrue(instance.processInput(errorExpression));
        }
        assertTrue(instance.isGameOver());
    }

    /*@ Start the game, the player enters the invalid equation once,
    @   and then enters the correct equation once, the game wins.
    @ requires flag3 is true
    @ ensures remainingAttempts == old(remainingAttempts) - 1;
    @ ensures gameWon == true;
    @*/
    @Test
    public void testCase3() {
        String invalidExpression = "12";
        int attemptsBefore = instance.getRemainingAttempts();
        boolean gameWonBefore = instance.isGameWon();

        boolean result = instance.processInput(invalidExpression);

        assertFalse("Processing invalid input should return false", result);
        assertEquals("Remaining attempts should not change", attemptsBefore, instance.getRemainingAttempts());
        assertEquals("Game won state should remain the same", gameWonBefore, instance.isGameWon());

        String correctExpression = "6*1-3=3";
        boolean result2 = instance.processInput(correctExpression);
        assertTrue("Processing valid input should return true", result2);
        assertTrue("Game should be won if the guess is correct", instance.isGameWon());
    }
}
