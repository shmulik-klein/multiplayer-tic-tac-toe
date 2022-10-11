import kotlinx.serialization.Serializable

/**
 * Represents the information of a single room and its current game board
 */
@Serializable
data class Room(val id: String, var numOfPlayers: Int, var board: Array<Array<String>>, var lastMove: String, var winner: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Room

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
