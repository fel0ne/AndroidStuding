import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

interface Moveble {
    fun getName(): String
    fun getCurrentSpeed(): Int
    fun getX(): Double
    fun getY(): Double
    fun setX(x: Double)
    fun setY(y: Double)

    fun move() {
        val r: Double = Random.nextDouble(0.0, 2 * Math.PI)
        val currentX = getX()
        val currentY = getY()
        val speed = getCurrentSpeed()

        setX(currentX + speed * sin(r))
        setY(currentY + speed * cos(r))
        //println("${getName()} перешел в (${getX()} ;${getY()})")
    }
}