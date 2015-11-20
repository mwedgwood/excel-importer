package com.github.mwedgwood;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;

public class ImporterTestRunner {

    public static void main(String[] args) throws Exception {
        InputStream inputStream =
                Thread.currentThread().getContextClassLoader().getResourceAsStream("test-simple.xls");

        Collection<TestClass> results = new Importer<>(TestClass.class).parse(inputStream);
        assertEquals(2, results.size());

        System.exit(0);
    }

    public static class TestClass {
        private String stringField;
        private Date dateField;
        private Double doubleField;

        public String getStringField() {
            return stringField;
        }

        public TestClass setStringField(String stringField) {
            this.stringField = stringField;
            return this;
        }

        public Date getDateField() {
            return dateField;
        }

        public TestClass setDateField(Date dateField) {
            this.dateField = dateField;
            return this;
        }

        public Double getDoubleField() {
            return doubleField;
        }

        public TestClass setDoubleField(Double doubleField) {
            this.doubleField = doubleField;
            return this;
        }
    }
}
