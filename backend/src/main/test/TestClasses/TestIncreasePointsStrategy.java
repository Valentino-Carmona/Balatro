package TestClasses;

import com.balatro.modelo.IncreasePointsStrategy;
import com.balatro.modelo.Score;
import com.balatro.modelo.ScoringStrategy;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class TestIncreasePointsStrategy {

    @Test
    public void Test01UnIncreaseStrategyAumentaLosPuntosDelScore () {
        ScoringStrategy strategy = new IncreasePointsStrategy(10);
        Score score = new Score(10,10,0);

        strategy.apply(score);

        int expectedValue = 200;

        assertEquals(expectedValue, score.getTotalPoints(), "Devolvio el puntaje final correcto luego de aplicar la estrategia");
    }
}
