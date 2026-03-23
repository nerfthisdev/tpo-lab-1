package org.example.math

import kotlin.math.PI
import kotlin.math.abs

object TaylorSin {
    fun evaluate(x: Double, epsilon: Double = 1e-12, maxIterations: Int = 10_000): Double {
        require(epsilon > 0) { "epsilon must be positive" }
        require(maxIterations > 0) { "maxIterations must be positive" }

        val normalizedX = normalize(x)
        var term = normalizedX
        var sum = term
        var n = 1

        while (abs(term) > epsilon && n < maxIterations) {
            term *= -normalizedX * normalizedX / ((2 * n).toDouble() * (2 * n + 1).toDouble())
            sum += term
            n++
        }

        return sum
    }

    internal fun normalize(x: Double): Double {
        val period = 2 * PI
        var reduced = x % period
        if (reduced > PI) reduced -= period
        if (reduced < -PI) reduced += period
        return reduced
    }
}
