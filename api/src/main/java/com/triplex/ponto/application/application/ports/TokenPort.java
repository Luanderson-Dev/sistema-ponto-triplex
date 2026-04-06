package com.triplex.ponto.application.application.ports;

import com.triplex.ponto.domain.DadosToken;
import com.triplex.ponto.domain.Role;

public interface TokenPort {
    String gerarAccessToken(Long usuarioId, String email, Role role);
    String gerarRefreshToken();
    DadosToken extrairDadosToken(String accessToken);
    boolean validarAccessToken(String accessToken);
}
