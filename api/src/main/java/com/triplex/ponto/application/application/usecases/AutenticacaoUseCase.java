package com.triplex.ponto.application.application.usecases;

import com.triplex.ponto.domain.RespostaLogin;

public interface AutenticacaoUseCase {
    RespostaLogin login(String email, String senha);
    RespostaLogin renovarToken(String refreshToken);
    void logout(String refreshToken);
}
