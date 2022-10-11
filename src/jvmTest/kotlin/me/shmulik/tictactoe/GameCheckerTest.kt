package me.shmulik.tictactoe

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GameCheckerTest {
    lateinit var board: Array<Array<String>>

    @BeforeEach
    internal fun setUp() {
        board = Array(10) { Array(10) { "" } }
    }

    @Test
    fun `win horizontally`() {
        board = fillHorizontally(3, board, "X")
        val result = GameChecker.isWinning(board, 3, 3, "X")
        Assertions.assertTrue(result)
    }

    @Test
    fun `no win horizontally`() {
        val filledRowIndex = 2
        fillHorizontally(filledRowIndex, board, "O")
        for (i in 0 until board[0].size step 2) {
            board[filledRowIndex][i] = "X"
        }
        val result = GameChecker.isWinning(board, filledRowIndex, 3, "O")
        Assertions.assertFalse(result)
    }

    @Test
    fun `win vertically`() {
        board = fillVertically(3, board, "X")
        val result = GameChecker.isWinning(board, 3, 3, "X")
        Assertions.assertTrue(result)
    }

    @Test
    fun `no win vertically`() {
        val filledColumnIndex = 3
        board = fillVertically(filledColumnIndex, board, "O")
        for (i in 0 until board.size step 2) {
            board[i][filledColumnIndex] = "X"
        }
        val result = GameChecker.isWinning(board, 3, 3, "X")
        Assertions.assertTrue(result)
    }

    private fun fillHorizontally(row: Int, board: Array<Array<String>>, sign: String): Array<Array<String>> {
        for (i in 0 until board[0].size) {
            board[row][i] = sign
        }
        return board
    }

    private fun fillVertically(col: Int, board: Array<Array<String>>, sign: String): Array<Array<String>> {
        for (i in board.indices) {
            board[i][col] = sign
        }
        return board
    }
}
