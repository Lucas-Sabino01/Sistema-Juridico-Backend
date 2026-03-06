package com.lucas.projeto.juridico.Service;

import com.lucas.projeto.juridico.Model.Processo;
import com.lucas.projeto.juridico.Repository.ProcessoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class RelatorioExcelService {

    private final ProcessoRepository repository;

    public RelatorioExcelService(ProcessoRepository repository) {
        this.repository = repository;
    }

    public byte[] gerarPlanilhaProcessos() {
        List<Processo> processos = repository.findAll();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Processos Ativos e Arquivados");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            String[] colunas = {"ID", "Nº do Processo", "Nome do Cliente", "Fase", "Prazo", "Honorários", "CPF", "Senha Gov.br", "Status (Arquivo)"};

            for (int i = 0; i < colunas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(colunas[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Processo p : processos) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(p.getId() != null ? p.getId() : 0);
                row.createCell(1).setCellValue(p.getNumeroProcesso() != null ? p.getNumeroProcesso() : "");
                row.createCell(2).setCellValue(p.getNomeCliente() != null ? p.getNomeCliente() : "");
                row.createCell(3).setCellValue(p.getStatus() != null ? p.getStatus() : "");
                row.createCell(4).setCellValue(p.getDataPrazo() != null ? p.getDataPrazo().toString() : "");
                row.createCell(5).setCellValue(p.getHonorarios() != null ? p.getHonorarios() : "");
                row.createCell(6).setCellValue(p.getCpfCliente() != null ? p.getCpfCliente() : "");
                row.createCell(7).setCellValue(p.getSenhaGov() != null ? p.getSenhaGov() : "");
                row.createCell(8).setCellValue(p.isArquivado() ? "ARQUIVADO" : "ATIVO");
            }

            for (int i = 0; i < colunas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar arquivo Excel", e);
        }
    }
}