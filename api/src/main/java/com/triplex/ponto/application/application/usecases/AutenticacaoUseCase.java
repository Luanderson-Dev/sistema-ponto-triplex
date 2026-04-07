package com.triplex.ponto.application.application.usecases;

import com.triplex.ponto.domain.RespostaLogin;

public interface AutenticacaoUseCase {
    RespostaLogin loginComDiscord(String code);
    RespostaLogin renovarToken(String refreshToken);
    void logout(String refreshToken);
}
