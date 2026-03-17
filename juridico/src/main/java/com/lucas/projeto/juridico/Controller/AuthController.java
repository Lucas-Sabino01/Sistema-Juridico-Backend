package com.lucas.projeto.juridico.Controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    @Value("${app.jwt.secret:chaveSecretaMuitoLongaParaOProjetoJuridicoQueTemPeloMenos32Bytes!}")
    private String jwtSecret;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciais) {
        try {
            // Agora espera "email" e "senha" do frontend
            String email = credenciais.get("email");
            String senha = credenciais.get("senha");

            // O AuthenticationManager recebe um token com (principal, credentials)
            // No seu caso, o principal é o email
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, senha)
            );

            if (authentication.isAuthenticated()) {
                // Cria a chave fixa a partir da string do properties
                Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

                // Gera o token JWT usando o email como subject
                String token = Jwts.builder()
                        .setSubject(email)
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 12 * 60 * 60 * 1000))
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();

                ResponseCookie cookie = ResponseCookie.from("auth_token", token)
                        .httpOnly(true)
                        .secure(false) // Mude para true em produção com HTTPS
                        .path("/")
                        .maxAge(12 * 60 * 60)
                        .sameSite("Lax")
                        .build();

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(Map.of("mensagem", "Login efetuado com sucesso!"));
            }

            return ResponseEntity.status(401).body(Map.of("erro", "Falha na autenticação."));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("erro", "Utilizador ou senha inválidos."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from("auth_token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}