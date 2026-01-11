import engine.TrafficLight
import utils.Time

fun main() {
    val inactiveStartTime = Time(22, 0, 0)
    val inactiveEndTime = Time(3, 59, 0)

    val trafficLight = TrafficLight(60, inactiveStartTime, inactiveEndTime)

    val predictionTime = Time(21, 58, 30)

    println(trafficLight.getState(predictionTime))

    trafficLight.startRuntime(predictionTime)

    Thread.sleep(1_000)

    trafficLight.stopRuntime()
}