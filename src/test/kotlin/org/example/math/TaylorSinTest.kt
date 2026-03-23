package org.example.math

import kotlin.math.PI
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TaylorSinTest {
    @Test
    fun `returns zero for zero`() {
        assertEquals(0.0, TaylorSin.evaluate(0.0), 1e-15)
    }

    @Test
    fun `matches stdlib for representative values`() {
        val values = listOf(-PI, -PI / 2, -PI / 6, PI / 6, PI / 2, PI)

        values.forEach { x ->
            assertEquals(sin(x), TaylorSin.evaluate(x), 1e-12, "x=$x")
        }
    }

    @Test
    fun `respects periodic normalization for large argument`() {
        val x = 1234 * PI + PI / 3
        assertEquals(sin(x), TaylorSin.evaluate(x), 1e-12)
    }

    @Test
    fun `preserves odd symmetry`() {
        val x = 0.73
        assertEquals(-TaylorSin.evaluate(x), TaylorSin.evaluate(-x), 1e-12)
    }

    @Test
    fun `stops on positive epsilon only`() {
        assertFailsWith<IllegalArgumentException> {
            TaylorSin.evaluate(1.0, epsilon = 0.0)
        }
    }

    @Test
    fun `is accurate for small values`() {
        val x = 1e-8
        assertTrue(TaylorSin.evaluate(x) > 0.0)
        assertEquals(sin(x), TaylorSin.evaluate(x), 1e-20)
    }
}
