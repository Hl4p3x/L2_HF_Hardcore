package com.l2jserver.loginserver.network;

import com.l2jserver.util.crypt.LoginCrypt;
import com.l2jserver.util.network.BaseRecievePacket;
import com.l2jserver.util.network.BaseSendablePacket;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
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


        public LoginInit(byte[] data) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
            byteBuffer.get();
            byteBuffer.get();
            packageId = byteBuffer.get();
            sessionId = byteBuffer.getInt();
            protocolRev = byteBuffer.getInt();
            publicKey = new byte[90];
            byteBuffer.get(publicKey);

            ggPart1 = byteBuffer.getInt();
            ggPart2 = byteBuffer.getInt();
            ggPart3 = byteBuffer.getInt();
            ggPart4 = byteBuffer.getInt();

            blowfishKey = new byte[16];
            byteBuffer.get(blowfishKey);
            nullTerminator = byteBuffer.get();
        }

        @Override
        public String toString() {
            return "LoginInit{" +
                    "packageId=" + packageId +
                    ", sessionId=" + sessionId +
                    ", protocolRev=" + protocolRev +
                    "\npublicKey=" + Arrays.toString(publicKey) + "\n" +
                    "ggPart1=" + ggPart1 +
                    ", ggPart2=" + ggPart2 +
                    ", ggPart3=" + ggPart3 +
                    ", ggPart4=" + ggPart4 +
                    "\nblowfishKey=" + Arrays.toString(blowfishKey) + "\n" +
                    "nullTerminator=" + nullTerminator +
                    '}';
        }

    }

    static class ReceivePublicKey extends BaseRecievePacket {

        int padding;
        int protocol;
        int publicKeyLength;
        byte[] publicKey;

        public ReceivePublicKey(byte[] decrypt) {
            super(decrypt);
            padding = readC();
            protocol = readD();
            publicKeyLength = readD();
            publicKey = readB(publicKeyLength);
        }

        public int getPadding() {
            return padding;
        }

        public int getProtocol() {
            return protocol;
        }

        public int getPublicKeyLength() {
            return publicKeyLength;
        }

        public byte[] getPublicKey() {
            return publicKey;
        }

        @Override
        public String toString() {
            return "ReceivePublicKey{" +
                    "padding=" + padding +
                    ", protocol=" + Integer.toHexString(protocol) +
                    ", publicKeyLength=" + publicKeyLength +
                    ", publicKey=" + Arrays.toString(publicKey) +
                    '}';
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

        BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
        InputStream in = socket.getInputStream();

        if (socket.isClosed()) {
            System.out.println("Socket is terminated.");
            return;
        }

        byte[] frame = new byte[187];
        in.read(frame);
        LoginCrypt loginCrypt = new LoginCrypt();
        loginCrypt.setKey(STATIC_BLOWFISH_KEY);

        System.out.println("Raw: " + Arrays.toString(frame));

        boolean checksumValid = loginCrypt.decrypt(frame, 2, 184);

        System.out.println("Decrypted: " + Arrays.toString(frame));

        if (!checksumValid) {
            System.out.println("Checksum is invalid");
        }

        System.out.println("Received package of size " + frame.length);

        LoginInit loginInit = new LoginInit(frame);
        System.out.println("Echo: " + loginInit);


        AuthGg authGg = new AuthGg(loginInit.sessionId, loginInit.ggPart1, loginInit.ggPart2, loginInit.ggPart3, loginInit.ggPart4);
        out.write(authGg.getContent());



/*
        while ((echo = in.read()) != null) {

            byte[] response = echo.getBytes();

            // blowfishKey.decrypt(response, 0, response.length);

            try {
                System.out.println("echo raw: " + new String(response));
                //System.out.println("echo package: " + new ReceivePublicKey(response));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

}
