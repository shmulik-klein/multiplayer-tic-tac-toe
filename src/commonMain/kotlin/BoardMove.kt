import kotlinx.serialization.Serializable

/**
 * Represents a single move (click) on the tic-tac-toe board
 * @param x the row index of the cell that were chosen
 * @param y the column index the cell that were chosen
 * @param sign the sign to put in the cell
 */
@Serializable
data class BoardMove(val x: Int, val y: Int, val sign: String)
