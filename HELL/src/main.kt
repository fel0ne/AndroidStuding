import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
class Human(t_name: String, t_age: Int, t_current_speed: Int){
    var name: String = t_name
    var age: Int = t_age
    var current_speed = t_current_speed
    var x: Double = 0.0
    var y: Double = 0.0
    fun move(){
        val r: Double = Random.nextDouble(0.0,2*Math.PI)
        x += current_speed * sin(r)
        y += current_speed * cos(r)
        println("$name перешел в ($x ;$y)")
    }
}

fun main(){
    val time: Int = 10
    val people = arrayOfNulls<Human>(7)

    for (i in 0..6){
        val newHuman = Human(t_name = "человек номер ${i+1}", t_age = Random.nextInt(1,65), t_current_speed = Random.nextInt(1,5))
        people[i] = newHuman
    }
    println("созданные люди:")
    for (i in 0..6){
        println("${i+1}. ${people[i]?.name}, возраст: ${people[i]?.age}, скорость: ${people[i]?.current_speed}")

    }

    println("-----Запуск симуляции-----\n")
    for (i in 1..time) {
        for (i in 0..6) {
            people[i]?.move()
        }
    }

    for (i in 0..6) {
        println("${i+1}. ${people[i]?.name}, конечная точка: (${people[i]?.x}, ${people[i]?.x})")
    }



}