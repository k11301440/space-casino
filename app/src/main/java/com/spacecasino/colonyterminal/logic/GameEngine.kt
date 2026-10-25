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
            Achievement("ACH_COLONY_MASTER", "アルカディア統帥者", "コロニー『アルカディア』を買収し完全独立を果たす")
        )

        val initialLogs = listOf(
            LogEntry(
                id = 1L,
                day = 1L,
                category = "ストーリー",
                message = "CAT-OS端末起動。初期資産 10,000 CR が設定されました。"
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
            logs = initialLogs
        )
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

        // 銀行預金オフライン利息 (24時間で約1.0% = 1時間あたり約 0.0416%)
        val bankRatePerHour = 0.01 / 24.0
        val rawBankEarned = (state.bank.deposited * bankRatePerHour * elapsedHours)
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

            // 1. 施設収入
            var dayFacilityIncome = 0L
            curState.facilities.forEach { fac ->
                if (fac.isOwned) {
                    val income = if (fac.type == FacilityType.PORT) {
                        // 貿易港は日によってランダム変動 (70%〜150%)
                        (fac.currentDailyIncome * Random.nextDouble(0.7, 1.5)).toLong()
                    } else {
                        fac.currentDailyIncome
                    }
                    dayFacilityIncome += (income * labBonusMultiplier).toLong()
                }
            }

            // 2. 銀行利息
            val bankInterest = (curState.bank.deposited * curState.bank.dailyInterestRate).toLong()
            val newBankDeposited = curState.bank.deposited + bankInterest

            // 3. 投資信託の価格変動
            val updatedFunds = curState.mutualFunds.map { fund ->
                val dailyExpected = fund.expectedAnnualReturn / 365.0
                val dailyVol = fund.riskVolatility
                val randomFactor = nextGaussian() * dailyVol
                val changePercent = dailyExpected + randomFactor
                val newNav = (fund.nav * (1.0 + changePercent)).coerceAtLeast(100.0)
                val roundedNav = Math.round(newNav * 100.0) / 100.0
                val newHistory = (fund.history + roundedNav).takeLast(20)
                fund.copy(nav = roundedNav, history = newHistory)
            }

            // 4. 株式価格変動
            val updatedStocks = curState.stocks.map { stock ->
                val vol = stock.volatility
                // たまに市場ニュース（好材料または悪材料）
                val newsFactor = if (Random.nextDouble() < 0.05) {
                    Random.nextDouble(-0.25, 0.35)
                } else {
                    nextGaussian() * vol
                }
                val change = (stock.currentPrice * newsFactor).toLong()
                val newPrice = (stock.currentPrice + change).coerceAtLeast(10L)
                val newHistory = (stock.history + newPrice).takeLast(20)
                stock.copy(previousPrice = stock.currentPrice, currentPrice = newPrice, history = newHistory)
            }

            // 5. FXレート変動 & ロスカット判定
            val updatedFxPairs = curState.fxPairs.map { pair ->
                val changePercent = nextGaussian() * 0.02
                val newRate = (pair.currentRate * (1.0 + changePercent)).coerceAtLeast(0.01)
                val roundedRate = Math.round(newRate * 10000.0) / 10000.0
                val newHistory = (pair.history + roundedRate).takeLast(20)
                pair.copy(previousRate = pair.currentRate, currentRate = roundedRate, history = newHistory)
            }

            // ポジションの未実現損益とロスカットチェック
            val remainingPositions = mutableListOf<FxPosition>()
            var fxCashAdjustment = 0L
            val newLogs = curState.logs.toMutableList()

            curState.fxPositions.forEach { pos ->
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

            val newCredits = curState.credits + dayFacilityIncome + fxCashAdjustment + basicIncome

            curState = curState.copy(
                day = currentDay,
                credits = newCredits,
                bank = curState.bank.copy(deposited = newBankDeposited),
                mutualFunds = updatedFunds,
                stocks = updatedStocks,
                fxPairs = updatedFxPairs,
                fxPositions = remainingPositions,
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

        // 0〜13のルーレットホイール
        // 0 = HAZARD (確率約 5.5%): 大負けハザード
        // 1〜12: 通常ナンバー (奇数: 赤, 偶数: 黒)
        val roll = Random.nextInt(0, 14) // 0..13 (13はボーナススロット)
        val isHazard = (roll == 0)

        var payout = 0L
        var winDesc = ""

        if (isHazard) {
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
                message = "【HAZARD】過電流サージ発生！チップ全損害およびペナルティ (-${totalLoss} CR)"
            )
            return state.copy(credits = newCredits, logs = (listOf(log) + state.logs).take(100)) to result
        }

        // 期待値約104%の配当設計
        when (betType) {
            RouletteBetType.RED -> {
                // 赤: 奇数 (1,3,5,7,9,11)
                val isRed = (roll in 1..12) && (roll % 2 != 0)
                if (isRed || roll == 13) {
                    payout = (betAmount * 2.15).toLong() // 2.15倍
                    winDesc = "的中！赤 [${roll}] (配当: ${payout} CR)"
                }
            }
            RouletteBetType.BLACK -> {
                // 黒: 偶数 (2,4,6,8,10,12)
                val isBlack = (roll in 1..12) && (roll % 2 == 0)
                if (isBlack || roll == 13) {
                    payout = (betAmount * 2.15).toLong()
                    winDesc = "的中！黒 [${roll}] (配当: ${payout} CR)"
                }
            }
            RouletteBetType.HIGH -> {
                // 7〜12
                if (roll in 7..12 || roll == 13) {
                    payout = (betAmount * 2.15).toLong()
                    winDesc = "的中！HIGH [${roll}] (配当: ${payout} CR)"
                }
            }
            RouletteBetType.LOW -> {
                // 1〜6
                if (roll in 1..6 || roll == 13) {
                    payout = (betAmount * 2.15).toLong()
                    winDesc = "的中！LOW [${roll}] (配当: ${payout} CR)"
                }
            }
            RouletteBetType.NUMBER_7 -> {
                // 単数7
                if (roll == 7 || roll == 13) {
                    payout = (betAmount * 13.5).toLong() // 13.5倍
                    winDesc = "大当たり！LUCKY NUMBER 7 的中！ (配当: ${payout} CR)"
                }
            }
            RouletteBetType.NUMBER_12 -> {
                if (roll == 12 || roll == 13) {
                    payout = (betAmount * 13.5).toLong()
                    winDesc = "大当たり！NUMBER 12 的中！ (配当: ${payout} CR)"
                }
            }
            RouletteBetType.HAZARD_LUCK -> {
                // 逆張りハザード回避保険: ハザード以外なら1.1倍
                payout = (betAmount * 1.12).toLong()
                winDesc = "ハザード回避成功！ [${roll}] (配当: ${payout} CR)"
            }
        }

        val netGain = payout - betAmount
        val newCredits = state.credits + netGain
        val msg = if (payout > 0) winDesc else "ハズレ... 出目: [${if (roll == 13) "BONUS" else roll.toString()}]"

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
            val multiplier = if (choice == HighLowChoice.SEVEN) 3.5 else 1.6
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
        BAR("BAR", 25.0),
        ORE("ORE", 12.0),
        ENERGY("ENG", 6.0),
        CHIP("CHP", 3.0),
        SKULL("SKULL", -2.0) // 大負けハザード
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
            val penalty = betAmount * 3
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
                payout = (betAmount * 1.5).toLong()
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
