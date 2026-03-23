package org.example.domain

enum class DoorState {
    OPEN,
    CLOSED
}

enum class Location {
    BRIDGE,
    CORRIDOR
}

data class Captain(val name: String, val notebook: Notebook = Notebook())

data class Guard(val name: String)

data class Detainee(val name: String, var location: Location = Location.BRIDGE)

data class Notebook(val poems: MutableList<String> = mutableListOf())

class BridgeScene(
    val captain: Captain,
    val guard: Guard,
    val detainees: MutableList<Detainee>,
    var doorState: DoorState = DoorState.OPEN
) {
    fun executeEscort() {
        require(detainees.size == 2) { "The scene requires exactly two detainees" }

        detainees.forEach { it.location = Location.CORRIDOR }
        doorState = DoorState.CLOSED
    }

    fun captainHum(poemFragment: String) {
        captain.notebook.poems += poemFragment
    }

    fun isCaptainAlone(): Boolean {
        return doorState == DoorState.CLOSED && detainees.all { it.location == Location.CORRIDOR }
    }
}
