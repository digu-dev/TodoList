# 📝 To Do List - CRUD Application

Projeto simples de uma aplicação de lista de tarefas (To Do List) com operações completas de CRUD, desenvolvido para praticar conceitos de API REST utilizando Java e Spring Boot.

---

## 🚀 Funcionalidades

- ✅ Criar nova tarefa
- 📋 Listar todas as tarefas
- 🔎 Buscar tarefa por ID
- ✏️ Atualizar tarefa existente
- ❌ Deletar tarefa
- ✔️ Marcar tarefa como concluída

---

## 🛠️ Tecnologias Utilizadas

- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- Banco de Dados (PostgreSQL ou H2)
- Maven

---

## 📁 Estrutura do Projeto

```
src
└── main
    ├── java
    │   └── com.seuprojeto.todo
    │       ├── controller
    │       ├── service
    │       ├── repository
    │       └── model
    └── resources
        └── application.properties
```

---

## 📌 Endpoints da API

| Método | Endpoint        | Descrição                  |
|--------|---------------|----------------------------|
| GET    | /tasks        | Listar todas as tarefas    |
| GET    | /tasks/{id}   | Buscar tarefa por ID       |
| POST   | /tasks        | Criar nova tarefa          |
| PUT    | /tasks/{id}   | Atualizar tarefa           |
| DELETE | /tasks/{id}   | Deletar tarefa             |

---

## 🗄️ Modelo da Entidade

A entidade `Task` possui os seguintes atributos:

- `id` (Long) – Identificador da tarefa  
- `title` (String) – Título da tarefa  
- `description` (String) – Descrição detalhada  
- `completed` (Boolean) – Indica se a tarefa foi concluída  

---

## ▶️ Como Executar o Projeto

1. Clone o repositório:

```
git clone https://github.com/seu-usuario/seu-repositorio.git
```

2. Acesse a pasta do projeto:

```
cd nome-do-projeto
```

3. Execute a aplicação:

Se estiver usando Maven Wrapper:

```
./mvnw spring-boot:run
```

Ou execute diretamente pela sua IDE (IntelliJ, Eclipse ou VSCode).

A aplicação estará disponível em:

```
http://localhost:8080
```

---

## 🎯 Objetivo do Projeto

Este projeto foi desenvolvido com o objetivo de praticar:

- Construção de APIs REST
- Arquitetura em camadas (Controller, Service, Repository)
- Integração com banco de dados relacional
- Operações CRUD completas
- Boas práticas com Spring Boot

---

## 📚 Aprendizados

- Organização de projeto backend
- Mapeamento de entidades com JPA
- Manipulação de requisições HTTP
- Tratamento básico de exceções
- Persistência de dados com Spring Data JPA

---

Feito por **Rodrigo de Oliveira Martins** 🚀  
