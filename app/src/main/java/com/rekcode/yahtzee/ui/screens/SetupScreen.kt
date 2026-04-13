package com.rekcode.yahtzee.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import com.rekcode.yahtzee.R
import com.rekcode.yahtzee.ui.components.DiceView
import com.rekcode.yahtzee.ui.theme.Dimens
import com.rekcode.yahtzee.ui.theme.UiConstants

/**
 * Setup screen composable.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Allows the user to select a player count via a stepper (1–4).</li>
 *   <li>Presents game start option to the user.</li>
 *   <li>Presents app exit option to the user.</li>
 * </ul>
 *
 * <p>Stateless with respect to navigation — emits callbacks only.
 * Player count state is local and lifted out via onStartGame.</p>
 *
 * @param onStartGame Callback invoked with the selected player count when
 *                    the user confirms they want to start the game.
 * @param onExitRequested Callback invoked when the user requests to exit
 *                        the application.
 */
@Composable
fun SetupScreen(
    onStartGame: (playerCount: Int) -> Unit,
    onExitRequested: () -> Unit
) {
    var playerCount by remember { mutableIntStateOf(UiConstants.DefaultPlayerCount) }

    Box(modifier = Modifier.fillMaxSize()) {

        CornerDie(
            value = 1,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = Dimens.SetupDiceEdgePadding,
                    top = Dimens.SetupDiceEdgePadding
                )
        )

        CornerDie(
            value = 2,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    end = Dimens.SetupDiceEdgePadding,
                    top = Dimens.SetupDiceEdgePadding
                )
        )

        CornerDie(
            value = 3,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = Dimens.SetupDiceEdgePadding,
                    bottom = Dimens.SetupDiceBottomPadding
                )
        )

        CornerDie(
            value = 4,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = Dimens.SetupDiceEdgePadding,
                    bottom = Dimens.SetupDiceBottomPadding
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(
                space = Dimens.ItemStackSpacing,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlayerStepper(
                playerCount = playerCount,
                onDecrement = { if (playerCount > UiConstants.MinPlayerCount) playerCount-- },
                onIncrement = { if (playerCount < UiConstants.MaxPlayerCount) playerCount++ }
            )

            // TODO: Online multiplayer entry point.
            //  When online mode is supported, add an onOnlineRequested callback
            //  to SetupScreen and present a separate flow here (e.g. lobby screen).
            //  The playerCount stepper above should remain reusable for both flows.

            Button(
                onClick = { onStartGame(playerCount) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.table_rail),
                    contentColor = colorResource(R.color.text_on_table)
                )
            ) {
                Text(
                    text = "Start Game",
                    fontWeight = UiConstants.ButtonLabelFontWeight,
                    fontSize = Dimens.ButtonLabelFontSize
                )
            }

            Button(
                onClick = onExitRequested,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.table_rail),
                    contentColor = colorResource(R.color.text_on_table)
                )
            ) {
                Text(
                    text = "Exit",
                    fontWeight = UiConstants.ButtonLabelFontWeight,
                    fontSize = Dimens.ButtonLabelFontSize
                )
            }
        }
    }
}

/**
 * Stepper control for selecting the number of players.
 *
 * <p>Displays the current player count between decrement and increment controls.
 * Disables decrement at minimum bound and increment at maximum bound.
 * Emits events only — does not own state.</p>
 *
 * <p>Renders inside a rounded card surface using the table_rail color,
 * with outlined circular buttons and text_on_table labeling.</p>
 *
 * @param playerCount Currently selected player count.
 * @param onDecrement Callback invoked when the user taps the minus button.
 * @param onIncrement Callback invoked when the user taps the plus button.
 */
@Composable
private fun PlayerStepper(
    playerCount: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    val textOnTable = colorResource(R.color.text_on_table)
    val borderColor = textOnTable.copy(alpha = UiConstants.StepperButtonBorderAlpha)
    val containerBackground = colorResource(R.color.table_rail)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(
                color = containerBackground,
                shape = RoundedCornerShape(Dimens.StepperCornerRadius)
            )
            .padding(Dimens.StepperContainerPadding)
    ) {
        StepperButton(
            label = "−",
            onClick = onDecrement,
            enabled = playerCount > UiConstants.MinPlayerCount,
            borderColor = borderColor,
            contentColor = textOnTable
        )

        Text(
            text = playerCount.toString(),
            modifier = Modifier.width(Dimens.StepperCountWidth),
            textAlign = TextAlign.Center,
            color = textOnTable,
            fontWeight = UiConstants.ButtonLabelFontWeight,
            fontSize = Dimens.ButtonLabelFontSize
        )

        StepperButton(
            label = "+",
            onClick = onIncrement,
            enabled = playerCount < UiConstants.MaxPlayerCount,
            borderColor = borderColor,
            contentColor = textOnTable
        )
    }
}

/**
 * Single outlined circular button used within [PlayerStepper].
 *
 * <p>Renders a transparent-fill, outlined circular icon button.
 * Applies reduced border and content alpha when disabled.
 * Emits click events only — owns no state.</p>
 *
 * @param label The symbol to display (e.g. "−" or "+").
 * @param onClick Callback invoked on tap.
 * @param enabled Whether the button is interactive.
 * @param borderColor Resolved border color at full alpha.
 * @param contentColor Resolved text color at full alpha.
 */
@Composable
private fun StepperButton(
    label: String,
    onClick: () -> Unit,
    enabled: Boolean,
    borderColor: Color,
    contentColor: Color
) {
    val resolvedAlpha = if (enabled) 1f else UiConstants.ButtonBorderDisabledAlpha
    val resolvedBorder = borderColor.copy(alpha = resolvedAlpha)
    val resolvedContent = contentColor.copy(alpha = resolvedAlpha)

    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(Dimens.ButtonHeight)
            .border(
                width = Dimens.ButtonBorderWidth,
                color = resolvedBorder,
                shape = CircleShape
            )
    ) {
        Text(
            text = label,
            color = resolvedContent,
            fontWeight = UiConstants.ButtonLabelFontWeight,
            fontSize = Dimens.ButtonLabelFontSize
        )
    }
}

/**
 * Renders a single decorative die positioned in a corner of the setup screen.
 *
 * <p>Encapsulates the repeated pattern of a positioned, non-interactive
 * [DiceView], eliminating code duplication across the four corner placements.</p>
 *
 * @param value The face value to display on the die (1–6).
 * @param modifier Modifier carrying alignment and padding from the parent scope.
 */
@Composable
private fun CornerDie(
    value: Int,
    modifier: Modifier
) {
    Box(modifier = modifier) {
        DiceView(value = value, isHeld = false, isRolling = false, onClick = {})
    }
}