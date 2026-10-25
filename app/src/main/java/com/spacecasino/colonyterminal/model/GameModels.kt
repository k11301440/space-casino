package com.spacecasino.colonyterminal.model

import kotlinx.serialization.Serializable

@Serializable
enum class ThemeMode(val displayName: String) {
    CRT_GREEN("CRT Green"),
    CRT_AMBER("CRT Amber"),
    FLAT_GREY("Flat Grey"),
    CYBER_CYAN("Cyber Cyan")
}

@Serializable
enum class BgmMode(val displayName: String) {
    OFF("なし (Mute)"),
    CHIPTUNE("8-bit Chiptune"),
    SCIFI("Sci-Fi Synth")
}

@Serializable
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0L
)

@Serializable
data class BankData(
    val deposited: Long = 0L,
    val dailyInterestRate: Double = 0.01 // 日歩約1% (24時間で1%)
)

@Serializable
data class MutualFund(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val nav: Double, // 基準価額 (初期 10,000 CR)
    val history: List<Double>,
    val ownedUnits: Double = 0.0,
    val investedCost: Long = 0L,
    val expectedAnnualReturn: Double, // 例: 0.08
    val riskVolatility: Double // 変動率
)

@Serializable
data class Stock(
    val symbol: String,
    val name: String,
    val sector: String,
    val description: String,
    val currentPrice: Long,
    val previousPrice: Long,
    val history: List<Long>,
    val volatility: Double,
    val ownedShares: Long = 0L,
    val totalCost: Long = 0L
)

@Serializable
enum class FxDirection {
    LONG, SHORT
}

@Serializable
data class FxPair(
    val symbol: String,
    val name: String,
    val description: String,
    val currentRate: Double,
    val previousRate: Double,
    val history: List<Double>,
    val spreadPercent: Double = 0.001
)

@Serializable
data class FxPosition(
    val id: String,
    val pairSymbol: String,
    val direction: FxDirection,
    val entryRate: Double,
    val lots: Long, // 1ロット = 1,000通貨単位
    val leverage: Int,
    val marginUsed: Long,
    val openDay: Long
)

@Serializable
enum class FacilityType {
    FARM,        // 宇宙農園: 安定中収入
    GRAVITY,     // 重力維持装置: 高額、大収入、ストーリークリアフラグ
    OXYGEN,      // 酸素精製施設: 低リスク安定収入
    ENERGY,      // エネルギー核融合炉: 高収入、初期投資大
    LAB,         // 研究ラボ: パッシブ+ボーナス効果
    PORT         // 貿易港: 変動のある高収入
}

@Serializable
data class Facility(
    val id: String,
    val type: FacilityType,
    val name: String,
    val description: String,
    val baseCost: Long,
    val baseDailyIncome: Long,
    val level: Int = 0, // 0 = 未所持, 1以上 = 所持・アップグレード
    val isOwned: Boolean = false
) {
    val upgradeCost: Long
        get() {
            if (!isOwned) return baseCost
            if (level >= 50) return Long.MAX_VALUE / 2
            val mult = Math.pow(1.5, level.toDouble())
            val cost = (baseCost * mult).toLong()
            return if (cost <= 0L) Long.MAX_VALUE / 2 else cost
        }

    val currentDailyIncome: Long
        get() = if (!isOwned) 0L else (baseDailyIncome * level)
}

@Serializable
data class StoryMilestone(
    val id: Int,
    val title: String,
    val description: String,
    val unlockCreditThreshold: Long,
    val requiresGravityCore: Boolean = false,
    val isUnlocked: Boolean = false,
    val unlockedDay: Long = 0L
)

@Serializable
data class LogEntry(
    val id: Long,
    val day: Long,
    val category: String, // "ストーリー", "銀行", "投信", "株式", "為替", "施設", "カジノ", "システム"
    val message: String,
    val amountChange: Long = 0L
)

@Serializable
enum class RouletteBetType {
    RED, BLACK, HIGH, LOW, NUMBER_7, NUMBER_12, HAZARD_LUCK
}

@Serializable
enum class HighLowChoice {
    HIGH, // 8〜12
    LOW,  // 2〜6
    SEVEN // 7ぴったり (高配当)
}

@Serializable
data class HighLowGame(
    val currentRoll: Int = 7,
    val currentBet: Long = 0L,
    val streak: Int = 0,
    val isActive: Boolean = false
)

@Serializable
data class GameState(
    val day: Long = 1L,
    val credits: Long = 10000L, // 初期所持 10,000 CR
    val lastSaveTimeMs: Long = 0L,
    val theme: ThemeMode = ThemeMode.CRT_GREEN,
    val bgmMode: BgmMode = BgmMode.CHIPTUNE,
    val soundEnabled: Boolean = true,
    val bank: BankData = BankData(),
    val mutualFunds: List<MutualFund> = emptyList(),
    val stocks: List<Stock> = emptyList(),
    val fxPairs: List<FxPair> = emptyList(),
    val fxPositions: List<FxPosition> = emptyList(),
    val facilities: List<Facility> = emptyList(),
    val milestones: List<StoryMilestone> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val logs: List<LogEntry> = emptyList(),
    val highLow: HighLowGame = HighLowGame(),
    val colonyPurchased: Boolean = false
) {
    // 総資産（純資産）の計算
    val totalNetWorth: Long
        get() {
            var total = credits + bank.deposited

            // 投資信託の評価額
            mutualFunds.forEach { fund ->
                total += (fund.ownedUnits * fund.nav).toLong()
            }

            // 株式の評価額
            stocks.forEach { stock ->
                total += stock.ownedShares * stock.currentPrice
            }

            // FXの証拠金＋未実現損益
            fxPositions.forEach { pos ->
                val pair = fxPairs.find { it.symbol == pos.pairSymbol }
                if (pair != null) {
                    val priceDiff = if (pos.direction == FxDirection.LONG) {
                        (pair.currentRate - pos.entryRate)
                    } else {
                        (pos.entryRate - pair.currentRate)
                    }
                    val pnl = (priceDiff * pos.lots * 1000).toLong()
                    total += (pos.marginUsed + pnl).coerceAtLeast(0L)
                } else {
                    total += pos.marginUsed
                }
            }

            // 施設投資価値（取得累計額）
            facilities.forEach { facility ->
                if (facility.isOwned) {
                    total += facility.baseCost * facility.level
                }
            }

            return total
        }
}
