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
import com.rekcode.yahtzee.ui.theme.UiConstants

/**
 * Displays a single die with support for:
 * - Rolling animation (bounce + rotation)
 * - Held state visualization
 * - Press interaction feedback
 *
 * This composable is stateless and fully driven by input parameters.
 *
 * @param value Current dice value (1–6). Invalid values render a blank die.
 * @param isHeld Whether the die is currently held (locked).
 * @param isRolling Whether the die is actively rolling.
 * @param onClick Callback invoked when the die is tapped.
 */
@Composable
fun DiceView(
    value: Int,
    isHeld: Boolean,
    isRolling: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val shouldAnimate = isRolling && !isHeld

    val transition = rememberInfiniteTransition(label = "dice_transition")

    val animatedScale = transition.animateFloat(
        initialValue = UiConstants.DiceBounceScaleMin,
        targetValue = UiConstants.DiceBounceScaleMax,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = UiConstants.DiceBounceDurationMs,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_anim"
    )

    val animatedOffsetY = transition.animateFloat(
        initialValue = 0f,
        targetValue = -Dimens.DiceBounceOffset.value,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = UiConstants.DiceBounceDurationMs,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset_anim"
    )

    val animatedRotation = transition.animateFloat(
        initialValue = 0f,
        targetValue = UiConstants.DiceRotationDegrees,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = UiConstants.DiceRotationDurationMs,
                easing = LinearEasing
            )
        ),
        label = "rotation_anim"
    )

    val scale = when {
        shouldAnimate -> animatedScale.value
        isPressed -> UiConstants.DicePressScale
        else -> UiConstants.DiceBounceScaleMin
    }

    val offsetY = if (shouldAnimate) {
        animatedOffsetY.value.dp
    } else {
        0.dp
    }

    val rotation = if (shouldAnimate) animatedRotation.value else 0f

    val backgroundColor = if (isHeld) {
        colorResource(id = R.color.dice_held_background)
    } else {
        Color.Transparent
    }

    val shape = RoundedCornerShape(Dimens.DiceHeldCornerRadius)

    val drawableId = when (value) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        6 -> R.drawable.dice_6
        else -> R.drawable.dice_blank
    }

    Box(
        modifier = Modifier
            .offset(y = Dimens.DiceVerticalOffset + offsetY)
            .size(Dimens.DiceSize)
            .scale(scale)
            .graphicsLayer { rotationZ = rotation }
            .background(color = backgroundColor, shape = shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Image(
            painter = painterResource(id = drawableId),
            contentDescription = "Die showing $value",
            modifier = Modifier.size(Dimens.DiceSize)
        )
    }
}

/**
 * Preview used for visual validation of DiceView behavior.
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