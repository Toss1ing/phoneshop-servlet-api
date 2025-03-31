package com.es.phoneshop.utility;

import org.junit.Test;

import java.text.ParseException;
import java.util.Locale;

import static org.junit.Assert.*;

public class InputValidatorTest {

    @Test
    public void testValidQuantity() {
        assertFalse(InputValidator.isInvalidQuantity("10"));
        assertFalse(InputValidator.isInvalidQuantity("5.5"));
        assertFalse(InputValidator.isInvalidQuantity("1,5"));
    }

    @Test
    public void testInvalidQuantity() {
        assertTrue(InputValidator.isInvalidQuantity("0"));
        assertTrue(InputValidator.isInvalidQuantity("012"));
        assertTrue(InputValidator.isInvalidQuantity("abc"));
        assertTrue(InputValidator.isInvalidQuantity(""));
    }

    @Test
    public void testParseValidQuantity() throws ParseException {
        assertEquals(10, InputValidator.parseQuantity("10", Locale.US));
        assertEquals(1000, InputValidator.parseQuantity("1,000", Locale.US));
    }

    @Test(expected = ParseException.class)
    public void testParseInvalidQuantity() throws ParseException {
        InputValidator.parseQuantity("abc", Locale.US);
    }

    @Test
    public void testParseQuantityWithDotInGermanLocale() throws ParseException {
        assertEquals(1000, InputValidator.parseQuantity("1.000", Locale.GERMANY));
    }
}
