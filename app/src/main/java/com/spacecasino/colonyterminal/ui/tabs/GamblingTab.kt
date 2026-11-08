package com.spacecasino.colonyterminal.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spacecasino.colonyterminal.logic.GameEngine
import com.spacecasino.colonyterminal.model.GameState
import com.spacecasino.colonyterminal.model.HighLowChoice
import com.spacecasino.colonyterminal.model.RouletteBetType
import com.spacecasino.colonyterminal.theme.LocalTerminalColors
import com.spacecasino.colonyterminal.ui.GameViewModel
import com.spacecasino.colonyterminal.ui.components.*

enum class CasinoGame(val title: String) {
    ROULETTE("ルーレット"),
    HIGHLOW("ハイ＆ロー"),
    SLOT("ネオ・スロット"),
    INTEL("情報屋")
}

@Composable
fun GamblingTab(
    state: GameState,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalTerminalColors.current
    var selectedGame by remember { mutableStateOf(CasinoGame.ROULETTE) }

    Column(modifier = modifier.fillMaxSize()) {
        // カジノメニュー切替
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CasinoGame.values().forEach { game ->
                val isSel = selectedGame == game
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (isSel) colors.primary.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { selectedGame = game }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = game.title,
                        color = if (isSel) colors.primary else colors.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            when (selectedGame) {
                CasinoGame.ROULETTE -> RouletteView(state, viewModel)
                CasinoGame.HIGHLOW -> HighLowView(state, viewModel)
                CasinoGame.SLOT -> SlotView(state, viewModel)
                CasinoGame.INTEL -> IntelView(state, viewModel)
            }
        }
    }
}

// 1. ルーレット
@Composable
private fun RouletteView(state: GameState, viewModel: GameViewModel) {
    val colors = LocalTerminalColors.current
    val scrollState = rememberScrollState()

    var betAmount by remember { mutableStateOf(500L) }
    var lastResult by remember { mutableStateOf<GameEngine.RouletteResult?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TerminalPanel {
            Text(
                text = "コロニー・ルーレット (期待値 104%)",
                color = colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "※プレイ中は時間は進みません。ハザード(0)が出ると過電流サージでペナルティ大負けが発生します。",
                color = colors.textSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // ベット額セレクター
        TerminalPanel(title = "掛け金選択 (現在: ${formatCredits(betAmount)})") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(100L, 500L, 2000L, 10000L).forEach { amount ->
                    val isSel = betAmount == amount
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isSel) colors.primary else colors.surfaceVariant)
                            .clickable { betAmount = amount }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${formatNumber(amount)}",
                            color = if (isSel) colors.background else colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // ベットボタン一覧
        TerminalPanel(title = "賭け枠選択") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TerminalButton(
                    text = "赤 (2.15倍)",
                    onClick = { lastResult = viewModel.playRoulette(RouletteBetType.RED, betAmount) },
                    enabled = state.credits >= betAmount,
                    modifier = Modifier.weight(1f),
                    isDanger = true
                )
                TerminalButton(
                    text = "黒 (2.15倍)",
                    onClick = { lastResult = viewModel.playRoulette(RouletteBetType.BLACK, betAmount) },
                    enabled = state.credits >= betAmount,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TerminalButton(
                    text = "LOW 1-6 (2.15倍)",
                    onClick = { lastResult = viewModel.playRoulette(RouletteBetType.LOW, betAmount) },
                    enabled = state.credits >= betAmount,
                    modifier = Modifier.weight(1f)
                )
                TerminalButton(
                    text = "HIGH 7-12 (2.15倍)",
                    onClick = { lastResult = viewModel.playRoulette(RouletteBetType.HIGH, betAmount) },
                    enabled = state.credits >= betAmount,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TerminalButton(
                    text = "単数 7 (13.5倍)",
                    onClick = { lastResult = viewModel.playRoulette(RouletteBetType.NUMBER_7, betAmount) },
                    enabled = state.credits >= betAmount,
                    modifier = Modifier.weight(1f),
                    isPrimary = true
                )
                TerminalButton(
                    text = "単数 12 (13.5倍)",
                    onClick = { lastResult = viewModel.playRoulette(RouletteBetType.NUMBER_12, betAmount) },
                    enabled = state.credits >= betAmount,
                    modifier = Modifier.weight(1f),
                    isPrimary = true
                )
            }
        }

        // 結果表示
        lastResult?.let { res ->
            TerminalPanel(title = "ルーレット結果") {
                val winText = if (res.isHazard) "【HAZARD】" else "出目: [${res.winningNumber}]"
                Text(
                    text = winText,
                    color = if (res.isHazard) colors.negative else colors.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = res.message,
                    color = if (res.netGain >= 0) colors.positive else colors.negative,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

// 2. ハイ＆ロー
@Composable
private fun HighLowView(state: GameState, viewModel: GameViewModel) {
    val colors = LocalTerminalColors.current
    val scrollState = rememberScrollState()

    var startBet by remember { mutableStateOf(500L) }
    var lastResult by remember { mutableStateOf<GameEngine.HighLowResult?>(null) }
    val game = state.highLow

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TerminalPanel {
            Text(
                text = "ダイス ハイ＆ロー (連続的中ボーナス)",
                color = colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "2個のサイコロ合計(2〜12)を予測。連続的中で賞金が累加！途中でキャッシュアウト可能ですが、コロニー警察の手入れで全没収されるリスクもあります。",
                color = colors.textSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        if (!game.isActive) {
            TerminalPanel(title = "ゲーム開始ベット") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(200L, 1000L, 5000L, 20000L).forEach { amount ->
                        val isSel = startBet == amount
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isSel) colors.primary else colors.surfaceVariant)
                                .clickable { startBet = amount }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${formatNumber(amount)}",
                                color = if (isSel) colors.background else colors.textPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                TerminalButton(
                    text = "ゲーム開始 (${formatCredits(startBet)})",
                    onClick = {
                        lastResult = null
                        viewModel.startHighLow(startBet)
                    },
                    enabled = state.credits >= startBet,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true
                )
            }
        } else {
            // ゲーム中表示
            TerminalPanel(title = "プレイ中 - 出目予測") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "現在のダイス出目",
                            color = colors.textSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "[ ${game.currentRoll} ]",
                            color = colors.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${game.streak}連勝中！",
                            color = colors.secondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "現在賞金: ${formatCredits(game.currentBet)}",
                            color = colors.positive,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TerminalButton(
                        text = "▼ LOW (2-6)",
                        onClick = { lastResult = viewModel.guessHighLow(HighLowChoice.LOW) },
                        modifier = Modifier.weight(1f)
                    )
                    TerminalButton(
                        text = "★ 7 (3.5倍)",
                        onClick = { lastResult = viewModel.guessHighLow(HighLowChoice.SEVEN) },
                        modifier = Modifier.weight(1f),
                        isPrimary = true
                    )
                    TerminalButton(
                        text = "▲ HIGH (8-12)",
                        onClick = { lastResult = viewModel.guessHighLow(HighLowChoice.HIGH) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                TerminalButton(
                    text = "利益確定！キャッシュアウト (${formatCredits(game.currentBet)})",
                    onClick = {
                        val won = viewModel.cashoutHighLow()
                        lastResult = GameEngine.HighLowResult(game.currentRoll, true, false, "キャッシュアウト成功！ +${formatCredits(won)}")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true
                )
            }
        }

        lastResult?.let { res ->
            TerminalPanel(title = "判定ログ") {
                Text(
                    text = res.message,
                    color = if (res.isWin) colors.positive else colors.negative,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

// 3. ネオ・スロット
@Composable
private fun SlotView(state: GameState, viewModel: GameViewModel) {
    val colors = LocalTerminalColors.current
    val scrollState = rememberScrollState()

    var betAmount by remember { mutableStateOf(300L) }
    var slotResult by remember { mutableStateOf<GameEngine.SlotResult?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TerminalPanel {
            Text(
                text = "ネオ・サイバースロット (期待値 103%)",
                color = colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "小当たりが多く手堅い仕様。ただし [SKULL-SKULL-SKULL] が揃うと端末暴走ペナルティで大負けします。",
                color = colors.textSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // スロットリール表示画面
        TerminalPanel {
            val r1 = slotResult?.reel1?.label ?: "---"
            val r2 = slotResult?.reel2?.label ?: "---"
            val r3 = slotResult?.reel3?.label ?: "---"

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ReelBox(r1)
                ReelBox(r2)
                ReelBox(r3)
            }

            slotResult?.let { res ->
                Text(
                    text = res.message,
                    color = if (res.netGain >= 0) colors.positive else colors.negative,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        // ベット額＆スピンボタン
        TerminalPanel(title = "スピン操作") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(100L, 300L, 1000L, 5000L).forEach { amount ->
                    val isSel = betAmount == amount
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isSel) colors.primary else colors.surfaceVariant)
                            .clickable { betAmount = amount }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${formatNumber(amount)}",
                            color = if (isSel) colors.background else colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            TerminalButton(
                text = "SPIN (スピン) ${formatCredits(betAmount)}",
                onClick = { slotResult = viewModel.playSlot(betAmount) },
                enabled = state.credits >= betAmount,
                modifier = Modifier.fillMaxWidth(),
                isPrimary = true
            )
        }

        // 配当テーブル
        TerminalPanel(title = "配当表 (3揃い / 2揃い)") {
            Text(
                text = "・777 揃い: 60倍\n・BAR 揃い: 25倍\n・ORE 揃い: 12倍\n・ENG 揃い: 6倍\n・CHP 揃い: 3倍\n・2つ揃い小当たり: 1.5倍\n・SKULL 3つ揃い: 端末暴走ペナルティ (ベットの3倍没収)",
                color = colors.textSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun ReelBox(text: String) {
    val colors = LocalTerminalColors.current
    Box(
        modifier = Modifier
            .size(width = 80.dp, height = 60.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(colors.background)
            .border(2.dp, colors.border, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (text == "SKULL") colors.negative else if (text == "777") colors.hazard else colors.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun IntelView(state: GameState, viewModel: GameViewModel) {
    val colors = LocalTerminalColors.current
    val scrollState = rememberScrollState()
    var intelMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TerminalPanel(title = "裏カジノの情報屋") {
            Text(
                text = "薄暗い個室で、顔を伏せた男が端末を叩いている。「次の市場レジームの筋を知りたいか? 2,500 CRで教えてやる」と低く囁いた。",
                color = colors.textPrimary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 17.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "カジノのゲームは長期では必ず負ける。しかし、この男が売る情報だけは本物だ。次のレジームを先読みし、恐慌前に安全資産へ避難するか、好況前に株を仕込むか。それが勝負の分かれ目になる。",
                color = colors.textSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            TerminalButton(
                text = "情報を買う (2,500 CR)",
                onClick = { intelMessage = viewModel.buyIntel().message },
                enabled = state.credits >= 2500L,
                isPrimary = true,
                modifier = Modifier.fillMaxWidth()
            )
            intelMessage?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = msg,
                    color = colors.positive,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 17.sp
                )
            }
        }

        TerminalPanel(title = "最近の情報屋報告") {
            val intelNews = state.news.filter { it.kind == "情報" }.take(4)
            if (intelNews.isEmpty()) {
                Text(
                    text = "まだ報告はない。",
                    color = colors.textSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            } else {
                intelNews.forEach { item ->
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                        Text(
                            text = "Day ${item.day} ${item.headline}",
                            color = colors.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = item.body,
                            color = colors.textPrimary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}
