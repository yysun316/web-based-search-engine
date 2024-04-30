package hk.ust.comp4321.indexer;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PhaseSearchTest {

    @Test
    void testGetPhases() {
        // Initialize StopStem object
        StopStem stopStem = new StopStem("resources/stopwords.txt");

        // Define a test query
        String query = "\"This is a test\" \"Another test phrase\"";

        // Expected result after stop word and stemming process
        ArrayList<String> expected = new ArrayList<>(Arrays.asList("thi is a test", "anoth test phrase"));

        // Call the method to test
        ArrayList<String> result = PhasesSearch.getPhases(query, stopStem);

        // Check if the result matches the expected output
        assertEquals(expected, result);
    }

    @Test
    void testWeightIncreaseByPhase() throws IOException {
    }
}