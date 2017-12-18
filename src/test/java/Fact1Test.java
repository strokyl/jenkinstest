import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Fact1Test {

    @Test
    public void testOne() {
        assertEquals(1, Fact.fact(1));
    }
}
