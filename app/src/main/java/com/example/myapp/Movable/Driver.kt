import kotlin.math.cos
import kotlin.math.sin

class Driver(t_name: String, t_age: Int, t_current_speed: Int, val direction: Double) : Human(t_name, t_age, t_current_speed), Moveble {

    override fun move() {
        val currentX = getX()
        val currentY = getY()
        val speed = getCurrentSpeed()

        val newX = currentX + speed * cos(direction)
        val newY = currentY + speed * sin(direction)

        setX(newX)
        setY(newY)

        println("${getName()} (водитель) движется прямо в (${"%.2f".format(newX)} ; ${"%.2f".format(newY)})")
    }
}