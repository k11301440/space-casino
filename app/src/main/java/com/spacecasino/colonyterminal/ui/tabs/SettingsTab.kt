package com.spacecasino.colonyterminal.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spacecasino.colonyterminal.BuildConfig
import com.spacecasino.colonyterminal.model.BgmMode
import com.spacecasino.colonyterminal.model.GameState
import com.spacecasino.colonyterminal.model.ThemeMode
import com.spacecasino.colonyterminal.theme.LocalTerminalColors
import com.spacecasino.colonyterminal.ui.GameViewModel
import com.spacecasino.colonyterminal.ui.components.TerminalButton
import com.spacecasino.colonyterminal.ui.components.TerminalPanel
import com.spacecasino.colonyterminal.ui.components.formatCredits

@Composable
fun SettingsTab(
    state: GameState,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalTerminalColors.current
    val scrollState = rememberScrollState()

    var customAmountText by remember { mutableStateOf("100000") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // テーマ切り替え
        TerminalPanel(title = "端末ディスプレイ・テーマ切り替え") {
            Text(
                text = "CRTブラウン管風やフラットスタイルなど表示テーマを切り替えられます。",
                color = colors.textSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ThemeMode.values().forEach { mode ->
                    val isSel = state.theme == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isSel) colors.primary else colors.surfaceVariant)
                            .clickable { viewModel.setTheme(mode) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.displayName,
                            color = if (isSel) colors.background else colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // BGMサウンド設定
        TerminalPanel(title = "BGM・音響システム") {
            Text(
                text = "端末の内蔵音響チップによるプロシージャルBGM（リアルタイム波形合成）を設定します。",
                color = colors.textSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BgmMode.values().forEach { mode ->
                    val isSel = state.bgmMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isSel) colors.primary else colors.surfaceVariant)
                            .clickable { viewModel.setBgmMode(mode) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.displayName,
                            color = if (isSel) colors.background else colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // 実績・アチーブメント (Google Play Games想定)
        TerminalPanel(title = "実績・アチーブメント (Play Games連携想定)") {
            val unlockedCount = state.achievements.count { it.isUnlocked }
            Text(
                text = "解除済み実績: $unlockedCount / ${state.achievements.size}",
                color = colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                state.achievements.forEach { ach ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ach.title,
                                color = if (ach.isUnlocked) colors.positive else colors.textSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = ach.description,
                                color = colors.textSecondary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = if (ach.isUnlocked) "【達成】" else "【未解除】",
                            color = if (ach.isUnlocked) colors.positive else colors.hazard,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // データ管理
        TerminalPanel(title = "セーブデータ管理") {
            Text(
                text = "アプリ終了時や時間進行時、取引時に自動セーブされます。",
                color = colors.textSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(8.dp))
            TerminalButton(
                text = "セーブデータを完全初期化",
                onClick = { viewModel.resetGame() },
                isDanger = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // デバッグ機能 (BuildConfig.DEBUG時のみ表示)
        if (BuildConfig.DEBUG) {
            TerminalPanel(
                title = "【DEBUG CONSOLE】デバッグ専用機能",
                borderColor = colors.hazard
            ) {
                Text(
                    text = "※このパネルはデバッグビルド時のみ表示されます（BuildConfig.DEBUG = true）。",
                    color = colors.hazard,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "クイック・クレジット増減:",
                    color = colors.textPrimary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TerminalButton(
                        text = "+1万",
                        onClick = { viewModel.debugAddCredits(10000L) },
                        modifier = Modifier.weight(1f)
                    )
                    TerminalButton(
                        text = "+100万",
                        onClick = { viewModel.debugAddCredits(1000000L) },
                        modifier = Modifier.weight(1f)
                    )
                    TerminalButton(
                        text = "+5000万",
                        onClick = { viewModel.debugAddCredits(50000000L) },
                        modifier = Modifier.weight(1f),
                        isPrimary = true
                    )
                    TerminalButton(
                        text = "-1万",
                        onClick = { viewModel.debugAddCredits(-10000L) },
                        modifier = Modifier.weight(1f),
                        isDanger = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "任意額クレジット直接追加:",
                    color = colors.textPrimary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customAmountText,
                        onValueChange = { customAmountText = it },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary,
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border
                        ),
                        singleLine = true
                    )
                    TerminalButton(
                        text = "追加",
                        onClick = {
                            val amt = customAmountText.toLongOrNull() ?: 0L
                            if (amt != 0L) viewModel.debugAddCredits(amt)
                        },
                        isPrimary = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TerminalButton(
                        text = "100日間スキップ",
                        onClick = { viewModel.debugSkipDays(100) },
                        modifier = Modifier.weight(1f)
                    )
                    TerminalButton(
                        text = "即時クリア準備(1.1億CR+重力装置)",
                        onClick = { viewModel.debugInstantClearSetup() },
                        modifier = Modifier.weight(1f),
                        isPrimary = true
                    )
                }
            }
        }
    }
}
