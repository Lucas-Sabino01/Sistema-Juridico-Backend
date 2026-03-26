package com.lucas.projeto.juridico.Service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AudienciaService {

    public String extrairTextoBruto(MultipartFile arquivo) {
        try (PDDocument documento = Loader.loadPDF(arquivo.getBytes())) {
            PDFTextStripper extratorTexto = new PDFTextStripper();
            return extratorTexto.getText(documento);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o PDF: " + e.getMessage());
        }
    }

    public String encontrarAutos(String texto) {
        Pattern padrao = Pattern.compile("\\d{7}-\\d{2}\\.\\d{4}\\.\\d{1,2}\\.\\d{2}\\.\\d{4}");
        Matcher matcher = padrao.matcher(texto);

        if (matcher.find()) {
            return matcher.group();
        }
        return "Autos não encontrados";
    }
}