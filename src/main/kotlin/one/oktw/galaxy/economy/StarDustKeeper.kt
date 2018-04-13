package one.oktw.galaxy.economy

import kotlin.math.roundToLong

open class StarDustKeeper {
    var interestRate = 0.0
    var starDust = 0L
        private set

    fun giveStarDust(number: Int) = giveStarDust(number.toLong())

    fun giveStarDust(number: Long): Boolean {
        if (number < 0) return false

        starDust += number

        return true
    }

    fun takeStarDust(number: Int) = takeStarDust(number.toLong())

    fun takeStarDust(number: Long): Boolean {
        if (number < 0 || starDust < number) return false

        starDust -= number

        return true
    }

    fun giveInterest() {
        starDust += (starDust * interestRate).roundToLong()
    }
}