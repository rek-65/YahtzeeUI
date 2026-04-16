package com.rekcode.yahtzee.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

/**
 * Centralized non-measurement UI constants for the application.
 *
 * Responsibilities:
 * - Provide a single source of truth for reusable UI values
 * - Ensure consistency across layouts and components
 * - Prevent hardcoded ("magic") values in composables
 *
 * Covers:
 * - Color values used for UI support (previews, alternating rows)
 * - Layout weight and fraction constants (Float)
 * - Alpha constants (Float)
 * - Animation duration constants (Long)
 * - Ratio constants (Float)
 * - String display constants
 * - Game setup bounds (Int)
 * - Typography weight constants
 *
 * This object does NOT:
 * - Contain business logic
 * - Manage state
 * - Reference game engine data
 * - Hold dimensional or measurement values (see Dimens)
 */
object UiConstants {

    // ------------------------------------------------------------------------
    // Colors (UI Support)
    // ------------------------------------------------------------------------

    /**
     * Neutral background color used for component previews in Android Studio.
     */
    val PreviewBackgroundColor = Color(0xFF2E2E2E)

    /**
     * Background color for even-indexed score rows.
     * Fully transparent — inherits the surface beneath.
     */
    val ScoreRowEvenBackground = Color(0x00000000)

    /**
     * Background color for odd-indexed score rows.
     * Subtle white tint to create visual row separation on dark surfaces.
     */
    val ScoreRowOddBackground = Color(0x1AFFFFFF)

    // ------------------------------------------------------------------------
    // Layout - Game Screen Section Weights
    // ------------------------------------------------------------------------

    /**
     * Number of dice used in the game.
     */
    const val DiceCount = 5

    /**
     * Vertical layout weight assigned to the dice display section.
     */
    const val DiceSectionWeight = 0.36f

    /**
     * Vertical layout weight assigned to the score sheet section.
     */
    const val ScoreSectionWeight = 0.50f

    /**
     * Vertical layout weight assigned to the action button section.
     */
    const val ActionSectionWeight = 0.14f

    // ------------------------------------------------------------------------
    // Layout - Button Weights and Fractions
    // ------------------------------------------------------------------------

    /**
     * Equal weight applied to each action button within a shared row.
     */
    const val ActionButtonWeight = 1f

    /**
     * Width fraction applied to primary buttons on setup screens.
     */
    const val ButtonWidthFraction = 0.6f

    /**
     * Weight used for row spacer alignment within scored rows.
     */
    const val RowSpacerWeight = 1f

    // ------------------------------------------------------------------------
    // Buttons - State Alpha
    // ------------------------------------------------------------------------

    /**
     * Alpha applied to the border of an enabled action button.
     */
    const val ButtonBorderEnabledAlpha = 0.5f

    /**
     * Alpha applied to the border of a disabled action button.
     */
    const val ButtonBorderDisabledAlpha = 0.3f

    // ------------------------------------------------------------------------
    // Score Rows - State Alpha
    // ------------------------------------------------------------------------

    /**
     * Alpha applied to locked score row text and value to signal
     * the category is no longer selectable by the current player.
     */
    const val LockedRowAlpha = 0.4f

    // ------------------------------------------------------------------------
// Dice - Animation & Interaction
// ------------------------------------------------------------------------

    /**
     * Minimum scale applied during dice bounce animation.
     */
    const val DiceBounceScaleMin = 1.0f

    /**
     * Maximum scale applied during dice bounce animation.
     */
    const val DiceBounceScaleMax = 1.08f

    /**
     * Duration of one bounce animation cycle in milliseconds.
     */
    const val DiceBounceDurationMs = 400

    /**
     * Scale applied when the die is pressed.
     */
    const val DicePressScale = 0.92f

    /**
     * Duration of one full dice rotation cycle in milliseconds.
     */
    const val DiceRotationDurationMs = 700

    /**
     * Total degrees rotated during one animation cycle.
     */
    const val DiceRotationDegrees = 360f

    /**
     * Duration in milliseconds for the dice rolling phase.
     *
     * This represents the logical roll duration used by the game flow,
     * not the visual animation cycle timing.
     *
     * Must remain separate from animation timing to support:
     * - Deterministic game flow
     * - Future multiplayer synchronization
     */
    const val DiceRollDurationMs = 600L

    // ------------------------------------------------------------------------
    // Celebration
    // ------------------------------------------------------------------------

    /**
     * Duration in milliseconds for the Yahtzee celebration overlay.
     */
    const val YahtzeeCelebrationDurationMs = 2000L

    // ------------------------------------------------------------------------
    // Game Logic Support - Score Sheet
    // ------------------------------------------------------------------------

    /**
     * Display name used to identify the boundary between the upper and lower
     * score sections within the score sheet.
     */
    const val ScoreSectionLowerBoundaryName = "Three of a Kind"

    // ------------------------------------------------------------------------
    // Setup Screen - Player Stepper Bounds
    // ------------------------------------------------------------------------

    /**
     * Minimum selectable player count on the setup screen.
     */
    const val MinPlayerCount = 1

    /**
     * Maximum selectable player count on the setup screen.
     */
    const val MaxPlayerCount = 4

    /**
     * Default player count shown when the setup screen first loads.
     */
    const val DefaultPlayerCount = 1

    /**
     * Alpha applied to the stepper button border to soften contrast
     * against the table surface while remaining visible.
     */
    const val StepperButtonBorderAlpha = 0.6f

    // ------------------------------------------------------------------------
    // Typography - Weight
    // ------------------------------------------------------------------------

    /**
     * Font weight applied to button and stepper control labels.
     * Bold weight ensures readability against the table_rail background.
     */
    val ButtonLabelFontWeight = FontWeight.Bold
}