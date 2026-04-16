package com.rekcode.yahtzee.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.rekcode.yahtzee.ui.AppScreen
import com.rekcode.yahtzee.ui.screens.GameCoordinator
import com.rekcode.yahtzee.ui.screens.GameScreen
import com.rekcode.yahtzee.ui.screens.SetupScreen
import com.rekcode.yahtzee.ui.theme.UiConstants

/**
 * Root composable responsible for controlling high-level UI navigation flow.
 *
 * @param onExitRequested Callback invoked when the user requests to fully exit the application.
 */
@Composable
fun AppRoot(
    onExitRequested: () -> Unit
) {
    var currentScreen by remember { mutableStateOf(AppScreen.Setup) }
    var playerCount by remember { mutableIntStateOf(UiConstants.DefaultPlayerCount) }
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
            key(gameInstanceId) {
                val controller = remember {
                    com.rekcode.yahtzee.api.createGame(numPlayers = playerCount)
                }

                val coordinator = remember(controller) {
                    GameCoordinator(controller)
                }

                GameScreen(
                    coordinator = coordinator,
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