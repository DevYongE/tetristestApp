package com.example.tetris

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    private lateinit var tetrisView: TetrisView
    private lateinit var game: TetrisGame
    private lateinit var gameHandler: Handler
    private lateinit var gameRunnable: Runnable
    
    private lateinit var btnNewGame: Button
    private lateinit var btnPause: Button
    private lateinit var btnLeft: Button
    private lateinit var btnRight: Button
    private lateinit var btnRotate: Button
    private lateinit var btnDrop: Button
    
    private var gameSpeed = 800L
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupGame()
        setupControls()
        startGameLoop()
    }
    
    private fun initViews() {
        tetrisView = findViewById(R.id.tetrisView)
        btnNewGame = findViewById(R.id.btnNewGame)
        btnPause = findViewById(R.id.btnPause)
        btnLeft = findViewById(R.id.btnLeft)
        btnRight = findViewById(R.id.btnRight)
        btnRotate = findViewById(R.id.btnRotate)
        btnDrop = findViewById(R.id.btnDrop)
    }
    
    private fun setupGame() {
        game = TetrisGame()
        tetrisView.setGame(game)
        gameHandler = Handler(Looper.getMainLooper())
    }
    
    private fun setupControls() {
        btnNewGame.setOnClickListener {
            game.restart()
            gameSpeed = 800L
            updatePauseButton()
            tetrisView.invalidate()
        }
        
        btnPause.setOnClickListener {
            if (game.isPaused) {
                game.resume()
            } else {
                game.pause()
            }
            updatePauseButton()
        }
        
        btnLeft.setOnClickListener {
            if (game.moveLeft()) {
                tetrisView.invalidate()
            }
        }
        
        btnRight.setOnClickListener {
            if (game.moveRight()) {
                tetrisView.invalidate()
            }
        }
        
        btnRotate.setOnClickListener {
            if (game.rotate()) {
                tetrisView.invalidate()
            }
        }
        
        btnDrop.setOnClickListener {
            game.drop()
            tetrisView.invalidate()
        }
        
        setupTouchControls()
    }
    
    private fun setupTouchControls() {
        var downX = 0f
        var downY = 0f
        val threshold = 50
        
        tetrisView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    downY = event.y
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val deltaX = event.x - downX
                    val deltaY = event.y - downY
                    
                    when {
                        kotlin.math.abs(deltaX) > kotlin.math.abs(deltaY) -> {
                            if (deltaX > threshold) {
                                game.moveRight()
                            } else if (deltaX < -threshold) {
                                game.moveLeft()
                            }
                        }
                        deltaY > threshold -> {
                            game.drop()
                        }
                        kotlin.math.abs(deltaX) < threshold && kotlin.math.abs(deltaY) < threshold -> {
                            game.rotate()
                        }
                    }
                    tetrisView.invalidate()
                    true
                }
                else -> false
            }
        }
    }
    
    private fun startGameLoop() {
        gameRunnable = object : Runnable {
            override fun run() {
                if (!game.isPaused && !game.isGameOver) {
                    game.moveDown()
                    gameSpeed = maxOf(100L, 800L - (game.level - 1) * 50L)
                    tetrisView.invalidate()
                }
                gameHandler.postDelayed(this, gameSpeed)
            }
        }
        gameHandler.post(gameRunnable)
    }
    
    private fun updatePauseButton() {
        btnPause.text = if (game.isPaused) {
            getString(R.string.resume)
        } else {
            getString(R.string.pause)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        gameHandler.removeCallbacks(gameRunnable)
    }
    
    override fun onPause() {
        super.onPause()
        if (!game.isGameOver) {
            game.pause()
            updatePauseButton()
        }
    }
}