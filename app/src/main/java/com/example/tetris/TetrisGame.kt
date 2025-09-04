package com.example.tetris

class TetrisGame {
    private val board = GameBoard()
    private var currentPiece: Tetromino? = null
    private var currentX = 0
    private var currentY = 0
    private var currentRotation = 0
    private var nextPiece: Tetromino = Tetromino.random()
    
    var score = 0
        private set
    var level = 1
        private set
    var linesCleared = 0
        private set
    var isGameOver = false
        private set
    var isPaused = false
        private set
    
    init {
        spawnNewPiece()
    }
    
    private fun spawnNewPiece() {
        currentPiece = nextPiece
        nextPiece = Tetromino.random()
        currentX = board.width / 2 - 1
        currentY = 0
        currentRotation = 0
        
        if (!isValidMove(currentX, currentY, currentRotation)) {
            isGameOver = true
        }
    }
    
    fun moveLeft(): Boolean {
        if (isPaused || isGameOver) return false
        return if (isValidMove(currentX - 1, currentY, currentRotation)) {
            currentX--
            true
        } else false
    }
    
    fun moveRight(): Boolean {
        if (isPaused || isGameOver) return false
        return if (isValidMove(currentX + 1, currentY, currentRotation)) {
            currentX++
            true
        } else false
    }
    
    fun moveDown(): Boolean {
        if (isPaused || isGameOver) return false
        return if (isValidMove(currentX, currentY + 1, currentRotation)) {
            currentY++
            true
        } else {
            lockPiece()
            false
        }
    }
    
    fun rotate(): Boolean {
        if (isPaused || isGameOver) return false
        val newRotation = (currentRotation + 1) % (currentPiece?.getMaxRotations() ?: 1)
        return if (isValidMove(currentX, currentY, newRotation)) {
            currentRotation = newRotation
            true
        } else false
    }
    
    fun drop() {
        if (isPaused || isGameOver) return
        while (moveDown()) {}
    }
    
    private fun isValidMove(x: Int, y: Int, rotation: Int): Boolean {
        return currentPiece?.let { piece ->
            board.isValidPosition(piece, x, y, rotation)
        } ?: false
    }
    
    private fun lockPiece() {
        currentPiece?.let { piece ->
            board.placePiece(piece, currentX, currentY, currentRotation)
            
            val clearedLines = board.clearFullLines()
            if (clearedLines > 0) {
                linesCleared += clearedLines
                score += calculateScore(clearedLines)
                level = (linesCleared / 10) + 1
            }
            
            if (board.isGameOver()) {
                isGameOver = true
            } else {
                spawnNewPiece()
            }
        }
    }
    
    private fun calculateScore(lines: Int): Int {
        return when (lines) {
            1 -> 40 * level
            2 -> 100 * level
            3 -> 300 * level
            4 -> 1200 * level
            else -> 0
        }
    }
    
    fun pause() {
        isPaused = true
    }
    
    fun resume() {
        isPaused = false
    }
    
    fun restart() {
        board.clear()
        score = 0
        level = 1
        linesCleared = 0
        isGameOver = false
        isPaused = false
        spawnNewPiece()
    }
    
    fun getBoardState(): Array<IntArray> {
        val state = Array(board.height) { row ->
            IntArray(board.width) { col ->
                board.getCell(col, row)
            }
        }
        
        currentPiece?.let { piece ->
            val shape = piece.getRotatedShape(currentRotation)
            for (row in shape.indices) {
                for (col in shape[row].indices) {
                    if (shape[row][col] != 0) {
                        val x = currentX + col
                        val y = currentY + row
                        if (x in 0 until board.width && y in 0 until board.height) {
                            state[y][x] = piece.color
                        }
                    }
                }
            }
        }
        
        return state
    }
    
    fun getNextPieceShape(): Array<IntArray> {
        return nextPiece.getRotatedShape(0)
    }
}