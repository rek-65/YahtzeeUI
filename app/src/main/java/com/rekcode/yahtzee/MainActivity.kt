package com.rekcode.yahtzee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/**
 * Entry point of the Yahtzee application.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Applies the global app theme.</li>
 *   <li>Orchestrates transitions between app screens: SplashScreen, SetupScreen, and GameScreen.</li>
 *   <li>Maintains a single entry and single exit point for the application.</li>
 * </ul>
 *
 * <p>Screen transitions are event-driven and non-blocking. SplashScreen is displayed
 * first; when its duration completes, the app transitions to SetupScreen.</p>
 *
 * @see com.rekcode.yahtzee.ui.screens.SetupScreen
 * @see com.rekcode.yahtzee.ui.screens.GameScreen
 */
class MainActivity : ComponentActivity() {

    /**
     * Android lifecycle entry point.
     * Initializes Compose and sets the root UI content.
     *
     * @param savedInstanceState Previously saved instance state, or null if none exists.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        actionBar?.hide()

        setContent {
            com.rekcode.yahtzee.ui.root.AppRoot(
                onExitRequested = { finish() }
            )
        }
    }
}