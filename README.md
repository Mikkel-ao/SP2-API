# SP2-API
---

A RESTful API built with **Javalin** and **Hibernate** that powers a discussion platform.  
The application automatically retrieves recent news articles from **NewsAPI.org**, creates discussion posts based on them, and allows registered users to engage by commenting and voting on both posts and comments.

---
## Tech Stack

- **Java 17+**  

- **Javalin Framework**  

- **Hibernate (JPA)**  

- **PostgreSQL**

- **JWT-based Authentication**  

- **Jackson (ObjectMapper)**  

- **Maven**  

- **Embedded Jetty Server (via Javalin)**  

- **SLF4J with Logback**  

- **JUnit 5**  

- **NewsAPI.org Integration**  
  Fetches and stores technology-related news articles as discussion topics via the `NewsApiService`.  
  Requires a valid `NEWS_API_KEY` environment variable.
--- 
# API Endpoint Documentation
---
## Authentication and Security

| Method | Endpoint | Access | Description |
|--------|-----------|--------|-------------|
| **GET** | `/auth/healthcheck` | ANYONE | Check if the service is running. |
| **GET** | `/auth/test` | ANYONE | Simple test endpoint used for deployment verification. |
| **POST** | `/auth/login` | ANYONE | Authenticate a user and return a JWT token. |
| **POST** | `/auth/register` | ANYONE | Register a new user account. |
| **POST** | `/auth/user/addrole` | USER | Add an additional role to an existing user. |
| **GET** | `/protected/user_demo` | USER | Test endpoint accessible to authenticated users. |
| **GET** | `/protected/admin_demo` | ADMIN | Test endpoint accessible to administrators. |

---

## Posts

| Method | Endpoint | Access | Description |
|--------|-----------|--------|-------------|
| **GET** | `/posts/` | ANYONE | Retrieve a list of all posts. |
| **GET** | `/posts/{id}` | ANYONE | Retrieve a specific post by its ID. |
| **POST** | `/posts/` | ADMIN | Create a new post. |
| **PUT** | `/posts/{id}` | ADMIN | Update an existing post by ID. |
| **DELETE** | `/posts/{id}` | ADMIN | Delete a post. |

---

## Comments

| Method | Endpoint | Access | Description |
|--------|-----------|--------|-------------|
| **GET** | `/comments/` | ANYONE | Retrieve a list of all comments. |
| **GET** | `/comments/{id}` | ANYONE | Retrieve a specific comment by its ID. |
| **POST** | `/comments/` | USER | Create a new comment associated with a post. |
| **DELETE** | `/comments/{id}` | USER | Delete a comment (restricted to author or administrator). |

---

## Votes

| Method | Endpoint | Access | Description |
|--------|-----------|--------|-------------|
| **GET** | `/votes/` | ANYONE | Retrieve all votes. |
| **GET** | `/votes/{id}` | ANYONE | Retrieve a specific vote by ID. |
| **POST** | `/votes/` | USER | Submit a new vote. |
| **DELETE** | `/votes/{id}` | USER | Delete a vote (restricted to author or administrator). |

---

## External News Integration

| Method | Endpoint | Access | Description |
|--------|-----------|--------|-------------|
| **POST** | `/external/fetch-news` | ADMIN | Fetch and store the latest technology-related articles from NewsAPI.org. |

---

## Access Roles

| Role | Description |
|------|--------------|
| **ANYONE** | Public access; no authentication required. |
| **USER** | Access restricted to authenticated users. |
| **ADMIN** | Access restricted to administrators. |

All protected endpoints require a valid JWT token in the `Authorization` header:


