package com.spacecasino.colonyterminal.logic

import com.spacecasino.colonyterminal.model.*
import java.util.UUID
import kotlin.math.*
import kotlin.random.Random

object GameEngine {

    private val jRandom = java.util.Random()
    private fun nextGaussian(): Double = jRandom.nextGaussian()

    fun createInitialState(): GameState {
        val initialFunds = listOf(
            MutualFund(
                id = "FUND_BOND",
                name = "太陽系防衛軌道債券ファンド",
                category = "債券型 (超低リスク)",
                description = "星系連合防衛艦隊の発行する安全債券。元本毀損リスクが極めて低い。",
                nav = 10000.0,
                history = generateInitialHistory(10000.0, 15, 0.003),
                expectedAnnualReturn = 0.04,
                riskVolatility = 0.003
            ),
            MutualFund(
                id = "FUND_INDEX",
                name = "コロニー・インデックス50",
                category = "インデックス型 (標準)",
                description = "軌道コロニー上位50社の平均株価に連動。中リスク・堅実成長。",
                nav = 10000.0,
                history = generateInitialHistory(10000.0, 15, 0.012),
                expectedAnnualReturn = 0.08,
                riskVolatility = 0.012
            ),
            MutualFund(
                id = "FUND_COMMODITY",
                name = "星間コモディティ＆資源投信",
                category = "資源商品型 (中高リスク)",
                description = "小惑星鉱物、水氷、ヘリウム3などの現物指数に連動。インフレヘッジに最適。",
                nav = 10000.0,
                history = generateInitialHistory(10000.0, 15, 0.020),
                expectedAnnualReturn = 0.12,
                riskVolatility = 0.020
            ),
            MutualFund(
                id = "FUND_GROWTH",
                name = "新惑星開拓グロース投信",
                category = "積極運用型 (高リスク)",
                description = "外惑星テラフォーミング計画関連銘柄に集中投資。高ボラティリティ・高リターン。",
                nav = 10000.0,
                history = generateInitialHistory(10000.0, 15, 0.035),
                expectedAnnualReturn = 0.18,
                riskVolatility = 0.035
            ),
            MutualFund(
                id = "FUND_AI_LEVERAGE",
                name = "量子AI超レバレッジ投信",
                category = "超投機型 (超高リスク)",
                description = "量子AIによる高頻度アルゴリズム取引。乱高下するが一攫千金のポテンシャル。",
                nav = 10000.0,
                history = generateInitialHistory(10000.0, 15, 0.070),
                expectedAnnualReturn = 0.36,
                riskVolatility = 0.070
            )
        )

        val initialStocks = listOf(
            Stock(
                symbol = "ASTRO",
                name = "アストロ小惑星採掘重工",
                sector = "重工業・鉱業",
                description = "カイパーベルトでのレアメタル採掘最大手。資源価格の影響を受けやすい。",
                currentPrice = 1250L,
                previousPrice = 1200L,
                history = generateInitialPriceHistory(1250L, 15, 0.04),
                volatility = 0.045
            ),
            Stock(
                symbol = "BIO",
                name = "ステラ・バイオ製薬",
                sector = "バイオ・医療",
                description = "無重力環境特有のウイルスワクチンを開発。新薬ニュースで急騰急落する。",
                currentPrice = 3400L,
                previousPrice = 3450L,
                history = generateInitialPriceHistory(3400L, 15, 0.08),
                volatility = 0.085
            ),
            Stock(
                symbol = "CYBER",
                name = "ネオ・サイバネティクス",
                sector = "ハイテク・AI",
                description = "コロニー全域の自動制御AIおよび義体インプラントを開発。成長著しい人気銘柄。",
                currentPrice = 8800L,
                previousPrice = 8600L,
                history = generateInitialPriceHistory(8800L, 15, 0.05),
                volatility = 0.055
            ),
            Stock(
                symbol = "PHOBOS",
                name = "フォボス軌道運輸",
                sector = "物流・宇宙船",
                description = "地球・火星・コロニー間の定期シャトル便を独占運航。景気に連動した値動き。",
                currentPrice = 620L,
                previousPrice = 615L,
                history = generateInitialPriceHistory(620L, 15, 0.025),
                volatility = 0.028
            ),
            Stock(
                symbol = "LUNA",
                name = "ルナ・エンタテインメント",
                sector = "娯楽・リゾート",
                description = "月面ドームカジノと宇宙観光ツアーを運営。コロニー住民の余暇需要で潤う。",
                currentPrice = 2100L,
                previousPrice = 2180L,
                history = generateInitialPriceHistory(2100L, 15, 0.06),
                volatility = 0.065
            )
        )

        val initialFxPairs = listOf(
            FxPair(
                symbol = "CR/SOL",
                name = "クレジット / 太陽系ドル",
                description = "コロニー通貨と地球連合通貨の基軸ペア。急激な為替介入あり。",
                currentRate = 1.2500,
                previousRate = 1.2480,
                history = generateInitialHistory(1.2500, 15, 0.008),
                spreadPercent = 0.001
            ),
            FxPair(
                symbol = "CR/MARS",
                name = "クレジット / 火星ポンド",
                description = "急成長する火星独立共和国の通貨。激しいインフレと急騰急落が特徴。",
                currentRate = 0.8200,
                previousRate = 0.8150,
                history = generateInitialHistory(0.8200, 15, 0.015),
                spreadPercent = 0.0015
            ),
            FxPair(
                symbol = "CR/ORE",
                name = "クレジット / 希少鉱石指数",
                description = "星系間物資取引で使われる商品通貨。投機マネーで乱高下する。",
                currentRate = 3.4500,
                previousRate = 3.4800,
                history = generateInitialHistory(3.4500, 15, 0.025),
                spreadPercent = 0.002
            )
        )

        val initialFacilities = listOf(
            Facility(
                id = "FAC_OXYGEN",
                type = FacilityType.OXYGEN,
                name = "酸素循環精製プラント",
                description = "コロニーの大気を循環精製し住民に配給。低リスクで手堅い日当収入。",
                baseCost = 20000L,
                baseDailyIncome = 180L
            ),
            Facility(
                id = "FAC_FARM",
                type = FacilityType.FARM,
                name = "高密度宇宙水耕農園",
                description = "人工太陽光と水耕栽培で生鮮野菜を供給。安定した中規模の収益。",
                baseCost = 60000L,
                baseDailyIncome = 650L
            ),
            Facility(
                id = "FAC_ENERGY",
                type = FacilityType.ENERGY,
                name = "ヘリウム3核融合発電所",
                description = "莫大な電力をコロニー全域に供給。初期投資は大きいが高収入。",
                baseCost = 300000L,
                baseDailyIncome = 4000L
            ),
            Facility(
                id = "FAC_LAB",
                type = FacilityType.LAB,
                name = "量子AI研究開発ラボ",
                description = "先端技術の特許収入に加え、全資産運用の収益性を+5%向上させる。",
                baseCost = 1200000L,
                baseDailyIncome = 15000L
            ),
            Facility(
                id = "FAC_PORT",
                type = FacilityType.PORT,
                name = "星間深宇宙貿易ドック",
                description = "外宇宙輸送船の停泊料と関税を徴収。日によって利用客数と収入が変動。",
                baseCost = 6000000L,
                baseDailyIncome = 90000L
            ),
            Facility(
                id = "FAC_GRAVITY",
                type = FacilityType.GRAVITY,
                name = "第1区画 人工重力維持リング",
                description = "コロニーの居住環境を根底から支える中枢施設。巨額の収益を生み、コロニー買収権の前提となる。",
                baseCost = 25000000L,
                baseDailyIncome = 400000L
            )
        )

        val initialMilestones = listOf(
            StoryMilestone(
                id = 0,
                title = "起動：古い端末CAT-OS",
                description = "スラム街の粗大ゴミ置き場で拾ったレトロな端末。画面には『Colony Asset Terminal』の文字が鈍く光っている。所持金10,000クレジット。ここから脱出してみせる。",
                unlockCreditThreshold = 0L,
                isUnlocked = true,
                unlockedDay = 1L
            ),
            StoryMilestone(
                id = 1,
                title = "下層区からの脱出",
                description = "純資産が50,000クレジットを超えた。薄汚れた下層スラムのアパートを引き払い、中層居住区の準市民IDを購入。端末の通信速度が上がった。",
                unlockCreditThreshold = 50000L
            ),
            StoryMilestone(
                id = 2,
                title = "機関投資家の注視",
                description = "純資産200,000クレジット到達。個人トレーダーとしてコロニー商業ギルドに名が知られ始めた。高配当な施設投資のライセンスが下りる。",
                unlockCreditThreshold = 200000L
            ),
            StoryMilestone(
                id = 3,
                title = "上層メガロポリスへ進出",
                description = "純資産1,000,000クレジット！正真正銘のミリオネアだ。上層区のスカイラウンジから見下ろすコロニーの星空は格別だ。だが、この街の真の主はコロニー統括企業『アルカディア・コーポレーション』だ。",
                unlockCreditThreshold = 1000000L
            ),
            StoryMilestone(
                id = 4,
                title = "コロニー評議会への参加",
                description = "純資産10,000,000クレジット達成。コロニー最高評議会に議席を確保。重力維持リングの売却話が水面下で持ちかけられている。",
                unlockCreditThreshold = 10000000L
            ),
            StoryMilestone(
                id = 5,
                title = "支配権獲得の最終承認",
                description = "純資産50,000,000クレジットを突破！コロニー買収の最終価格は100,000,000クレジット。独立宣言まであと一歩だ。",
                unlockCreditThreshold = 50000000L,
                requiresGravityCore = false
            ),
            StoryMilestone(
                id = 6,
                title = "完全買収：コロニー独立宣言",
                description = "1億クレジットでコロニー『アルカディア』の全株式とインフラを買い取った！アルカディア・コーポレーションは撤退し、あなたがこのコロニーの最高統帥者となった。ゲームクリア！",
                unlockCreditThreshold = 100000000L,
                requiresGravityCore = false
            )
        )

        val initialAchievements = listOf(
            Achievement("ACH_FIRST_DAY", "初陣のトレーダー", "初めて端末の日付を進める"),
            Achievement("ACH_BANK_USER", "堅実な市民", "中央銀行に初めて預金する"),
            Achievement("ACH_FIRST_MILLION", "ミリオネア誕生", "総純資産 1,000,000 CR を達成"),
            Achievement("ACH_DECA_MILLION", "メガコーポ級資産家", "総純資産 10,000,000 CR を達成"),
            Achievement("ACH_BUY_FACILITY", "コロニーの地主", "初めてコロニー施設を購入する"),
            Achievement("ACH_CASINO_WIN", "勝負師の閃き", "カジノゲームで勝利する"),
            Achievement("ACH_COLONY_MASTER", "アルカディア統帥者", "コロニー『アルカディア』を買収し完全独立を果たす"),
            Achievement("ACH_INTEL", "情報屋と契約", "カジノの情報屋から市場情報を初めて購入する"),
            Achievement("ACH_CRISIS_SURVIVOR", "恐慌を生き延びる", "恐慌レジームを1回乗り切る")
        )

        val initialLogs = listOf(
            LogEntry(
                id = 1L,
                day = 1L,
                category = "ストーリー",
                message = "CAT-OS端末起動。初期資産 10,000 CR が設定されました。"
            )
        )

        val initialNews = listOf(
            NewsItem(
                id = System.currentTimeMillis(),
                day = 1L,
                kind = "市場",
                headline = "CAT-OS市場フィード接続",
                body = "端末の市場フィードが復旧した。今後、景気レジームの変化や個別銘柄の材料が配信される。情報を先読みして資産を運用せよ。"
            )
        )

        return GameState(
            day = 1L,
            credits = 10000L,
            lastSaveTimeMs = System.currentTimeMillis(),
            mutualFunds = initialFunds,
            stocks = initialStocks,
            fxPairs = initialFxPairs,
            facilities = initialFacilities,
            milestones = initialMilestones,
            achievements = initialAchievements,
            logs = initialLogs,
            marketRegime = MarketRegime.STABLE,
            regimeDaysLeft = 7,
            nextRegime = sampleNextRegime(MarketRegime.STABLE),
            news = initialNews
        )
    }

    private fun regimeDriftForStocks(regime: MarketRegime): Double = when (regime) {
        MarketRegime.BOOM -> 0.006
        MarketRegime.STABLE -> 0.0005
        MarketRegime.SLOWDOWN -> -0.004
        MarketRegime.CRISIS -> -0.012
    }

    private fun regimeDriftForFunds(regime: MarketRegime, fundId: String): Double {
        val isBond = fundId == "FUND_BOND"
        return when (regime) {
            MarketRegime.BOOM -> if (isBond) 0.0003 else 0.0035
            MarketRegime.STABLE -> if (isBond) 0.0004 else 0.0005
            MarketRegime.SLOWDOWN -> if (isBond) 0.0006 else -0.002
            MarketRegime.CRISIS -> if (isBond) 0.0008 else -0.006
        }
    }

    private fun regimeFacilityMultiplier(regime: MarketRegime): Double = when (regime) {
        MarketRegime.BOOM -> 1.15
        MarketRegime.STABLE -> 1.0
        MarketRegime.SLOWDOWN -> 0.9
        MarketRegime.CRISIS -> 0.7
    }

    private fun regimeVolatilityMultiplier(regime: MarketRegime): Double = when (regime) {
        MarketRegime.BOOM -> 1.2
        MarketRegime.STABLE -> 1.0
        MarketRegime.SLOWDOWN -> 1.3
        MarketRegime.CRISIS -> 2.0
    }

    private fun sampleNextRegime(current: MarketRegime): MarketRegime {
        val weights: List<Pair<MarketRegime, Int>> = when (current) {
            MarketRegime.BOOM -> listOf(MarketRegime.BOOM to 5, MarketRegime.STABLE to 45, MarketRegime.SLOWDOWN to 40, MarketRegime.CRISIS to 10)
            MarketRegime.STABLE -> listOf(MarketRegime.BOOM to 30, MarketRegime.STABLE to 30, MarketRegime.SLOWDOWN to 30, MarketRegime.CRISIS to 10)
            MarketRegime.SLOWDOWN -> listOf(MarketRegime.BOOM to 25, MarketRegime.STABLE to 40, MarketRegime.SLOWDOWN to 20, MarketRegime.CRISIS to 15)
            MarketRegime.CRISIS -> listOf(MarketRegime.BOOM to 10, MarketRegime.STABLE to 55, MarketRegime.SLOWDOWN to 25, MarketRegime.CRISIS to 10)
        }
        val total = weights.sumOf { it.second }
        var roll = Random.nextInt(total)
        for ((regime, w) in weights) {
            if (roll < w) return regime
            roll -= w
        }
        return MarketRegime.STABLE
    }

    private fun addNews(state: GameState, kind: String, headline: String, body: String): GameState {
        val item = NewsItem(
            id = System.currentTimeMillis() + Random.nextLong(10000),
            day = state.day,
            kind = kind,
            headline = headline,
            body = body
        )
        return state.copy(news = (listOf(item) + state.news).take(30))
    }

    private fun regimeHintHeadline(regime: MarketRegime): String = when (regime) {
        MarketRegime.BOOM -> "連邦議会で金融緩和法案が審議入り。関係者は「大型景気対策は時間の問題」と楽観"
        MarketRegime.STABLE -> "コロニー経済は平静。アナリストは「現状維持が続く」と分析"
        MarketRegime.SLOWDOWN -> "鉱山労組が賃上げ交渉決裂を警告。資源セクターに減速の影"
        MarketRegime.CRISIS -> "観測所が「太陽フレアの連鎖により通信網崩壊の危険」と異例の警告"
    }

    private fun regimeTransitionHeadline(regime: MarketRegime): String = when (regime) {
        MarketRegime.BOOM -> "【速報】コロニー景気、正式に好況局面へ。市場は活気を取り戻す"
        MarketRegime.STABLE -> "【速報】市場混乱は収束。コロニー経済は安定期に回帰"
        MarketRegime.SLOWDOWN -> "【速報】景気減速が確定。セクター別に資金逃避が進行"
        MarketRegime.CRISIS -> "【速報】市場パニック発生！ 恐慌レジームに突入"
    }

    private fun generateInitialHistory(base: Double, count: Int, vol: Double): List<Double> {
        val list = mutableListOf<Double>()
        var cur = base * 0.92
        for (i in 0 until count) {
            val delta = cur * (Random.nextDouble(-vol, vol * 1.05))
            cur = (cur + delta).coerceAtLeast(base * 0.5)
            list.add(Math.round(cur * 100.0) / 100.0)
        }
        list.add(base)
        return list
    }

    private fun generateInitialPriceHistory(base: Long, count: Int, vol: Double): List<Long> {
        val list = mutableListOf<Long>()
        var cur = (base * 0.9).toLong()
        for (i in 0 until count) {
            val delta = (cur * Random.nextDouble(-vol, vol * 1.05)).toLong()
            cur = (cur + delta).coerceAtLeast(10L)
            list.add(cur)
        }
        list.add(base)
        return list
    }

    // --- オフライン時間経過による利息計算 ---
    data class OfflineResult(
        val newState: GameState,
        val elapsedSeconds: Long,
        val bankEarned: Long,
        val fundsEarned: Long,
        val summaryMessage: String?
    )

    fun processOfflineEarnings(state: GameState, currentTimeMs: Long): OfflineResult {
        if (state.lastSaveTimeMs <= 0L) {
            return OfflineResult(state.copy(lastSaveTimeMs = currentTimeMs), 0L, 0L, 0L, null)
        }

        val elapsedMs = (currentTimeMs - state.lastSaveTimeMs).coerceAtLeast(0L)
        val elapsedSeconds = elapsedMs / 1000
        if (elapsedSeconds < 60) {
            // 1分未満はスキップ
            return OfflineResult(state.copy(lastSaveTimeMs = currentTimeMs), elapsedSeconds, 0L, 0L, null)
        }

        // 最大7日間 (604,800秒) に制限
        val cappedSeconds = elapsedSeconds.coerceAtMost(7 * 24 * 3600L)
        val elapsedHours = cappedSeconds / 3600.0

        // 銀行預金オフライン利息 (50万CRまで1%/日、超過分0.05%/日の段階金利)
        val lowTier = state.bank.deposited.coerceAtMost(500000L)
        val highTier = (state.bank.deposited - 500000L).coerceAtLeast(0L)
        val dailyBankRate = (lowTier * 0.01 + highTier * 0.0005) /
            if (state.bank.deposited > 0L) state.bank.deposited.toDouble() else 1.0
        val rawBankEarned = (state.bank.deposited * dailyBankRate / 24.0 * elapsedHours)
        val bankEarned = if (state.bank.deposited > 0 && elapsedHours >= 1.0) {
            Math.max(1L, Math.round(rawBankEarned))
        } else {
            Math.round(rawBankEarned)
        }

        // 投資信託のオフライン成長 (保有口数に応じて緩やかに成長)
        var fundsEarned = 0L
        val updatedFunds = state.mutualFunds.map { fund ->
            if (fund.ownedUnits > 0) {
                val fundAnnualRate = fund.expectedAnnualReturn
                // 1時間あたりの期待成長率
                val ratePerHour = (fundAnnualRate / (365 * 24)) * 2.0 // ゲーム体験向上のためオフライン進行ボーナス
                val gainPerUnit = fund.nav * ratePerHour * elapsedHours
                val rawGain = fund.ownedUnits * gainPerUnit
                val totalGain = if (gainPerUnit > 0) Math.max(1L, Math.round(rawGain)) else 0L
                fundsEarned += totalGain
                val newNav = (fund.nav + gainPerUnit).coerceAtLeast(100.0)
                fund.copy(nav = Math.round(newNav * 100.0) / 100.0)
            } else {
                fund
            }
        }

        val newBankDeposited = state.bank.deposited + bankEarned
        val hoursInt = (elapsedSeconds / 3600).toInt()
        val minutesInt = ((elapsedSeconds % 3600) / 60).toInt()

        val newLogs = state.logs.toMutableList()
        val msg = if (bankEarned > 0 || fundsEarned > 0) {
            val logMsg = "端末スリープ復帰 (${hoursInt}時間${minutesInt}分経過): 預金利息 +${bankEarned} CR、投信成長 +${fundsEarned} CR を受領。"
            newLogs.add(0, LogEntry(System.currentTimeMillis(), state.day, "システム", logMsg, bankEarned + fundsEarned))
            "【オフライン運用報告】\n前回起動から ${hoursInt}時間${minutesInt}分 が経過しました。\n\n・銀行預金利息: +${bankEarned} CR\n・投資信託成長: +${fundsEarned} CR\n\n資産運用が継続されています。"
        } else {
            null
        }

        val newState = state.copy(
            lastSaveTimeMs = currentTimeMs,
            bank = state.bank.copy(deposited = newBankDeposited),
            mutualFunds = updatedFunds,
            logs = newLogs.take(100)
        )

        return OfflineResult(newState, elapsedSeconds, bankEarned, fundsEarned, msg)
    }

    // --- 日数進行 (1日または30日) ---
    data class AdvanceResult(
        val newState: GameState,
        val newMilestones: List<StoryMilestone>,
        val eventsReport: String
    )

    fun advanceDays(state: GameState, daysToAdvance: Int): AdvanceResult {
        var curState = state
        val newlyUnlockedMilestones = mutableListOf<StoryMilestone>()
        val reportLines = mutableListOf<String>()

        val hasLab = curState.facilities.find { it.type == FacilityType.LAB && it.isOwned } != null
        val labBonusMultiplier = if (hasLab) 1.05 else 1.0

        for (d in 1..daysToAdvance) {
            val currentDay = curState.day + 1

            val newLogs = curState.logs.toMutableList()
            var newsState = curState

            var regime = curState.marketRegime
            var regimeDaysLeft = curState.regimeDaysLeft
            var nextRegime = curState.nextRegime
            var crisisWeathered = curState.crisisWeathered

            if (regimeDaysLeft <= 1) {
                val previousRegime = regime
                regime = nextRegime
                nextRegime = sampleNextRegime(regime)
                regimeDaysLeft = Random.nextInt(5, 13)
                if (previousRegime == MarketRegime.CRISIS) crisisWeathered += 1
                newsState = addNews(newsState.copy(day = currentDay), "レジーム", regimeTransitionHeadline(regime), "コロニー市場のレジームが「${regime.displayName}」へ移行した。${regime.description}")
                newLogs.add(0, LogEntry(
                    id = System.currentTimeMillis() + Random.nextLong(1000),
                    day = currentDay,
                    category = "市場",
                    message = "【レジーム移行】市場は「${regime.displayName}」へ移行。資産配分を見直すべきだ。"
                ))
            } else {
                regimeDaysLeft -= 1
                if (Random.nextDouble() < 0.25) {
                    newsState = addNews(newsState.copy(day = currentDay), "レジーム", regimeHintHeadline(nextRegime), "専門家の間では次のレジームが「${nextRegime.displayName}」であるとの見方が広がっている。早めの備えが肝心だ。")
                }
            }

            val facMultiplier = regimeFacilityMultiplier(regime)
            val volMultiplier = regimeVolatilityMultiplier(regime)

            // 1. 施設収入 (停止中は0、レジーム倍率適用)
            var dayFacilityIncome = 0L
            val updatedFacilities = curState.facilities.map { fac ->
                val nextOutage = if (fac.isOwned && fac.outageDays > 0) fac.outageDays - 1 else fac.outageDays
                if (fac.isOwned && fac.outageDays <= 0) {
                    val income = if (fac.type == FacilityType.PORT) {
                        (fac.currentDailyIncome * Random.nextDouble(0.7, 1.5)).toLong()
                    } else {
                        fac.currentDailyIncome
                    }
                    dayFacilityIncome += (income * facMultiplier * labBonusMultiplier).toLong()
                }
                if (nextOutage != fac.outageDays) fac.copy(outageDays = nextOutage) else fac
            }
            val facilityUpkeep = (dayFacilityIncome * 0.03).toLong()

            // 2. 銀行利息 (恐慌中はゼロ。50万CRまで1%/日、超過分は0.05%/日の段階金利)
            val bankInterest = if (regime == MarketRegime.CRISIS) {
                0L
            } else {
                val dep = curState.bank.deposited
                val lowTier = dep.coerceAtMost(500000L)
                val highTier = (dep - 500000L).coerceAtLeast(0L)
                (lowTier * 0.01).toLong() + (highTier * 0.0005).toLong()
            }
            val newBankDeposited = curState.bank.deposited + bankInterest

            // 3. 投資信託の価格変動 (レジーム連動ドリフト)
            val updatedFunds = curState.mutualFunds.map { fund ->
                val dailyExpected = fund.expectedAnnualReturn / 365.0 + regimeDriftForFunds(regime, fund.id)
                val dailyVol = fund.riskVolatility * volMultiplier
                val randomFactor = nextGaussian() * dailyVol
                val changePercent = dailyExpected + randomFactor
                val newNav = (fund.nav * (1.0 + changePercent)).coerceAtLeast(100.0)
                val roundedNav = Math.round(newNav * 100.0) / 100.0
                val newHistory = (fund.history + roundedNav).takeLast(20)
                fund.copy(nav = roundedNav, history = newHistory)
            }

            // 4. 株式価格変動 (事前告知ショック + レジームドリフト)
            val updatedStocks = curState.stocks.map { stock ->
                val shock = stock.pendingShockPct
                val drift = regimeDriftForStocks(regime)
                val noise = nextGaussian() * stock.volatility * volMultiplier
                val changePct = shock + drift + noise
                val newPrice = ((stock.currentPrice * (1.0 + changePct)).toLong()).coerceAtLeast(10L)
                val newHistory = (stock.history + newPrice).takeLast(20)
                stock.copy(previousPrice = stock.currentPrice, currentPrice = newPrice, history = newHistory, pendingShockPct = 0.0)
            }
            var shockSymbol: String? = null
            var shockPct = 0.0
            if (Random.nextDouble() < 0.12) {
                val target = updatedStocks[Random.nextInt(updatedStocks.size)]
                val raw = if (Random.nextBoolean()) Random.nextDouble(0.06, 0.18) else -Random.nextDouble(0.06, 0.18)
                shockPct = Math.round(raw * 1000.0) / 1000.0
                shockSymbol = target.symbol
            }
            val stocksWithShock = if (shockSymbol != null) {
                updatedStocks.map { if (it.symbol == shockSymbol) it.copy(pendingShockPct = shockPct) else it }
            } else {
                updatedStocks
            }
            if (shockSymbol != null) {
                val dirText = if (shockPct > 0) "急騰" else "急落"
                newsState = addNews(newsState.copy(day = currentDay), "銘柄", "${shockSymbol}に重大材料", "${shockSymbol}の決算・認可関連で「${dirText}」が観測されているという報道。翌日の値動きに注意。")
            }

            // 5. FXレート変動 & ロスカット判定
            val updatedFxPairs = curState.fxPairs.map { pair ->
                val fxDrift = when (regime) {
                    MarketRegime.CRISIS -> if (pair.symbol == "CR/SOL") 0.004 else 0.002
                    MarketRegime.BOOM -> -0.001
                    else -> 0.0
                }
                val changePercent = fxDrift + nextGaussian() * 0.02 * volMultiplier
                val newRate = (pair.currentRate * (1.0 + changePercent)).coerceAtLeast(0.01)
                val roundedRate = Math.round(newRate * 10000.0) / 10000.0
                val newHistory = (pair.history + roundedRate).takeLast(20)
                pair.copy(previousRate = pair.currentRate, currentRate = roundedRate, history = newHistory)
            }

            val remainingPositions = mutableListOf<FxPosition>()
            var fxCashAdjustment = 0L
            var carryCost = 0L

            curState.fxPositions.forEach { pos ->
                val notional = (pos.entryRate * pos.lots * 1000).toLong()
                carryCost += (notional * 0.0005 * pos.leverage).toLong()
                val pair = updatedFxPairs.find { it.symbol == pos.pairSymbol }
                if (pair != null) {
                    val priceDiff = if (pos.direction == FxDirection.LONG) {
                        (pair.currentRate - pos.entryRate)
                    } else {
                        (pos.entryRate - pair.currentRate)
                    }
                    val pnl = (priceDiff * pos.lots * 1000).toLong()
                    val equity = pos.marginUsed + pnl

                    // 証拠金維持率が50%を下回った場合、強制ロスカット
                    if (equity <= pos.marginUsed * 0.5) {
                        val returnedCredit = equity.coerceAtLeast(0L)
                        fxCashAdjustment += returnedCredit
                        newLogs.add(0, LogEntry(
                            id = System.currentTimeMillis() + Random.nextLong(1000),
                            day = currentDay,
                            category = "為替",
                            message = "【警告】${pos.pairSymbol} のポジションが強制ロスカットされました！損失: ${pos.marginUsed - returnedCredit} CR",
                            amountChange = - (pos.marginUsed - returnedCredit)
                        ))
                    } else {
                        remainingPositions.add(pos)
                    }
                }
            }

            // 6. ベーシックインカム (生活保障手当: 所持金が10,000 CR未満なら毎日5,000 CR支給)
            var basicIncome = 0L
            if (curState.credits < 10000L) {
                basicIncome = 5000L
                newLogs.add(0, LogEntry(
                    id = System.currentTimeMillis() + Random.nextLong(1000),
                    day = currentDay,
                    category = "システム",
                    message = "【生活保障】所持金が10,000 CR未満のため、ベーシックインカム手当 +5,000 CR が支給されました。",
                    amountChange = 5000L
                ))
            }

            // 7. 生活コスト (純資産10万CR超の分に対し0.2%/日)
            val livingCost = if (curState.totalNetWorth > 100000L) {
                ((curState.totalNetWorth - 100000L) * 0.002).toLong()
            } else {
                0L
            }

            // 8. 判断型イベントの発生
            var pendingEvent = curState.pendingEvent
            if (pendingEvent == null && Random.nextDouble() < 0.18) {
                pendingEvent = pickRandomEvent()
                newsState = addNews(newsState.copy(day = currentDay), "イベント", pendingEvent.title, "コロニーから対応が求められている。[イベント]パネルで判断せよ。")
                newLogs.add(0, LogEntry(
                    id = System.currentTimeMillis() + Random.nextLong(1000),
                    day = currentDay,
                    category = "イベント",
                    message = "【要判断】${pendingEvent.title}"
                ))
            }

            val newCredits = (curState.credits + dayFacilityIncome - facilityUpkeep + fxCashAdjustment + basicIncome - carryCost - livingCost).coerceAtLeast(0L)

            curState = curState.copy(
                day = currentDay,
                credits = newCredits,
                bank = curState.bank.copy(deposited = newBankDeposited),
                mutualFunds = updatedFunds,
                stocks = stocksWithShock,
                fxPairs = updatedFxPairs,
                fxPositions = remainingPositions,
                facilities = updatedFacilities,
                marketRegime = regime,
                regimeDaysLeft = regimeDaysLeft,
                nextRegime = nextRegime,
                crisisWeathered = crisisWeathered,
                pendingEvent = pendingEvent,
                news = newsState.news,
                logs = newLogs.take(100)
            )
        }


        // 施設・利息等のサマリー集計
        val totalWorth = curState.totalNetWorth
        val hasGravity = curState.facilities.find { it.type == FacilityType.GRAVITY && it.isOwned } != null

        // ストーリーマイルストーン判定
        val updatedMilestones = curState.milestones.map { m ->
            if (!m.isUnlocked) {
                val creditMet = totalWorth >= m.unlockCreditThreshold
                val gravityMet = !m.requiresGravityCore || hasGravity
                if (creditMet && gravityMet) {
                    val unlocked = m.copy(isUnlocked = true, unlockedDay = curState.day)
                    newlyUnlockedMilestones.add(unlocked)
                    unlocked
                } else {
                    m
                }
            } else {
                m
            }
        }

        val logsWithStory = curState.logs.toMutableList()
        newlyUnlockedMilestones.forEach { m ->
            logsWithStory.add(0, LogEntry(
                id = System.currentTimeMillis() + Random.nextLong(1000),
                day = curState.day,
                category = "ストーリー",
                message = "★ストーリー進行: ${m.title}"
            ))
        }

        curState = curState.copy(
            milestones = updatedMilestones,
            logs = logsWithStory.take(100),
            lastSaveTimeMs = System.currentTimeMillis()
        )

        // 実績チェック
        curState = updateAchievements(curState)

        val report = if (daysToAdvance == 1) {
            "宇宙歴 Day ${curState.day} へ進行しました。"
        } else {
            "${daysToAdvance}日間進行し、宇宙歴 Day ${curState.day} になりました。"
        }

        return AdvanceResult(curState, newlyUnlockedMilestones, report)
    }

    // --- 銀行操作 ---
    fun depositBank(state: GameState, amount: Long): GameState {
        if (amount <= 0 || state.credits < amount) return state
        val newCredits = state.credits - amount
        val newDeposited = state.bank.deposited + amount
        val log = LogEntry(
            id = System.currentTimeMillis(),
            day = state.day,
            category = "銀行",
            message = "中央銀行に預金: ${amount} CR",
            amountChange = -amount
        )
        val nextState = state.copy(
            credits = newCredits,
            bank = state.bank.copy(deposited = newDeposited),
            logs = (listOf(log) + state.logs).take(100)
        )
        return updateAchievements(nextState)
    }

    fun withdrawBank(state: GameState, amount: Long): GameState {
        if (amount <= 0 || state.bank.deposited < amount) return state
        val newCredits = state.credits + amount
        val newDeposited = state.bank.deposited - amount
        val log = LogEntry(
            id = System.currentTimeMillis(),
            day = state.day,
            category = "銀行",
            message = "中央銀行から引出: ${amount} CR",
            amountChange = amount
        )
        val nextState = state.copy(
            credits = newCredits,
            bank = state.bank.copy(deposited = newDeposited),
            logs = (listOf(log) + state.logs).take(100)
        )
        return updateAchievements(nextState)
    }

    // --- 投資信託操作 ---
    fun buyMutualFund(state: GameState, fundId: String, amount: Long): GameState {
        if (amount <= 0 || state.credits < amount) return state
        val fund = state.mutualFunds.find { it.id == fundId } ?: return state
        val unitsBought = amount.toDouble() / fund.nav

        val updatedFunds = state.mutualFunds.map {
            if (it.id == fundId) {
                it.copy(
                    ownedUnits = it.ownedUnits + unitsBought,
                    investedCost = it.investedCost + amount
                )
            } else it
        }

        val log = LogEntry(
            id = System.currentTimeMillis(),
            day = state.day,
            category = "投信",
            message = "${fund.name} を購入: ${amount} CR (${String.format("%.2f", unitsBought)}口)",
            amountChange = -amount
        )

        return state.copy(
            credits = state.credits - amount,
            mutualFunds = updatedFunds,
            logs = (listOf(log) + state.logs).take(100)
        )
    }

    fun sellMutualFund(state: GameState, fundId: String, unitsToSell: Double): GameState {
        val fund = state.mutualFunds.find { it.id == fundId } ?: return state
        if (unitsToSell <= 0.0 || fund.ownedUnits < unitsToSell) return state

        val isAll = (unitsToSell >= fund.ownedUnits) || (fund.ownedUnits - unitsToSell < 0.0001)
        val returnCredits = if (isAll) (fund.ownedUnits * fund.nav).toLong() else (unitsToSell * fund.nav).toLong()
        val costProportion = if (fund.ownedUnits > 0.0) (unitsToSell / fund.ownedUnits) else 0.0
        val costDeducted = if (isAll) fund.investedCost else (fund.investedCost * costProportion).toLong()

        val updatedFunds = state.mutualFunds.map {
            if (it.id == fundId) {
                if (isAll) {
                    it.copy(ownedUnits = 0.0, investedCost = 0L)
                } else {
                    it.copy(
                        ownedUnits = (it.ownedUnits - unitsToSell).coerceAtLeast(0.0),
                        investedCost = (it.investedCost - costDeducted).coerceAtLeast(0L)
                    )
                }
            } else it
        }

        val log = LogEntry(
            id = System.currentTimeMillis(),
            day = state.day,
            category = "投信",
            message = "${fund.name} を解約: ${returnCredits} CR 受取 (${String.format("%.2f", unitsToSell)}口)",
            amountChange = returnCredits
        )

        return state.copy(
            credits = state.credits + returnCredits,
            mutualFunds = updatedFunds,
            logs = (listOf(log) + state.logs).take(100)
        )
    }

    // --- 株式売買 ---
    fun buyStock(state: GameState, symbol: String, shares: Long): GameState {
        if (shares <= 0) return state
        val stock = state.stocks.find { it.symbol == symbol } ?: return state
        val totalCost = stock.currentPrice * shares
        if (state.credits < totalCost) return state

        val updatedStocks = state.stocks.map {
            if (it.symbol == symbol) {
                it.copy(
                    ownedShares = it.ownedShares + shares,
                    totalCost = it.totalCost + totalCost
                )
            } else it
        }

        val log = LogEntry(
            id = System.currentTimeMillis(),
            day = state.day,
            category = "株式",
            message = "${stock.name} [${stock.symbol}] を ${shares}株 買付 (${totalCost} CR)",
            amountChange = -totalCost
        )

        return state.copy(
            credits = state.credits - totalCost,
            stocks = updatedStocks,
            logs = (listOf(log) + state.logs).take(100)
        )
    }

    fun sellStock(state: GameState, symbol: String, shares: Long): GameState {
        val stock = state.stocks.find { it.symbol == symbol } ?: return state
        if (shares <= 0 || stock.ownedShares < shares) return state

        val isAll = (shares >= stock.ownedShares)
        val returnCredits = stock.currentPrice * shares
        val costProportion = shares.toDouble() / stock.ownedShares
        val costDeducted = if (isAll) stock.totalCost else (stock.totalCost * costProportion).toLong()

        val updatedStocks = state.stocks.map {
            if (it.symbol == symbol) {
                if (isAll) {
                    it.copy(ownedShares = 0L, totalCost = 0L)
                } else {
                    it.copy(
                        ownedShares = stock.ownedShares - shares,
                        totalCost = (stock.totalCost - costDeducted).coerceAtLeast(0L)
                    )
                }
            } else it
        }

        val profit = returnCredits - costDeducted
        val profitText = if (profit >= 0) "+${profit}" else "$profit"

        val log = LogEntry(
            id = System.currentTimeMillis(),
            day = state.day,
            category = "株式",
            message = "${stock.name} を ${shares}株 売却 (${returnCredits} CR, 損益: ${profitText} CR)",
            amountChange = returnCredits
        )

        return state.copy(
            credits = state.credits + returnCredits,
            stocks = updatedStocks,
            logs = (listOf(log) + state.logs).take(100)
        )
    }

    // --- FX操作 ---
    fun openFxPosition(
        state: GameState,
        pairSymbol: String,
        direction: FxDirection,
        lots: Long,
        leverage: Int
    ): GameState {
        if (lots <= 0 || leverage <= 0) return state
        val pair = state.fxPairs.find { it.symbol == pairSymbol } ?: return state

        // 1ロット = 1,000 通貨単位
        val notionalAmount = pair.currentRate * lots * 1000
        val requiredMargin = (notionalAmount / leverage).toLong()
        if (state.credits < requiredMargin) return state

        // スプレッド考慮のエントリー価格
        val entryRate = if (direction == FxDirection.LONG) {
            pair.currentRate * (1.0 + pair.spreadPercent)
        } else {
            pair.currentRate * (1.0 - pair.spreadPercent)
        }

        val position = FxPosition(
            id = UUID.randomUUID().toString(),
            pairSymbol = pairSymbol,
            direction = direction,
            entryRate = Math.round(entryRate * 10000.0) / 10000.0,
            lots = lots,
            leverage = leverage,
            marginUsed = requiredMargin,
            openDay = state.day
        )

        val log = LogEntry(
            id = System.currentTimeMillis(),
            day = state.day,
            category = "為替",
            message = "${pairSymbol} ${if (direction == FxDirection.LONG) "ロング(買)" else "ショート(売)"} ${lots}ロット (レバ${leverage}倍, 証拠金: ${requiredMargin} CR)",
            amountChange = -requiredMargin
        )

        return state.copy(
            credits = state.credits - requiredMargin,
            fxPositions = state.fxPositions + position,
            logs = (listOf(log) + state.logs).take(100)
        )
    }

    fun closeFxPosition(state: GameState, positionId: String): GameState {
        val pos = state.fxPositions.find { it.id == positionId } ?: return state
        val pair = state.fxPairs.find { it.symbol == pos.pairSymbol } ?: return state

        val priceDiff = if (pos.direction == FxDirection.LONG) {
            (pair.currentRate - pos.entryRate)
        } else {
            (pos.entryRate - pair.currentRate)
        }
        val pnl = (priceDiff * pos.lots * 1000).toLong()
        val returnedCredits = (pos.marginUsed + pnl).coerceAtLeast(0L)

        val log = LogEntry(
            id = System.currentTimeMillis(),
            day = state.day,
            category = "為替",
            message = "${pos.pairSymbol} ポジション決済 (損益: ${if (pnl >= 0) "+$pnl" else "$pnl"} CR, 払戻: ${returnedCredits} CR)",
            amountChange = returnedCredits
        )

        return state.copy(
            credits = state.credits + returnedCredits,
            fxPositions = state.fxPositions.filterNot { it.id == positionId },
            logs = (listOf(log) + state.logs).take(100)
        )
    }

    // --- 施設購入・アップグレード ---
    fun buyOrUpgradeFacility(state: GameState, facilityId: String): GameState {
        val facility = state.facilities.find { it.id == facilityId } ?: return state
        val cost = facility.upgradeCost
        if (state.credits < cost) return state

        val updatedFacilities = state.facilities.map {
            if (it.id == facilityId) {
                if (!it.isOwned) {
                    it.copy(isOwned = true, level = 1)
                } else {
                    it.copy(level = it.level + 1)
                }
            } else it
        }

        val actionName = if (!facility.isOwned) "新設購入" else "Lv.${facility.level + 1}へ増築"
        val log = LogEntry(
            id = System.currentTimeMillis(),
            day = state.day,
            category = "施設",
            message = "施設『${facility.name}』を${actionName} (${cost} CR)",
            amountChange = -cost
        )

        val nextState = state.copy(
            credits = state.credits - cost,
            facilities = updatedFacilities,
            logs = (listOf(log) + state.logs).take(100)
        )
        return updateAchievements(nextState)
    }

    // --- ギャンブル（時間進行なし、期待値ややプラス、たまの大負けスリル） ---

    // 1. コロニー・ルーレット
    data class RouletteResult(
        val winningNumber: Int, // 0 = HAZARD, 1〜12
        val isHazard: Boolean,
        val payout: Long,
        val netGain: Long,
        val message: String
    )

    fun playRoulette(state: GameState, betType: RouletteBetType, betAmount: Long): Pair<GameState, RouletteResult> {
        if (betAmount <= 0 || state.credits < betAmount) {
            return state to RouletteResult(1, false, 0L, 0L, "クレジットが不足しています。")
        }
        val maxBet = (state.credits * 0.2).toLong()
        if (betAmount > maxBet) {
            return state to RouletteResult(1, false, 0L, 0L, "1回のベットは所持金の20%までに制限されている。")
        }

        // 0〜12のルーレットホイール
        // 0 = HAZARD (確率約 7.7%): 大負けハザード
        // 1〜12: 通常ナンバー (奇数: 赤, 偶数: 黒)
        val roll = Random.nextInt(0, 13)
        val isHazard = (roll == 0)

        var payout = 0L
        var winDesc = ""

        if (isHazard && betType != RouletteBetType.HAZARD_LUCK) {
            // 大負けペナルティ: 賭け金没収に加え、ペナルティショック (さらに賭け金の20%喪失)
            val penalty = (betAmount * 0.2).toLong()
            val totalLoss = betAmount + penalty
            val newCredits = (state.credits - totalLoss).coerceAtLeast(0L)
            val log = LogEntry(
                id = System.currentTimeMillis(),
                day = state.day,
                category = "カジノ",
                message = "【大凶】ルーレットでハザード発生！賭け金と過電流損害金により計 ${totalLoss} CR 喪失！",
                amountChange = -totalLoss
            )
            val result = RouletteResult(
                winningNumber = 0,
                isHazard = true,
                payout = 0L,
                netGain = -totalLoss,
                message = "【HAZARD】過電流サージ発生！チップ全損およびペナルティ (-${totalLoss} CR)"
            )
            return state.copy(credits = newCredits, logs = (listOf(log) + state.logs).take(100)) to result
        }

        // 期待値約92%の配当設計 (カジノは長期的に負ける娯楽。儲けは市場で)
        when (betType) {
            RouletteBetType.RED -> {
                val isRed = (roll in 1..12) && (roll % 2 != 0)
                if (isRed) {
                    payout = (betAmount * 2.0).toLong()
                    winDesc = "的中！赤 [${roll}] (配当: ${payout} CR)"
                }
            }
            RouletteBetType.BLACK -> {
                val isBlack = (roll in 1..12) && (roll % 2 == 0)
                if (isBlack) {
                    payout = (betAmount * 2.0).toLong()
                    winDesc = "的中！黒 [${roll}] (配当: ${payout} CR)"
                }
            }
            RouletteBetType.HIGH -> {
                if (roll in 7..12) {
                    payout = (betAmount * 2.0).toLong()
                    winDesc = "的中！HIGH [${roll}] (配当: ${payout} CR)"
                }
            }
            RouletteBetType.LOW -> {
                if (roll in 1..6) {
                    payout = (betAmount * 2.0).toLong()
                    winDesc = "的中！LOW [${roll}] (配当: ${payout} CR)"
                }
            }
            RouletteBetType.NUMBER_7 -> {
                if (roll == 7) {
                    payout = (betAmount * 12.0).toLong()
                    winDesc = "大当たり！LUCKY NUMBER 7 的中！ (配当: ${payout} CR)"
                }
            }
            RouletteBetType.NUMBER_12 -> {
                if (roll == 12) {
                    payout = (betAmount * 12.0).toLong()
                    winDesc = "大当たり！NUMBER 12 的中！ (配当: ${payout} CR)"
                }
            }
            RouletteBetType.HAZARD_LUCK -> {
                if (isHazard) {
                    payout = (betAmount * 12.0).toLong()
                    winDesc = "逆張り的中！HAZARD [0] を捉えた！ (配当: ${payout} CR)"
                }
            }
        }

        val netGain = payout - betAmount
        val newCredits = state.credits + netGain
        val msg = if (payout > 0) winDesc else "ハズレ... 出目: [${roll}]"

        val log = LogEntry(
            id = System.currentTimeMillis(),
            day = state.day,
            category = "カジノ",
            message = "ルーレット (${betType.name}): ${if (payout > 0) pnlText(netGain) else "-$betAmount CR"}",
            amountChange = netGain
        )

        val result = RouletteResult(
            winningNumber = roll,
            isHazard = false,
            payout = payout,
            netGain = netGain,
            message = msg
        )

        val nextState = state.copy(credits = newCredits, logs = (listOf(log) + state.logs).take(100))
        return updateAchievements(nextState, casinoWon = (payout > 0)) to result
    }

    private fun pnlText(amount: Long) = if (amount >= 0) "+${amount} CR" else "${amount} CR"

    // 2. ハイ＆ロー・ダイス
    data class HighLowResult(
        val newRoll: Int,
        val isWin: Boolean,
        val isHazardBust: Boolean,
        val message: String
    )

    fun startHighLow(state: GameState, betAmount: Long): GameState {
        if (betAmount <= 0 || state.credits < betAmount) return state
        val maxBet = (state.credits * 0.2).toLong()
        if (betAmount > maxBet) return state
        val initialRoll = Random.nextInt(2, 13)
        val log = LogEntry(
            id = System.currentTimeMillis(),
            day = state.day,
            category = "カジノ",
            message = "ハイ＆ロー開始 (ベット: ${betAmount} CR, 出目: [${initialRoll}])",
            amountChange = -betAmount
        )
        return state.copy(
            credits = state.credits - betAmount,
            highLow = HighLowGame(
                currentRoll = initialRoll,
                currentBet = betAmount,
                streak = 0,
                isActive = true
            ),
            logs = (listOf(log) + state.logs).take(100)
        )
    }

    fun guessHighLow(state: GameState, choice: HighLowChoice): Pair<GameState, HighLowResult> {
        val game = state.highLow
        if (!game.isActive) return state to HighLowResult(7, false, false, "ゲームが開始されていません。")

        // 稀にコロニー警察の手入れ（バストハザード 4%）
        if (Random.nextDouble() < 0.04) {
            val log = LogEntry(
                id = System.currentTimeMillis(),
                day = state.day,
                category = "カジノ",
                message = "【手入れ】治安部隊が突入！獲得賞金 ${game.currentBet} CR は全額没収されました。",
                amountChange = 0L
            )
            val newState = state.copy(
                highLow = HighLowGame(),
                logs = (listOf(log) + state.logs).take(100)
            )
            return newState to HighLowResult(game.currentRoll, false, true, "コロニー治安部隊の手入れ発生！賞金没収！")
        }

        // 2つのサイコロ合計 (2〜12)
        val d1 = Random.nextInt(1, 7)
        val d2 = Random.nextInt(1, 7)
        val nextRoll = d1 + d2

        val isWin = when (choice) {
            HighLowChoice.HIGH -> nextRoll in 8..12
            HighLowChoice.LOW -> nextRoll in 2..6
            HighLowChoice.SEVEN -> nextRoll == 7
        }

        if (isWin) {
            val newStreak = game.streak + 1
            // 期待値プラスの倍率掛け (連続成功で倍率上昇)
            val multiplier = if (choice == HighLowChoice.SEVEN) 5.0 else 2.0
            val newBet = (game.currentBet * multiplier).toLong()

            val updatedGame = game.copy(
                currentRoll = nextRoll,
                currentBet = newBet,
                streak = newStreak,
                isActive = true
            )
            val result = HighLowResult(
                newRoll = nextRoll,
                isWin = true,
                isHazardBust = false,
                message = "的中！出目: ${nextRoll} (${newStreak}連勝中！現在の賞金: ${newBet} CR)"
            )
            return state.copy(highLow = updatedGame) to result
        } else {
            // 敗北
            val log = LogEntry(
                id = System.currentTimeMillis(),
                day = state.day,
                category = "カジノ",
                message = "ハイ＆ロー敗北。出目: ${nextRoll}",
                amountChange = 0L
            )
            val newState = state.copy(
                highLow = HighLowGame(),
                logs = (listOf(log) + state.logs).take(100)
            )
            return newState to HighLowResult(nextRoll, false, false, "ハズレ！出目: ${nextRoll}。掛け金が没収されました。")
        }
    }

    fun cashoutHighLow(state: GameState): Pair<GameState, Long> {
        val game = state.highLow
        if (!game.isActive || game.currentBet <= 0) return state to 0L

        val payout = game.currentBet
        val log = LogEntry(
            id = System.currentTimeMillis(),
            day = state.day,
            category = "カジノ",
            message = "ハイ＆ローでキャッシュアウト！ ${game.streak}連勝で ${payout} CR を獲得！",
            amountChange = payout
        )

        val newState = state.copy(
            credits = state.credits + payout,
            highLow = HighLowGame(),
            logs = (listOf(log) + state.logs).take(100)
        )
        return updateAchievements(newState, casinoWon = true) to payout
    }

    // 3. ネオ・スロット
    enum class SlotSymbol(val label: String, val multiplier: Double) {
        SEVEN("777", 60.0),
        BAR("BAR", 20.0),
        ORE("ORE", 12.0),
        ENERGY("ENG", 5.0),
        CHIP("CHP", 2.5),
        SKULL("SKULL", -2.0)
    }

    data class SlotResult(
        val reel1: SlotSymbol,
        val reel2: SlotSymbol,
        val reel3: SlotSymbol,
        val payout: Long,
        val netGain: Long,
        val message: String
    )

    fun playSlot(state: GameState, betAmount: Long): Pair<GameState, SlotResult> {
        if (betAmount <= 0 || state.credits < betAmount) {
            return state to SlotResult(SlotSymbol.CHIP, SlotSymbol.CHIP, SlotSymbol.CHIP, 0L, 0L, "クレジット不足です。")
        }
        val maxBet = (state.credits * 0.2).toLong()
        if (betAmount > maxBet) {
            return state to SlotResult(SlotSymbol.CHIP, SlotSymbol.CHIP, SlotSymbol.CHIP, 0L, 0L, "1回のベットは所持金の20%までに制限されている。")
        }

        // リール確率プール (期待値ややプラス + SKULLハザード)
        val symbols = listOf(
            SlotSymbol.CHIP, SlotSymbol.CHIP, SlotSymbol.CHIP, SlotSymbol.CHIP,
            SlotSymbol.ENERGY, SlotSymbol.ENERGY, SlotSymbol.ENERGY,
            SlotSymbol.ORE, SlotSymbol.ORE,
            SlotSymbol.BAR,
            SlotSymbol.SEVEN,
            SlotSymbol.SKULL // たまに出現するドクロ
        )

        val r1 = symbols.random()
        val r2 = symbols.random()
        val r3 = symbols.random()

        var payout = 0L
        var msg = ""
        var netGain = 0L

        if (r1 == SlotSymbol.SKULL && r2 == SlotSymbol.SKULL && r3 == SlotSymbol.SKULL) {
            // ドクロ3つ揃い: 大負けハザード！ベット額の3倍没収
            val penalty = betAmount * 2
            val newCredits = (state.credits - penalty).coerceAtLeast(0L)
            val log = LogEntry(
                id = System.currentTimeMillis(),
                day = state.day,
                category = "カジノ",
                message = "【大凶】スロットで[SKULL-SKULL-SKULL]！端末が暴走し ${penalty} CR 喪失！",
                amountChange = -penalty
            )
            val res = SlotResult(r1, r2, r3, 0L, -penalty, "【大惨事】SKULL揃い！端末暴走ペナルティ (-${penalty} CR)")
            return state.copy(credits = newCredits, logs = (listOf(log) + state.logs).take(100)) to res
        }

        if (r1 == r2 && r2 == r3) {
            // 3つ揃い当たり
            payout = (betAmount * r1.multiplier).toLong()
            msg = "JACKPOT！[${r1.label}] 3つ揃い！ (${payout} CR 獲得)"
        } else if (r1 == r2 || r2 == r3 || r1 == r3) {
            // 2つ揃い小当たり (期待値を支える)
            val match = if (r1 == r2 || r1 == r3) r1 else r2
            if (match != SlotSymbol.SKULL) {
                payout = (betAmount * 2.5).toLong()
                msg = "小当たり！[${match.label}] 2つ揃い (+${payout} CR)"
            } else {
                msg = "ドクロ警戒... ハズレ"
            }
        } else {
            msg = "ハズレ... [${r1.label} | ${r2.label} | ${r3.label}]"
        }

        netGain = payout - betAmount
        val newCredits = state.credits + netGain

        val log = LogEntry(
            id = System.currentTimeMillis(),
            day = state.day,
            category = "カジノ",
            message = "スロット: [${r1.label} | ${r2.label} | ${r3.label}] -> ${pnlText(netGain)}",
            amountChange = netGain
        )

        val res = SlotResult(r1, r2, r3, payout, netGain, msg)
        val nextState = state.copy(credits = newCredits, logs = (listOf(log) + state.logs).take(100))
        return updateAchievements(nextState, casinoWon = (payout > 0)) to res
    }

    // --- 判断型イベント ---
    private fun pickRandomEvent(): GameEvent {
        val events = listOf(
            GameEvent(
                id = "EVT_ASTEROID",
                title = "小惑星接近警報",
                description = "無認可の小惑星がコロニー軌道に接近。評議会は対応を求めている。衝突すれば施設は壊滅的な停止を余儀なくされる。",
                options = listOf(
                    EventOption("避難コストを負担する", "純資産の1%を支出するが、施設は無傷で済む。", creditsDeltaPercentOfNetWorth = -0.01),
                    EventOption("警告を無視する", "支出ゼロ。しかし全所有施設が3日間停止する。", facilityOutageDays = 3)
                )
            ),
            GameEvent(
                id = "EVT_STRIKE",
                title = "施設労組の賃上げ交渉決裂",
                description = "宇宙農園と発電所の労働組合が賃上げを要求。決裂すればストライキで施設が止まる。",
                options = listOf(
                    EventOption("賃上げ要求を呑む", "純資産の2%を支出するが、操業は継続する。", creditsDeltaPercentOfNetWorth = -0.02),
                    EventOption("要求を拒否する", "支出ゼロ。ただし全所有施設が4日間ストライキで停止する。", facilityOutageDays = 4)
                )
            ),
            GameEvent(
                id = "EVT_SURVEYOR",
                title = "探査請負人の来訪",
                description = "古い観測データを持つ探査請負人が「次の市場レジーム」を3,000 CRで売ると持ちかけてきた。",
                options = listOf(
                    EventOption("3,000 CRで購入する", "次のレジームが開示される。恐慌なら安い買い物だ。", creditsDelta = -3000L, revealsNextRegime = true),
                    EventOption("話を断る", "情報は得られない。", creditsDelta = 0L)
                )
            ),
            GameEvent(
                id = "EVT_HACKER",
                title = "端末の脆弱性を発見",
                description = "CAT-OSに不正アクセスの痕跡。放置すれば所持金を掠われる恐れがある。",
                options = listOf(
                    EventOption("2,000 CRで修正する", "セキュリティパッチを適用する。", creditsDelta = -2000L),
                    EventOption("放置する", "所持現金の10%を盗まれる。", creditsDeltaPercentOfCredits = -0.10)
                )
            ),
            GameEvent(
                id = "EVT_REPAIR",
                title = "老朽化した施設設備",
                description = "点検で全所有施設の設備劣化が判明。今すぐ直すか、壊れるまで使い倒すか。",
                options = listOf(
                    EventOption("5,000 CRで緊急修理する", "施設の停止を回避する。", creditsDelta = -5000L),
                    EventOption("様子を見る", "全所有施設が2日間停止する。", facilityOutageDays = 2)
                )
            )
        )
        return events[Random.nextInt(events.size)]
    }

    fun resolveEvent(state: GameState, optionIndex: Int): GameState {
        val event = state.pendingEvent ?: return state
        val option = event.options.getOrNull(optionIndex) ?: return state

        var delta = option.creditsDelta
        if (option.creditsDeltaPercentOfNetWorth != 0.0) {
            delta = (state.totalNetWorth * option.creditsDeltaPercentOfNetWorth).toLong()
        }
        if (option.creditsDeltaPercentOfCredits != 0.0) {
            delta = (state.credits * option.creditsDeltaPercentOfCredits).toLong()
        }
        val newCredits = (state.credits + delta).coerceAtLeast(0L)

        val updatedFacilities = if (option.facilityOutageDays > 0) {
            state.facilities.map { if (it.isOwned) it.copy(outageDays = option.facilityOutageDays) else it }
        } else {
            state.facilities
        }

        var resultState = state.copy(
            credits = newCredits,
            facilities = updatedFacilities,
            pendingEvent = null
        )

        var logMsg = "【イベント判断】${event.title} → ${option.label}"
        if (option.revealsNextRegime) {
            resultState = addNews(resultState, "情報", "次期レジーム開示: ${state.nextRegime.displayName}", "探査請負人の報告により、次のレジームが「${state.nextRegime.displayName}」であることが判明した。${state.nextRegime.description}")
            logMsg += " (次のレジーム: ${state.nextRegime.displayName})"
        }

        val log = LogEntry(
            id = System.currentTimeMillis(),
            day = state.day,
            category = "イベント",
            message = logMsg,
            amountChange = delta
        )
        resultState = resultState.copy(logs = (listOf(log) + resultState.logs).take(100))
        return updateAchievements(resultState)
    }

    // --- 情報屋 (カジノの市場情報販売) ---
    data class IntelResult(
        val message: String,
        val success: Boolean
    )

    fun buyIntel(state: GameState): Pair<GameState, IntelResult> {
        val cost = 2500L
        if (state.credits < cost) {
            return state to IntelResult("情報料 2,500 CR が不足している。", false)
        }

        var newState = state.copy(
            credits = state.credits - cost,
            intelPurchasedCount = state.intelPurchasedCount + 1
        )

        val regimeMsg = "次のレジームは「${state.nextRegime.displayName}」。${state.nextRegime.description}"
        newState = addNews(newState, "情報", "情報屋の報告: 次期レジーム", regimeMsg)

        var extra = ""
        val shockStocks = newState.stocks.filter { it.pendingShockPct != 0.0 }
        if (shockStocks.isNotEmpty()) {
            val shockStock = shockStocks[Random.nextInt(shockStocks.size)]
            val dir = if (shockStock.pendingShockPct > 0) "急騰" else "急落"
            extra = " さらに銘柄情報: ${shockStock.symbol} は明日${dir}の観測。"
            newState = addNews(newState, "情報", "情報屋の報告: ${shockStock.symbol}", "${shockStock.symbol} に重大材料。明日は${dir}が観測されている。")
        }

        val log = LogEntry(
            id = System.currentTimeMillis(),
            day = state.day,
            category = "情報",
            message = "情報屋に ${cost} CR 支払い、市場情報を購入。${regimeMsg}${extra}",
            amountChange = -cost
        )
        newState = newState.copy(logs = (listOf(log) + newState.logs).take(100))
        return updateAchievements(newState) to IntelResult("情報屋: ${regimeMsg}${extra}", true)
    }

    // --- 実績の更新 ---
    fun updateAchievements(state: GameState, casinoWon: Boolean = false): GameState {
        if (state.achievements.isEmpty()) return state

        val netWorth = state.totalNetWorth
        val newlyUnlocked = mutableListOf<Achievement>()

        val updated = state.achievements.map { ach ->
            if (ach.isUnlocked) {
                ach
            } else {
                val conditionMet = when (ach.id) {
                    "ACH_FIRST_DAY" -> state.day > 1L
                    "ACH_BANK_USER" -> state.bank.deposited > 0L
                    "ACH_FIRST_MILLION" -> netWorth >= 1000000L
                    "ACH_DECA_MILLION" -> netWorth >= 10000000L
                    "ACH_BUY_FACILITY" -> state.facilities.any { it.isOwned }
                    "ACH_CASINO_WIN" -> casinoWon
                    "ACH_COLONY_MASTER" -> state.colonyPurchased
                    "ACH_INTEL" -> state.intelPurchasedCount > 0
                    "ACH_CRISIS_SURVIVOR" -> state.crisisWeathered > 0
                    else -> false
                }
                if (conditionMet) {
                    val unlocked = ach.copy(isUnlocked = true, unlockedAt = System.currentTimeMillis())
                    newlyUnlocked.add(unlocked)
                    unlocked
                } else {
                    ach
                }
            }
        }

        if (newlyUnlocked.isEmpty()) {
            return state.copy(achievements = updated)
        }

        val logsWithAch = state.logs.toMutableList()
        newlyUnlocked.forEach { ach ->
            logsWithAch.add(0, LogEntry(
                id = System.currentTimeMillis() + Random.nextLong(1000),
                day = state.day,
                category = "実績",
                message = "【実績解除】${ach.title}（${ach.description}）"
            ))
        }

        return state.copy(
            achievements = updated,
            logs = logsWithAch.take(100)
        )
    }

    // --- コロニー買収 ---
    fun purchaseColony(state: GameState): GameState {
        val cost = 100000000L // 1億クレジット

        if (state.credits < cost) return state

        val updatedMilestones = state.milestones.map {
            if (it.id == 6) it.copy(isUnlocked = true, unlockedDay = state.day) else it
        }

        val log = LogEntry(
            id = System.currentTimeMillis(),
            day = state.day,
            category = "ストーリー",
            message = "★ コロニー『アルカディア』買収完了！あなたが新たな最高統帥者です！",
            amountChange = -cost
        )

        val nextState = state.copy(
            credits = state.credits - cost,
            colonyPurchased = true,
            milestones = updatedMilestones,
            logs = (listOf(log) + state.logs).take(100)
        )

        return updateAchievements(nextState)
    }

    // --- デバッグ機能 (BuildConfig.DEBUG時) ---
    fun debugAddCredits(state: GameState, amount: Long): GameState {
        val newCredits = (state.credits + amount).coerceAtLeast(0L)
        val log = LogEntry(
            id = System.currentTimeMillis(),
            day = state.day,
            category = "デバッグ",
            message = "デバッグ操作: クレジットを ${if (amount >= 0) "+$amount" else "$amount"} CR 変動",
            amountChange = amount
        )
        return state.copy(
            credits = newCredits,
            logs = (listOf(log) + state.logs).take(100)
        )
    }

    fun debugSkipDays(state: GameState, days: Int): AdvanceResult {
        return advanceDays(state, days)
    }

    fun debugInstantClearSetup(state: GameState): GameState {
        // 重力維持装置を所持させ、所持金を1億1千万に
        val updatedFacilities = state.facilities.map {
            if (it.type == FacilityType.GRAVITY) it.copy(isOwned = true, level = 1) else it
        }
        val log = LogEntry(
            id = System.currentTimeMillis(),
            day = state.day,
            category = "デバッグ",
            message = "デバッグ操作: クリア用セットアップ（1.1億CR付与 ＆ 重力維持装置取得）",
            amountChange = 110000000L
        )
        return state.copy(
            credits = 110000000L,
            facilities = updatedFacilities,
            logs = (listOf(log) + state.logs).take(100)
        )
    }
}
