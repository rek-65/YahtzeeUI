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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.rekcode.yahtzee.api.YahtzeeGame
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
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Enforce fixed screen regions (dice, score, actions)</li>
 *   <li>Render score data directly from game controller</li>
 *   <li>Emit user interaction intents to the root layer</li>
 * </ul>
 *
 * <p>This composable does not perform navigation, contain business logic,
 * or manage game rules.</p>
 *
 * @param controller The game engine instance providing all game state.
 * @param onExitRequested Callback invoked when the user requests to leave the game screen.
 * @param onPlayAgainRequested Callback invoked when the user chooses to start a new game
 *                             with the same player count after game over.
 */
@Composable
fun GameScreen(
    controller: YahtzeeGame,
    onExitRequested: () -> Unit,
    onPlayAgainRequested: () -> Unit
) {
    /**
     * Reactive current player index sourced from the engine.
     * Updated after each call to controller.nextPlayer().
     */
    var currentPlayerIndex by remember { mutableIntStateOf(controller.getCurrentPlayerIndex()) }

    /**
     * Reactive score sheet for the active player.
     * Updated after each turn to reflect the next player's scores.
     */
    var scoreSheet by remember { mutableStateOf(controller.getScoreSheet(currentPlayerIndex)) }

    val splitIndex = scoreSheet.indexOfFirst {
        it.displayName.equals(UiConstants.ScoreSectionLowerBoundaryName, ignoreCase = true)
    }
    val upperSection = if (splitIndex != -1) {
        scoreSheet.subList(0, splitIndex)
    } else {
        scoreSheet
    }
    val lowerSection = if (splitIndex != -1) {
        scoreSheet.subList(splitIndex, scoreSheet.size)
    } else {
        emptyList()
    }

    var isRolling by remember { mutableStateOf(false) }

    /**
     * Current face values of all five dice.
     * Initialized to zero so [DiceView] renders a blank die before the first roll.
     * Updated from the game engine after each roll.
     */
    var diceValues by remember { mutableStateOf(List(5) { 0 }) }

    /**
     * Held state for each die.
     * Will be replaced by values provided directly from the game engine.
     */
    var heldStates by remember { mutableStateOf(List(5) { false }) }

    /**
     * Whether the roll action is currently enabled.
     * Will be replaced by values provided directly from the game engine.
     */
    var isRollEnabled by remember { mutableStateOf(true) }

    var showGameOverDialog by remember { mutableStateOf(false) }

    /**
     * Live preview of potential scores based on current dice values.
     * Null before the first roll. Populated from the engine after each roll
     * and cleared on turn completion to prevent stale previews.
     */
    var previewSheet by remember { mutableStateOf<List<com.rekcode.yahtzee.api.ScoreSheetItem>?>(null) }

    /**
     * Controls visibility of the Yahtzee celebration overlay.
     * Set to true when [controller.previewScores] confirms a Yahtzee event.
     * Auto-dismissed after [UiConstants.YahtzeeCelebrationDurationMs] elapses.
     */
    var showYahtzeeCelebration by remember { mutableStateOf(false) }

    /**
     * Handles die tap interactions.
     * Guards against hold attempts before the first roll, as blank dice
     * have no valid hold state. Only toggles hold state when the engine
     * confirms a roll has occurred.
     */
    val onDieClick: (Int) -> Unit = { index ->
        if (controller.hasRolled()) {
            heldStates = controller.setDieHold(index, !heldStates[index]).toMutableList()
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

        Column(
            modifier = Modifier
                .weight(UiConstants.ScoreSectionWeight)
                .padding(horizontal = Dimens.ScreenPadding),
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
                upperSection.forEachIndexed { index, entry ->
                    ScoreRow(
                        label = stringResource(id = entry.category.toDisplayStringRes()),
                        value = when {
                            entry.isLocked -> entry.score?.toString() ?: "-"
                            previewSheet != null -> previewSheet!!
                                .find { it.category == entry.category }
                                ?.score?.toString() ?: "-"
                            else -> "-"
                        },
                        backgroundColor = if (index % 2 == 0)
                            UiConstants.ScoreRowEvenBackground
                        else
                            UiConstants.ScoreRowOddBackground,
                        isClickable = !entry.isLocked,
                        isLocked = entry.isLocked,
                        onClick = {
                            if (!entry.isLocked) {
                                controller.score(entry.category)
                                currentPlayerIndex = controller.getCurrentPlayerIndex()
                                scoreSheet = controller.getScoreSheet(currentPlayerIndex)
                                diceValues = List(5) { 0 }
                                heldStates = List(5) { false }
                                isRollEnabled = controller.canRoll()
                                previewSheet = null
                                if (controller.isGameOver()) {
                                    showGameOverDialog = true
                                }
                            }
                        }
                    )
                }
                ScoreRow(
                    label = stringResource(id = R.string.section_upper_bonus),
                    value = controller.getUpperSectionBonusStatus(currentPlayerIndex).bonusAmount.toString(),
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
                    lowerSection.forEachIndexed { index, entry ->
                        ScoreRow(
                            label = stringResource(id = entry.category.toDisplayStringRes()),
                            value = when {
                                entry.isLocked -> entry.score?.toString() ?: "-"
                                previewSheet != null -> previewSheet!!
                                    .find { it.category == entry.category }
                                    ?.score?.toString() ?: "-"
                                else -> "-"
                            },
                            backgroundColor = if (index % 2 == 0)
                                UiConstants.ScoreRowEvenBackground
                            else
                                UiConstants.ScoreRowOddBackground,
                            isClickable = !entry.isLocked,
                            isLocked = entry.isLocked,
                            onClick = {
                                if (!entry.isLocked) {
                                    controller.score(entry.category)
                                    currentPlayerIndex = controller.getCurrentPlayerIndex()
                                    scoreSheet = controller.getScoreSheet(currentPlayerIndex)
                                    diceValues = List(5) { 0 }
                                    heldStates = List(5) { false }
                                    isRollEnabled = controller.canRoll()
                                    previewSheet = null
                                    if (controller.isGameOver()) {
                                        showGameOverDialog = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(UiConstants.ActionSectionWeight)
                .padding(
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
                        onClick = {
                            coroutineScope.launch {
                                isRolling = true
                                delay(UiConstants.DiceRollDurationMs)
                                controller.rollDice()
                                diceValues = controller.getCurrentDice()
                                isRollEnabled = controller.canRoll()
                                previewSheet = controller.previewScores()
                                isRolling = false
                                val isYahtzee = previewSheet
                                    ?.find { it.category == ScoreCategory.YAHTZEE }
                                    ?.score == 50
                                if (isYahtzee) {
                                    showYahtzeeCelebration = true
                                    delay(UiConstants.YahtzeeCelebrationDurationMs)
                                    showYahtzeeCelebration = false
                                }
                            }
                        },
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
        if (showGameOverDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    OutlinedButton(
                        onClick = {
                            showGameOverDialog = false
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
                    val winnerIndex = controller.getWinnerIndex() ?: 0
                    val scoreLines = (0 until controller.getPlayerCount()).joinToString(separator = "\n") { index ->
                        "Player ${index + 1}: ${controller.getPlayerFinalScore(index)}"
                    }
                    Text(
                        text = "$scoreLines\n\n${stringResource(id = R.string.dialog_game_over_winner, winnerIndex + 1)}",
                        fontSize = Dimens.ScoreTextSize,
                        color = colorResource(id = R.color.text_on_table)
                    )
                },
                containerColor = colorResource(R.color.table_felt)
            )
        }

        if (showYahtzeeCelebration) {
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
        isLocked -> colorResource(id = R.color.text_on_table).copy(alpha = UiConstants.LockedRowAlpha)
        isEmphasized -> colorResource(id = R.color.text_section_header)
        else -> colorResource(id = R.color.text_on_table)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .then(
                if (isClickable) Modifier.clickable { onClick() }
                else Modifier
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