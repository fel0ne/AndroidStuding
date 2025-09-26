import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlin.concurrent.thread
import Human
import Driver
import Moveble
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