package engine

import enumClasses.State
import exception.NoActiveRuntimeException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.Time

class TrafficLightTest {

 //------- Constructor -------//

 @Test
 fun constructor_redGreenStateDurationValueLowerThan0_IllegalArgumentException() {
  assertThrows<IllegalArgumentException> {
   TrafficLight(-5, Time(22, 0, 0), Time(3, 59, 0))
  }
 }

 @Test
 fun constructor_inactiveStartTimeValueDaysSinceFirstMidnightHigherThan0_IllegalArgumentException() {
  val inactiveStartTime = Time(22, 0, 0)
  inactiveStartTime.addSeconds(86400)

  assertThrows<IllegalArgumentException> {
   TrafficLight(60, inactiveStartTime, Time(3, 59, 0))
  }
 }

 @Test
 fun constructor_inactiveEndTimeValueDaysSinceFirstMidnightHigherThan0_IllegalArgumentException() {
  val inactiveEndTime = Time(3, 59, 0)
  inactiveEndTime.addSeconds(86400)

  assertThrows<IllegalArgumentException> {
   TrafficLight(60, Time(22, 0, 0), inactiveEndTime)
  }
 }

 @Test
 fun constructor_inactiveStartTimeValueAndInactiveEndTimeValueEqual_IllegalArgumentException() {
  assertThrows<IllegalArgumentException> { TrafficLight(60, Time(22, 0, 0), Time(22, 0, 0)) }
 }

 @Test
 fun constructor_allParameterMeetRequirements_objectCreated() {
  val trafficLight = TrafficLight(60, Time(22, 0, 0), Time(3, 59, 0))

  assertEquals(60, trafficLight.redGreenStateDuration)
  assertEquals(Time(22, 0, 0), trafficLight.inactiveStartTime)
  assertEquals(Time(3, 59, 0), trafficLight.inactiveEndTime) }

 //------ Setters -------//

 @Test
 fun setRedGreenStateDuration_valueHigherThan0_fieldEdited() {
  val trafficLight = TrafficLight(60, Time(22, 0, 0), Time(3, 59, 0))

  trafficLight.redGreenStateDuration = 120

  assertEquals(120, trafficLight.redGreenStateDuration)
 }

 @Test
 fun setRedGreenStateDuration_valueEqualTo0_fieldEdited() {
  val trafficLight = TrafficLight(60, Time(22, 0, 0), Time(3, 59, 0))

  trafficLight.redGreenStateDuration = 0

  assertEquals(0, trafficLight.redGreenStateDuration)
 }

 @Test
 fun setRedGreenStateDuration_valueLowerThan0_IllegalArgumentException() {
  val trafficLight = TrafficLight(60, Time(22, 0, 0), Time(3, 59, 0))

  assertThrows<IllegalArgumentException> { trafficLight.redGreenStateDuration = -1 }
 }

 //------ Getters -------//

 @Test
 fun getYellowStateDuration_returnsHalfOfRedGreenDuration() {
  val trafficLight = TrafficLight(60, Time(22, 0, 0), Time(3, 59, 0))

  assertEquals(30, trafficLight.yellowStateDuration) }

 //------ Functions -------//

 @Test
 fun getState_duringInactiveTime_returnsYellow() {
  val trafficLight = TrafficLight(60, Time(22, 0, 0), Time(3, 59, 0))

  val time = Time(23, 15, 0)

  assertEquals(State.YELLOW, trafficLight.getState(time))
 }

 @Test
 fun getState_atActivationTime_returnsGreen() {
  val trafficLight = TrafficLight(60, Time(22, 0, 0), Time(3, 59, 0))

  val time = Time(4, 0, 0)

  assertEquals(State.GREEN, trafficLight.getState(time))
 }

 @Test
 fun getState_afterGreenPhase_returnsYellow() {
  val trafficLight = TrafficLight(60, Time(22, 0, 0), Time(3, 59, 0))

  val time = Time(4, 1, 0)

  assertEquals(State.YELLOW, trafficLight.getState(time))
 }

 @Test
 fun getState_duringYellowPhase_returnsYellow() {
  val trafficLight = TrafficLight(60, Time(22, 0, 0), Time(3, 59, 0))

  val time = Time(4, 1, 20)

  assertEquals(State.YELLOW, trafficLight.getState(time))
 }

 @Test
 fun getState_afterYellowPhase_returnsRed() {
  val trafficLight = TrafficLight(60, Time(22, 0, 0), Time(3, 59, 0))

  val time = Time(4, 1, 30)

  assertEquals(State.RED, trafficLight.getState(time))
 }

 @Test
 fun getState_duringRedPhase_returnsRed() {
  val trafficLight = TrafficLight(60, Time(22, 0, 0), Time(3, 59, 0))

  val time = Time(4, 2, 0)

  assertEquals(State.RED, trafficLight.getState(time))
 }

 @Test
 fun stopRuntime_withoutStartRuntime_throwsNoActiveRuntimeException() {
  val trafficLight = TrafficLight(60, Time(22, 0, 0), Time(3, 59, 0))

  assertThrows<NoActiveRuntimeException> { trafficLight.stopRuntime() }
 }

 @Test
 fun getState_activeAtNightAtActivationTime_returnsGreen() {
  val trafficLight = TrafficLight(60, Time(4, 0, 0), Time(21, 59, 0))

  val time = Time(22, 0, 0)

  assertEquals(State.GREEN, trafficLight.getState(time))
 }

 @Test
 fun getState_activeAtNightDuringGreenPhase_returnsGreen() {
  val trafficLight = TrafficLight(60, Time(4, 0, 0), Time(21, 59, 0))

  val time = Time(22, 0, 30)

  assertEquals(State.GREEN, trafficLight.getState(time))
 }

 @Test
 fun getState_activeAtNightAfterGreenPhase_returnsYellow() {
  val trafficLight = TrafficLight(60, Time(4, 0, 0), Time(21, 59, 0))

  val time = Time(22, 1, 0)

  assertEquals(State.YELLOW, trafficLight.getState(time))
 }

 @Test
 fun getState_activeAtNightDuringYellowPhase_returnsYellow() {
  val trafficLight = TrafficLight(60, Time(4, 0, 0), Time(21, 59, 0))

  val time = Time(22, 1, 20)

  assertEquals(State.YELLOW, trafficLight.getState(time))
 }

 @Test
 fun getState_activeAtNightAfterYellowPhase_returnsRed() {
  val trafficLight = TrafficLight(60, Time(4, 0, 0), Time(21, 59, 0))

  val time = Time(22, 1, 30)

  assertEquals(State.RED, trafficLight.getState(time))
 }

 @Test
 fun getState_activeAtNightDuringRedPhase_returnsRed() {
  val trafficLight = TrafficLight(60, Time(4, 0, 0), Time(21, 59, 0))

  val time = Time(22, 2, 0)

  assertEquals(State.RED, trafficLight.getState(time))
 }

 @Test
 fun getState_activeAtNightAfterRedPhase_returnsGreen() {
  val trafficLight = TrafficLight(60, Time(4, 0, 0), Time(21, 59, 0))

  val time = Time(22, 2, 30)

  assertEquals(State.GREEN, trafficLight.getState(time))
 }

 @Test
 fun getState_activeAtNightAtDeactivationTime_returnsYellow() {
  val trafficLight = TrafficLight(60, Time(4, 0, 0), Time(21, 59, 0))

  val time = Time(4, 0, 0)

  assertEquals(State.YELLOW, trafficLight.getState(time))
 }
}
