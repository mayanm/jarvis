package com.josiassena.server

import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset

/**
 * @author Josias Sena
 */
class ClientHandler(private val client: Socket) {
    //    private val reader: Scanner = Scanner(client.getInputStream())
    private val writer: OutputStream = client.getOutputStream()
    private var running: Boolean = false

    fun run() {
        running = true

        write("Welcome to the server!")
//        write("To Exit, write: 'EXIT'.")

//        while (running) {
//            try {
//                val text = reader.nextLine()
//
//                if (text == "EXIT") {
//                    shutdown()
//                    continue
//                }
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//                shutdown()
//            }
//        }
    }

    fun write(message: String) {
        if (running) {
            writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
            writer.flush()
        } else {
            println("Not running. Message cannot be sent.")
        }
    }

    fun shutdown() {
        running = false

        client.close()

        println("${client.inetAddress.hostAddress} closed the connection")
    }

}