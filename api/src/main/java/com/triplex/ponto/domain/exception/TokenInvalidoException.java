package com.triplex.ponto.domain.exception;

public class TokenInvalidoException extends RuntimeException {
    public TokenInvalidoException() {
        super("Token inválido ou expirado.");
    }
}
