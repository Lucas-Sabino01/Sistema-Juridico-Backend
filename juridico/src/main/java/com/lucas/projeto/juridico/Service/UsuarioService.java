package com.lucas.projeto.juridico.Service;

import com.lucas.projeto.juridico.Model.Usuario;
import com.lucas.projeto.juridico.Repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final NotificacaoEmailService emailService;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder, NotificacaoEmailService emailService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // SEMENTE: Cria o primeiro Admin se o banco estiver vazio
    @PostConstruct
    public void init() {
        if (repository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@juridico.com");
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setRole("ROLE_ADMIN");
            admin.setPrimeiroAcesso(false); // Admin master não precisa trocar
            repository.save(admin);
            System.out.println("Usuário Admin Master criado: admin@juridico.com / admin123");
        }
    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Usuario criarUsuario(Usuario novoUsuario) {
        if (repository.existsByEmail(novoUsuario.getEmail())) {
            throw new RuntimeException("Já existe um usuário com este e-mail.");
        }

        // Gera uma senha temporária aleatória de 8 caracteres
        String senhaTemporaria = UUID.randomUUID().toString().substring(0, 8);

        novoUsuario.setSenha(passwordEncoder.encode(senhaTemporaria));
        novoUsuario.setPrimeiroAcesso(true);

        Usuario usuarioSalvo = repository.save(novoUsuario);

        new Thread(() -> {
            emailService.enviarEmailBoasVindas(usuarioSalvo.getEmail(), usuarioSalvo.getNome(), senhaTemporaria);
        }).start();

        return usuarioSalvo;
    }
}