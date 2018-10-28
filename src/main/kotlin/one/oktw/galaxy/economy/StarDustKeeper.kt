package one.oktw.galaxy.economy

import kotlin.math.roundToLong

open class StarDustKeeper {
    open var interestRate = 0.0
    var starDust = 0L
        private set

    fun giveStarDust(number: Number): Boolean {
        val i = number.toLong()

        if (i < 0) return false

        starDust += i

        return true
    }

    fun setStarDust(number: Long): Boolean {
        if (number < 0) return false

        starDust = number

        return true
    }

    fun takeStarDust(number: Number): Boolean {
        val i = number.toLong()

        if (i < 0 || starDust < i) return false

        starDust -= i

        return true
    }

    fun giveInterest() {
        starDust += (starDust * interestRate).roundToLong()
    }
}
