package me.shmulik.tictactoe

class GameChecker {
    fun isWinning(board: Array<Array<String>>, move: String): Boolean {
        return true
    }

    companion object {
        fun isWinning(board: Array<Array<String>>, row: Int, col: Int, sign: String): Boolean {
            return checkHorizontal(board, row, sign) || checkVertical(board, col, sign)
        }

        private fun checkHorizontal(board: Array<Array<String>>, row: Int, sign: String): Boolean {
            var consecutive = 0
            for (i in 0 until board[0].size) {
                if (board[row][i] == sign) {
                    consecutive++
                    if (consecutive == 5) {
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
                    if (consecutive == 5) {
                        return true
                    }
                } else {
                    consecutive = 0
                }
            }
            return false
        }
    }
}