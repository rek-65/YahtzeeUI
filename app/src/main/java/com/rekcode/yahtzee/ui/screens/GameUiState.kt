
package com.rekcode.yahtzee.ui.screens

import com.rekcode.yahtzee.api.ScoreSheetItem

/**
 * Immutable representation of all UI-facing state for the Game screen.
 *
 * @param currentPlayerIndex Active player index currently shown by the UI
 * @param scoreSheet Score sheet currently displayed for the active player
 * @param diceValues Current face values for the five displayed dice
 * @param heldStates Current held state for each displayed die
 * @param isRolling Whether the roll animation phase is active
 * @param isRollEnabled Whether the roll action is currently allowed
 * @param previewSheet Optional preview score sheet based on the current dice state
 * @param upperBonusValue Current upper section bonus value for the active player
 * @param showGameOverDialog Whether the game over dialog should be visible
 * @param finalScores List of final scores for each player in order
 * @param winnerIndex Index of the winning player if game is over
 * @param showYahtzeeCelebration Whether the Yahtzee celebration overlay should be visible
 */
data class GameUiState(
    val currentPlayerIndex: Int,
    val scoreSheet: List<ScoreSheetItem>,
    val diceValues: List<Int>,
    val heldStates: List<Boolean>,
    val isRolling: Boolean,
    val isRollEnabled: Boolean,
    val previewSheet: List<ScoreSheetItem>?,
    val upperBonusValue: Int,
    val showGameOverDialog: Boolean,
    val finalScores: List<Int>?,
    val winnerIndex: Int?,
    val showYahtzeeCelebration: Boolean
)