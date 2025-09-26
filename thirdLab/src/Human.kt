import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

open class Human(t_name: String, t_age: Int, t_current_speed: Int) : Moveble {
    private var name: String = t_name
    private var age: Int = t_age
    private var current_speed = t_current_speed
    private var x: Double = 0.0
    private var y: Double = 0.0

    //--------getters
    override fun getName(): String = name
    override fun getCurrentSpeed(): Int = current_speed
    override fun getX(): Double = x
    override fun getY(): Double = y

    fun getAge(): Int = age


    //--------setters
    override fun setX(p: Double) { x = p }
    override fun setY(p: Double) { y = p }

    fun setName(p: String) { name = p }
    fun setAge(p: Int) { age = p }
    fun setCurrent(p: Int) { current_speed = p }
}