# API de Cadastro de Clientes e Contatos

API REST desenvolvida em Java e Spring Boot para gerenciamento de clientes e seus contatos. A aplicação oferece operações de cadastro, consulta, atualização e remoção lógica, com paginação, filtros de busca e associação entre clientes e contatos.

O projeto também implementa autenticação JWT, autorização com Spring Security e controle de acesso por roles, auditoria, respostas padronizadas, documentação OpenAPI e monitoramento.

## Funcionalidades

- Cadastro, consulta, atualização e remoção lógica de clientes.
- Listagem paginada de clientes e seus contatos.
- Busca de clientes por CPF completo ou parcial.
- Cadastro, consulta, atualização e remoção lógica de contatos.
- Associação obrigatória de cada contato a um cliente.
- Listagem paginada de contatos.
- Busca de contatos por e-mail completo ou parcial.
- Consulta dos contatos vinculados a um cliente específico.
- Registro e autenticação de usuários com JWT.
- Controle de acesso com Spring Security e roles.
- Validação dos dados de entrada.
- Respostas padronizadas para sucesso, erros e metadados de paginação.
- Auditoria de criação, atualização e remoção de registros.
- Configuração de CORS por ambiente.
- Migrações de banco de dados com Flyway.
- Logs estruturados em JSON.
- Métricas e monitoramento com Spring Boot Actuator, Prometheus e Grafana.
- Documentação interativa com OpenAPI e Swagger UI.
- Testes unitários e de integração.

## Tecnologias

- **Linguagem:** Java 21
- **Framework:** Spring Boot 3.4
- **API REST:** Spring Web e Bean Validation
- **Segurança:** Spring Security e JWT (java-jwt)
- **Persistência:** Spring Data JPA / Hibernate
- **Banco de dados:** PostgreSQL e H2
- **Migrações:** Flyway
- **Mapeamento:** MapStruct
- **Documentação:** Springdoc OpenAPI / Swagger UI
- **Observabilidade:** Spring Boot Actuator, Micrometer, Prometheus e Grafana
- **Logs:** Logback e Logstash Logback Encoder
- **Testes e cobertura:** Spring Boot Test, Spring Security Test, JUnit, Mockito e JaCoCo
- **Containerização:** Docker e Docker Compose
- **Build:** Maven
- **Utilitários:** Lombok

## Requisitos

- Java 21
- Maven
- PostgreSQL para execução local
- Docker e Docker Compose (opcionais, para execução em containers)

## Como executar

### Execução local

1. Clone o repositório:

   ```bash
   git clone https://github.com/bispobr/Spring-java-CadastroCliente.git
   ```

2. Acesse a pasta do projeto:

   ```bash
   cd Spring-java-CadastroCliente
   ```

3. Crie um arquivo `.env` na raiz do projeto, utilizando `.env.example` como referência. Para o perfil `dev`, configure as variáveis:

   ```dotenv
   DEV_Database_url=jdbc:postgresql://localhost:5432/clientes_contatos
   DEV_Postgres_User=postgres
   DEV_Postgres_Password=sua_senha
   ```

   Crie previamente o banco de dados indicado na URL e ajuste os valores conforme seu ambiente.

4. Execute a aplicação:

   ```bash
   mvn spring-boot:run
   ```

A configuração padrão utiliza o perfil `dev`. O projeto também possui configurações para os perfis `test` e `prod`.

### Execução com Docker Compose

Na raiz do projeto, execute:

```bash
docker compose up --build
```

O ambiente inicia a aplicação, o PostgreSQL, o Prometheus e o Grafana.

Para encerrar os serviços:

```bash
docker compose down
```

## Autenticação

As rotas de autenticação são disponibilizadas pelo prefixo `/auth`. As demais rotas de gerenciamento exigem um token JWT.

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/auth/register` | Registra um usuário. |
| `POST` | `/auth/login` | Autentica um usuário e retorna um token JWT. |

### Registrar usuário

**Requisição**

```http
POST /auth/register
Content-Type: application/json
```

**Corpo**

```json
{
  "name": "Nome do usuário",
  "email": "usuario@example.com",
  "password": "senha",
  "role": "ROLE_USER"
}
```

O campo `role` é opcional. Quando não informado, o usuário recebe a role `ROLE_USER`.

### Obter token JWT

**Requisição**

```http
POST /auth/login
Content-Type: application/json
```

**Corpo**

```json
{
  "email": "usuario@example.com",
  "password": "senha"
}
```

Utilize o token retornado nas requisições protegidas, no cabeçalho:

```http
Authorization: Bearer <token>
```

No Swagger UI, clique em **Authorize** e informe o token para testar os endpoints protegidos.

## Endpoints de clientes

Todas as rotas abaixo utilizam o prefixo `/clientes`.

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/clientes` | Cadastra um cliente. |
| `GET` | `/clientes` | Lista clientes com paginação. |
| `GET` | `/clientes/buscar` | Busca clientes por CPF completo ou parcial, com paginação. |
| `PUT` | `/clientes/{id}` | Atualiza os dados de um cliente. |
| `DELETE` | `/clientes/remover/{id}` | Realiza a remoção lógica de um cliente. |

### Cadastrar cliente

```http
POST /clientes
Content-Type: application/json
Authorization: Bearer <token>
```

**Corpo**

```json
{
  "nome": "Nome do cliente",
  "cpf": "00000000000"
}
```

Os campos `nome` e `cpf` são obrigatórios.

### Listar clientes

```http
GET /clientes?page=0&size=10
Authorization: Bearer <token>
```

### Buscar clientes por CPF

```http
GET /clientes/buscar?cpf=00000000000&page=0&size=10
Authorization: Bearer <token>
```

O parâmetro `cpf` aceita uma busca completa ou parcial. Os parâmetros `page` e `size` controlam a paginação; os valores padrão são `0` e `10`, respectivamente.

### Atualizar cliente

```http
PUT /clientes/{id}
Content-Type: application/json
Authorization: Bearer <token>
```

**Corpo**

```json
{
  "nome": "Nome atualizado",
  "cpf": "00000000000"
}
```

Os campos de atualização são opcionais, conforme o DTO utilizado pela aplicação.

### Remover cliente

```http
DELETE /clientes/remover/{id}
Authorization: Bearer <token>
```

A operação realiza soft delete, mantendo o registro no banco de dados com indicação de remoção.

## Endpoints de contatos

Todas as rotas abaixo utilizam o prefixo `/contatos`.

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/contatos` | Cadastra um contato associado a um cliente. |
| `GET` | `/contatos` | Lista contatos com paginação. |
| `GET` | `/contatos/buscar` | Busca contatos por e-mail completo ou parcial, com paginação. |
| `GET` | `/contatos/{id}` | Lista os contatos associados ao cliente informado. |
| `PUT` | `/contatos/{id}` | Atualiza um contato. |
| `DELETE` | `/contatos/remover/{id}` | Realiza a remoção lógica de um contato. |

### Cadastrar contato

```http
POST /contatos
Content-Type: application/json
Authorization: Bearer <token>
```

**Corpo**

```json
{
  "clienteId": 1,
  "telefone": "11999999999",
  "email": "contato@example.com"
}
```

- `clienteId`: identificador do cliente ao qual o contato será associado, obrigatório.
- `telefone`: telefone do contato, obrigatório.
- `email`: endereço de e-mail válido, obrigatório.

### Listar contatos

```http
GET /contatos?page=0&size=10
Authorization: Bearer <token>
```

### Buscar contatos por e-mail

```http
GET /contatos/buscar?email=contato@example.com&page=0&size=10
Authorization: Bearer <token>
```

O parâmetro `email` aceita uma busca completa ou parcial. A paginação utiliza `page` e `size`, com valores padrão `0` e `10`.

### Listar contatos de um cliente

```http
GET /contatos/{id}
Authorization: Bearer <token>
```

O identificador informado corresponde ao cliente cujos contatos serão listados.

### Atualizar contato

```http
PUT /contatos/{id}
Content-Type: application/json
Authorization: Bearer <token>
```

**Corpo**

```json
{
  "telefone": "11988887777",
  "email": "novo-contato@example.com"
}
```

### Remover contato

```http
DELETE /contatos/remover/{id}
Authorization: Bearer <token>
```

A operação realiza soft delete do contato.

## Respostas da API

As operações utilizam um formato padronizado de resposta, com estrutura para indicar sucesso, dados retornados, erros e metadados. Nas operações de listagem paginada, os metadados incluem informações da paginação.

## Documentação e monitoramento

Com a aplicação em execução, os serviços ficam disponíveis nos endereços padrão:

| Serviço | URL |
| --- | --- |
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| Actuator Health | http://localhost:8080/actuator/health |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3000 |

O Actuator disponibiliza informações de saúde e métricas. O Prometheus coleta métricas da aplicação, e o Grafana utiliza o Prometheus como fonte de dados.

## Testes

Para executar os testes automatizados:

```bash
mvn test
```

O projeto contém testes unitários e de integração para os controladores e serviços, utilizando JUnit, Mockito e Spring Boot Test. O JaCoCo está configurado para gerar relatório de cobertura durante a execução dos testes.

## Estrutura do projeto

O código está organizado por responsabilidades, incluindo configurações de CORS, banco de dados, OpenAPI e segurança; controladores REST; DTOs de requisição e resposta; entidades e enums; repositórios; serviços; componentes de auditoria, logs e respostas padronizadas. O projeto também contém migrações Flyway, configurações por perfil, testes automatizados e arquivos de Docker e monitoramento.
