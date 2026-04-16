package com.rekcode.yahtzee.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rekcode.yahtzee.api.ScoreCategory
import com.rekcode.yahtzee.api.ScoreSheetItem
import com.rekcode.yahtzee.api.YahtzeeGame
import com.rekcode.yahtzee.ui.theme.UiConstants

/**
 * Coordinator responsible for managing Game screen flow logic and UI state transitions.
 *
 * @param controller Game engine instance.
 */
class GameCoordinator(
    private val controller: YahtzeeGame
) {

    /**
     * Current UI state for the game screen.
     */
    var uiState by mutableStateOf(createInitialState())
        private set

    /**
     * Creates the initial UI state from the engine.
     *
     * @return Initial GameUiState.
     */
    private fun createInitialState(): GameUiState {
        val playerIndex = controller.getCurrentPlayerIndex()

        return GameUiState(
            currentPlayerIndex = playerIndex,
            scoreSheet = controller.getScoreSheet(playerIndex),
            diceValues = createEmptyDiceValues(),
            heldStates = createEmptyHeldStates(),
            isRolling = false,
            isRollEnabled = controller.canRoll(),
            previewSheet = null,
            upperBonusValue = getUpperBonusValue(playerIndex),
            showGameOverDialog = false,
            finalScores = null,
            winnerIndex = null,
            showYahtzeeCelebration = false
        )
    }

    /**
     * Tracks whether Yahtzee celebration has been triggered for the current roll.
     */
    private var yahtzeeTriggeredThisRoll: Boolean = false

    /**
     * Immutable snapshot of game-over-related state.
     *
     * @param isGameOver Whether the game is over.
     * @param finalScores Final scores for all players if the game is over.
     * @param winnerIndex Index of the winning player if the game is over.
     */
    private data class GameOverState(
        val isGameOver: Boolean,
        val finalScores: List<Int>?,
        val winnerIndex: Int?
    )

    /**
     * Immutable snapshot of current player state.
     *
     * @param scoreSheet Score sheet for the active player.
     * @param isRollEnabled Whether rolling is currently allowed.
     * @param upperBonusValue Current upper section bonus value.
     */
    private data class PlayerState(
        val scoreSheet: List<ScoreSheetItem>,
        val isRollEnabled: Boolean,
        val upperBonusValue: Int
    )

    /**
     * Handles selection of a die.
     *
     * @param index Index of the selected die.
     */
    fun onDieClicked(index: Int) {
        if (controller.hasRolled()) {
            uiState = uiState.copy(
                heldStates = controller
                    .setDieHold(index, !uiState.heldStates[index])
                    .toMutableList()
            )
        }
    }

    /**
     * Handles score category selection.
     *
     * @param category Selected score category.
     */
    fun onScoreSelected(category: ScoreCategory) {
        controller.score(category)
        applyPostScoreState()
        resetDiceState()
    }

    /**
     * Applies UI state updates required after score submission.
     */
    private fun applyPostScoreState() {
        val newIndex = controller.getCurrentPlayerIndex()
        applyPostScoreResult(newIndex)
    }

    /**
     * Applies post-score UI state using collected snapshots.
     *
     * @param playerIndex Active player index.
     */
    private fun applyPostScoreResult(playerIndex: Int) {
        val gameOverState = collectGameOverState()
        val playerState = collectPlayerState(playerIndex)

        uiState = uiState.copy(
            currentPlayerIndex = playerIndex,
            scoreSheet = playerState.scoreSheet,
            isRollEnabled = playerState.isRollEnabled,
            previewSheet = null,
            upperBonusValue = playerState.upperBonusValue,
            showGameOverDialog = gameOverState.isGameOver,
            finalScores = gameOverState.finalScores,
            winnerIndex = gameOverState.winnerIndex
        )
    }

    /**
     * Collects game-over-related state from the engine.
     *
     * @return Snapshot of current game-over-related state.
     */
    private fun collectGameOverState(): GameOverState {
        val isGameOver = controller.isGameOver()

        val finalScores = if (isGameOver) {
            (0 until controller.getPlayerCount()).map { index ->
                controller.getPlayerFinalScore(index)
            }
        } else {
            null
        }

        val winnerIndex = if (isGameOver) {
            controller.getWinnerIndex()
        } else {
            null
        }

        return GameOverState(
            isGameOver = isGameOver,
            finalScores = finalScores,
            winnerIndex = winnerIndex
        )
    }

    /**
     * Collects current player-related state from the engine.
     *
     * @param playerIndex Index of the active player.
     * @return Snapshot of current player state.
     */
    private fun collectPlayerState(playerIndex: Int): PlayerState {
        return PlayerState(
            scoreSheet = controller.getScoreSheet(playerIndex),
            isRollEnabled = getRollEligibility(),
            upperBonusValue = getUpperBonusValue(playerIndex)
        )
    }

    /**
     * Determines whether rolling is currently allowed.
     *
     * @return True if rolling is allowed, otherwise false.
     */
    private fun getRollEligibility(): Boolean = controller.canRoll()

    /**
     * Retrieves the current upper section bonus value for a player.
     *
     * @param playerIndex Index of the player.
     * @return Current upper section bonus value.
     */
    private fun getUpperBonusValue(playerIndex: Int): Int {
        return controller
            .getUpperSectionBonusStatus(playerIndex)
            .bonusAmount
    }

    /**
     * Creates an empty dice value list.
     *
     * @return List of dice values initialized to zero.
     */
    private fun createEmptyDiceValues(): List<Int> =
        List(UiConstants.DiceCount) { 0 }

    /**
     * Creates an empty held-state list.
     *
     * @return List of held states initialized to false.
     */
    private fun createEmptyHeldStates(): List<Boolean> =
        List(UiConstants.DiceCount) { false }

    /**
     * Sets rolling state before roll execution.
     */
    fun beginRoll() {
        resetRollCycle()
        applyRollingState()
    }

    /**
     * Resets per-roll cycle state.
     */
    private fun resetRollCycle() {
        yahtzeeTriggeredThisRoll = false
    }

    /**
     * Applies rolling UI state.
     */
    private fun applyRollingState() {
        uiState = uiState.copy(
            isRolling = true
        )
    }

    /**
     * Resets dice-related UI state after scoring.
     */
    private fun resetDiceState() {
        uiState = uiState.copy(
            diceValues = createEmptyDiceValues(),
            heldStates = createEmptyHeldStates(),
            isRolling = false
        )
    }

    /**
     * Completes a roll and updates UI state from engine results.
     */
    fun onRollCompleted() {
        controller.rollDice()

        val diceValues = controller.getCurrentDice()
        val preview = controller.previewScores()

        applyRollResult(diceValues, preview)
        evaluateYahtzee(diceValues)
    }

    /**
     * Applies UI state updates resulting from a completed roll.
     *
     * @param diceValues Dice values from the completed roll.
     * @param preview Preview score sheet from the engine.
     */
    private fun applyRollResult(
        diceValues: List<Int>,
        preview: List<ScoreSheetItem>
    ) {
        uiState = uiState.copy(
            diceValues = diceValues,
            isRollEnabled = getRollEligibility(),
            previewSheet = preview,
            isRolling = false
        )
    }

    /**
     * Evaluates Yahtzee conditions and triggers celebration if appropriate.
     *
     * @param diceValues Dice values from the completed roll.
     */
    private fun evaluateYahtzee(diceValues: List<Int>) {
        if (!yahtzeeTriggeredThisRoll && isYahtzee(diceValues)) {
            triggerYahtzeeCelebration()
            yahtzeeTriggeredThisRoll = true
        }
    }

    /**
     * Determines whether the current roll results in a Yahtzee.
     *
     * @param diceValues Dice values from the completed roll.
     * @return True if all dice show the same non-zero value, otherwise false.
     */
    private fun isYahtzee(diceValues: List<Int>): Boolean {
        return diceValues.size == UiConstants.DiceCount &&
            diceValues.all { it != 0 } &&
            diceValues.distinct().size == 1
    }

    /**
     * Triggers Yahtzee celebration state.
     */
    private fun triggerYahtzeeCelebration() {
        uiState = uiState.copy(showYahtzeeCelebration = true)
    }

    /**
     * Dismisses the Yahtzee celebration overlay.
     */
    fun dismissYahtzeeCelebration() {
        uiState = uiState.copy(showYahtzeeCelebration = false)
    }

    /**
     * Dismisses the game-over dialog and clears game-over state.
     */
    fun dismissGameOver() {
        clearGameOverState()
    }

    /**
     * Clears game-over-related UI state.
     */
    private fun clearGameOverState() {
        uiState = uiState.copy(
            showGameOverDialog = false,
            finalScores = null,
            winnerIndex = null
        )
    }
}