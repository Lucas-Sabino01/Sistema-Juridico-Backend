package com.lucas.projeto.juridico.Service;

import com.lucas.projeto.juridico.Model.Processo;
import com.lucas.projeto.juridico.Repository.ProcessoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacaoEmailService {

    private final ProcessoRepository repository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    @Value("${app.notificacoes.email-destino}")
    private String destinatario;

    public NotificacaoEmailService(ProcessoRepository repository, JavaMailSender mailSender) {
        this.repository = repository;
        this.mailSender = mailSender;
    }

    // O @Scheduled usa a expressão cron: Segundos, Minutos, Horas, Dia do Mês, Mês, Dia da Semana
     @Scheduled(cron = "0 0 8 * * *")
    //@Scheduled(cron = "0 * * * * *")
    public void verificarPrazosEEnviarEmailDiario() {
        System.out.println("A iniciar verificação diária de prazos...");

        List<Processo> processosAtivos = repository.findAll().stream()
                .filter(p -> !"Concluído".equals(p.getStatus()))
                .collect(Collectors.toList());

        LocalDate hoje = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("Bom dia, Doutora!\n");
        relatorio.append("Aqui está o resumo dos prazos que exigem a sua atenção hoje (").append(hoje.format(formatter)).append(").\n\n");

        int atrasadosCount = 0;
        int vencemHojeCount = 0;
        int vencemEmBreveCount = 0; // Próximos 3 dias

        StringBuilder detalhes = new StringBuilder();

        for (Processo p : processosAtivos) {
            if (p.getDataPrazo() == null) continue;

            long diasDiferenca = ChronoUnit.DAYS.between(hoje, p.getDataPrazo());

            if (diasDiferenca < 0) {
                atrasadosCount++;
                detalhes.append("🔴 [ATRASADO ").append(Math.abs(diasDiferenca)).append(" dias] - ")
                        .append(p.getNomeCliente()).append(" (Nº: ").append(p.getNumeroProcesso()).append(")\n");
            } else if (diasDiferenca == 0) {
                vencemHojeCount++;
                detalhes.append("🟠 [VENCE HOJE] - ")
                        .append(p.getNomeCliente()).append(" (Nº: ").append(p.getNumeroProcesso()).append(")\n");
            } else if (diasDiferenca > 0 && diasDiferenca <= 3) {
                vencemEmBreveCount++;
                detalhes.append("🟡 [VENCE EM ").append(diasDiferenca).append(" DIAS] - ")
                        .append(p.getNomeCliente()).append(" (Nº: ").append(p.getNumeroProcesso()).append(")\n");
            }
        }

        if (atrasadosCount > 0 || vencemHojeCount > 0 || vencemEmBreveCount > 0) {
            relatorio.append("⚠️ RESUMO:\n");
            relatorio.append("- Prazos Atrasados: ").append(atrasadosCount).append("\n");
            relatorio.append("- Vencem Hoje: ").append(vencemHojeCount).append("\n");
            relatorio.append("- Vencem nos próximos 3 dias: ").append(vencemEmBreveCount).append("\n\n");

            relatorio.append("📋 LISTA DETALHADA:\n");
            relatorio.append(detalhes.toString());

            relatorio.append("\nBom trabalho!\nJurídicoApp - O seu Assistente Automático.");

            enviarEmail("🚨 Alerta de Prazos do Escritório - " + hoje.format(formatter), relatorio.toString());
            System.out.println("E-mail diário de prazos enviado com sucesso!");
        } else {
            System.out.println("Nenhum prazo crítico hoje. E-mail não enviado.");
        }
    }

    private void enviarEmail(String assunto, String texto) {
        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom(remetente);
            mensagem.setTo(destinatario);
            mensagem.setSubject(assunto);
            mensagem.setText(texto);

            mailSender.send(mensagem);
        } catch (Exception e) {
            System.err.println("Falha ao enviar e-mail: " + e.getMessage());
        }
    }

    public void enviarEmailBoasVindas(String email, String nome, String senhaProvisoria) {
        String assunto = "Bem-vindo ao Sistema Jurídico - Sua Senha de Acesso";
        String texto = "Olá, " + nome + "!\n\n" +
                "A sua conta foi criada com sucesso.\n" +
                "Aqui estão as suas credenciais de acesso:\n\n" +
                "Email: " + email + "\n" +
                "Senha: " + senhaProvisoria + "\n\n" +
                "Por favor, altere sua senha após o primeiro login.\n\n" +
                "Atenciosamente,\nEquipe Jurídico";

        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom(remetente);
            mensagem.setTo(email);
            mensagem.setSubject(assunto);
            mensagem.setText(texto);

            mailSender.send(mensagem);
        } catch (Exception e) {
            System.err.println("Falha ao enviar e-mail de boas-vindas: " + e.getMessage());
        }
    }
}