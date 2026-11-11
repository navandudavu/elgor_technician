package rs.elgor.technician.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test

class HoursPickerTest {

    @Test fun parsesServerAndTypedFormats() {
        assertEquals(0, hoursToHundredths(""))
        assertEquals(150, hoursToHundredths("1.50"))
        assertEquals(150, hoursToHundredths("1,5"))
        assertEquals(225, hoursToHundredths("2.25"))
        assertEquals(300, hoursToHundredths("3"))
        assertEquals(0, hoursToHundredths("abc"))
    }

    @Test fun formatsForServerWithTwoDecimals() {
        assertEquals("0.00", hundredthsToHours(0))
        assertEquals("1.50", hundredthsToHours(150))
        assertEquals("2.05", hundredthsToHours(205))
        assertEquals("1.75", hundredthsToHours(175))
    }

    @Test fun quartersConvertToExactMinutes() {
        assertEquals(0, hundredthsToMinutes(0))
        assertEquals(15, hundredthsToMinutes(25))
        assertEquals(30, hundredthsToMinutes(50))
        assertEquals(45, hundredthsToMinutes(75))
    }

    @Test fun minuteStepsMoveInQuartersAndSnapOddValues() {
        assertEquals(25, stepQuarter(0, up = true))
        assertEquals(75, stepQuarter(75, up = true))
        assertEquals(50, stepQuarter(75, up = false))
        assertEquals(0, stepQuarter(0, up = false))
        assertEquals(50, stepQuarter(33, up = true))    // .33 -> next quarter up
        assertEquals(25, stepQuarter(33, up = false))   // .33 -> previous quarter down
    }
}
