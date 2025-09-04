package com.example.tetris

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class TetrisView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var game: TetrisGame? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var cellSize = 0f
    private var boardStartX = 0f
    private var boardStartY = 0f
    private val cornerRadius = 8f
    
    // Enhanced color schemes with gradients
    private val pieceColors = arrayOf(
        intArrayOf(Color.parseColor("#2D2D2D"), Color.parseColor("#1A1A1A")), // Empty - dark gray gradient
        intArrayOf(Color.parseColor("#00FFFF"), Color.parseColor("#0099CC")), // I - cyan gradient
        intArrayOf(Color.parseColor("#FFFF00"), Color.parseColor("#CCCC00")), // O - yellow gradient
        intArrayOf(Color.parseColor("#FF00FF"), Color.parseColor("#CC00CC")), // T - magenta gradient
        intArrayOf(Color.parseColor("#00FF00"), Color.parseColor("#00CC00")), // S - green gradient
        intArrayOf(Color.parseColor("#FF0000"), Color.parseColor("#CC0000")), // Z - red gradient
        intArrayOf(Color.parseColor("#0080FF"), Color.parseColor("#0066CC")), // J - blue gradient
        intArrayOf(Color.parseColor("#FFA500"), Color.parseColor("#CC8400"))  // L - orange gradient
    )
    
    init {
        gridPaint.color = Color.parseColor("#404040")
        gridPaint.style = Paint.Style.STROKE
        gridPaint.strokeWidth = 1.5f
        
        shadowPaint.color = Color.parseColor("#40000000")
        shadowPaint.maskFilter = BlurMaskFilter(6f, BlurMaskFilter.Blur.NORMAL)
        
        glowPaint.style = Paint.Style.STROKE
        glowPaint.strokeWidth = 3f
        glowPaint.maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)
        
        paint.style = Paint.Style.FILL
        setLayerType(LAYER_TYPE_SOFTWARE, null) // Enable blur effects
    }
    
    fun setGame(game: TetrisGame) {
        this.game = game
        invalidate()
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        val boardWidth = 10
        val boardHeight = 20
        
        cellSize = minOf(w / (boardWidth + 6), h / (boardHeight + 2)).toFloat()
        
        boardStartX = (w - (boardWidth * cellSize)) / 2
        boardStartY = (h - (boardHeight * cellSize)) / 2
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        canvas.drawColor(Color.parseColor("#1a1a1a"))
        
        game?.let { game ->
            drawBoard(canvas, game.getBoardState())
            drawNextPiece(canvas, game.getNextPieceShape())
            drawUI(canvas, game)
        }
    }
    
    private fun drawBoard(canvas: Canvas, boardState: Array<IntArray>) {
        // Draw board background with subtle gradient
        val boardRect = RectF(
            boardStartX - cellSize/4, 
            boardStartY - cellSize/4,
            boardStartX + 10 * cellSize + cellSize/4, 
            boardStartY + 20 * cellSize + cellSize/4
        )
        val bgGradient = LinearGradient(
            boardRect.left, boardRect.top, boardRect.right, boardRect.bottom,
            Color.parseColor("#1E1E1E"), Color.parseColor("#0A0A0A"),
            Shader.TileMode.CLAMP
        )
        paint.shader = bgGradient
        canvas.drawRoundRect(boardRect, cornerRadius * 2, cornerRadius * 2, paint)
        paint.shader = null
        
        for (row in boardState.indices) {
            for (col in boardState[row].indices) {
                val x = boardStartX + col * cellSize
                val y = boardStartY + row * cellSize
                val cellRect = RectF(x + 2, y + 2, x + cellSize - 2, y + cellSize - 2)
                
                val colorIndex = boardState[row][col]
                if (colorIndex > 0) {
                    // Draw shadow
                    val shadowRect = RectF(x + 4, y + 4, x + cellSize, y + cellSize)
                    canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaint)
                    
                    // Draw gradient fill
                    val gradient = LinearGradient(
                        x, y, x + cellSize, y + cellSize,
                        pieceColors[colorIndex][0], pieceColors[colorIndex][1],
                        Shader.TileMode.CLAMP
                    )
                    paint.shader = gradient
                    canvas.drawRoundRect(cellRect, cornerRadius, cornerRadius, paint)
                    paint.shader = null
                    
                    // Add subtle glow for active pieces
                    if (isActivePiece(row, col)) {
                        glowPaint.color = pieceColors[colorIndex][0]
                        canvas.drawRoundRect(cellRect, cornerRadius, cornerRadius, glowPaint)
                    }
                } else {
                    // Draw empty cell with subtle border
                    paint.color = Color.parseColor("#252525")
                    canvas.drawRoundRect(cellRect, cornerRadius/2, cornerRadius/2, paint)
                }
                
                // Draw subtle grid lines
                canvas.drawRoundRect(cellRect, cornerRadius/2, cornerRadius/2, gridPaint)
            }
        }
    }
    
    private fun isActivePiece(row: Int, col: Int): Boolean {
        // Simple animation check - can be enhanced with actual piece tracking
        return (row + col) % 7 == (System.currentTimeMillis() / 500 % 7).toInt()
    }
    
    private fun drawNextPiece(canvas: Canvas, nextShape: Array<IntArray>) {
        val nextStartX = boardStartX + 11 * cellSize
        val nextStartY = boardStartY + 2 * cellSize
        
        paint.color = Color.WHITE
        paint.textSize = cellSize * 0.8f
        canvas.drawText("NEXT", nextStartX, nextStartY - cellSize/2, paint)
        
        for (row in nextShape.indices) {
            for (col in nextShape[row].indices) {
                if (nextShape[row][col] != 0) {
                    val x = nextStartX + col * cellSize * 0.8f
                    val y = nextStartY + row * cellSize * 0.8f
                    
                    paint.color = colors[nextShape[row][col]]
                    canvas.drawRect(x, y, x + cellSize * 0.8f, y + cellSize * 0.8f, paint)
                }
            }
        }
    }
    
    private fun drawUI(canvas: Canvas, game: TetrisGame) {
        val uiStartX = boardStartX + 11 * cellSize
        val uiStartY = boardStartY + 8 * cellSize
        
        // Draw stats with enhanced styling
        drawEnhancedText(canvas, "SCORE", game.score.toString(), uiStartX, uiStartY, Color.parseColor("#FFD700"))
        drawEnhancedText(canvas, "LEVEL", game.level.toString(), uiStartX, uiStartY + 3 * cellSize, Color.parseColor("#00FFFF"))
        drawEnhancedText(canvas, "LINES", game.linesCleared.toString(), uiStartX, uiStartY + 6 * cellSize, Color.parseColor("#FF6B6B"))
        
        // Draw active items
        drawActiveItems(canvas, game)
        
        // Draw inventory
        drawInventory(canvas, game)
        
        if (game.isGameOver) {
            paint.color = Color.RED
            paint.textSize = cellSize * 1.2f
            val text = "GAME OVER"
            val textWidth = paint.measureText(text)
            canvas.drawText(text, 
                (width - textWidth) / 2, 
                height / 2f, 
                paint)
        }
        
        if (game.isPaused) {
            paint.color = Color.YELLOW
            paint.textSize = cellSize * 1.2f
            val text = "PAUSED"
            val textWidth = paint.measureText(text)
            canvas.drawText(text,
                (width - textWidth) / 2,
                height / 2f,
                paint)
        }
    }
    
    private fun drawEnhancedText(canvas: Canvas, label: String, value: String, x: Float, y: Float, color: Int) {
        // Draw label
        paint.color = Color.parseColor("#CCCCCC")
        paint.textSize = cellSize * 0.5f
        canvas.drawText(label, x, y, paint)
        
        // Draw value with glow effect
        paint.color = color
        paint.textSize = cellSize * 0.7f
        paint.setShadowLayer(6f, 2f, 2f, Color.parseColor("#40000000"))
        canvas.drawText(value, x, y + cellSize, paint)
        paint.clearShadowLayer()
    }
    
    private fun drawActiveItems(canvas: Canvas, game: TetrisGame) {
        val activeItems = game.getActiveItems()
        if (activeItems.isEmpty()) return
        
        val itemStartX = cellSize
        val itemStartY = cellSize * 2
        
        paint.color = Color.parseColor("#FFD700")
        paint.textSize = cellSize * 0.6f
        canvas.drawText("ACTIVE EFFECTS", itemStartX, itemStartY, paint)
        
        var yOffset = itemStartY + cellSize
        val currentTime = System.currentTimeMillis()
        
        for (activeItem in activeItems) {
            val remainingTime = activeItem.getRemainingTime(currentTime)
            val timeText = if (remainingTime > 0) "(${remainingTime / 1000}s)" else ""
            
            paint.color = activeItem.item.rarity.color
            paint.textSize = cellSize * 0.4f
            canvas.drawText("${activeItem.item.name} $timeText", itemStartX, yOffset, paint)
            
            // Draw progress bar for timed effects
            if (activeItem.item.duration > 0) {
                val progress = 1f - (remainingTime.toFloat() / activeItem.item.duration)
                val barWidth = cellSize * 3
                val barHeight = cellSize * 0.1f
                
                // Background bar
                paint.color = Color.parseColor("#333333")
                canvas.drawRect(itemStartX, yOffset + cellSize * 0.2f, 
                    itemStartX + barWidth, yOffset + cellSize * 0.2f + barHeight, paint)
                
                // Progress bar
                paint.color = activeItem.item.rarity.color
                canvas.drawRect(itemStartX, yOffset + cellSize * 0.2f,
                    itemStartX + barWidth * progress, yOffset + cellSize * 0.2f + barHeight, paint)
            }
            
            yOffset += cellSize * 0.8f
        }
    }
    
    private fun drawInventory(canvas: Canvas, game: TetrisGame) {
        val inventory = game.getInventory()
        if (inventory.isEmpty()) return
        
        val invStartX = cellSize
        val invStartY = height - cellSize * 6
        
        paint.color = Color.parseColor("#4ECDC4")
        paint.textSize = cellSize * 0.6f
        canvas.drawText("ITEMS", invStartX, invStartY, paint)
        
        // Draw item slots
        for (i in 0 until minOf(3, inventory.size)) {
            val item = inventory[i]
            val slotX = invStartX + i * cellSize * 1.5f
            val slotY = invStartY + cellSize * 0.5f
            val slotSize = cellSize * 1.2f
            
            // Draw slot background
            val slotRect = RectF(slotX, slotY, slotX + slotSize, slotY + slotSize)
            paint.color = Color.parseColor("#2D2D2D")
            canvas.drawRoundRect(slotRect, cornerRadius, cornerRadius, paint)
            
            // Draw rarity border
            paint.color = item.rarity.color
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f
            canvas.drawRoundRect(slotRect, cornerRadius, cornerRadius, paint)
            paint.style = Paint.Style.FILL
            
            // Draw item icon (simplified text representation)
            paint.color = item.rarity.color
            paint.textSize = cellSize * 0.3f
            val iconText = item.name.take(2).uppercase()
            val textWidth = paint.measureText(iconText)
            canvas.drawText(iconText, 
                slotX + (slotSize - textWidth) / 2, 
                slotY + slotSize * 0.6f, 
                paint)
        }
        
        // Show item count if more than 3
        if (inventory.size > 3) {
            paint.color = Color.WHITE
            paint.textSize = cellSize * 0.4f
            canvas.drawText("+${inventory.size - 3} more", 
                invStartX + 3 * cellSize * 1.5f, 
                invStartY + cellSize * 1.7f, 
                paint)
        }
    }
    
    private fun drawParticleEffects(canvas: Canvas) {
        // Simple particle system for visual flair
        val particleCount = 20
        val time = System.currentTimeMillis() % 10000
        
        for (i in 0 until particleCount) {
            val angle = (i * 360f / particleCount) + (time * 0.1f)
            val radius = cellSize * 2 + kotlin.math.sin(time * 0.01f + i) * cellSize
            val x = width / 2f + kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat() * radius
            val y = height / 2f + kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat() * radius * 0.5f
            
            val alpha = (128 + 127 * kotlin.math.sin(time * 0.005f + i)).toInt()
            paint.color = Color.argb(alpha, 100, 200, 255)
            canvas.drawCircle(x, y, 4f, paint)
        }
    }
}