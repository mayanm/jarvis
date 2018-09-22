package com.josiassena.jarvis

/**
 * @author Josias Sena
 */
object Utils {

    fun getDegreeSymbol() = 223.toChar()

    fun celsiusToFahrenheit(celsiusTemp: Float): Float {
        return celsiusTemp.times(9/5).plus(32)
    }

}