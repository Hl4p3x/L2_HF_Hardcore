package com.l2j.bot.loginserver.requests

import com.l2jserver.util.network.BaseSendablePacket
import java.io.IOException

data class AuthGg(
        val sessionId: Int, val ggPart1: Int, val ggPart2: Int, val ggPart3: Int, val ggPart4: Int
) : BaseSendablePacket() {

    init {
        writeC(0x07)
        writeD(sessionId)
        writeD(ggPart1)
        writeD(ggPart2)
        writeD(ggPart3)
        writeD(ggPart4)
    }

    @Throws(IOException::class)
    override fun getContent(): ByteArray {
        return bytes
    }

}