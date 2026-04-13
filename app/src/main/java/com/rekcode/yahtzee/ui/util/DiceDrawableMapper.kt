package com.rekcode.yahtzee.ui.util

import com.rekcode.yahtzee.R

/**
 * Utility object responsible for mapping dice face values to drawable resources.
 *
 * This class contains no game logic and serves only as a UI-layer translation
 * between engine-provided dice values and visual representations.
 */
object DiceDrawableMapper {

    /**
     * Returns the drawable resource ID corresponding to a dice face value.
     *
     * @param value The dice face value (expected range: 1–6). Any value outside
     * this range will return the blank dice drawable.
     *
     * @return The drawable resource ID representing the given dice value.
     */
    fun getDrawableForValue(value: Int?): Int {
        return when (value) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            6 -> R.drawable.dice_6
            else -> R.drawable.dice_blank
        }
    }
}