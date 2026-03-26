package com.lucas.projeto.juridico.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${CHAVE_SECRETA_GEMINI}")
    private String geminiApiKey;

    private final String[] modelosDisponiveis = {
            "gemini-2.0-flash-lite",
            "gemini-flash-lite-latest",
            "gemini-2.0-flash",
            "gemini-flash-latest",
            "gemini-2.5-flash"
    };

    public String analisarAudiencia(String textoPdf) {
        for (String modelo : modelosDisponiveis) {
            try {
                System.out.println("🤖 Analisando com lógica refinada no modelo: " + modelo);
                return realizarChamadaIA(textoPdf, modelo);
            } catch (HttpClientErrorException.TooManyRequests e) {
                System.err.println("⚠️ Limite atingido para " + modelo + ". Pulando...");
                continue;
            } catch (Exception e) {
                System.err.println("❌ Erro no modelo " + modelo + ": " + e.getMessage());
                continue;
            }
        }

        return "{\"data\": \"Erro\", \"horario\": \"Erro\", \"termino\": \"Erro\", \"duracao\": \"Erro\", \"autos\": \"Erro\", \"area\": \"Erro\", \"resultado\": \"Limite Excedido\", \"encaminhamento\": \"Erro\", \"confianca\": 0}";
    }

    private String realizarChamadaIA(String textoPdf, String nomeModelo) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + nomeModelo + ":generateContent?key=" + geminiApiKey;

        String prompt = "Você é um assistente jurídico sênior especializado em triagem do Projudi.\n" +
                "Analise o Termo de Audiência e extraia os dados seguindo estas REGRAS DE OURO:\n\n" +

                "1. CLASSIFICAÇÃO DE ÁREA:\n" +
                "   - FAMÍLIA: Se houver palavras como 'Alimentos', 'Guarda', 'Visitas', 'Divórcio', 'União Estável', 'Parental' ou se as duas partes forem pessoas físicas com nomes parecidos.\n" +
                "   - FAZENDA PÚBLICA: Se uma das partes for 'Município', 'Estado', 'IPMC', 'Autarquia' ou 'COHAB'.\n" +
                "   - CÍVEL: Casos de 'Indenização', 'Danos Morais', 'Cobrança', 'Consumidor' ou se houver Empresas/Bancos (Ltda, S/A, Banco) no polo passivo.\n\n" +

                "2. RESULTADO E ENCAMINHAMENTO:\n" +
                "   - Se houve acordo (total ou parcial): RESULTADO='Com Acordo', ENCAMINHAMENTO='Homologação'.\n" +
                "   - Se NÃO houve acordo e o juiz abriu prazo para o autor falar: RESULTADO='Sem Acordo', ENCAMINHAMENTO='Impugnação a Contestação'.\n" +
                "   - Se a audiência NÃO ocorreu (falta de parte, pedido de adiamento): RESULTADO='Negativa', ENCAMINHAMENTO='Redesignada'.\n" +
                "   - Se o juiz decidiu mandar o processo de volta: RESULTADO='Negativa', ENCAMINHAMENTO='Devolvido para Vara de Origem'.\n" +
                "   - Se as partes pediram tempo para negociar fora: RESULTADO='Suspensão de Prazo', ENCAMINHAMENTO='Aguardando Prazo'.\n\n" +

                "Retorne APENAS um JSON puro com estas 9 chaves:\n" +
                "\"data\" (DD/MM/AAAA), \"horario\" (Início HH:MM:SS), \"termino\" (Fim HH:MM:SS), \"duracao\" (HH:MM:SS), \"autos\", \"area\", \"resultado\", \"encaminhamento\", \"confianca\" (0-100).\n\n" +
                "TEXTO DO PDF:\n" + textoPdf;

        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(content));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        String response = restTemplate.postForObject(url, request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);
        String respostaIA = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

        return respostaIA.replace("```json\n", "").replace("```json", "").replace("\n```", "").replace("```", "").trim();
    }
}