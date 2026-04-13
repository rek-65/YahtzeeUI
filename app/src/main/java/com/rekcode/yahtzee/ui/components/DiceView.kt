package com.rekcode.yahtzee.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rekcode.yahtzee.R
import com.rekcode.yahtzee.ui.theme.Dimens

/**
 * Constants for dice animation and layout.
 */
private const val DICE_SIZE_DP = 120          /** Dice box size in dp */
private const val DICE_OFFSET_Y_DP = 48       /** Base vertical offset for dice in dp */
private const val BOUNCE_SCALE_MIN = 1.0f     /** Minimum scale factor for dice bounce */
private const val BOUNCE_SCALE_MAX = 1.08f    /** Maximum scale factor for dice bounce */
private const val BOUNCE_DURATION_MS = 400    /** Duration of one bounce cycle in milliseconds */
private const val BOUNCE_OFFSET_Y = -24f      /** Vertical offset for bounce animation in dp */
private const val PRESS_SCALE = 0.92f         /** Scale factor when die is pressed */
private const val ROTATION_DURATION_MS = 700  /** Duration of one full rotation in milliseconds */
private const val ROTATION_DEGREES = 360f     /** Rotation degrees for rolling dice */

/**
 * Composable function to display a single dice.
 *
 * Displays a die with animation for rolling and visual cues for held or pressed state.
 * Supports click interactions through [onClick].
 *
 * @param value Current dice value (1-6), defaults to blank if out of range.
 * @param isHeld Whether the die is locked/held. Alters background.
 * @param isRolling Whether the die is rolling. Triggers bounce and rotation animations.
 * @param onClick Lambda to be called when the die is clicked.
 */
@Composable
fun DiceView(
    value: Int,
    isHeld: Boolean,
    isRolling: Boolean,
    onClick: () -> Unit
) {
    /** Tracks press state for scaling effect */
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    /** Determines if animations should run for this die */
    val shouldAnimate = isRolling && !isHeld

    /** Infinite transition for bounce and rotation animations */
    val transition = rememberInfiniteTransition(label = "dice_transition")

    /** Animated scale for bounce effect */
    val animatedScale = transition.animateFloat(
        initialValue = BOUNCE_SCALE_MIN,
        targetValue = BOUNCE_SCALE_MAX,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = BOUNCE_DURATION_MS,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_anim"
    )

    /** Animated vertical offset for bounce effect */
    val animatedOffsetY = transition.animateFloat(
        initialValue = 0f,
        targetValue = BOUNCE_OFFSET_Y,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = BOUNCE_DURATION_MS,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset_anim"
    )

    /** Animated rotation for rolling effect */
    val animatedRotation = transition.animateFloat(
        initialValue = 0f,
        targetValue = ROTATION_DEGREES,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = ROTATION_DURATION_MS,
                easing = LinearEasing
            )
        ),
        label = "rotation_anim"
    )

    /** Current scale based on rolling or pressed state */
    val scale = if (shouldAnimate) {
        animatedScale.value
    } else {
        if (isPressed) PRESS_SCALE else BOUNCE_SCALE_MIN
    }

    /** Current vertical offset based on animation */
    val offsetY = if (shouldAnimate) {
        animatedOffsetY.value.dp
    } else {
        0.dp
    }

    /** Current rotation value based on animation */
    val rotationValue = if (shouldAnimate) {
        animatedRotation.value
    } else {
        0f
    }

    /** Background color and shape for held dice */
    val backgroundColor = if (isHeld) {
        colorResource(id = R.color.dice_held_background)
    } else {
        Color.Transparent
    }

    /** Corner shape applied to the held die background container */
    val backgroundShape = RoundedCornerShape(Dimens.DiceHeldCornerRadius)

    /** Drawable resource ID for the current dice value */
    val drawableId = when (value) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        6 -> R.drawable.dice_6
        else -> R.drawable.dice_blank
    }

    /** Box composable holding the die image, animations, and click handling */
    Box(
        modifier = Modifier
            .offset(y = DICE_OFFSET_Y_DP.dp + offsetY)
            .size(DICE_SIZE_DP.dp)
            .scale(scale)
            .graphicsLayer {
                rotationZ = rotationValue
            }
            .background(color = backgroundColor, shape = backgroundShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Image(
            painter = painterResource(id = drawableId),
            contentDescription = "Die showing $value",
            modifier = Modifier.size(DICE_SIZE_DP.dp)
        )
    }
}

/**
 * Preview of the DiceView composable in both default and held states.
 *
 * This preview is used for visual verification during development and does not
 * participate in application runtime logic.
 */
@Preview(showBackground = true)
@Composable
fun DiceViewPreview() {
    DiceView(
        value = 3,
        isHeld = true,
        isRolling = false,
        onClick = {}
    )
}