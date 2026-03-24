package org.example.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Stream

class BridgeSceneTest {
    @ParameterizedTest
    @MethodSource("defaultScenes")
    fun `captain is not alone before escort`(scene: BridgeScene) {
        assertFalse(scene.isCaptainAlone())
        assertEquals(DoorState.OPEN, scene.doorState)
        assertTrue(scene.detainees.all { it.location == Location.BRIDGE })
    }

    @ParameterizedTest
    @MethodSource("defaultScenes")
    fun `captain is alone after escort`(scene: BridgeScene) {
        scene.executeEscort()

        assertTrue(scene.isCaptainAlone())
    }

    @ParameterizedTest
    @MethodSource("captainAloneStates")
    fun `captain is alone only when door is closed and both detainees are in corridor`(
        doorState: DoorState,
        firstLocation: Location,
        secondLocation: Location,
        expected: Boolean
    ) {
        val scene = scene()
        scene.doorState = doorState
        scene.detainees[0].location = firstLocation
        scene.detainees[1].location = secondLocation

        assertEquals(expected, scene.isCaptainAlone())
    }

    @ParameterizedTest
    @MethodSource("defaultScenes")
    fun `escort moves both detainees out and closes the door`(scene: BridgeScene) {
        scene.executeEscort()

        assertTrue(scene.isCaptainAlone())
        assertEquals(DoorState.CLOSED, scene.doorState)
        assertTrue(scene.detainees.all { it.location == Location.CORRIDOR })
    }

    @ParameterizedTest
    @ValueSource(strings = ["что-то", "обрывок стиха"])
    fun `captain can store poem fragments in notebook`(fragment: String) {
        val scene = scene()

        scene.captainHum(fragment)

        assertEquals(listOf(fragment), scene.captain.notebook.poems)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 3])
    fun `scene requires exactly two detainees`(count: Int) {
        val scene = BridgeScene(
            captain = Captain("Captain"),
            guard = Guard("Guard"),
            detainees = MutableList(count) { index -> Detainee("Detainee $index") }
        )

        assertThrows(IllegalArgumentException::class.java) {
            scene.executeEscort()
        }
    }

    companion object {
        @JvmStatic
        fun defaultScenes(): Stream<Arguments> =
            Stream.of(Arguments.of(scene()))

        @JvmStatic
        fun captainAloneStates(): Stream<Arguments> =
            Stream.of(
                Arguments.of(DoorState.OPEN, Location.BRIDGE, Location.BRIDGE, false),
                Arguments.of(DoorState.CLOSED, Location.BRIDGE, Location.BRIDGE, false),
                Arguments.of(DoorState.CLOSED, Location.CORRIDOR, Location.BRIDGE, false),
                Arguments.of(DoorState.CLOSED, Location.CORRIDOR, Location.CORRIDOR, true)
            )

        private fun scene(): BridgeScene {
            return BridgeScene(
                captain = Captain("Captain"),
                guard = Guard("Guard"),
                detainees = mutableListOf(Detainee("First"), Detainee("Second"))
            )
        }
    }
}
