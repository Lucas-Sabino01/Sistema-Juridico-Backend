# ⚙️ Sistema Jurídico Integrado (SaaS) - Backend

Este repositório contém o "motor" de processamento do Sistema Jurídico, uma API REST robusta desenvolvida em **Java** e **Spring Boot**. O backend foi projetado sob os princípios de **Clean Architecture** e **SOLID**, garantindo escalabilidade e facilidade de manutenção.

## 🛠️ Stack Tecnológica

* **Linguagem:** Java 21.
* **Framework:** Spring Boot 3.x (Spring Security, Spring Data JPA).
* **Base de Dados:** PostgreSQL (Hospedado via Neon DB).
* **Autenticação:** Stateless com JWT (JSON Web Token).
* **IA & Processamento:** Google Gemini API (Multi-model Fallback).
* **Leitura de Documentos:** Apache PDFBox para extração de texto bruto de PDFs jurídicos.

## 🧠 Core Intelligence: O Motor de Extração

A principal inovação deste backend é o serviço de extração semântica de dados, desenhado para ser resiliente a falhas de API externas:

* **Lógica de Fallback Automático:** O sistema possui uma lista prioritária de modelos de IA (Gemini 2.0 Flash, 2.5 Flash-Lite, etc.). Se um modelo atinge o limite de quota (Erro 429), o serviço "salta" automaticamente para o próximo modelo disponível, garantindo que o processamento em lote nunca pare.
* **Regras de Negócio Jurídicas:** O prompt enviado à IA contém "Regras de Ouro" que permitem distinguir áreas do direito (Cível, Família, Fazenda Pública) com base no vocabulário técnico e nas partes envolvidas (ex: identificação de autarquias ou termos como 'alimentos').
* **Cálculos Automatizados:** O servidor processa os horários de início e término extraídos para calcular a duração exata das audiências em tempo real.

## 🛡️ Segurança e Resiliência

* **Gestão de Segredos:** Todas as chaves de API e credenciais de banco de dados são geridas via variáveis de ambiente, nunca expostas no código-fonte.
* **Monitoramento de Health Check:** Inclui um endpoint exclusivo (`/api/health`) utilizado por serviços de monitoramento externo (UptimeRobot) para mitigar o *cold start* do Render e garantir que a API esteja sempre pronta para uso imediato.
* **CORS & Proteção:** Configuração granular para permitir apenas requisições de domínios confiáveis (como o frontend na Vercel).
