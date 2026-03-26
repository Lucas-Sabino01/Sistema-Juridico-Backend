package com.lucas.projeto.juridico.Controller;

import com.lucas.projeto.juridico.Service.AudienciaService;
import com.lucas.projeto.juridico.Service.GeminiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/audiencias")
public class AudienciaController {

    private final AudienciaService audienciaService;
    private final GeminiService geminiService;

    public AudienciaController(AudienciaService audienciaService, GeminiService geminiService) {
        this.audienciaService = audienciaService;
        this.geminiService = geminiService;
    }

    @PostMapping(value = "/extrair", produces = "application/json")
    public ResponseEntity<?> extrairDadosPdf(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || !file.getContentType().equals("application/pdf")) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Por favor, envie um PDF válido."));
        }

        try {
            String textoBruto = audienciaService.extrairTextoBruto(file);

            String respostaGeminiJson = geminiService.analisarAudiencia(textoBruto);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(respostaGeminiJson);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("erro", "Falha ao processar PDF: " + e.getMessage()));
        }
    }
}