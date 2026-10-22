package cafe.adriel.lyricist.processor.xml.internal.ktx

import org.junit.Assert.assertEquals
import org.junit.Test

public class OutputTest {
    @Test
    public fun escapedPercentDoesNotBecomeAFormatParameter() {
        assertEquals(emptyList<String>(), "%%d".params)
        assertEquals("%d", java.lang.String.format(java.util.Locale.ROOT, "%%d"))
        assertEquals(emptyList<String>(), "%%s".params)
        assertEquals(emptyList<String>(), "%%%%d".params)
    }

    @Test
    public fun escapedPercentDoesNotHideFollowingFormatParameter() {
        assertEquals(listOf("Int"), "%%%d".params)
        assertEquals(listOf("String"), "%%%s".params)
        assertEquals(listOf("Int"), "%%%%%d".params)
    }

    @Test
    public fun parsesOnlySupportedUnescapedParameters() {
        assertEquals(listOf("String", "Int"), "%1\$s %2\$d".params)
    }
}
