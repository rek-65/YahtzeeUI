package com.rekcode.yahtzee.ui.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Centralized dimension and measurement constants for the application.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Eliminate magic numbers from composables.</li>
 *   <li>Ensure consistent spacing, sizing, and typography scale across the application.</li>
 *   <li>Allow single-point updates for layout and type tuning.</li>
 * </ul>
 *
 * <p>Covers layout spacing and padding (.dp), component sizing (.dp),
 * and typography sizing (.sp).</p>
 *
 * <p>This object does not contain business logic, manage state, reference game
 * engine data, or hold non-measurement constants (see {@link UiConstants}).</p>
 *
 * <p>All composables must reference these values instead of hard-coded dp or sp values.</p>
 */
object Dimens {

    // ------------------------------------------------------------------------
    // Screen Layout
    // ------------------------------------------------------------------------

    /**
     * Standard padding applied to root screen containers on all sides.
     */
    val ScreenPadding = 24.dp

    /**
     * Standard spacing between major vertically stacked UI sections.
     */
    val SectionSpacing = 8.dp

    /**
     * Standard spacing between vertically stacked interactive elements
     * such as buttons, steppers, and input controls.
     */
    val ItemStackSpacing = 24.dp

    /**
     * Large vertical spacing between major UI groupings.
     */
    val VerticalSpacingLarge = 24.dp

    /**
     * Medium vertical spacing between standard UI elements.
     */
    val VerticalSpacingMedium = 16.dp

    // ------------------------------------------------------------------------
    // Splash Screen
    // ------------------------------------------------------------------------

    /**
     * Rendered size of the splash screen logo image.
     * Ensures consistent appearance across screen densities.
     */
    val SplashImageSize = 200.dp

    // ------------------------------------------------------------------------
    // Buttons
    // ------------------------------------------------------------------------

    /**
     * Standard height for all interactive button controls across all screens.
     * Single source of truth for consistent button sizing.
     * Consolidates ActionButtonHeight, InteractiveControlSize, and ButtonHeight.
     */
    val ButtonHeight = 56.dp

    /**
     * Corner radius applied to action buttons.
     */
    val ButtonCornerRadius = 8.dp

    /**
     * Border stroke width applied to outlined action buttons.
     */
    val ButtonBorderWidth = 2.dp

    // ------------------------------------------------------------------------
    // Setup Screen - Dice Decoration
    // ------------------------------------------------------------------------

    /**
     * Padding applied to corner dice away from the top and side screen edges.
     */
    val SetupDiceEdgePadding = 8.dp

    /**
     * Bottom padding applied to corner dice to clear the action buttons.
     */
    val SetupDiceBottomPadding = 120.dp

    // ------------------------------------------------------------------------
    // Setup Screen - Player Stepper
    // ------------------------------------------------------------------------

    /**
     * Fixed width of the player count display between stepper buttons.
     * Prevents layout shift as the digit changes.
     */
    val StepperCountWidth = 48.dp

    /**
     * Diameter of each circular stepper button.
     */
    val StepperButtonSize = 40.dp

    /**
     * Corner radius applied to the stepper container background surface.
     */
    val StepperCornerRadius = 12.dp

    /**
     * Internal padding surrounding the stepper controls within their container.
     */
    val StepperContainerPadding = 12.dp

    // ------------------------------------------------------------------------
    // Dice Layout
    // ------------------------------------------------------------------------

    /**
     * Spacing between individual dice elements in the dice display area.
     */
    val DiceSpacing = 5.dp

    /**
     * Corner radius applied to the held die background container.
     * Rounds the held state indicator to visually align with the
     * rounded button aesthetic across the application.
     */
    val DiceHeldCornerRadius = 12.dp

    /**
     * Horizontal spacing between individual dice in a row.
     * Provides subtle visual separation between adjacent dice
     * without introducing a dramatic divider between them.
     */
    val DiceHorizontalSpacing = 8.dp

    // ------------------------------------------------------------------------
// DiceView Layout (Component-Specific)
// ------------------------------------------------------------------------

    /**
     * Fixed size of an individual die component.
     *
     * Ensures consistent rendering across all dice in the game.
     */
    val DiceSize = 120.dp

    /**
     * Base vertical offset applied to dice within their container.
     *
     * Used to visually position dice higher within their layout region.
     */
    val DiceVerticalOffset = 48.dp

    /**
     * Vertical translation distance used during bounce animation.
     *
     * Negative value moves the die upward during animation.
     */
    val DiceBounceOffset = (-24).dp

    // ------------------------------------------------------------------------
    // Score Sheet Layout
    // ------------------------------------------------------------------------

    /**
     * Fixed width of the score value column in the score sheet.
     */
    val ScoreValueWidth = 48.dp

    /**
     * Vertical spacing between individual score rows.
     */
    val ScoreRowSpacing = 6.dp

    // ------------------------------------------------------------------------
    // Player Header Layout
    // ------------------------------------------------------------------------

    /**
     * Spacing applied below the player header to separate it from
     * the content beneath.
     */
    val PlayerHeaderBottomSpacing = 10.dp

    // ------------------------------------------------------------------------
    // Typography
    // ------------------------------------------------------------------------

    /**
     * Font size for score values and labels in the score sheet.
     */
    val ScoreTextSize = 14.sp

    /**
     * Font size for the player header display.
     */
    val PlayerHeaderTextSize = 20.sp

    /**
     * Font size applied to button and stepper control labels.
     * Sized to render crisply within the standard button height.
     */
    val ButtonLabelFontSize = 18.sp

    // ------------------------------------------------------------------------
    // Celebration
    // ------------------------------------------------------------------------

    /**
     * Letter spacing applied to the Yahtzee celebration overlay text.
     * Widens character spacing for dramatic visual impact.
     */
    val YahtzeeCelebrationLetterSpacing = 6.sp
}