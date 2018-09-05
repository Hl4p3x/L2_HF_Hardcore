package com.l2jserver.loginserver.network;

import com.l2jserver.util.crypt.LoginCrypt;
import com.l2jserver.util.crypt.NewCrypt;
import com.l2jserver.util.network.BaseSendablePacket;

import javax.crypto.Cipher;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;

public class L2PseudoClient {

    static class AuthGg extends BaseSendablePacket {

        public AuthGg(int sessionId, int ggPart1, int ggPart2, int ggPart3, int ggPart4) {
            writeC(0x07);
            writeD(sessionId);
            writeD(ggPart1);
            writeD(ggPart2);
            writeD(ggPart3);
            writeD(ggPart4);
        }

        @Override
        public byte[] getContent() throws IOException {
            return getBytes();
        }

    }

    static class LoginInit {

        int packageId;
        int sessionId;
        int protocolRev;
        byte[] publicKey;
        int ggPart1;
        int ggPart2;
        int ggPart3;
        int ggPart4;
        byte[] blowfishKey;
        int nullTerminator;

        public LoginInit(ByteBuffer byteBuffer) {
            // Skip two bytes
            byte first = byteBuffer.get();
            if (first != -70) {
                throw new IllegalStateException("Wrong first byte " + first);
            }
            byte second = byteBuffer.get();
            if (second != 0) {
                throw new IllegalStateException("Wrong second byte " + second);
            }

            packageId = byteBuffer.get();
            sessionId = byteBuffer.getInt();

            protocolRev = byteBuffer.getInt();
            if (protocolRev != 0x0000c621) {
                throw new IllegalStateException("Wrong protocol revision " + protocolRev);
            }

            publicKey = new byte[128];
            byteBuffer.get(publicKey);

            ggPart1 = byteBuffer.getInt();
            ggPart2 = byteBuffer.getInt();
            ggPart3 = byteBuffer.getInt();
            ggPart4 = byteBuffer.getInt();

            blowfishKey = new byte[16];
            byteBuffer.get(blowfishKey);
            nullTerminator = byteBuffer.get();

            unscrambleRsaPubKey();
        }

        public void unscrambleRsaPubKey() {
            int i;
            // step 4 xor last 0x40 bytes with first 0x40 bytes
            for (i = 0; i < 0x40; i++) {
                publicKey[0x40 + i] = (byte) (publicKey[0x40 + i] ^ publicKey[i]);
            }
            // step 3 xor bytes 0x0d-0x10 with bytes 0x34-0x38
            for (i = 0; i < 4; i++) {
                publicKey[0x0d + i] = (byte) (publicKey[0x0d + i] ^ publicKey[0x34 + i]);
            }
            // step 2 xor first 0x40 bytes with last 0x40 bytes
            for (i = 0; i < 0x40; i++) {
                publicKey[i] = (byte) (publicKey[i] ^ publicKey[0x40 + i]);
            }
            // step 1
            for (i = 0; i < 4; i++) {
                byte temp = publicKey[i];
                publicKey[i] = publicKey[0x4d + i];
                publicKey[0x4d + i] = temp;
            }
        }

        @Override
        public String toString() {
            return "LoginInit {\n" +
                    "\tpackageId=" + packageId +
                    ", sessionId=" + sessionId +
                    ", protocolRev=" + protocolRev +
                    "\n\tpublicKey=" + Arrays.toString(publicKey) + "\n" +
                    "\tggPart1=" + ggPart1 +
                    ", ggPart2=" + ggPart2 +
                    ", ggPart3=" + ggPart3 +
                    ", ggPart4=" + ggPart4 +
                    "\n\tblowfishKey=" + Arrays.toString(blowfishKey) + "\n" +
                    "\tnullTerminator=" + nullTerminator +
                    "\n}";
        }

    }

    private static final byte[] STATIC_BLOWFISH_KEY =
            {
                    (byte) 0x6b,
                    (byte) 0x60,
                    (byte) 0xcb,
                    (byte) 0x5b,
                    (byte) 0x82,
                    (byte) 0xce,
                    (byte) 0x90,
                    (byte) 0xb1,
                    (byte) 0xcc,
                    (byte) 0x2b,
                    (byte) 0x6c,
                    (byte) 0x55,
                    (byte) 0x6c,
                    (byte) 0x6c,
                    (byte) 0x6c,
                    (byte) 0x6c
            };

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("127.0.0.1", 2106);
        InputStream in = socket.getInputStream();

        if (socket.isClosed()) {
            System.out.println("Socket is terminated.");
            return;
        }

        byte[] frame = new byte[186];
        in.read(frame);

        LoginCrypt loginCrypt = new LoginCrypt();
        loginCrypt.setKey(STATIC_BLOWFISH_KEY);

        System.out.println("Raw: " + Arrays.toString(frame));

        boolean checksumValid = loginCrypt.decrypt(frame, 2, 184);

        System.out.println("Decrypted: " + Arrays.toString(frame) + " len " + frame.length);

        System.out.println("Expected rev bytes " + Arrays.toString(ByteBuffer.allocate(20).putInt(0x0000c621).array()));


        byte[] xorKeyBytes = new byte[]{frame[178], frame[179], frame[180], frame[181]};

        int xorKey = ByteBuffer.wrap(xorKeyBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();

        System.out.println("XOR key bytes " + Arrays.toString(xorKeyBytes));
        NewCrypt.decXORPass(frame, 2, 184, xorKey);

        ByteBuffer byteBuffer = ByteBuffer.wrap(frame).order(ByteOrder.LITTLE_ENDIAN);

        System.out.println("UNXORED Little Indian: " + Arrays.toString(byteBuffer.array()));

        System.out.println("Received package of size " + frame.length);

        LoginInit loginInit = new LoginInit(byteBuffer);
        loginCrypt.setKey(loginInit.blowfishKey);
        System.out.println("Echo: " + loginInit);

        // Skip static
        loginCrypt.encrypt(new byte[34], 0, 22);

        AuthGg authGg = new AuthGg(loginInit.sessionId, loginInit.ggPart1, loginInit.ggPart2, loginInit.ggPart3, loginInit.ggPart4);
        sendPackage(socket, loginCrypt, authGg);

        PublicKey publicKey =
                KeyFactory.getInstance("RSA").generatePublic(
                        new RSAPublicKeySpec(
                                new BigInteger(loginInit.publicKey),
                                new BigInteger("65537")
                        )
                );

        final Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        decrypted = rsaCipher.doFinal(_raw, 0x00, 0x80);

    }

    public static void sendPackage(Socket socket, LoginCrypt loginCrypt, BaseSendablePacket sendablePacket) throws IOException {
        ByteBuffer authGgBuffer = ByteBuffer.wrap(sendablePacket.getContent());
        System.out.println("Sending package: " + Arrays.toString(authGgBuffer.array()));

        System.out.println("Sending package actual size " + sendablePacket.getLength());
        loginCrypt.encrypt(authGgBuffer.array(), 0, sendablePacket.getLength() - 8);

        socket.getOutputStream().write(sendablePacket.getLength() & 0xff);
        socket.getOutputStream().write((sendablePacket.getLength() >> 8) & 0xff);
        socket.getOutputStream().write(authGgBuffer.array());
        socket.getOutputStream().flush();
    }

}
