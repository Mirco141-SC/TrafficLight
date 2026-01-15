package utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TimeTest {

 //------- Constructor -------//

 @Test
 fun constructor_hoursLowerThan0_IllegalArgumentException() {
  assertThrows<IllegalArgumentException> { Time(-1, 0, 0) }
 }

 @Test
 fun constructor_hoursHigherThan24_IllegalArgumentException() {
  assertThrows<IllegalArgumentException> { Time(25, 0, 0) }
 }

 @Test
 fun constructor_minutesLowerThan0_IllegalArgumentException() {
  assertThrows<IllegalArgumentException> { Time(10, -1, 0) }
 }

 @Test
 fun constructor_minutesHigherThan59_IllegalArgumentException() {
  assertThrows<IllegalArgumentException> { Time(10, 60, 0) }
 }

 @Test
 fun constructor_secondsLowerThan0_IllegalArgumentException() {
  assertThrows<IllegalArgumentException> { Time(10, 0, -1) }
 }

 @Test
 fun constructor_secondsHigherThan59_IllegalArgumentException() {
  assertThrows<IllegalArgumentException> { Time(10, 0, 60) }
 }

 @Test
 fun constructor_parametersMeetRequirements_objectCreated() {
  val time = Time(12, 30, 45)

  assertEquals(12, time.hours)
  assertEquals(30, time.minutes)
  assertEquals(45, time.seconds)
  assertEquals(0, time.daysSinceFirstMidnight)
 }

 //------- addSeconds -------//

 @Test
 fun addSeconds_valueLowerOrEqualTo0_IllegalArgumentException() {
  val time = Time(10, 0, 0)

  assertThrows<IllegalArgumentException> { time.addSeconds(0) }
 }

 @Test
 fun addSeconds_validValue_secondsAdded() {
  val time = Time(10, 0, 0)
  val previousSeconds = time.secondsSinceFirstMidnight

  time.addSeconds(90)

  assertEquals(previousSeconds + 90, time.secondsSinceFirstMidnight)
  assertEquals(10, time.hours)
  assertEquals(1, time.minutes)
  assertEquals(30, time.seconds)
 }

 @Test
 fun addSeconds_overMidnight_daysIncreased() {
  val time = Time(23, 59, 30)
  val previousSeconds = time.secondsSinceFirstMidnight

  time.addSeconds(60)

  assertEquals(previousSeconds + 60, time.secondsSinceFirstMidnight)
  assertEquals(0, time.hours)
  assertEquals(0, time.minutes)
  assertEquals(30, time.seconds)
  assertEquals(1, time.daysSinceFirstMidnight)
 }

 //------- isBefore -------//

 @Test
 fun isBefore_timeIsBeforeOther_returnsTrue() {
  val t1 = Time(10, 0, 0)
  val t2 = Time(11, 0, 0)

  assertTrue(t1.isBefore(t2))
 }

 @Test
 fun isBefore_timeIsAfterOther_returnsFalse() {
  val t1 = Time(12, 0, 0)
  val t2 = Time(11, 0, 0)

  assertFalse(t1.isBefore(t2))
 }

 //------- differenceFrom -------//

 @Test
 fun differenceFrom_sameTime_returnsZero() {
  val t1 = Time(10, 0, 0)
  val t2 = Time(10, 0, 0)

  assertEquals(0, t1.differenceFrom(t2))
 }

 @Test
 fun differenceFrom_laterTimeSameDay_returnsSeconds() {
  val t1 = Time(10, 0, 0)
  val t2 = Time(10, 1, 0)

  assertEquals(60, t1.differenceFrom(t2))
 }

 @Test
 fun differenceFrom_timeAcrossMidnight_returnsSeconds() {
  val t1 = Time(23, 59, 0)
  val t2 = Time(0, 1, 0)

  assertEquals(120, t1.differenceFrom(t2))
 }

 //------- toISOFormat -------//

 @Test
 fun toISOFormat_sameDay_returnsFormat() {
  val time = Time(9, 5, 3)

  assertEquals("09:05:03", time.toISOFormat())
 }

 @Test
 fun toISOFormat_oneDayLater_returnsFormat() {
  val time = Time(23, 59, 59)
  time.addSeconds(1)

  assertEquals("00:00:00 + 1 day", time.toISOFormat())
 }

 @Test
 fun toISOFormat_multipleDaysLater_returnsFormat() {
  val time = Time(0, 0, 0)
  time.addSeconds(172800) // 2 days

  assertEquals("00:00:00 + 2 days", time.toISOFormat())
 }

 //------- equals & hashCode -------//

 @Test
 fun equals_sameSecondsSinceMidnight_returnsTrue() {
  val t1 = Time(10, 0, 0)
  val t2 = Time(10, 0, 0)

  assertEquals(t1, t2)
  assertEquals(t1.hashCode(), t2.hashCode())
 }

 @Test
 fun equals_differentTime_returnsFalse() {
  val t1 = Time(10, 0, 0)
  val t2 = Time(10, 0, 1)

  assertNotEquals(t1, t2)
 }
}