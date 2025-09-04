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
    private var cellSize = 0f
    private var boardStartX = 0f
    private var boardStartY = 0f
    
    private val colors = arrayOf(
        Color.BLACK,      // 0 - empty
        Color.CYAN,       // 1 - I piece
        Color.YELLOW,     // 2 - O piece  
        Color.MAGENTA,    // 3 - T piece
        Color.GREEN,      // 4 - S piece
        Color.RED,        // 5 - Z piece
        Color.BLUE,       // 6 - J piece
        Color.rgb(255, 165, 0) // 7 - L piece (orange)
    )
    
    init {
        gridPaint.color = Color.GRAY
        gridPaint.style = Paint.Style.STROKE
        gridPaint.strokeWidth = 2f
        
        paint.style = Paint.Style.FILL
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
        for (row in boardState.indices) {
            for (col in boardState[row].indices) {
                val x = boardStartX + col * cellSize
                val y = boardStartY + row * cellSize
                
                paint.color = colors[boardState[row][col]]
                canvas.drawRect(x, y, x + cellSize, y + cellSize, paint)
                
                canvas.drawRect(x, y, x + cellSize, y + cellSize, gridPaint)
            }
        }
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
        
        paint.color = Color.WHITE
        paint.textSize = cellSize * 0.6f
        
        canvas.drawText("SCORE", uiStartX, uiStartY, paint)
        canvas.drawText(game.score.toString(), uiStartX, uiStartY + cellSize, paint)
        
        canvas.drawText("LEVEL", uiStartX, uiStartY + 3 * cellSize, paint)
        canvas.drawText(game.level.toString(), uiStartX, uiStartY + 4 * cellSize, paint)
        
        canvas.drawText("LINES", uiStartX, uiStartY + 6 * cellSize, paint)
        canvas.drawText(game.linesCleared.toString(), uiStartX, uiStartY + 7 * cellSize, paint)
        
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
}