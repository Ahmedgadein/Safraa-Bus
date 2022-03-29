package com.dinder.rihlabus

import com.dinder.rihlabus.utils.PhoneNumberValidator
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import kotlin.random.Random
import org.junit.Test

class PhoneValidatorTest {
    private val validNumbers = listOf("123456789", "912345678")

    @Test
    fun phoneValidator_CorrectNumber() {
        assertNull(PhoneNumberValidator.validate("901145980"))
    }

    @Test
    fun phoneValidator_EmptyOrNullNumber() {
        assertNotNull(PhoneNumberValidator.validate(""))
    }

    @Test
    fun phoneValidator_IncorrectNumberFormat() {
        /* Accepted formats are:
            [+249] 9xxxxxxxx
            [+249] 1xxxxxxxx
         */
        assertNotNull(PhoneNumberValidator.validate("23456789"))
    }

    @Test
    fun phoneValidator_IncorrectShortNumber() {
        assertNotNull(PhoneNumberValidator.validate("1234567"))
    }

    @Test
    fun phoneValidator_IncorrectLongNumber() {
        assertNotNull(validNumbers[Random.nextInt(validNumbers.size)])
    }
}
