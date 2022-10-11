package me.shmulik.tictactoe

private const val consecutiveCells = 5

/**
 * Checks for if the last move lead to a winning position
 */
class GameChecker {
    companion object {
        fun isWinning(board: Array<Array<String>>, row: Int, col: Int, sign: String): Boolean {
            return checkHorizontal(board, row, sign) || checkVertical(board, col, sign) ||
                    checkDiagonally(row, col, board, sign) || checkDiagonallyReversed(row, col, board, sign)
        }

        private fun checkHorizontal(board: Array<Array<String>>, row: Int, sign: String): Boolean {
            var consecutive = 0
            for (i in 0 until board[0].size) {
                if (board[row][i] == sign) {
                    consecutive++
                    if (consecutive == consecutiveCells) {
                        return true
                    }
                } else {
                    consecutive = 0
                }
            }
            return false
        }

        private fun checkVertical(board: Array<Array<String>>, col: Int, sign: String): Boolean {
            var consecutive = 0
            for (i in board.indices) {
                if (board[i][col] == sign) {
                    consecutive++
                    if (consecutive == consecutiveCells) {
                        return true
                    }
                } else {
                    consecutive = 0
                }
            }
            return false
        }

        private fun checkDiagonally(
            row: Int,
            col: Int,
            board: Array<Array<String>>,
            sign: String
        ): Boolean {
            var startRow: Int = row
            var startCol: Int = col
            while (startRow - 1 >= 0 && startCol - 1 >= 0) {
                startRow--
                startCol--
            }
            var consecutive = 0
            while (startRow < board.size && startCol < board[0].size) {
                if (board[startRow][startCol] == sign) {
                    consecutive++
                    if (consecutive == consecutiveCells) {
                        return true
                    }
                } else {
                    consecutive = 0
                }
                startRow++
                startCol++
            }
            return false
        }

        private fun checkDiagonallyReversed(
            row: Int,
            col: Int,
            board: Array<Array<String>>,
            sign: String
        ): Boolean {
            var startRow: Int = row
            var startCol: Int = col
            while (startRow - 1 >= 0 && startCol + 1 < board.size) {
                startRow--
                startCol++
            }
            var consecutive = 0
            while (startRow < board.size && startCol >= 0) {
                if (board[startRow][startCol] == sign) {
                    consecutive++
                    if (consecutive == consecutiveCells) {
                        return true
                    }
                } else {
                    consecutive = 0
                }
                startRow++
                startCol--
            }
            return false
        }
    }
}