package engine

import enumClasses.State
import exception.NoActiveRuntimeException
import utils.Time
import java.time.LocalTime

class TrafficLight(redGreenStateDurationValue:Int, inactiveStartTimeValue: Time, inactiveEndTimeValue: Time) {
    init {
        require(redGreenStateDurationValue >= 0){"The red and green state duration value must be higher or equal to 0.0"}
        require(inactiveStartTimeValue.daysSinceFirstMidnight == 0){"The inactive start time value must be on the same day as the end time"}
        require(inactiveEndTimeValue.daysSinceFirstMidnight == 0){"The inactive end time value must be on the same day as the start time"}
        require(inactiveStartTimeValue != inactiveEndTimeValue){"The inactive start and end time values must be different"}
    }

    var redGreenStateDuration = redGreenStateDurationValue
        set(value){
            require(value >= 0){"The new value higher than 0 (seconds)"}

            field = value
        }

    val yellowStateDuration:Int
        get() {
            if(redGreenStateDuration/2 == 0) {
                return 1
            } else {
                return redGreenStateDuration/2
            }
        }

    var inactiveStartTime: Time = inactiveStartTimeValue
        set(value){
            require(value.daysSinceFirstMidnight == 0){"The new inactive end time value must be on the same day as the start time"}
            require(value != inactiveEndTime){"The inactive start and end time values must be different"}

            field = value
        }

    var inactiveEndTime: Time = inactiveEndTimeValue
        set(value){
            require(value.daysSinceFirstMidnight == 0){"The new inactive end time value must be on the same day as the start time"}
            require(value != inactiveStartTime){"The inactive start and end time values must be different"}
            require(!inactiveEndTime.isBefore(inactiveStartTime)){"The inactive end time must be before the inactive start time"}

            field = value
        }

    var currentState: State = State.RED
        private set

    private var runtimeActive: Boolean = false

    private var runtimeThread:Thread? = null

    fun getState(time: Time):State {
        require(time.daysSinceFirstMidnight == 0) {"The provided time must be on the same day"}

        var prediction:State = State.YELLOW

        val start = inactiveStartTime.secondsSinceFirstMidnight
        val end = inactiveEndTime.secondsSinceFirstMidnight

        val isInactive = if (start < end) {
            time.secondsSinceFirstMidnight in start..end
        } else {
            time.secondsSinceFirstMidnight >= start || time.secondsSinceFirstMidnight <= end
        }

        if (isInactive) return prediction

        val cycleTime = (redGreenStateDuration * 2) + yellowStateDuration
        val secondsFromStart = if(time.secondsSinceFirstMidnight - (inactiveEndTime.secondsSinceFirstMidnight + 59) < 0) {
            (86400 - inactiveEndTime.secondsSinceFirstMidnight) + time.secondsSinceFirstMidnight
        } else time.secondsSinceFirstMidnight - (inactiveEndTime.secondsSinceFirstMidnight + 59)

        when(secondsFromStart % cycleTime) {
            in 0..<redGreenStateDuration -> prediction = State.GREEN
            in redGreenStateDuration..<redGreenStateDuration + yellowStateDuration -> prediction = State.YELLOW
            in redGreenStateDuration + yellowStateDuration..<redGreenStateDuration + yellowStateDuration + redGreenStateDuration -> prediction = State.RED
        }

        return prediction
    }

    /**
     * @param timeValue Accepts an object of type Time to start the traffic light from that time, if null, uses your current device time
     */
    fun startRuntime(timeValue: Time?) {
        runtimeActive = true

        val time = timeValue ?: Time(LocalTime.now().hour, LocalTime.now().minute, LocalTime.now().second)

        runtimeThread = Thread {
            try {
                while(runtimeActive) {
                    if (time.secondsSinceFirstMidnight >= inactiveStartTime.secondsSinceFirstMidnight ||
                        time.secondsSinceFirstMidnight <= inactiveEndTime.secondsSinceFirstMidnight + 59
                    ) {
                        currentState = State.YELLOW

                        println(
                            "---- De-Activated phase ----\n"+
                            "Current State: $currentState\n"+
                            "Current Time: ${time.toISOFormat()}\n"
                        )

                        time.addSeconds(yellowStateDuration)
                        Thread.sleep(yellowStateDuration.toLong() * 1000)
                    } else {
                        when (currentState) {
                            State.GREEN -> {
                                currentState = State.YELLOW

                                println(
                                    "---- Phase 2 ----\n"+
                                    "Current State: $currentState\n"+
                                    "Current Time: ${time.toISOFormat()}\n"
                                )

                                time.addSeconds(yellowStateDuration)
                                Thread.sleep(yellowStateDuration.toLong() * 1000)
                            }

                            State.YELLOW -> {
                                currentState = State.RED

                                println(
                                    "---- Phase 3 ----\n"+
                                    "Current State: $currentState\n"+
                                    "Current Time: ${time.toISOFormat()}\n"
                                )

                                time.addSeconds(redGreenStateDuration)
                                Thread.sleep(redGreenStateDuration.toLong() * 1000)
                            }

                            State.RED -> {
                                currentState = State.GREEN

                                println(
                                    "---- Phase 1 ----\n"+
                                    "Current State: $currentState\n"+
                                    "Current Time: ${time.toISOFormat()}\n"
                                )

                                time.addSeconds(redGreenStateDuration)
                                Thread.sleep(redGreenStateDuration.toLong() * 1000)
                            }
                        }
                    }
                }

                println(
                    "---- ATTENTION -----\n"+
                            "Traffic Light cycle interrupted manually\n"
                )
            } catch (error: InterruptedException) {
                println(
                    "---- ATTENTION -----\n"+
                    "Traffic Light cycle interrupted manually\n"
                )
            }
        }.apply {
            start()
        }
    }

    fun stopRuntime() {
        if(runtimeThread == null) {
            throw NoActiveRuntimeException()
        } else {
            runtimeActive = false
            runtimeThread?.interrupt()
        }
    }
}