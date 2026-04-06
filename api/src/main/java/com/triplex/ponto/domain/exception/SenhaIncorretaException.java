package com.triplex.ponto.domain.exception;

public class SenhaIncorretaException extends RuntimeException {
    public SenhaIncorretaException() {
        super("Senha atual incorreta.");
    }
}
