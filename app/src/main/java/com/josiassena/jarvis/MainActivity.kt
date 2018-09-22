package com.josiassena.jarvis

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.google.android.things.pio.I2cDevice
import com.google.android.things.pio.PeripheralManager
import com.josiassena.jarvis.CurrentTime.getCurrentTime
import com.nilhcem.androidthings.driver.lcdpcf8574.LcdPcf8574
import java.io.DataInputStream
import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity() {

    private var device: I2cDevice? = null
    private var socket: Socket? = null
    private var dataInputStream: DataInputStream? = null

    private lateinit var peripheralManager: PeripheralManager
    private lateinit var lcd: LcdPcf8574

    companion object {
        private const val TAG = "MainActivity"
        private const val I2C_DEVICE_NAME = "I2C1"
        private const val I2C_DEVICE_ADDRESS = 0x27

        private const val I2C_LED_COLUMNS = 20
        private const val I2C_LED_ROWS = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        peripheralManager = PeripheralManager.getInstance()

        lcd = LcdPcf8574(I2C_DEVICE_NAME, I2C_DEVICE_ADDRESS)
        lcd.begin(I2C_LED_COLUMNS, I2C_LED_ROWS)
        lcd.setBacklight(true)

        try {
            thread {
                socket = Socket("192.168.1.5", 8888)
                dataInputStream = DataInputStream(socket?.getInputStream())

                val reader = Scanner(dataInputStream)

                while (reader.hasNextLine()) {
                    val message = reader.nextLine()

                    when {
                        message.startsWith("CPU:") -> displayCpuInformation(message)
                        message.startsWith("GPU") -> displayGpuInformation(message)
                        else -> {
                            Log.e(TAG, message)
                        }
                    }
                }
            }
        } catch (ex: UnknownHostException) {
            ex.printStackTrace()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        lcd.setCursor(0, 2)
        lcd.print("PST - ${getCurrentTime("PST")} CET - ${getCurrentTime("CET")}")

        lcd.setCursor(0, 3)
        lcd.print("----0----")

        startScrollingDisplay()
    }

    private fun startScrollingDisplay() {
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                lcd.scrollDisplayLeft()
            }
        }, TimeUnit.SECONDS.toMillis(2), TimeUnit.SECONDS.toMillis(1))
    }

    private fun displayCpuInformation(message: String) {
        lcd.setCursor(0, 0)
        lcd.print("")
        lcd.print(message)
    }

    private fun displayGpuInformation(message: String) {
        lcd.setCursor(0, 1)
        lcd.print("")
        lcd.print(message)
    }

    override fun onDestroy() {
        super.onDestroy()

        lcd.close()

        try {
            device?.close()
            device = null
        } catch (e: IOException) {
            Log.w(TAG, "Unable to close I2C device", e)
        }

        try {
            socket?.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        try {
            dataInputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
