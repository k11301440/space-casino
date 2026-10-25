package com.spacecasino.colonyterminal.ui.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spacecasino.colonyterminal.model.FacilityType
import com.spacecasino.colonyterminal.model.GameState
import com.spacecasino.colonyterminal.theme.LocalTerminalColors
import com.spacecasino.colonyterminal.ui.GameViewModel
import com.spacecasino.colonyterminal.ui.components.*

@Composable
fun FacilitiesTab(
    state: GameState,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalTerminalColors.current
    val scrollState = rememberScrollState()

    val totalFacilityIncome = state.facilities.filter { it.isOwned }.sumOf { it.currentDailyIncome }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TerminalPanel(title = "コロニー施設・インフラ投資") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "インフラ日収合計: +${formatCredits(totalFacilityIncome)} /日",
                        color = colors.positive,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "日数を進めるたびに手持ち現金に直接配当されます。",
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        state.facilities.forEach { fac ->
            val isOwned = fac.isOwned
            val cost = fac.upgradeCost
            val canAfford = state.credits >= cost

            TerminalPanel(
                title = if (isOwned) "${fac.name} [Lv.${fac.level}]" else "${fac.name} [未建設]"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = fac.description,
                            color = colors.textPrimary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (isOwned) {
                            Text(
                                text = "現在日収: +${formatCredits(fac.currentDailyIncome)} /日",
                                color = colors.positive,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        if (fac.type == FacilityType.LAB && isOwned) {
                            Text(
                                text = "★特殊効果: 全資産運用収益 +5% ブースト発動中",
                                color = colors.secondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        if (fac.type == FacilityType.GRAVITY) {
                            Text(
                                text = "★コロニー買収・独立宣言の必須要件施設",
                                color = colors.hazard,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (!isOwned) "建設費用: ${formatCredits(cost)}" else "増築費用: ${formatCredits(cost)}",
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    TerminalButton(
                        text = if (!isOwned) "建設する" else "増築 (Lv.${fac.level + 1})",
                        onClick = { viewModel.buyOrUpgradeFacility(fac.id) },
                        enabled = canAfford,
                        isPrimary = canAfford
                    )
                }
            }
        }
    }
}
