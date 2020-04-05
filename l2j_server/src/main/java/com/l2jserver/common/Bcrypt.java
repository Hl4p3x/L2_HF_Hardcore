package com.l2jserver.common;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class Bcrypt {

    private static final BCrypt.Hasher HASHER = BCrypt.withDefaults();
    private static final BCrypt.Verifyer VERIFIER = BCrypt.verifyer();

    private static final Integer COMPLEXITY = 14;

    public static String hash(String value) {
        return HASHER.hashToString(COMPLEXITY, value.toCharArray());
    }

    public static boolean valid(String hash, String salt) {
        return VERIFIER.verify(hash.toCharArray(), salt).verified;
    }

}
