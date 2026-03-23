package org.example.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BridgeSceneTest {
    @Test
    fun `captain is not alone before escort`() {
        val scene = scene()

        assertFalse(scene.isCaptainAlone())
        assertEquals(DoorState.OPEN, scene.doorState)
        assertTrue(scene.detainees.all { it.location == Location.BRIDGE })
    }

    @Test
    fun `escort moves both detainees out and closes the door`() {
        val scene = scene()

        scene.executeEscort()

        assertTrue(scene.isCaptainAlone())
        assertEquals(DoorState.CLOSED, scene.doorState)
        assertTrue(scene.detainees.all { it.location == Location.CORRIDOR })
    }

    @Test
    fun `captain can store poem fragment in notebook`() {
        val scene = scene()

        scene.captainHum("что-то")

        assertEquals(listOf("что-то"), scene.captain.notebook.poems)
    }

    @Test
    fun `scene requires exactly two detainees`() {
        val scene = BridgeScene(
            captain = Captain("Captain"),
            guard = Guard("Guard"),
            detainees = mutableListOf(Detainee("Only one"))
        )

        assertFailsWith<IllegalArgumentException> {
            scene.executeEscort()
        }
    }

    private fun scene(): BridgeScene {
        return BridgeScene(
            captain = Captain("Captain"),
            guard = Guard("Guard"),
            detainees = mutableListOf(Detainee("First"), Detainee("Second"))
        )
    }
}
