#  API  de Cadastro para Cliente & Contato 
## Descrição
API RESTful para gerenciamento de **clientes** e seus **contatos**. Permitindo realizar operações CRUD (criação, leitura, atualização e remoção) de clientes com foco em boas práticas, segurança, observailidade e observabilidade e manutenibilidade.

---
## 📝 Funcionalidades

- CRUD de clientes com Soft Delete, paginação e filtros por nome e email
- CRUD de contatos com vínculo obrigatório a cliente e listagem paginada
- Autenticação via JWT com endpoints protegidos por Spring Security e controle por roles (RBAC)
- Padronização de respostas da API com wrapper (success, data, errors, meta)
- Logs estruturados em JSON, métricas com Actuator, integração com Prometheus e dashboards no Grafana
- Auditoria automática com createdAt, updatedAt, createdBy e deletedAt
- Controle de acesso e rastreabilidade de operações sensíveis
- CORS configurável por ambiente (origins, headers, métodos)
- Suporte a profiles dev, test e prod com configurações isoladas
- Migração de banco versionada com Flyway
- Testes unitários e de integração com JUnit 5 e Mockito
- Documentação da API com Swagger/OpenAPI
- Containerização com Docker e docker-compose
- Uso de MapStruct para mapeamento entre DTOs e entidades
- Lombok para redução de boilerplate

---
## 🛠️  Tecnologias

- Java 21 + Spring Boot
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL / H2
- Flyway
- MapStruct
- Jacoco
- Lombok
- Actuator
- Micrometer + Prometheus
- Grafana
- Swagger / OpenAPI
- Docker
- Maven

---

## 📦 Requisitos

- Java 21
- Maven
- PostgreSQL
- Docker(opcional)

---

# ▶️ Como Executar
## Executando o Projeto (Sem docker)

1. Clone o repositório:

```bash
git hhttps://github.com/bispobr/Java-Spring-cadastroCliente.git
```

2. Configure o arquivo  **.env** com as credenciais do seu PostgreSQL.

3. Execute a aplicação:

```bash
mvn spring-boot:run
```
---

## Executando com Docker (Opcional)


1. Gere a imagem Docker e inicie o container:

```bash
docker compose up --build
```
---

## Acessando a API (Localmente)


- **Base URL:** http://localhost:8080
- **Documentação (Swagger):**  http://localhost:8080/swagger-ui/index.html#/
- **Health Check (Actuator):**  http://localhost:8080/actuator/health
-  **Grafana (Docker):**  http://localhost:3000
- - usuario: admin
- - senha: admin
- **Prometheus (Docker):**  http://localhost:9090
---

## Endpoints da API

### 🔑 Autenticação

1. Registrar Usuário

```http request
POST /auth/register
Content-Type: application/json
```

Body

```http
{
  "name": "string",
  "email": "string",
  "password": "string",
  "role": "ROLE_ADMIN"
}
```

---

2. Obter Token JWT

```http
POST /auth/login
Content-Type: application/json
```

Body

```http
{
 "email": "string",
  "password": "string"
}
```

---

3. **Usar token no Swagger**
4. Clique em Authorize
5. informe o token obtido

---


### comunicacao Controller

#### **Cliente Controller**

1. **Criar Cliente**

```http request
POST /cliente 
Content-Type: application/json
```

**Body**

```http request
{
  "nome": "string",
  "cpf": "string"
}
```
| Parâmetro | Tipo       | Descrição                           |
|:----------| :--------- | :---------------------------------- |
| `nome`    | `String` |   Nome do cliente(**Obrigatório**)
| `cpf`     | `String` |   Cpf do cliente(**Obrigatório**) 
---

2. **Buscar Clientes**: Retorna a lista de clientes pelo cpf completo ou parcial com paginação (Usuario Admin Autenticado)

```http request
GET /clientes/buscar?cpf=00000000000&page=0&size=10
```
---
3. **Listagem Clientes**: Retorna a lista de clientes e seus contatos com paginação

```http request
GET /clientes/?page=0&size=10
```
---

4. **Atualizar Cliente** : atualiza informações do cliente

```http request
PUT /clientes/{id}
```
**Body:**

```http request
{
  "nome": "string",
  "cpf": "string"
}
```

| Parâmetro | Tipo       | Descrição                           |
|:----------| :--------- | :---------------------------------- |
| `nome`    | `String` | **Opcional**. O nome do cliente 
| `cpf`     | `String` | **Opcional**. O cpf do cliente 

4. **Remover Cliente** Soft Delete

```http request
DELETE /clientes/remover/{id}
```
---
### Contato Controller

#### **Contatos**

1. **Cadastrar Contato**
```http request
POST /contatos - Cadastra um novo contato.
Content-Type: application/json
```
**Body:**

```http request
{
  "clienteId": 0,
  "telefone": "string",
  "email": "string"
}
```

| Parâmetro   | Tipo     | Descrição                           |
| :---------- |:---------| :---------------------------------- |
| `telefone` | `String` | **Obrigatório**. O telefone do cliente 
| `email` | `String` | **Obrigatório**. O email do cliente 
| `clienteId` | `Long`   | **Obrigatório**. O id do cliente 
---


2. **Buscar Contatos**: Retorna uma lista de contatos pelo email completo ou parcial com paginação
```http request
GET /contatos/buscar?email=email&page=0&size=10

```
---


3. **Listar Contatos**: Retorna uma lista de contatos na base de dados com paginação
```http request
GET /contatos/contatos?page=0&size=10'

```
---

4. **Listar Contatos de um Cliente:** Retorna todos os contatos associados a um cliente específico. 

```http request
GET /contatos/{id} - listagem de contatos de um cliente existente.

```

5. **Atualizar Contato**

```http request
PUT /contatos/{id}
```

**Body:**

```http request
{
  "telefone": "string",
  "email": "string"
}
```

| Parâmetro | Tipo       | Descrição                           |
|:----------| :--------- | :---------------------------------- |
| `nome`    | `String` | **Opcional**. O telefone  
| `cpf`     | `String` | **Opcional**. O email  

---
6. **Remover Contato** Remove um contato existente (Soft Delete)

```http request
DELETE /contatos/remover/{id}  
```




