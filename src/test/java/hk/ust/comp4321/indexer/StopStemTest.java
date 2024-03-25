package hk.ust.comp4321.indexer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StopStemTest {
    @Test
    public void testIsStopWord() {
        StopStem stopStem = new StopStem("resources/stopwords.txt");
        assertTrue(stopStem.isStopWord("the"));
        assertFalse(stopStem.isStopWord("computer"));
    }

    @Test
    public void testStem() {
        StopStem stopStem = new StopStem("resources/stopwords.txt");
        assertEquals("comput", stopStem.stem("computer"));
        assertEquals("run", stopStem.stem("running"));
    }
}