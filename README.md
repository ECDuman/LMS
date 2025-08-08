# Learning Management System (LMS) - Java Backend
Erencan Duman  08.08.2025
## ✨ Overview

This is a secure and scalable **Learning Management System** (LMS) backend built with **Spring Boot**, implementing JWT-based authentication, role-based access control, auditing, and RESTful APIs.

---

## 🌐 Technologies

* **Java 17**
* **Spring Boot 3.x**
* **Spring Security** with JWT
* **Spring Data JPA**
* **Hibernate**
* **PostgreSQL**
* **Redis** (Login throttling)
* **MapStruct** (DTO mapping)
* **Lombok**
* **OpenAPI / Swagger**
* **JUnit + Mockito**
* **Docker + Docker Compose**

---

## 👥 User Roles & Permissions

| Role         | Description                                 |
| ------------ | ------------------------------------------- |
| SUPER_ADMIN | Full CRUD access to all resources           |
| TEACHER      | Limited access (e.g., only assigned data)   |
| STUDENT      | Read-only access to own courses/assignments |

Role-based access is implemented via `@PreAuthorize` and `PermissionCheckerService`.

---

## ⛓ Authentication & Authorization

* **JWT** tokens are used for stateless authentication.
* **Access Token + Refresh Token** strategy is used.
* Role & permission checks are enforced using `@PreAuthorize`.

Example:

```java
@PreAuthorize("hasPermission('User', 'DELETE')")
```

---

## ✉️ Email & Password Reset

* Users can initiate password reset via email (logs sout to console for now) (token-based).
* Token validity is 15 minutes.
* Once used, token is deleted.

---

## 📊 Audit Logging

The following actions are logged:

* `CREATE`, `UPDATE`, `DELETE` for all entities
* `RESET_PASSWORD`, `FORGOT_PASSWORD_INITIATED`
* `LOGOUT`

Audit logs contain:

* Action
* Entity Type
* Entity ID
* Performed By (User)
* Timestamp

---

## 🔒 Login Attempt Throttling

* **Redis** is used to track failed login attempts.
* Users are locked out after 5 failed attempts within 5 minutes.

---

## 📚 Validation

All DTOs use `jakarta.validation` annotations like `@NotBlank`, `@Email`, etc. to ensure data integrity.

---

## 🎨 Swagger / OpenAPI

* Swagger UI is available at: `http://localhost:8080/swagger-ui/index.html`
* JWT authentication can be used via the "Authorize" button.

---

## 📉 Testing

* Unit tests provided for core services: `UserService`, `BrandService`, etc.
* Mockito used for mocking dependencies.

---

## 🧱 Docker Compose

!!! Maven clean install without tests Than dont forget to open Docker Desktop
This project is fully containerized. To run the app:

```bash
docker-compose up --build
```

Services:

* `lms-app`: Spring Boot backend (port `8080`)
* `postgres`: PostgreSQL DB (port `5433` -> container `5432`)
* `redis`: Redis cache (port `6379`)

---

## ⚡ Sample API Usage

postman.collection added. dont forget enviroments.

---

## 🗃️ Manually Required Database Tables

If not auto-created by Hibernate, ensure the following reference tables are manually populated:

### `profile_types`

| id | name         |
| -- | ------------ |
| 0  | SUPER\_ADMIN |
| 1  | TEACHER      |
| 2  | STUDENT      |

### `permissions`

At minimum, define permissions for each resource:

* resource\_name: e.g. `User`, `Course`, `Assignment`, etc.
* role\_id: foreign key to `profile_types`
* can\_create, can\_read, can\_update, can\_delete: boolean flags

These are used by the `PermissionCheckerService`.

### `users`

Manually insert an initial admin:

```sql
INSERT INTO users (id, email, password, first_name, last_name, profile_id, organization_id)
VALUES (
  '11111111-1111-1111-1111-111111111111',
  'admin@example.com',
  '$2a$10$XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX', -- BCrypt hash of your password
  'Admin',
  'User',
  0,
  'e58ed763-928c-4155-bee9-fdbaaadc15f3'
);



