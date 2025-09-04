package com.example.tetris

enum class TetrominoType(val color: Int, val shapes: Array<Array<IntArray>>) {
    I(1, arrayOf(
        arrayOf(
            intArrayOf(0,0,0,0),
            intArrayOf(1,1,1,1),
            intArrayOf(0,0,0,0),
            intArrayOf(0,0,0,0)
        ),
        arrayOf(
            intArrayOf(0,0,1,0),
            intArrayOf(0,0,1,0),
            intArrayOf(0,0,1,0),
            intArrayOf(0,0,1,0)
        ),
        arrayOf(
            intArrayOf(0,0,0,0),
            intArrayOf(0,0,0,0),
            intArrayOf(1,1,1,1),
            intArrayOf(0,0,0,0)
        ),
        arrayOf(
            intArrayOf(0,1,0,0),
            intArrayOf(0,1,0,0),
            intArrayOf(0,1,0,0),
            intArrayOf(0,1,0,0)
        )
    )),
    
    O(2, arrayOf(
        arrayOf(
            intArrayOf(1,1),
            intArrayOf(1,1)
        )
    )),
    
    T(3, arrayOf(
        arrayOf(
            intArrayOf(0,1,0),
            intArrayOf(1,1,1),
            intArrayOf(0,0,0)
        ),
        arrayOf(
            intArrayOf(0,1,0),
            intArrayOf(0,1,1),
            intArrayOf(0,1,0)
        ),
        arrayOf(
            intArrayOf(0,0,0),
            intArrayOf(1,1,1),
            intArrayOf(0,1,0)
        ),
        arrayOf(
            intArrayOf(0,1,0),
            intArrayOf(1,1,0),
            intArrayOf(0,1,0)
        )
    )),
    
    S(4, arrayOf(
        arrayOf(
            intArrayOf(0,1,1),
            intArrayOf(1,1,0),
            intArrayOf(0,0,0)
        ),
        arrayOf(
            intArrayOf(0,1,0),
            intArrayOf(0,1,1),
            intArrayOf(0,0,1)
        )
    )),
    
    Z(5, arrayOf(
        arrayOf(
            intArrayOf(1,1,0),
            intArrayOf(0,1,1),
            intArrayOf(0,0,0)
        ),
        arrayOf(
            intArrayOf(0,0,1),
            intArrayOf(0,1,1),
            intArrayOf(0,1,0)
        )
    )),
    
    J(6, arrayOf(
        arrayOf(
            intArrayOf(1,0,0),
            intArrayOf(1,1,1),
            intArrayOf(0,0,0)
        ),
        arrayOf(
            intArrayOf(0,1,1),
            intArrayOf(0,1,0),
            intArrayOf(0,1,0)
        ),
        arrayOf(
            intArrayOf(0,0,0),
            intArrayOf(1,1,1),
            intArrayOf(0,0,1)
        ),
        arrayOf(
            intArrayOf(0,1,0),
            intArrayOf(0,1,0),
            intArrayOf(1,1,0)
        )
    )),
    
    L(7, arrayOf(
        arrayOf(
            intArrayOf(0,0,1),
            intArrayOf(1,1,1),
            intArrayOf(0,0,0)
        ),
        arrayOf(
            intArrayOf(0,1,0),
            intArrayOf(0,1,0),
            intArrayOf(0,1,1)
        ),
        arrayOf(
            intArrayOf(0,0,0),
            intArrayOf(1,1,1),
            intArrayOf(1,0,0)
        ),
        arrayOf(
            intArrayOf(1,1,0),
            intArrayOf(0,1,0),
            intArrayOf(0,1,0)
        )
    ))
}

class Tetromino(private val type: TetrominoType) {
    val color: Int = type.color
    
    fun getRotatedShape(rotation: Int): Array<IntArray> {
        val rotationIndex = rotation % type.shapes.size
        return type.shapes[rotationIndex]
    }
    
    fun getMaxRotations(): Int = type.shapes.size
    
    companion object {
        fun random(): Tetromino {
            return Tetromino(TetrominoType.values().random())
        }
    }
}