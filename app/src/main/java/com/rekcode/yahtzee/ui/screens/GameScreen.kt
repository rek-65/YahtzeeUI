package com.rekcode.yahtzee.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.rekcode.yahtzee.R
import com.rekcode.yahtzee.api.ScoreCategory
import com.rekcode.yahtzee.api.ScoreSheetItem
import com.rekcode.yahtzee.ui.components.DiceView
import com.rekcode.yahtzee.ui.theme.Dimens
import com.rekcode.yahtzee.ui.theme.UiConstants
import com.rekcode.yahtzee.ui.util.toDisplayStringRes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Composable representing the primary game screen layout using
 * deterministic vertical partitioning and real score data binding.
 *
 * @param coordinator Coordinator that owns Game UI state and flow transitions.
 * @param onExitRequested Callback invoked when the user requests to leave the game screen.
 * @param onPlayAgainRequested Callback invoked when the user chooses to start a new game
 *                             with the same player count after game over.
 */
@Composable
fun GameScreen(
    coordinator: GameCoordinator,
    onExitRequested: () -> Unit,
    onPlayAgainRequested: () -> Unit
) {
    val uiState = coordinator.uiState

    val (upperSection, lowerSection) = remember(uiState.scoreSheet) {
        val splitIndex = uiState.scoreSheet.indexOfFirst {
            it.displayName.equals(
                UiConstants.ScoreSectionLowerBoundaryName,
                ignoreCase = true
            )
        }

        if (splitIndex != -1) {
            Pair(
                uiState.scoreSheet.subList(0, splitIndex),
                uiState.scoreSheet.subList(splitIndex, uiState.scoreSheet.size)
            )
        } else {
            Pair(uiState.scoreSheet, emptyList())
        }
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.weight(UiConstants.DiceSectionWeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            DiceSection(
                diceValues = uiState.diceValues,
                heldStates = uiState.heldStates,
                isRolling = uiState.isRolling,
                onDieClick = { index ->
                    coordinator.onDieClicked(index)
                }
            )
        }

        Column(
            modifier = Modifier.weight(UiConstants.ScoreSectionWeight)
        ) {
            ScoreSection(
                currentPlayerIndex = uiState.currentPlayerIndex,
                upperSection = upperSection,
                lowerSection = lowerSection,
                previewSheet = uiState.previewSheet,
                upperBonusValue = uiState.upperBonusValue,
                onScoreSelected = { category ->
                    coordinator.onScoreSelected(category)
                }
            )
        }

        Column(
            modifier = Modifier.weight(UiConstants.ActionSectionWeight)
        ) {
            ActionSection(
                isRollEnabled = uiState.isRollEnabled,
                onRollRequested = {
                    coroutineScope.launch {
                        coordinator.beginRoll()
                        delay(UiConstants.DiceRollDurationMs)
                        coordinator.onRollCompleted()

                        val isYahtzee = coordinator.uiState.showYahtzeeCelebration
                        if (isYahtzee) {
                            delay(UiConstants.YahtzeeCelebrationDurationMs)
                            coordinator.dismissYahtzeeCelebration()
                        }
                    }
                },
                onExitRequested = onExitRequested
            )
        }

        if (uiState.showGameOverDialog) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    OutlinedButton(
                        onClick = {
                            coordinator.dismissGameOver()
                            onPlayAgainRequested()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = colorResource(R.color.table_rail),
                            contentColor = colorResource(R.color.text_on_table)
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.action_play_again),
                            fontWeight = UiConstants.ButtonLabelFontWeight,
                            fontSize = Dimens.ButtonLabelFontSize
                        )
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = onExitRequested,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = colorResource(R.color.table_rail),
                            contentColor = colorResource(R.color.text_on_table)
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.action_exit),
                            fontWeight = UiConstants.ButtonLabelFontWeight,
                            fontSize = Dimens.ButtonLabelFontSize
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.dialog_game_over_title),
                        fontWeight = UiConstants.ButtonLabelFontWeight,
                        fontSize = Dimens.PlayerHeaderTextSize,
                        color = colorResource(id = R.color.text_on_table)
                    )
                },
                text = {
                    val scores = uiState.finalScores ?: emptyList()
                    val winner = uiState.winnerIndex ?: 0
                    val scoreLines = scores
                        .mapIndexed { index, score ->
                            stringResource(
                                id = R.string.dialog_game_over_score_line,
                                index + 1,
                                score
                            )
                        }
                        .joinToString(separator = "\n")

                    val winnerLine = stringResource(
                        id = R.string.dialog_game_over_winner,
                        winner + 1
                    )

                    Text(
                        text = "$scoreLines\n\n$winnerLine",
                        fontSize = Dimens.ScoreTextSize,
                        color = colorResource(id = R.color.text_on_table)
                    )
                },
                containerColor = colorResource(R.color.table_felt)
            )
        }

        if (uiState.showYahtzeeCelebration) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        colorResource(id = R.color.table_felt).copy(alpha = 0.85f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.celebration_yahtzee),
                    fontSize = Dimens.PlayerHeaderTextSize,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.text_section_header),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        letterSpacing = Dimens.YahtzeeCelebrationLetterSpacing
                    )
                )
            }
        }
    }
}

/**
 * Renders the dice area of the game screen.
 *
 * <p>This composable is stateless and only reflects the current dice state.
 * All interactions are delegated upward via callbacks.</p>
 *
 * @param diceValues Current face values of all dice.
 * @param heldStates Current held state of each die.
 * @param isRolling Whether dice animation is currently active.
 * @param onDieClick Callback when a die is tapped.
 */
@Composable
fun DiceSection(
    diceValues: List<Int>,
    heldStates: List<Boolean>,
    isRolling: Boolean,
    onDieClick: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.DiceSpacing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = Dimens.DiceHorizontalSpacing,
                    alignment = Alignment.CenterHorizontally
                )
            ) {
                DiceView(diceValues[0], heldStates[0], isRolling) { onDieClick(0) }
                DiceView(diceValues[1], heldStates[1], isRolling) { onDieClick(1) }
                DiceView(diceValues[2], heldStates[2], isRolling) { onDieClick(2) }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = Dimens.DiceHorizontalSpacing,
                    alignment = Alignment.CenterHorizontally
                )
            ) {
                DiceView(diceValues[3], heldStates[3], isRolling) { onDieClick(3) }
                DiceView(diceValues[4], heldStates[4], isRolling) { onDieClick(4) }
            }
        }
    }
}

/**
 * Renders the full score section including upper section, bonus, and lower section.
 *
 * @param currentPlayerIndex Active player index.
 * @param upperSection Upper score entries.
 * @param lowerSection Lower score entries.
 * @param previewSheet Optional preview score sheet.
 * @param upperBonusValue Current upper section bonus value.
 * @param onScoreSelected Callback when a score category is selected.
 * @param modifier Modifier applied to the root score section container.
 */
@Composable
fun ScoreSection(
    currentPlayerIndex: Int,
    upperSection: List<ScoreSheetItem>,
    lowerSection: List<ScoreSheetItem>,
    previewSheet: List<ScoreSheetItem>?,
    upperBonusValue: Int,
    onScoreSelected: (ScoreCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = Dimens.ScreenPadding),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(
                id = R.string.label_player,
                currentPlayerIndex + 1
            ),
            fontSize = Dimens.PlayerHeaderTextSize,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.PlayerHeaderBottomSpacing),
            textAlign = TextAlign.Center,
            color = colorResource(id = R.color.text_on_table)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.ScoreRowSpacing)
        ) {
            Text(
                text = stringResource(id = R.string.section_upper),
                fontSize = Dimens.ScoreTextSize,
                color = colorResource(id = R.color.text_section_header)
            )

            ScoreRowList(
                entries = upperSection,
                previewSheet = previewSheet,
                onScoreSelected = onScoreSelected
            )

            ScoreRow(
                label = stringResource(id = R.string.section_upper_bonus),
                value = upperBonusValue.toString(),
                backgroundColor = UiConstants.ScoreRowEvenBackground,
                isEmphasized = true
            )

            Spacer(modifier = Modifier.height(Dimens.SectionSpacing))

            if (lowerSection.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.section_lower),
                    fontSize = Dimens.ScoreTextSize,
                    color = colorResource(id = R.color.text_section_header)
                )

                ScoreRowList(
                    entries = lowerSection,
                    previewSheet = previewSheet,
                    onScoreSelected = onScoreSelected
                )
            }
        }
    }
}

/**
 * Renders a list of score rows for a given section.
 *
 * <p>This composable is stateless and only responsible for rendering
 * score entries. Interaction is delegated via callback.</p>
 *
 * @param entries List of score sheet items to render.
 * @param previewSheet Optional preview score sheet.
 * @param onScoreSelected Callback invoked when a score category is selected.
 */
@Composable
fun ScoreRowList(
    entries: List<ScoreSheetItem>,
    previewSheet: List<ScoreSheetItem>?,
    onScoreSelected: (ScoreCategory) -> Unit
) {
    entries.forEachIndexed { index, entry ->
        ScoreRow(
            label = stringResource(id = entry.category.toDisplayStringRes()),
            value = resolveScoreDisplayValue(entry, previewSheet),
            backgroundColor = if (index % 2 == 0) {
                UiConstants.ScoreRowEvenBackground
            } else {
                UiConstants.ScoreRowOddBackground
            },
            isClickable = !entry.isLocked,
            isLocked = entry.isLocked,
            onClick = {
                if (!entry.isLocked) {
                    onScoreSelected(entry.category)
                }
            }
        )
    }
}

/**
 * Stateless UI component representing a single row in the score sheet.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Display a score label and its corresponding value</li>
 *   <li>Reflect emphasized and clickable visual states</li>
 * </ul>
 *
 * <p>This composable does not contain business logic, handle game rules,
 * or manage state.</p>
 *
 * @param label Display name for the score category.
 * @param value String representation of the score value.
 * @param backgroundColor Background color for the row.
 * @param isEmphasized Controls bold and highlighted text styling.
 * @param isClickable Whether the row responds to tap interactions.
 * @param isLocked Whether the row represents a locked score category.
 *                 Locked rows are non-clickable and rendered at reduced alpha.
 * @param onClick Lambda invoked when the row is clicked.
 */
@Composable
fun ScoreRow(
    label: String,
    value: String,
    backgroundColor: Color,
    isEmphasized: Boolean = false,
    isClickable: Boolean = false,
    isLocked: Boolean = false,
    onClick: () -> Unit = {}
) {
    val resolvedTextColor = when {
        isLocked -> colorResource(id = R.color.text_on_table)
            .copy(alpha = UiConstants.LockedRowAlpha)
        isEmphasized -> colorResource(id = R.color.text_section_header)
        else -> colorResource(id = R.color.text_on_table)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .then(
                if (isClickable) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
            .padding(horizontal = Dimens.ScreenPadding)
    ) {
        Text(
            text = label,
            fontSize = Dimens.ScoreTextSize,
            fontWeight = if (isEmphasized) FontWeight.Bold else FontWeight.Normal,
            color = resolvedTextColor
        )
        Spacer(modifier = Modifier.weight(UiConstants.RowSpacerWeight))
        Text(
            text = value,
            fontSize = Dimens.ScoreTextSize,
            fontWeight = if (isEmphasized) FontWeight.Bold else FontWeight.Normal,
            color = resolvedTextColor,
            modifier = Modifier.width(Dimens.ScoreValueWidth),
            textAlign = TextAlign.End
        )
    }
}

/**
 * Resolves the display value for a score row based on lock state and preview data.
 *
 * <p>Priority:</p>
 * <ul>
 *   <li>Locked score -> actual score</li>
 *   <li>Preview available -> preview score</li>
 *   <li>Otherwise -> placeholder</li>
 * </ul>
 *
 * @param entry The score sheet item representing the category.
 * @param previewSheet Optional preview score sheet.
 * @return String representation of the score for UI display.
 */
private fun resolveScoreDisplayValue(
    entry: ScoreSheetItem,
    previewSheet: List<ScoreSheetItem>?
): String {
    return when {
        entry.isLocked -> entry.score?.toString() ?: "-"
        previewSheet != null -> previewSheet
            .find { it.category == entry.category }
            ?.score?.toString() ?: "-"
        else -> "-"
    }
}

/**
 * Renders the primary action area for the game screen.
 *
 * @param isRollEnabled Whether the roll button is currently enabled.
 * @param onRollRequested Callback invoked when the roll action is requested.
 * @param onExitRequested Callback invoked when the exit action is requested.
 * @param modifier Modifier applied to the root container of the action section.
 */
@Composable
fun ActionSection(
    isRollEnabled: Boolean,
    onRollRequested: () -> Unit,
    onExitRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(
            horizontal = Dimens.ScreenPadding,
            vertical = Dimens.ScreenPadding
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(UiConstants.ButtonWidthFraction),
                horizontalArrangement = Arrangement.spacedBy(Dimens.ScreenPadding)
            ) {
                OutlinedButton(
                    onClick = onRollRequested,
                    enabled = isRollEnabled,
                    modifier = Modifier.weight(UiConstants.ActionButtonWeight),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = colorResource(R.color.table_rail),
                        contentColor = colorResource(R.color.text_on_table),
                        disabledContainerColor = colorResource(R.color.button_disabled_background),
                        disabledContentColor = colorResource(R.color.text_disabled)
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.action_roll),
                        fontWeight = UiConstants.ButtonLabelFontWeight,
                        fontSize = Dimens.ButtonLabelFontSize
                    )
                }

                OutlinedButton(
                    onClick = onExitRequested,
                    modifier = Modifier.weight(UiConstants.ActionButtonWeight),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = colorResource(R.color.table_rail),
                        contentColor = colorResource(R.color.text_on_table)
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.action_exit),
                        fontWeight = UiConstants.ButtonLabelFontWeight,
                        fontSize = Dimens.ButtonLabelFontSize
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SectionSpacing))
    }
}