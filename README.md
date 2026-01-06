# 📊 App de Controle de Vendas

Aplicação **full-stack** desenvolvida para auxiliar no controle e gerenciamento de vendas, metas e clientes, voltada para cenários reais do ambiente comercial.

O sistema foi pensado para resolver problemas comuns enfrentados por equipes de vendas, como acompanhamento de metas, organização de pedidos e gestão de clientes.

---

## 🚀 Funcionalidades
- Cadastro e gerenciamento de clientes
- Registro e acompanhamento de pedidos de vendas
- Controle de metas comerciais
- Visualização de dados para apoio à tomada de decisão
- Integração entre backend e frontend via API REST

---

## 🛠️ Tecnologias Utilizadas
- **Back-end:** Java, Spring Boot
- **Front-end:** HTML, CSS, JavaScript
- **Banco de Dados:** PostgreSQL
- **Arquitetura:** API REST
- **Deploy:** Railway
- **Controle de versão:** Git & GitHub

---

## 🧠 Arquitetura da Aplicação
A aplicação foi construída utilizando uma arquitetura **API REST**, onde:

- O **backend** é responsável pelas regras de negócio, persistência de dados e exposição dos endpoints.
- O **frontend** consome a API de forma desacoplada, permitindo maior flexibilidade e escalabilidade.
- O banco de dados **PostgreSQL** é utilizado para armazenar informações de clientes, pedidos e metas.

Essa separação permite manutenção mais simples, reutilização da API e fácil evolução do sistema.

---

## ☁️ Deploy em Nuvem
A aplicação foi configurada e publicada utilizando a plataforma **Railway**, com serviços separados para:
- Backend
- Frontend
- Banco de dados PostgreSQL

> ℹ️ No momento, os serviços podem estar offline devido ao término do período de testes da plataforma.

---

## ▶️ Como Executar o Projeto Localmente

### Backend
```bash
# Clone o repositório
git clone https://github.com/LuanClemente/app-vendas-fullstack.git

# Acesse a pasta do backend
cd app-vendas-fullstack/backend

# Configure as variáveis de ambiente (banco de dados)
# Execute a aplicação Spring Boot
