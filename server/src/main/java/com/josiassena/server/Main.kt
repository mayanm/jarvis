package com.josiassena.server

import com.profesorfalken.jsensors.JSensors
import oshi.SystemInfo
import oshi.util.FormatUtil
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author Josias Sena
 */
class Main {

    companion object {

        private const val DEGREE_SYMBOL = 223.toChar()

        private val systemInfo = SystemInfo()

        @JvmStatic
        fun main(args: Array<String>) {
            val server = Server()
            var timer = Timer()

            server.connect(8888, object : Server.OnConnectedListener {

                override fun onConnected(clientHandler: ClientHandler) {
                    try {
                        timer.cancel()
                    } catch (ex: IllegalStateException) {
                        ex.printStackTrace()
                    } finally {
                        timer.purge()
                        timer = Timer()

                        timer.scheduleAtFixedRate(object : TimerTask() {
                            override fun run() {

                                println("\n\n############ Sending system information ############")
                                sendCpuInformation(clientHandler)
                                sendGpuInformation(clientHandler)
                                println("############ System information sent ############\n")
                            }
                        }, 0, TimeUnit.MINUTES.toMillis(1))
                    }
                }
            })
        }

        private fun sendGpuInformation(clientHandler: ClientHandler) {
            val components = JSensors.get.components()

            if (components.gpus.isNotEmpty()) {
                val gpu = components.gpus.first()
                val sensors = gpu.sensors

                val temp = sensors?.temperatures?.first()?.value
                val load = sensors?.loads?.first()?.value

                clientHandler.write("GPU: ${"%.2f".format(load)}% | $temp$DEGREE_SYMBOL")
            } else {
                clientHandler.write("GPU: Information not available")
            }
        }

        private fun sendCpuInformation(clientHandler: ClientHandler) {
            val hardware = systemInfo.hardware

            val totalMemory = hardware.memory.total
            val memoryAvailable = hardware.memory.available

            val percentage = memoryAvailable.toFloat()
                    .div(totalMemory)
                    .times(100)

            val formattedPercentage = "${"%.2f".format(percentage)}%"

            clientHandler.write("CPU: $formattedPercentage | ${hardware.sensors.cpuTemperature}$DEGREE_SYMBOL | ${FormatUtil.formatBytes(memoryAvailable)}")
        }
    }
}