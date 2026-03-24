package org.example.math

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.math.PI
import kotlin.math.sin

class TaylorSinTest {
    @ParameterizedTest
    @MethodSource("zeroCases")
    fun `returns zero for zero`(x: Double, expected: Double, delta: Double) {
        assertEquals(expected, TaylorSin.evaluate(x), delta)
    }

    @ParameterizedTest
    @MethodSource("representativeValues")
    fun `matches stdlib for representative values`(x: Double) {
        assertEquals(sin(x), TaylorSin.evaluate(x), 1e-12, "x=$x")
    }

    @ParameterizedTest
    @MethodSource("largeArguments")
    fun `respects periodic normalization for large argument`(x: Double) {
        assertEquals(sin(x), TaylorSin.evaluate(x), 1e-12)
    }

    @ParameterizedTest
    @MethodSource("oddSymmetryValues")
    fun `preserves odd symmetry`(x: Double) {
        assertEquals(-TaylorSin.evaluate(x), TaylorSin.evaluate(-x), 1e-12)
    }

    @ParameterizedTest
    @MethodSource("invalidEpsilons")
    fun `stops on positive epsilon only`(epsilon: Double) {
        assertThrows(IllegalArgumentException::class.java) {
            TaylorSin.evaluate(1.0, epsilon = epsilon)
        }
    }

    @ParameterizedTest
    @MethodSource("smallValues")
    fun `is accurate for small values`(x: Double, delta: Double) {
        assertTrue(TaylorSin.evaluate(x) > 0.0)
        assertEquals(sin(x), TaylorSin.evaluate(x), delta)
    }

    companion object {
        @JvmStatic
        fun zeroCases(): Stream<Arguments> =
            Stream.of(Arguments.of(0.0, 0.0, 1e-15))

        @JvmStatic
        fun representativeValues(): Stream<Arguments> =
            Stream.of(
                Arguments.of(-PI),
                Arguments.of(-PI / 2),
                Arguments.of(-PI / 6),
                Arguments.of(PI / 6),
                Arguments.of(PI / 2),
                Arguments.of(PI)
            )

        @JvmStatic
        fun largeArguments(): Stream<Arguments> =
            Stream.of(Arguments.of(1234 * PI + PI / 3))

        @JvmStatic
        fun oddSymmetryValues(): Stream<Arguments> =
            Stream.of(Arguments.of(0.73))

        @JvmStatic
        fun invalidEpsilons(): Stream<Arguments> =
            Stream.of(
                Arguments.of(0.0),
                Arguments.of(-1e-12)
            )

        @JvmStatic
        fun smallValues(): Stream<Arguments> =
            Stream.of(Arguments.of(1e-8, 1e-20))
    }
}
