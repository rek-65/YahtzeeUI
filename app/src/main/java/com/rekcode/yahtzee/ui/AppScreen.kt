package com.rekcode.yahtzee.ui

/**
 * Represents the set of top-level screens in the Yahtzee application.
 *
 * Responsibilities:
 * - Defines all valid navigation destinations.
 * - Provides a type-safe way to control screen transitions.
 *
 * Architectural Notes:
 * - This replaces the use of strings or integers for navigation state.
 * - Ensures compile-time safety for screen switching logic.
 *
 * Constraints:
 * - Must remain lightweight (enum only, no logic).
 * - Must not depend on UI or business logic layers.
 */
enum class AppScreen {

    /**
     * Setup screen where the user starts or exits the game.
     */
    Setup,

    /**
     * Main gameplay screen.
     */
    Game
}