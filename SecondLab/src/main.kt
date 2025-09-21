import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlin.concurrent.thread
open class Human(t_name: String , t_age: Int , t_current_speed: Int ){
    private var name: String = t_name
    private var age: Int = t_age
    private var current_speed = t_current_speed
    private var x: Double = 0.0
    private var y: Double = 0.0

    open fun move(){
        val r: Double = Random.nextDouble(0.0,2*Math.PI)
        x += current_speed * sin(r)
        y += current_speed * cos(r)
        //println("$name перешел в ($x ;$y)")
    }

    //---------geters--------
    fun getName(): String{
        return(name)
    }
    fun getAge(): Int{
        return(age)
    }
    fun getCurrentSpeed(): Int{
        return(current_speed)
    }
    fun getX(): Double{
        return(x)
    }
    fun getY(): Double{
        return(y)
    }


    //-----setters-----
    fun setName(p: String){
        name = p
    }
    fun setAge(p: Int){
        age = p
    }
    fun setCurrent(p: Int){
        current_speed = p
    }
    fun setX(p: Double){
        x = p
    }
    fun setY(p: Double){
        y = p
    }

}

class Driver(t_name: String, t_age: Int, t_current_speed: Int, val direction: Double) : Human(t_name, t_age, t_current_speed) {

    // Переопределяем метод move для прямолинейного движения
    override fun move() {
        val currentX = getX()
        val currentY = getY()
        val speed = getCurrentSpeed()

        // Прямолинейное движение в заданном направлении
        val newX = currentX + speed * cos(direction)
        val newY = currentY + speed * sin(direction)

        setX(newX)
        setY(newY)

        println("${getName()} (водитель) движется прямо в (${"%.2f".format(newX)} ; ${"%.2f".format(newY)})")
    }
}

fun main() {
    val time: Int = 10

    // Создаем 3 объекта Human и 1 объект Driver
    val humans = listOf(
        Human("человек 1", Random.nextInt(18, 80), Random.nextInt(1, 10)),
        Human("человек 2", Random.nextInt(18, 80), Random.nextInt(1, 10)),
        Human("человек 3", Random.nextInt(18, 80), Random.nextInt(1, 10)),
        Human("человек 4", Random.nextInt(18, 80), Random.nextInt(1, 10))
    )

    // Создаем водителя с направлением 45 градусов (π/4 радиан)
    val driver = Driver("водитель", Random.nextInt(25, 60), Random.nextInt(5, 15), Math.PI / 4)

    println("созданные люди:")
    humans.forEachIndexed { index, human ->
        println("${index + 1}. ${human.getName()}, возраст: ${human.getAge()}, скорость: ${human.getCurrentSpeed()}")
    }
    println("водитель: ${driver.getName()}, возраст: ${driver.getAge()}, скорость: ${driver.getCurrentSpeed()}, направление: ${Math.toDegrees(driver.direction)}°")

    println("\n-----Запуск параллельной симуляции-----\n")

    // Создаем список всех объектов для движения
    val allEntities = humans + driver
    val threads = mutableListOf<Thread>()

    // Создаем и запускаем потоки для каждого объекта
    allEntities.forEach { entity ->
        val thread = thread {
            for (i in 1..time) {
                entity.move()
                Thread.sleep(100) // Небольшая задержка для наглядности
            }
        }
        threads.add(thread)
    }

    // Ждем завершения всех потоков
    threads.forEach { it.join() }

    println("\n-----Конечные позиции-----\n")
    allEntities.forEachIndexed { index, entity ->
        val type = if (entity is Driver) " (водитель)" else ""
        println("${index + 1}. ${entity.getName()}$type, конечная точка: (${"%.2f".format(entity.getX())}, ${"%.2f".format(entity.getY())})")
    }
}