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
    /**
     * Navigation state tracking.
     * Hoisted here to manage transitions between top-level screens.
     */
    var currentScreen by remember { mutableStateOf(AppScreen.Setup) }

    /**
     * Configuration state captured during Setup to be passed into the Game engine.
     */
    var playerCount by remember { mutableIntStateOf(UiConstants.DefaultPlayerCount) }

    /**
     * Unique identifier for the current game session.
     *
     * Incremented to force a total UI and state reset when a "Play Again"
     * event occurs, ensuring no stale game data persists between matches.
     */
    var gameInstanceId by remember { mutableIntStateOf(0) }

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
             * The [key] block ensures that when [gameInstanceId] changes:
             * 1. The previous [GameScreen] and its internal states are fully disposed.
             * 2. A fresh game controller is instantiated via [com.rekcode.yahtzee.api.createGame].
             * 3. The UI tree is rebuilt from a clean state.
             */
            key(gameInstanceId) {
                val controller = remember {
                    com.rekcode.yahtzee.api.createGame(numPlayers = playerCount)
                }

                GameScreen(
                    controller = controller,
                    onExitRequested = {
                        currentScreen = AppScreen.Setup
                    },
                    onPlayAgainRequested = {
                        gameInstanceId++
                    }
                )
            }
        }
    }
}
