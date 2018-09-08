package com.l2j.bot.loginserver

import java.net.Socket

class LoginServerClient(val host: String, val port: Int) {

    private var socket: Socket? = null

    fun connect() {
        socket = Socket(host, port)
    }

    fun disconnect() {
        if (socket?.isConnected == true) {
            socket?.close()
        }
    }

}