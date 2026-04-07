package com.triplex.ponto.infrastructure.config;

import com.triplex.ponto.application.application.ports.UsuarioRepositoryPort;
import com.triplex.ponto.domain.Role;
import com.triplex.ponto.domain.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.nome:#{null}}")
    private String adminNome;

    @Value("${app.admin.email:#{null}}")
    private String adminEmail;

    @Value("${app.admin.senha:#{null}}")
    private String adminSenha;

    public AdminSeeder(UsuarioRepositoryPort usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (adminEmail == null || adminSenha == null) {
            log.info("Admin seed não configurado (ADMIN_EMAIL/ADMIN_SENHA ausentes). Pulando.");
            return;
        }

        if (usuarioRepository.buscarPorEmail(adminEmail).isPresent()) {
            log.info("Admin '{}' já existe. Pulando seed.", adminEmail);
            return;
        }

        String nome = adminNome != null ? adminNome : "Administrador";
        Usuario admin = new Usuario(null, nome, adminEmail, passwordEncoder.encode(adminSenha), Role.RH);
        usuarioRepository.salvar(admin);
        log.info("Admin '{}' criado com sucesso.", adminEmail);
    }
}
