package com.flipkart.sherlock.semantic.autosuggest.helpers;

import com.flipkart.sherlock.semantic.mocks.autosuggest.helpers.MockAutoSuggestQueryAnalyzer;
import org.junit.Before;
import org.junit.Test;

import static com.flipkart.sherlock.semantic.autosuggest.helpers.AutoSuggestQueryAnalyzer.getCleanQuery;
import static org.junit.Assert.*;

/**
 * Created by dhruv.pancholi on 16/10/17.
 */
public class AutoSuggestQueryAnalyzerTest {

    private AutoSuggestQueryAnalyzer autoSuggestQueryAnalyzer;

    @Before
    public void setUp() {
        autoSuggestQueryAnalyzer = new MockAutoSuggestQueryAnalyzer().getAutoSuggestQueryAnalyzer();
    }

    @Test
    public void testGetCleanQuery() throws Exception {
        assertEquals("abx   abx&uh   ws23", getCleanQuery("abx+, ABX^&\\uh   ws23"));
    }

    @Test
    public void testIsDisabled() throws Exception {
        assertFalse(autoSuggestQueryAnalyzer.isDisabled("dhruv"));

        assertTrue(autoSuggestQueryAnalyzer.isDisabled("12345678901234567890123456789012345678901234567890"));
        assertFalse(autoSuggestQueryAnalyzer.isDisabled("p"));
        assertFalse(autoSuggestQueryAnalyzer.isDisabled("pa"));
        assertTrue(autoSuggestQueryAnalyzer.isDisabled("pan"));
        assertTrue(autoSuggestQueryAnalyzer.isDisabled("panc"));
        assertTrue(autoSuggestQueryAnalyzer.isDisabled("panch"));
        assertTrue(autoSuggestQueryAnalyzer.isDisabled("pancho"));
        assertTrue(autoSuggestQueryAnalyzer.isDisabled("panchol"));
        assertTrue(autoSuggestQueryAnalyzer.isDisabled("pancholi"));
    }
}