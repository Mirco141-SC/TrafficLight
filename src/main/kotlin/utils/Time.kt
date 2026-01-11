package utils

import kotlin.math.abs

class Time(hoursValue:Int, minutesValue:Int, secondsValue:Int) {
    init {
        require(hoursValue in 0..24){"The hours field must be a value within 0 and 24"}
        require(minutesValue in 0..59){"The minutes field must be a value within 0 and 60"}
        require(secondsValue in 0..59){"The seconds field must be a value within 0 and 60"}
    }

    var secondsSinceFirstMidnight = (hoursValue * 60 * 60) + (minutesValue * 60) + secondsValue
        private set(value) {
            require(value >= 0){"The given value must be higher or equal to 0"}

            field = value
        }

    val hours:Int
        get() {
            val daySeconds = secondsSinceFirstMidnight % 86400 //86400 = 1 day

            return daySeconds / 3600 //3600 = 1 hour
        }

    val minutes:Int
        get() {
            val daySeconds = secondsSinceFirstMidnight % 86400
            val remainingSecondsAfterHours = daySeconds % 3600
            return remainingSecondsAfterHours / 60
        }

    val seconds:Int
        get() {
            return secondsSinceFirstMidnight % 60
        }

    val daysSinceFirstMidnight:Int
        get() {
            return secondsSinceFirstMidnight / 86400
        }

    fun addSeconds(value:Int) {
        require(value > 0){"The given value must be higher than 0"}

        secondsSinceFirstMidnight += value
    }

    fun isBefore(obj:Time):Boolean {
        return obj.secondsSinceFirstMidnight > this.secondsSinceFirstMidnight
    }

    fun differenceFrom(obj:Time):Int {
        /**
         * Since we don't use a unique epoch timestamp,
         * this function calculates the difference between the two times
         * assuming they are on the same day, and ignores the fact that
         * they could be on different days of distance
         */

        val difference = obj.secondsSinceFirstMidnight - this.secondsSinceFirstMidnight

        return if(difference > 0) {
            difference
        } else if (difference < 0) {
            86400 - abs(difference)
        } else 0
    }

    fun toISOFormat():String {
        val ISOFormat: String

        val readableHours: String = if(hours.toString().length < 2 ) {
            "0${hours}"
        } else "$hours"

        val readableMinutes: String = if(minutes.toString().length < 2 ) {
            "0${minutes}"
        } else "$minutes"

        val readableSeconds: String = if(seconds.toString().length < 2 ) {
            "0${seconds}"
        } else "$seconds"

        ISOFormat = if(daysSinceFirstMidnight == 1) {
            "$readableHours:$readableMinutes:$readableSeconds + $daysSinceFirstMidnight day"
        } else if(daysSinceFirstMidnight > 1) {
            "$readableHours:$readableMinutes:$readableSeconds + $daysSinceFirstMidnight days"
        } else {
            "$readableHours:$readableMinutes:$readableSeconds"
        }

        return ISOFormat
    }

    override fun equals(other: Any?): Boolean {
        /**
         * Since we don't use a unique epoch timestamp,
         * this function calculates the difference between the two times
         * assuming they are on the same day, and ignores the fact that
         * they could be on different days of distance
         */

        if(this === other) return true
        if(other !is Time) return false
        return this.secondsSinceFirstMidnight == other.secondsSinceFirstMidnight
    }


    override fun hashCode(): Int {
        return secondsSinceFirstMidnight.hashCode()
    }
}