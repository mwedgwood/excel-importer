package com.github.mwedgwood;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImporterTest {

    @Test
    public void testExtractColumnType() throws Exception {
        String testType = "[stringField]";

        assertEquals("stringField", new Importer<>(ImporterTestRunner.TestClass.class).extractColumnType(testType));
    }
}
