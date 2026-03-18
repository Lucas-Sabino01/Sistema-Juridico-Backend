package com.lucas.projeto.juridico.Security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Component
@Converter
public class CryptoConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES";
    private static byte[] KEY;

    @SuppressWarnings("unused")
    @Value("${APP_SECURITY_ENCRYPTION_KEY}")
    public void setKey(String key) {
        CryptoConverter.KEY = key.getBytes();
    }

    @Override
    public String convertToDatabaseColumn(String dadoOriginal) {
        if (dadoOriginal == null || dadoOriginal.trim().isEmpty()) {
            return dadoOriginal;
        }
        try {
            Key key = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] criptografado = cipher.doFinal(dadoOriginal.getBytes());
            return Base64.getEncoder().encodeToString(criptografado);
        } catch (Exception e) {
            System.err.println("ERRO AES: O tamanho da sua APP_SECURITY_ENCRYPTION_KEY é válido? Deve ter 16, 24 ou 32 caracteres.");
            throw new RuntimeException("Erro ao criptografar dado", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dadoCriptografado) {
        if (dadoCriptografado == null || dadoCriptografado.trim().isEmpty()) {
            return dadoCriptografado;
        }
        try {
            Key key = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] descriptografado = cipher.doFinal(Base64.getDecoder().decode(dadoCriptografado));
            return new String(descriptografado);
        } catch (Exception e) {
            return dadoCriptografado;
        }
    }
}