import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
class Human(t_name: String , t_age: Int , t_current_speed: Int ){
    private var name: String = t_name
    private var age: Int = t_age
    private var current_speed = t_current_speed
    private var x: Double = 0.0
    private var y: Double = 0.0

    fun move(){
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

fun main(){
    val time: Int = 10
    val people = arrayOfNulls<Human>(7)

    for (i in 0..6){
        // Инициализация через конструктор
        val newHuman = Human(
            "человек $i",
            Random.nextInt(18, 80), // Возраст от 18 до 79
            Random.nextInt(1, 10)   // Скорость от 1 до 9
        )
        people[i] = newHuman
    }

    println("созданные люди:")
    for (i in 0..6){
        println("${i+1}. ${people[i]?.getName()}, возраст: ${people[i]?.getAge()}, скорость: ${people[i]?.getCurrentSpeed()}")

    }

    println("-----Запуск симуляции-----\n")
    for (i in 1..time) {
        for (i in 0..6) {
            people[i]?.move()
        }
    }

    for (i in 0..6) {
        println("${i+1}. ${people[i]?.getName()}, конечная точка: (${people[i]?.getX()}, ${people[i]?.getY()})")
    }



}