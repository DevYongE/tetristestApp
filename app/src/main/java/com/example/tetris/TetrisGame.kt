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
    
    // Item system
    private val activeItems = mutableListOf<ActiveItem>()
    private val inventory = mutableListOf<GameItem>()
    private var itemSpawnTimer = 0
    private val itemSpawnInterval = 50 // Every 50 pieces
    private var slowMotionMultiplier = 1.0
    private var freezeTimeRemaining = 0L
    private var multiClearActive = false
    private var ghostPieceEnabled = false
    
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
    
    // Item system methods
    fun updateItems(currentTime: Long) {
        // Remove expired items
        activeItems.removeAll { it.isExpired(currentTime) }
        
        // Update item effects
        updateActiveEffects(currentTime)
        
        // Spawn new items occasionally
        if (itemSpawnTimer >= itemSpawnInterval) {
            val newItem = GameItem.createRandomItem()
            inventory.add(newItem)
            itemSpawnTimer = 0
        }
    }
    
    private fun updateActiveEffects(currentTime: Long) {
        slowMotionMultiplier = 1.0
        freezeTimeRemaining = 0L
        multiClearActive = false
        ghostPieceEnabled = false
        
        for (activeItem in activeItems) {
            when (activeItem.item.type) {
                ItemType.SLOW_MOTION -> slowMotionMultiplier = 0.5
                ItemType.FREEZE -> freezeTimeRemaining = activeItem.getRemainingTime(currentTime)
                ItemType.MULTI_CLEAR -> multiClearActive = true
                ItemType.GHOST_PIECE -> ghostPieceEnabled = true
                else -> {} // Instant effects handled elsewhere
            }
        }
    }
    
    fun useItem(itemIndex: Int): Boolean {
        if (itemIndex < 0 || itemIndex >= inventory.size) return false
        
        val item = inventory.removeAt(itemIndex)
        val currentTime = System.currentTimeMillis()
        
        when (item.type) {
            ItemType.CLEAR_LINE -> {
                // Remove bottom line
                for (row in board.height - 1 downTo 1) {
                    for (col in 0 until board.width) {
                        if (board.getCell(col, row - 1) != 0) {
                            board.clearSingleLine(row)
                            break
                        }
                    }
                }
                return true
            }
            ItemType.LINE_BOMB -> {
                // Remove a specific line (could be enhanced with user selection)
                val targetRow = board.height - 5 // Remove 5th row from bottom
                if (targetRow >= 0) {
                    board.clearSingleLine(targetRow)
                }
                return true
            }
            ItemType.BOMB -> {
                // Remove 3x3 area around center of board
                val centerX = board.width / 2
                val centerY = board.height / 2
                for (dy in -1..1) {
                    for (dx in -1..1) {
                        val x = centerX + dx
                        val y = centerY + dy
                        if (x in 0 until board.width && y in 0 until board.height) {
                            board.clearCell(x, y)
                        }
                    }
                }
                return true
            }
            else -> {
                // Duration-based items
                if (item.duration > 0) {
                    val activeItem = ActiveItem(item, currentTime)
                    activeItems.add(activeItem)
                    return true
                }
            }
        }
        return false
    }
    
    fun getGameSpeed(): Long {
        val baseSpeed = maxOf(100L, 800L - (level - 1) * 50L)
        return (baseSpeed * slowMotionMultiplier).toLong()
    }
    
    fun isFrozen(): Boolean = freezeTimeRemaining > 0
    
    fun getActiveItems(): List<ActiveItem> = activeItems.toList()
    
    fun getInventory(): List<GameItem> = inventory.toList()
    
    fun getScoreMultiplier(): Int = if (multiClearActive) 2 else 1
    
    fun isGhostPieceEnabled(): Boolean = ghostPieceEnabled
    
    private fun spawnItemOccasionally() {
        itemSpawnTimer++
        if (itemSpawnTimer >= itemSpawnInterval) {
            val newItem = GameItem.createRandomItem()
            inventory.add(newItem)
            itemSpawnTimer = 0
        }
    }
}