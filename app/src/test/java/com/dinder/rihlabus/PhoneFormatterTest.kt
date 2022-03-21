package com.dinder.rihlabus

import com.dinder.rihlabus.utils.PhoneNumberFormatter
import junit.framework.Assert.assertEquals
import org.junit.Test

class PhoneFormatterTest {
    @Test
    fun phoneFormatter_AddCountryCode_ReturnsEqual() {
        assertEquals(PhoneNumberFormatter.getFullNumber("123456789"), "+249123456789")
    }
}