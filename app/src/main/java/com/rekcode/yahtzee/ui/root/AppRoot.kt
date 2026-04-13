package com.rekcode.yahtzee.ui.root

import androidx.compose.runtime.*
import com.rekcode.yahtzee.ui.screens.SetupScreen
import com.rekcode.yahtzee.ui.screens.GameScreen
import com.rekcode.yahtzee.ui.theme.UiConstants
import com.rekcode.yahtzee.ui.AppScreen

/**
 * Root composable responsible for controlling high-level UI navigation flow.
 *
 * Architectural Role:
 * - Acts as the single entry point for all UI screen rendering.
 * - Owns and manages the current screen state.
 * - Delegates UI rendering to screen-level composables.
 *
 * Responsibilities:
 * - Display Splash screen on launch.
 * - Transition to Setup screen after splash completes.
 * - Serve as the centralized location for future navigation expansion.
 *
 * Design Constraints:
 * - Contains minimal state (screen routing only).
 * - Does NOT contain business logic.
 * - Does NOT directly manipulate UI components.
 * - Maintains strict separation between UI and game logic layers.
 *
 * Future Extension Points:
 * - Player setup screen
 * - Game screen
 * - Score review screen
 * - Multiplayer flow integration
 *
 * @see SetupScreen
 * @param onExitRequested Callback invoked when the user requests to fully exit the application.
 *
 */
@Composable
fun AppRoot(
    onExitRequested: () -> Unit
) {
    /** Tracks the currently displayed screen. */
    var currentScreen by remember { mutableStateOf(AppScreen.Setup) }

    /**
     * Player count selected on the Setup screen.
     * Captured here so it can be passed to the game engine on transition to Game.
     */
    var playerCount by remember { mutableIntStateOf(UiConstants.DefaultPlayerCount) }

    when (currentScreen) {

        AppScreen.Setup -> {
            SetupScreen(
                onStartGame = { selectedCount ->
                    playerCount = selectedCount
                    currentScreen = AppScreen.Game
                },
                onExitRequested = onExitRequested
            )
        }

        AppScreen.Game -> {
            /**
             * Game controller scoped to the Game screen lifecycle.
             * Created with the player count selected on the Setup screen.
             * Recreated on Play Again by cycling through AppScreen.Setup.
             */
            val controller = remember {
                com.rekcode.yahtzee.api.createGame(numPlayers = playerCount)
            }

            GameScreen(
                controller = controller,
                onExitRequested = {
                    currentScreen = AppScreen.Setup
                },
                onPlayAgainRequested = {
                    currentScreen = AppScreen.Setup
                    currentScreen = AppScreen.Game
                }
            )
        }
    }
}