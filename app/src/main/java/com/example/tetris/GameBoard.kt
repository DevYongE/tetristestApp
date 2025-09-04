package com.example.tetris

class GameBoard(val width: Int = 10, val height: Int = 20) {
    private val grid = Array(height) { IntArray(width) { 0 } }
    
    fun isValidPosition(tetromino: Tetromino, x: Int, y: Int, rotation: Int): Boolean {
        val shape = tetromino.getRotatedShape(rotation)
        
        for (row in shape.indices) {
            for (col in shape[row].indices) {
                if (shape[row][col] != 0) {
                    val newX = x + col
                    val newY = y + row
                    
                    if (newX < 0 || newX >= width || newY < 0 || newY >= height) {
                        return false
                    }
                    
                    if (grid[newY][newX] != 0) {
                        return false
                    }
                }
            }
        }
        return true
    }
    
    fun placePiece(tetromino: Tetromino, x: Int, y: Int, rotation: Int) {
        val shape = tetromino.getRotatedShape(rotation)
        val color = tetromino.color
        
        for (row in shape.indices) {
            for (col in shape[row].indices) {
                if (shape[row][col] != 0) {
                    val newX = x + col
                    val newY = y + row
                    if (newX in 0 until width && newY in 0 until height) {
                        grid[newY][newX] = color
                    }
                }
            }
        }
    }
    
    fun clearFullLines(): Int {
        var linesCleared = 0
        
        for (row in height - 1 downTo 0) {
            if (isLineFull(row)) {
                clearLine(row)
                linesCleared++
            }
        }
        
        return linesCleared
    }
    
    private fun isLineFull(row: Int): Boolean {
        return grid[row].all { it != 0 }
    }
    
    private fun clearLine(lineIndex: Int) {
        for (row in lineIndex downTo 1) {
            grid[row] = grid[row - 1].clone()
        }
        grid[0] = IntArray(width) { 0 }
    }
    
    fun getCell(x: Int, y: Int): Int {
        return if (x in 0 until width && y in 0 until height) {
            grid[y][x]
        } else {
            0
        }
    }
    
    fun isGameOver(): Boolean {
        return grid[0].any { it != 0 }
    }
    
    fun clear() {
        for (row in grid.indices) {
            for (col in grid[row].indices) {
                grid[row][col] = 0
            }
        }
    }
    
    // Additional methods for item system
    fun clearSingleLine(lineIndex: Int) {
        if (lineIndex in 0 until height) {
            for (row in lineIndex downTo 1) {
                grid[row] = grid[row - 1].clone()
            }
            grid[0] = IntArray(width) { 0 }
        }
    }
    
    fun clearCell(x: Int, y: Int) {
        if (x in 0 until width && y in 0 until height) {
            grid[y][x] = 0
        }
    }
    
    fun fillRandomLine(): Int {
        // Add a random line at the bottom for added difficulty
        for (row in 0 until height - 1) {
            grid[row] = grid[row + 1].clone()
        }
        
        // Fill bottom line with random blocks, leaving some gaps
        for (col in 0 until width) {
            grid[height - 1][col] = if ((0..100).random() < 70) {
                (1..7).random() // Random piece color
            } else {
                0 // Empty space
            }
        }
        
        return 1 // Return lines added
    }
}