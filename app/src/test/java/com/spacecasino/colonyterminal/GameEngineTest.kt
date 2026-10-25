package com.spacecasino.colonyterminal

import com.spacecasino.colonyterminal.logic.GameEngine
import com.spacecasino.colonyterminal.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class GameEngineTest {

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    @Test
    fun testInitialStateIntegrity() {
        val state = GameEngine.createInitialState()

        assertEquals("初期所持金は10,000CRであること", 10000L, state.credits)
        assertEquals("初期日数は1であること", 1L, state.day)
        assertEquals("初期預金は0であること", 0L, state.bank.deposited)
        assertEquals("投信は5銘柄用意されていること", 5, state.mutualFunds.size)
        assertEquals("株式は5銘柄用意されていること", 5, state.stocks.size)
        assertEquals("為替は3ペア用意されていること", 3, state.fxPairs.size)
        assertEquals("施設は6種類用意されていること", 6, state.facilities.size)
        assertEquals("マイルストーンは7件用意されていること", 7, state.milestones.size)

        // 初期総資産
        assertEquals("初期純資産は手持ち現金と同じ10,000CRであること", 10000L, state.totalNetWorth)
        assertFalse("初期状態ではクリアしていないこと", state.colonyPurchased)
    }

    @Test
    fun testBankOperations() {
        var state = GameEngine.createInitialState()

        // 預金預入
        state = GameEngine.depositBank(state, 3000L)
        assertEquals(7000L, state.credits)
        assertEquals(3000L, state.bank.deposited)
        assertEquals("純資産は変わらないこと", 10000L, state.totalNetWorth)

        // 預金引出
        state = GameEngine.withdrawBank(state, 1000L)
        assertEquals(8000L, state.credits)
        assertEquals(2000L, state.bank.deposited)
        assertEquals(10000L, state.totalNetWorth)

        // 残高以上の引出は無視されること
        val ignoredState = GameEngine.withdrawBank(state, 50000L)
        assertEquals(state.bank.deposited, ignoredState.bank.deposited)
    }

    @Test
    fun testMutualFundTrading() {
        var state = GameEngine.createInitialState()
        val fundId = "FUND_INDEX"

        // 買付 5000 CR (NAV 10000 なので 0.5口)
        state = GameEngine.buyMutualFund(state, fundId, 5000L)
        assertEquals(5000L, state.credits)
        val fund = state.mutualFunds.find { it.id == fundId }!!
        assertEquals(0.5, fund.ownedUnits, 0.001)
        assertEquals(5000L, fund.investedCost)

        // 一部解約 0.2口
        state = GameEngine.sellMutualFund(state, fundId, 0.2)
        val updatedFund = state.mutualFunds.find { it.id == fundId }!!
        assertEquals(0.3, updatedFund.ownedUnits, 0.001)
        assertEquals(7000L, state.credits) // 5000 + (0.2 * 10000)
    }

    @Test
    fun testStockTrading() {
        var state = GameEngine.createInitialState()
        val symbol = "ASTRO"
        val stock = state.stocks.find { it.symbol == symbol }!!
        val price = stock.currentPrice

        // 2株買付
        state = GameEngine.buyStock(state, symbol, 2L)
        assertEquals(10000L - price * 2, state.credits)
        val ownedStock = state.stocks.find { it.symbol == symbol }!!
        assertEquals(2L, ownedStock.ownedShares)

        // 1株売却
        state = GameEngine.sellStock(state, symbol, 1L)
        assertEquals(10000L - price, state.credits)
        val afterSell = state.stocks.find { it.symbol == symbol }!!
        assertEquals(1L, afterSell.ownedShares)
    }

    @Test
    fun testFxOperationsAndLiquidation() {
        var state = GameEngine.createInitialState()
        val pair = "CR/SOL"

        // 1ロット、レバレッジ10倍でLong注文
        state = GameEngine.openFxPosition(state, pair, FxDirection.LONG, 1L, 10)
        assertEquals("ポジションが1件作成されていること", 1, state.fxPositions.size)
        val pos = state.fxPositions.first()
        assertTrue("証拠金が手持ちから引かれていること", state.credits < 10000L)

        // 手動決済
        val closedState = GameEngine.closeFxPosition(state, pos.id)
        assertEquals(0, closedState.fxPositions.size)
        assertTrue("証拠金が払い戻されていること", closedState.credits > 0)
    }

    @Test
    fun testFacilityPurchaseAndUpgrade() {
        var state = GameEngine.createInitialState()
        val facId = "FAC_OXYGEN"

        // 酸素精製施設購入 (20,000 CR) のため、デバッグクレジットを一時付与
        state = GameEngine.debugAddCredits(state, 50000L)
        state = GameEngine.buyOrUpgradeFacility(state, facId)

        val facility = state.facilities.find { it.id == facId }!!
        assertTrue("施設を所有していること", facility.isOwned)
        assertEquals("レベル1であること", 1, facility.level)
        assertEquals(180L, facility.currentDailyIncome)

        // レベルアップ (Lv.2)
        val costLv2 = facility.upgradeCost
        state = GameEngine.buyOrUpgradeFacility(state, facId)
        val upgraded = state.facilities.find { it.id == facId }!!
        assertEquals(2, upgraded.level)
        assertEquals(360L, upgraded.currentDailyIncome)
    }

    @Test
    fun testTimeAdvanceAndFacilityIncome() {
        var state = GameEngine.createInitialState()
        // 酸素施設(180CR/日)と宇宙農園(650CR/日)を所有
        state = GameEngine.debugAddCredits(state, 100000L)
        state = GameEngine.buyOrUpgradeFacility(state, "FAC_OXYGEN")
        state = GameEngine.buyOrUpgradeFacility(state, "FAC_FARM")
        val currentCash = state.credits

        // 1日進める
        val result = GameEngine.advanceDays(state, 1)
        assertEquals(2L, result.newState.day)
        val expectedDailyIncome = 180L + 650L
        assertEquals(currentCash + expectedDailyIncome, result.newState.credits)

        // 30日進める
        val result30 = GameEngine.advanceDays(result.newState, 30)
        assertEquals(32L, result30.newState.day)
        assertTrue("30日分の施設収入が加算されていること", result30.newState.credits > currentCash + expectedDailyIncome)
    }

    @Test
    fun testOfflineEarnings() {
        var state = GameEngine.createInitialState()
        state = GameEngine.depositBank(state, 10000L) // 10,000 CR預金
        state = GameEngine.buyMutualFund(state, "FUND_INDEX", 5000L)

        val saveTime = 1000000000000L
        state = state.copy(lastSaveTimeMs = saveTime)

        // 24時間後 (86,400秒後)
        val currentTime = saveTime + (24 * 3600 * 1000L)
        val offlineResult = GameEngine.processOfflineEarnings(state, currentTime)

        assertTrue("経過時間が24時間分計算されていること", offlineResult.elapsedSeconds >= 86400L)
        assertTrue("銀行利息が付与されていること", offlineResult.bankEarned > 0)
        assertTrue("サマリーメッセージが生成されていること", offlineResult.summaryMessage != null)
        assertTrue("預金残高が増加していること", offlineResult.newState.bank.deposited > state.bank.deposited)
    }

    @Test
    fun testGamblingDoesNotAdvanceTime() {
        val state = GameEngine.createInitialState()
        val initialDay = state.day

        // ルーレット実行
        val (stateRoulette, _) = GameEngine.playRoulette(state, RouletteBetType.RED, 100L)
        assertEquals("ルーレット中に時間は進まないこと", initialDay, stateRoulette.day)

        // ハイ＆ロー実行
        val stateHL = GameEngine.startHighLow(state, 100L)
        assertEquals("ハイ＆ロー中に時間は進まないこと", initialDay, stateHL.day)

        // スロット実行
        val (stateSlot, _) = GameEngine.playSlot(state, 100L)
        assertEquals("スロット中に時間は進まないこと", initialDay, stateSlot.day)
    }

    @Test
    fun testColonyPurchaseConditionAndClear() {
        var state = GameEngine.createInitialState()

        // 資金不足（10,000 CR）での買収は失敗すること
        val failState = GameEngine.purchaseColony(state)
        assertFalse("資金不足では買収不可", failState.colonyPurchased)

        // 資金を1億CR付与（重力維持装置は未所持）
        state = GameEngine.debugAddCredits(state, 100000000L)

        // 資金が1億CR以上ある状態で買収執行（重力装置不要で成功）
        val clearState = GameEngine.purchaseColony(state)
        assertTrue("1億CRでコロニー買収成功", clearState.colonyPurchased)
        val clearMilestone = clearState.milestones.find { it.id == 6 }!!
        assertTrue("最終マイルストーンがアンロックされていること", clearMilestone.isUnlocked)
        assertTrue("実績『アルカディア統帥者』が解除されていること", clearState.achievements.any { it.id == "ACH_COLONY_MASTER" && it.isUnlocked })
    }

    @Test
    fun testBasicIncomeWhenUnder10000() {
        var state = GameEngine.createInitialState()
        // 所持金を3,000 CRに減らす
        state = state.copy(credits = 3000L)

        // 1日進める
        val result = GameEngine.advanceDays(state, 1)
        val newState = result.newState

        // 3000 + 5000 = 8000 CR (施設収入なし)
        assertEquals("10,000 CR未満の時、毎日5,000 CRのベーシックインカムが支給されること", 8000L, newState.credits)
        assertTrue("生活保障のログが記録されていること", newState.logs.any { it.message.contains("生活保障") })
    }

    @Test
    fun testSerializationRoundTrip() {
        val originalState = GameEngine.createInitialState()
        val jsonStr = json.encodeToString(originalState)
        assertNotNull(jsonStr)

        val restoredState = json.decodeFromString<GameState>(jsonStr)
        assertEquals(originalState.credits, restoredState.credits)
        assertEquals(originalState.day, restoredState.day)
        assertEquals(originalState.stocks.size, restoredState.stocks.size)
        assertEquals(originalState.facilities.size, restoredState.facilities.size)
        assertEquals(originalState.milestones.size, restoredState.milestones.size)
    }

    @Test
    fun testZeroCreditGuards() {
        var state = GameEngine.createInitialState()
        // 所持金を0にする
        state = state.copy(credits = 0L)

        // 投信買付不可
        val fundState = GameEngine.buyMutualFund(state, "FUND_INDEX", 1000L)
        assertEquals(0L, fundState.credits)
        assertEquals(0.0, fundState.mutualFunds.first().ownedUnits, 0.0)

        // 株買付不可
        val stockState = GameEngine.buyStock(state, "ASTRO", 1L)
        assertEquals(0L, stockState.credits)
        assertEquals(0L, stockState.stocks.first().ownedShares)

        // FX注文不可
        val fxState = GameEngine.openFxPosition(state, "CR/SOL", FxDirection.LONG, 1L, 10)
        assertEquals(0, fxState.fxPositions.size)

        // 施設購入不可
        val facState = GameEngine.buyOrUpgradeFacility(state, "FAC_OXYGEN")
        assertFalse(facState.facilities.first().isOwned)

        // ギャンブル不可
        val (rouletteState, res) = GameEngine.playRoulette(state, RouletteBetType.RED, 100L)
        assertEquals(0L, rouletteState.credits)
        assertEquals(0L, res.payout)

        val hlState = GameEngine.startHighLow(state, 100L)
        assertFalse(hlState.highLow.isActive)

        val (slotState, slotRes) = GameEngine.playSlot(state, 100L)
        assertEquals(0L, slotState.credits)
        assertEquals(0L, slotRes.payout)
    }

    @Test
    fun testZeroResidualOnAllSold() {
        var state = GameEngine.createInitialState()

        // 投信購入 -> 全口解約
        state = GameEngine.buyMutualFund(state, "FUND_INDEX", 3333L)
        val fund = state.mutualFunds.find { it.id == "FUND_INDEX" }!!
        state = GameEngine.sellMutualFund(state, "FUND_INDEX", fund.ownedUnits)
        val soldFund = state.mutualFunds.find { it.id == "FUND_INDEX" }!!
        assertEquals(0.0, soldFund.ownedUnits, 0.0)
        assertEquals(0L, soldFund.investedCost)

        // 株式購入 -> 全株売却
        state = GameEngine.buyStock(state, "ASTRO", 3L)
        val stock = state.stocks.find { it.symbol == "ASTRO" }!!
        state = GameEngine.sellStock(state, "ASTRO", stock.ownedShares)
        val soldStock = state.stocks.find { it.symbol == "ASTRO" }!!
        assertEquals(0L, soldStock.ownedShares)
        assertEquals(0L, soldStock.totalCost)
    }

    @Test
    fun testHighNetWorthOverflowGuard() {
        var state = GameEngine.createInitialState()
        // 1,000億CR の所持金
        state = state.copy(credits = 100_000_000_000L)
        assertTrue(state.totalNetWorth >= 100_000_000_000L)

        // 施設を最大レベル近くまでアップグレード
        val fac = state.facilities.first().copy(isOwned = true, level = 55)
        assertTrue("オーバーフローせず正の数であること", fac.upgradeCost > 0L)
        assertTrue("正の日収であること", fac.currentDailyIncome > 0L)
    }

    @Test
    fun testHighLowGameFlow() {
        var state = GameEngine.createInitialState()
        // 開始ベット 1000 CR
        state = GameEngine.startHighLow(state, 1000L)
        assertTrue("ハイ＆ローがアクティブになること", state.highLow.isActive)
        assertEquals(9000L, state.credits)
        assertEquals(1000L, state.highLow.currentBet)
        assertTrue("初期出目は2〜12の範囲", state.highLow.currentRoll in 2..12)

        // キャッシュアウト
        val (afterCashout, payout) = GameEngine.cashoutHighLow(state)
        assertFalse("キャッシュアウト後は非アクティブ", afterCashout.highLow.isActive)
        assertEquals(1000L, payout)
        assertEquals(10000L, afterCashout.credits)
    }

    @Test
    fun testSlotMechanics() {
        var state = GameEngine.createInitialState()
        val bet = 300L
        val (afterSlot, result) = GameEngine.playSlot(state, bet)

        assertNotNull(result.reel1)
        assertNotNull(result.reel2)
        assertNotNull(result.reel3)
        // 払戻または損失が反映されていること
        assertEquals(state.credits - bet + result.payout, afterSlot.credits)
    }

    @Test
    fun testFxLiquidationTrigger() {
        var state = GameEngine.createInitialState()
        val pairSymbol = state.fxPairs.first().symbol
        // レバレッジ25倍でLONG注文 (1ロット)
        state = GameEngine.openFxPosition(state, pairSymbol, FxDirection.LONG, 1L, 25)
        assertEquals(1, state.fxPositions.size)

        // 為替レートが大幅に下落した状態を作成（25倍レバなら-10%で即座に維持率50%以下へ）
        val pair = state.fxPairs.find { it.symbol == pairSymbol }!!
        val crashedPair = pair.copy(currentRate = pair.currentRate * 0.90)
        state = state.copy(
            fxPairs = state.fxPairs.map { if (it.symbol == pairSymbol) crashedPair else it }
        )

        // 日付を進めるとロスカット判定が走る
        val advanceRes = GameEngine.advanceDays(state, 1)
        assertTrue("ロスカットされてポジションが解消されていること", advanceRes.newState.fxPositions.isEmpty())
        assertTrue("ロスカットのログが記録されていること", advanceRes.newState.logs.any { it.category == "為替" && it.message.contains("強制ロスカット") })
    }
}
