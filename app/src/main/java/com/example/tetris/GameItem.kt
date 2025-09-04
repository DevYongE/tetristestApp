package com.example.tetris

enum class ItemType {
    CLEAR_LINE,      // 한 줄 즉시 제거
    SLOW_MOTION,     // 속도 느려짐
    GHOST_PIECE,     // 다음 조각 미리보기
    BOMB,           // 주변 블록 폭파
    LINE_BOMB,      // 한 줄 전체 폭파
    FREEZE,         // 시간 정지
    MULTI_CLEAR,    // 다중 라인 클리어 보너스
    RAINBOW_PIECE   // 모든 색과 매치 가능한 조각
}

data class GameItem(
    val type: ItemType,
    val name: String,
    val description: String,
    val duration: Long = 0L, // 지속 시간 (ms), 0이면 즉시 효과
    val rarity: ItemRarity = ItemRarity.COMMON
) {
    companion object {
        fun createRandomItem(): GameItem {
            val items = listOf(
                GameItem(
                    ItemType.CLEAR_LINE,
                    "라인 클리어",
                    "가장 아래 라인을 즉시 제거합니다",
                    0L,
                    ItemRarity.COMMON
                ),
                GameItem(
                    ItemType.SLOW_MOTION,
                    "슬로우 모션",
                    "10초간 조각이 천천히 떨어집니다",
                    10000L,
                    ItemRarity.COMMON
                ),
                GameItem(
                    ItemType.GHOST_PIECE,
                    "고스트 피스",
                    "15초간 조각이 떨어질 위치를 미리 볼 수 있습니다",
                    15000L,
                    ItemRarity.UNCOMMON
                ),
                GameItem(
                    ItemType.BOMB,
                    "폭탄",
                    "선택한 위치 주변 3x3 영역의 블록을 제거합니다",
                    0L,
                    ItemRarity.UNCOMMON
                ),
                GameItem(
                    ItemType.LINE_BOMB,
                    "라인 폭탄",
                    "선택한 가로줄을 완전히 제거합니다",
                    0L,
                    ItemRarity.RARE
                ),
                GameItem(
                    ItemType.FREEZE,
                    "시간 정지",
                    "8초간 조각이 떨어지지 않습니다",
                    8000L,
                    ItemRarity.RARE
                ),
                GameItem(
                    ItemType.MULTI_CLEAR,
                    "멀티 클리어",
                    "다음 20초간 라인 클리어 점수가 2배가 됩니다",
                    20000L,
                    ItemRarity.EPIC
                ),
                GameItem(
                    ItemType.RAINBOW_PIECE,
                    "레인보우 피스",
                    "다음 조각이 모든 색과 매치되는 특수 조각이 됩니다",
                    0L,
                    ItemRarity.LEGENDARY
                )
            )
            
            val weights = mapOf(
                ItemRarity.COMMON to 50,
                ItemRarity.UNCOMMON to 30,
                ItemRarity.RARE to 15,
                ItemRarity.EPIC to 4,
                ItemRarity.LEGENDARY to 1
            )
            
            val totalWeight = weights.values.sum()
            val randomValue = (0 until totalWeight).random()
            
            var currentWeight = 0
            for ((rarity, weight) in weights) {
                currentWeight += weight
                if (randomValue < currentWeight) {
                    return items.filter { it.rarity == rarity }.random()
                }
            }
            
            return items.first() // Fallback
        }
    }
}

enum class ItemRarity(val displayName: String, val color: Int) {
    COMMON("일반", android.graphics.Color.parseColor("#FFFFFF")),
    UNCOMMON("고급", android.graphics.Color.parseColor("#00FF00")),
    RARE("희귀", android.graphics.Color.parseColor("#0080FF")),
    EPIC("영웅", android.graphics.Color.parseColor("#8000FF")),
    LEGENDARY("전설", android.graphics.Color.parseColor("#FF8000"))
}

data class ActiveItem(
    val item: GameItem,
    val startTime: Long,
    val endTime: Long = if (item.duration > 0) startTime + item.duration else startTime
) {
    fun isExpired(currentTime: Long): Boolean {
        return item.duration > 0 && currentTime > endTime
    }
    
    fun getRemainingTime(currentTime: Long): Long {
        return if (item.duration > 0) maxOf(0, endTime - currentTime) else 0
    }
}