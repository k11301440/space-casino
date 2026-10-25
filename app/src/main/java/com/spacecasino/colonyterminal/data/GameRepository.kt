package com.spacecasino.colonyterminal.data

import android.content.Context
import android.content.SharedPreferences
import com.spacecasino.colonyterminal.logic.GameEngine
import com.spacecasino.colonyterminal.model.GameState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GameRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("colony_asset_terminal_save", Context.MODE_PRIVATE)

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val KEY_SAVE_DATA = "save_game_state_json"

    fun loadGameState(): Pair<GameState, GameEngine.OfflineResult?> {
        val jsonString = prefs.getString(KEY_SAVE_DATA, null)
        val state = if (!jsonString.isNullOrBlank()) {
            try {
                json.decodeFromString<GameState>(jsonString)
            } catch (e: Exception) {
                // パース失敗時は初期状態へフォールバック
                GameEngine.createInitialState()
            }
        } else {
            GameEngine.createInitialState()
        }

        // オフライン時間経過による利息処理
        val currentTimeMs = System.currentTimeMillis()
        val offlineResult = GameEngine.processOfflineEarnings(state, currentTimeMs)

        // 最新のオフライン状態を保存
        saveGameState(offlineResult.newState)

        return offlineResult.newState to offlineResult
    }

    fun saveGameState(state: GameState) {
        try {
            val stateToSave = state.copy(lastSaveTimeMs = System.currentTimeMillis())
            val encoded = json.encodeToString(stateToSave)
            prefs.edit().putString(KEY_SAVE_DATA, encoded).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resetGame(): GameState {
        val newState = GameEngine.createInitialState()
        saveGameState(newState)
        return newState
    }
}
