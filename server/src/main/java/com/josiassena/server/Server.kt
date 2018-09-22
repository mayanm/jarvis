package com.josiassena.server

import java.net.ServerSocket
import kotlin.concurrent.thread

/**
 * @author Josias Sena
 */
class Server {

    fun connect(port: Int, onConnectedListener: OnConnectedListener) {
        val server = ServerSocket(port)
        println("Server is running on port ${server.localPort}")

        while (true) {
            val client = server.accept()

            println("Client connected: ${client.inetAddress.hostAddress}")

            thread {
                val clientHandler = ClientHandler(client)
                clientHandler.run()

                onConnectedListener.onConnected(clientHandler)
            }
        }
    }

    interface OnConnectedListener {
        fun onConnected(clientHandler: ClientHandler)
    }

}

