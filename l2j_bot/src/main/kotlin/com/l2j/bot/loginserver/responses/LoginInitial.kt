package com.l2j.bot.loginserver.responses

import java.nio.ByteBuffer
import java.util.*
import kotlin.experimental.xor

class LoginInitial(byteBuffer: ByteBuffer) {

    val packageId: Int
    val sessionId: Int
    val protocolRev: Int
    val publicKey: ByteArray
    val ggPart1: Int
    val ggPart2: Int
    val ggPart3: Int
    val ggPart4: Int
    val blowfishKey: ByteArray
    val nullTerminator: Int

    init {
        val first = byteBuffer.get()
        if (first.toInt() != -70) {
            throw IllegalStateException("Wrong first byte $first")
        }
        val second = byteBuffer.get()
        if (second.toInt() != 0) {
            throw IllegalStateException("Wrong second byte $second")
        }
        packageId = byteBuffer.get().toInt()
        sessionId = byteBuffer.int
        protocolRev = byteBuffer.int
        if (protocolRev != 0x0000c621) {
            throw IllegalStateException("Wrong protocol revision $protocolRev")
        }
        publicKey = ByteArray(128)
        byteBuffer.get(publicKey)
        ggPart1 = byteBuffer.int
        ggPart2 = byteBuffer.int
        ggPart3 = byteBuffer.int
        ggPart4 = byteBuffer.int
        blowfishKey = ByteArray(16)
        byteBuffer.get(blowfishKey)
        nullTerminator = byteBuffer.get().toInt()
        unscrambleRsaPubKey()
    }

    fun unscrambleRsaPubKey() {
        var i: Int
        // step 4 xor last 0x40 bytes with first 0x40 bytes
        i = 0
        while (i < 0x40) {
            publicKey[0x40 + i] = (publicKey[0x40 + i] xor publicKey[i])
            i++
        }
        // step 3 xor bytes 0x0d-0x10 with bytes 0x34-0x38
        i = 0
        while (i < 4) {
            publicKey[0x0d + i] = (publicKey[0x0d + i] xor publicKey[0x34 + i])
            i++
        }
        // step 2 xor first 0x40 bytes with last 0x40 bytes
        i = 0
        while (i < 0x40) {
            publicKey[i] = (publicKey[i] xor publicKey[0x40 + i])
            i++
        }
        // step 1
        i = 0
        while (i < 4) {
            val temp = publicKey[i]
            publicKey[i] = publicKey[0x4d + i]
            publicKey[0x4d + i] = temp
            i++
        }
    }

    override fun toString(): String {
        return "LoginInitial(packageId=$packageId, sessionId=$sessionId, protocolRev=$protocolRev, publicKey=${Arrays.toString(publicKey)}, ggPart1=$ggPart1, ggPart2=$ggPart2, ggPart3=$ggPart3, ggPart4=$ggPart4, blowfishKey=${Arrays.toString(blowfishKey)}, nullTerminator=$nullTerminator)"
    }

}