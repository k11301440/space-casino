package com.spacecasino.colonyterminal.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.spacecasino.colonyterminal.data.GameRepository
import com.spacecasino.colonyterminal.logic.GameEngine
import com.spacecasino.colonyterminal.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MainTab(val title: String) {
    DASHBOARD("概要"),
    INVEST("運用"),
    FACILITIES("施設"),
    CASINO("カジノ"),
    LOGS("記録"),
    SETTINGS("設定")
}

sealed interface GameDialogState {
    data class OfflineReport(val message: String) : GameDialogState
    data class StoryEvent(val milestone: StoryMilestone) : GameDialogState
    data class GameClear(val day: Long, val netWorth: Long) : GameDialogState
    data class Notice(val title: String, val message: String) : GameDialogState
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(application)

    private val _gameState = MutableStateFlow(GameEngine.createInitialState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _currentTab = MutableStateFlow(MainTab.DASHBOARD)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _currentDialog = MutableStateFlow<GameDialogState?>(null)
    val currentDialog: StateFlow<GameDialogState?> = _currentDialog.asStateFlow()

    private val pendingStoryPopups = ArrayDeque<StoryMilestone>()

    init {
        loadGame()
    }

    private fun loadGame() {
        val (loadedState, offlineResult) = repository.loadGameState()
        _gameState.value = loadedState
        com.spacecasino.colonyterminal.audio.SoundManager.setBgmMode(loadedState.bgmMode)

        if (offlineResult?.summaryMessage != null) {
            _currentDialog.value = GameDialogState.OfflineReport(offlineResult.summaryMessage)
        }
    }

    fun setBgmMode(mode: BgmMode) {
        val newState = _gameState.value.copy(bgmMode = mode)
        handleStateUpdate(newState)
        com.spacecasino.colonyterminal.audio.SoundManager.setBgmMode(mode)
    }

    override fun onCleared() {
        super.onCleared()
        com.spacecasino.colonyterminal.audio.SoundManager.stopBgm()
    }

    fun setTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun dismissDialog() {
        if (pendingStoryPopups.isNotEmpty()) {
            val nextMilestone = pendingStoryPopups.removeFirst()
            _currentDialog.value = GameDialogState.StoryEvent(nextMilestone)
        } else {
            _currentDialog.value = null
        }
    }

    private fun handleStateUpdate(newState: GameState) {
        _gameState.value = newState
        repository.saveGameState(newState)
    }

    // --- 日時進行 ---
    fun advanceTime(days: Int) {
        val result = GameEngine.advanceDays(_gameState.value, days)
        handleStateUpdate(result.newState)

        if (result.newMilestones.isNotEmpty()) {
            pendingStoryPopups.addAll(result.newMilestones)
            if (_currentDialog.value == null) {
                dismissDialog() // 最初のポップアップを表示
            }
        }
    }

    // --- 銀行 ---
    fun depositBank(amount: Long) {
        handleStateUpdate(GameEngine.depositBank(_gameState.value, amount))
    }

    fun withdrawBank(amount: Long) {
        handleStateUpdate(GameEngine.withdrawBank(_gameState.value, amount))
    }

    // --- 投資信託 ---
    fun buyMutualFund(fundId: String, amount: Long) {
        handleStateUpdate(GameEngine.buyMutualFund(_gameState.value, fundId, amount))
    }

    fun sellMutualFund(fundId: String, units: Double) {
        handleStateUpdate(GameEngine.sellMutualFund(_gameState.value, fundId, units))
    }

    // --- 株式 ---
    fun buyStock(symbol: String, shares: Long) {
        handleStateUpdate(GameEngine.buyStock(_gameState.value, symbol, shares))
    }

    fun sellStock(symbol: String, shares: Long) {
        handleStateUpdate(GameEngine.sellStock(_gameState.value, symbol, shares))
    }

    // --- FX ---
    fun openFxPosition(pairSymbol: String, direction: FxDirection, lots: Long, leverage: Int) {
        handleStateUpdate(GameEngine.openFxPosition(_gameState.value, pairSymbol, direction, lots, leverage))
    }

    fun closeFxPosition(positionId: String) {
        handleStateUpdate(GameEngine.closeFxPosition(_gameState.value, positionId))
    }

    // --- 施設 ---
    fun buyOrUpgradeFacility(facilityId: String) {
        handleStateUpdate(GameEngine.buyOrUpgradeFacility(_gameState.value, facilityId))
    }

    // --- カジノ (時間進行なし) ---
    fun playRoulette(betType: RouletteBetType, amount: Long): GameEngine.RouletteResult {
        val (newState, result) = GameEngine.playRoulette(_gameState.value, betType, amount)
        handleStateUpdate(newState)
        return result
    }

    fun startHighLow(betAmount: Long) {
        handleStateUpdate(GameEngine.startHighLow(_gameState.value, betAmount))
    }

    fun guessHighLow(choice: HighLowChoice): GameEngine.HighLowResult {
        val (newState, result) = GameEngine.guessHighLow(_gameState.value, choice)
        handleStateUpdate(newState)
        return result
    }

    fun cashoutHighLow(): Long {
        val (newState, payout) = GameEngine.cashoutHighLow(_gameState.value)
        handleStateUpdate(newState)
        return payout
    }

    fun playSlot(betAmount: Long): GameEngine.SlotResult {
        val (newState, result) = GameEngine.playSlot(_gameState.value, betAmount)
        handleStateUpdate(newState)
        return result
    }

    // --- コロニー買収 ---
    fun purchaseColony() {
        val state = _gameState.value
        val newState = GameEngine.purchaseColony(state)
        if (newState.colonyPurchased && !state.colonyPurchased) {
            handleStateUpdate(newState)
            _currentDialog.value = GameDialogState.GameClear(newState.day, newState.totalNetWorth)
        }
    }

    // --- テーマ変更 ---
    fun setTheme(theme: ThemeMode) {
        handleStateUpdate(_gameState.value.copy(theme = theme))
    }

    // --- リセット ---
    fun resetGame() {
        val newState = repository.resetGame()
        _gameState.value = newState
        _currentDialog.value = GameDialogState.Notice("リセット完了", "端末データを初期化しました。")
    }

    // --- デバッグ機能 (BuildConfig.DEBUG時) ---
    fun debugAddCredits(amount: Long) {
        handleStateUpdate(GameEngine.debugAddCredits(_gameState.value, amount))
    }

    fun debugSkipDays(days: Int) {
        advanceTime(days)
    }

    fun debugInstantClearSetup() {
        handleStateUpdate(GameEngine.debugInstantClearSetup(_gameState.value))
    }

    fun onAppPause() {
        repository.saveGameState(_gameState.value)
    }
}
