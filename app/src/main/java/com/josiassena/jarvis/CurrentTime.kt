package com.josiassena.jarvis

import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Josias Sena
 */
object CurrentTime {

    fun getCurrentTime(timeZone: String): String {
        // create SimpleDateFormat object with input format
        val sdf = SimpleDateFormat("hh:mm")
        sdf.timeZone = TimeZone.getTimeZone(timeZone.toUpperCase())

        return sdf.format(Calendar.getInstance().time)
    }

}