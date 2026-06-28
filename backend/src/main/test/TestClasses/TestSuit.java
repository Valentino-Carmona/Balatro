package TestClasses;

import com.balatro.modelo.Suit;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class TestSuit {

    @Test
    public void Test01UnSuitTieneNombreIgualAOtroDelMismoSuit () {
        Suit suit1 = Suit.DIAMONDS;
        Suit suit2 = Suit.DIAMONDS;

        assertTrue(suit1.equalsSuit(suit2));
    }
}
