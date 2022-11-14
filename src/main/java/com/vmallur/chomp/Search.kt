package com.vmallur.chomp

import kotlin.math.max
import kotlin.math.min

class Search(private val rows: Int, private val columns: Int) {

    fun minimax(squares: List<Boolean>, depth: Int, alpha: Int, beta: Int, isMaximizingPlayer: Boolean): Int {
        if (isWinning(squares)) {
            return if (isMaximizingPlayer) 1 else -1
        }
        if (depth == 0 || !squares.contains(true) || isWinning(squares)) {
            return getEval(squares, isMaximizingPlayer)
        }
        else if (isMaximizingPlayer) {
            var maxEval = -1
            for (child in getChildSquares(squares).values) {
                val eval = minimax(child, depth - 1, alpha, beta, false)
                maxEval = max(maxEval, eval)
                val alpha = max(alpha, eval)
                if (beta <= alpha)
                    break
            }
            return maxEval
        }
        else {
            var minEval = 1
            for (child in getChildSquares(squares).values) {
                val eval = minimax(child, depth - 1, alpha, beta, true)
                minEval = min(minEval, eval)
                val beta = min(beta, eval)
                if (beta <= alpha)
                    break
            }
            return minEval
        }
    }

    fun getChildSquares(squares: List<Boolean>): HashMap<Int, List<Boolean>> {
        val childSquares = HashMap<Int, List<Boolean>>()
        for (index in squares.indices) {
            if (squares[index]) {
                childSquares[index] = getChompedSquares(index, squares)
            }
        }
        return childSquares
    }

    fun getChompedSquares(index: Int, squares: List<Boolean>): List<Boolean> {
        val chompedSquares = squares.toMutableList()
        for (i in index until (columns * ((index/columns) + 1))) {
            var j = i
            while (j < rows * columns) {
                chompedSquares[j] = false
                j += columns
            }
        }
        return chompedSquares
    }

    private fun getEval(squares: List<Boolean>, isMaximizingPlayer: Boolean): Int {
        if (!squares.contains(true)) {
            return if (isMaximizingPlayer) -1 else 1
        }
        return 0
    }

    fun isWinning(squares: List<Boolean>): Boolean {
        var width = 0
        for (i in 1 until columns) {
            if (squares[i]) {
                width++
            }
        }
        var height = 0
        for (i in columns until rows * columns step columns) {
            if (squares[i]) {
                height++
            }
        }
        return (width == height && squares[columns + 1])
    }
}