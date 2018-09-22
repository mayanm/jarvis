package com.josiassena.jarvis

import android.content.Context
import com.josiassena.jarvis.Utils.celsiusToFahrenheit
import com.josiassena.jarvis.Utils.getDegreeSymbol
import java.io.IOException


/**
 * @author Josias Sena
 */
class CpuInformation(private val context: Context) {

    companion object {
        private const val TAG = "CpuInformation"
//        private const val CPU_TEMPERATURE_COMMAND = "acpi -t"
        private const val CPU_TEMPERATURE_COMMAND = "cat sys/class/thermal/thermal_zone0/temp"
        private const val AVAILABLE_MEMORY_COMMAND = "free -m  | grep ^Mem | tr -s ' ' | cut -d ' ' -f 4"
        private const val TOTAL_CPU_COMMAND = "dumpsys cpuinfo | grep -o '.*TOTAL:'"

        private const val GPU_TEMPERATURE_COMMAND = "cat sys/class/thermal/thermal_zone0/temp"
    }

    fun getCpuInformation(): String {
        return "CPU: ${getTotalCpuUsage()} | ${getCpuTemperature()} | ${getAvailableCpu()}"
    }

    private fun getAvailableCpu(): String {
        var result = ""

        try {
            val commands = arrayOf("sh", "-c", AVAILABLE_MEMORY_COMMAND)
            val process = Runtime.getRuntime().exec(commands)

            val processInputStream = process.inputStream

            result = processInputStream.bufferedReader().use { it.readText() }

            processInputStream.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        val resultInMb = result.trim().toInt()

        return "${resultInMb.times(0.001)}GB"
    }

    private fun getTotalCpuUsage(): String {
        var result = ""

        try {
            val cpuInfoCommand = arrayOf("sh", "-c", TOTAL_CPU_COMMAND)
            val process = Runtime.getRuntime().exec(cpuInfoCommand)

            val processInputStream = process.inputStream

            result = processInputStream.bufferedReader().use { it.readText() }

            processInputStream.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }


        return result.trim()
                .replace("TOTAL:", "")
                .replace(" ", "")
    }

    private fun getCpuTemperature(): String {
        var temperature = "0"

        try {
            val process = Runtime.getRuntime().exec(CPU_TEMPERATURE_COMMAND)

            val processInputStream = process.inputStream

            temperature = process.inputStream.bufferedReader()
                    .use { it.readText() }
                    .replace("\n", "")

            processInputStream.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        val readableTemp = temperature.toLong().div(1000.toFloat())
        val tempInFahrenheit = "%.2f".format(celsiusToFahrenheit(readableTemp))

        return "$tempInFahrenheit${getDegreeSymbol()}F"
    }
}