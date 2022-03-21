package com.dinder.rihlabus

import com.dinder.rihlabus.utils.NameValidator
import junit.framework.Assert.*
import org.junit.Test

class NameValidatorTest {
    @Test
    fun nameValidator_CorrectName() {
        assertNull(NameValidator.validate("Ahmed Gadein"))
    }

    @Test
    fun nameValidator_IncorrectShortName() {
        assertNotNull(NameValidator.validate("Ahmed"))
    }

    @Test
    fun nameValidator_IncorrectEmptyName() {
        assertNotNull(NameValidator.validate(""))
    }
}