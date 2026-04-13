package com.rekcode.yahtzee.ui.util

import androidx.annotation.StringRes
import com.rekcode.yahtzee.R
import com.rekcode.yahtzee.api.ScoreCategory

@StringRes
fun ScoreCategory.toDisplayStringRes(): Int {
    return when (this) {
        ScoreCategory.ONES -> R.string.score_ones
        ScoreCategory.TWOS -> R.string.score_twos
        ScoreCategory.THREES -> R.string.score_threes
        ScoreCategory.FOURS -> R.string.score_fours
        ScoreCategory.FIVES -> R.string.score_fives
        ScoreCategory.SIXES -> R.string.score_sixes

        ScoreCategory.THREE_OF_A_KIND -> R.string.score_three_of_a_kind
        ScoreCategory.FOUR_OF_A_KIND -> R.string.score_four_of_a_kind
        ScoreCategory.FULL_HOUSE -> R.string.score_full_house
        ScoreCategory.SMALL_STRAIGHT -> R.string.score_small_straight
        ScoreCategory.LARGE_STRAIGHT -> R.string.score_large_straight
        ScoreCategory.YAHTZEE -> R.string.score_yahtzee
        ScoreCategory.CHANCE -> R.string.score_chance
    }
}
