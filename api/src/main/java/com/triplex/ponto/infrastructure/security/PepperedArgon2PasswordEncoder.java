package com.triplex.ponto.infrastructure.security;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PepperedArgon2PasswordEncoder implements PasswordEncoder {
    private final Argon2PasswordEncoder argon2Encoder;
    private final String pepper;

    public PepperedArgon2PasswordEncoder(String pepper) {
        this.pepper = pepper;
        // Argon2id: saltLength=16, hashLength=32, parallelism=1, memory=19456 KiB (~19 MB), iterations=2
        this.argon2Encoder = new Argon2PasswordEncoder(16, 32, 1, 19456, 2);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return argon2Encoder.encode(rawPassword + pepper);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return argon2Encoder.matches(rawPassword + pepper, encodedPassword);
    }
}
