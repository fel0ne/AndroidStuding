import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Human(
    var fullName: String,
    var age: Int,
    var currentSpeed: Double,
    var x: Double = 0.0,
    var y: Double = 0.0
) {
    fun move() {
        // Генерируем случайный угол в радианах от 0 до 2 * PI
        val angle = Random.nextDouble(0.0, 2 * Math.PI)

        // Рассчитываем смещение по осям X и Y
        val deltaX = currentSpeed * cos(angle)
        val deltaY = currentSpeed * sin(angle)

        // Обновляем текущие координаты
        x += deltaX
        y += deltaY

        println("$fullName движется в ($x, $y)")
    }
}


fun main() {
    // 1. Создаем массив экземпляров класса Human
    val numberOfHumans = 7
    val humans = mutableListOf<Human>()

    for (i in 1..numberOfHumans) {
        humans.add(
            Human(
                fullName = "Человек №$i",
                age = Random.nextInt(18, 60), // Случайный возраст от 18 до 59
                currentSpeed = Random.nextDouble(1.0, 5.0) // Случайная скорость от 1.0 до 4.99
            )
        )
    }

    // 2. Задаем время симуляции в "секундах" (итерациях)
    val simulationTime = 10 // Симуляция будет длиться 10 секунд

    println("--- НАЧАЛО СИМУЛЯЦИИ ---")

    // 3. Основной цикл времени
    for (time in 1..simulationTime) {
        println("\n--- Время: $time секунда ---")

        // Заставляем каждого человека ходить
        for (human in humans) {
            human.move()
        }
    }

    println("\n--- КОНЕЦ СИМУЛЯЦИИ ---")

    // Выведем конечное положение всех людей
    println("\nИтоговое положение:")
    humans.forEach {
        println("${it.fullName} остановился в точке (${String.format("%.2f", it.x)}, ${String.format("%.2f", it.y)})")
    }
}