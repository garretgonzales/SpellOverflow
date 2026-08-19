# 🔮 SpellOverflow

### _Where clumsy wizards debug their broken incantations._

SpellOverflow is a full-stack question-and-answer web application inspired by Stack Overflow. Users can create accounts, ask technical questions, answer other users’ questions, and follow questions they want to revisit.

The wizarding theme appears in the language and eventual visual design, while the application keeps familiar and practical Q&A workflows.

---

## ✨ Features

### While logged out

- Create an account
- Log in to an existing account
- View all posted questions and their authors
- View an individual question, its answers, and each answer’s author

### While logged in

- Post a question
- Edit or delete questions you created
- Post an answer to a question
- Edit or delete answers you created
- Follow a question
- Unfollow a question you are currently following
- View a list of questions you follow

### Authentication and authorization

- Passwords are securely hashed before being stored
- Login returns a JSON Web Token (JWT)
- Protected actions require a valid JWT
- Users can edit or delete only their own questions and answers

---

## 🪄 Thematic Terminology

SpellOverflow uses wizard-themed language in the interface while retaining standard technical names in the codebase and database.

| Application concept     | SpellOverflow label  |
| ----------------------- | -------------------- |
| User                    | Wizard               |
| Question                | Incantation          |
| Answer                  | Remedy               |
| Post a question         | Cast an Incantation  |
| Post an answer          | Offer a Remedy       |
| Follow a question       | Mark for Study       |
| Unfollow a question     | Remove from Study    |
| Followed questions page | Study List           |
| Log in                  | Enter the Great Hall |
| Log out                 | Leave the Great Hall |

---

## 🛠️ Tech Stack

### Frontend

- React
- React Router
- JavaScript
- Plain CSS, added after the application features are complete

### Backend

- Java
- Spring Boot
- Spring Security
- JWT-based authentication
- Spring Data JPA
- REST API

---

## 🧪 Project Status

- [x] Project structure created
- [x] MySQL connection configured
- [ ] User registration
- [ ] User login and JWT authentication
- [ ] Question CRUD
- [ ] Answer CRUD
- [ ] Followed-question feature
- [ ] Authorization and error handling
- [ ] Functional testing
- [ ] Styling and final UI polish

---

## 📚 Learning Goals

This project is being built incrementally to practice:

- Designing a relational database
- Building REST APIs with Spring Boot
- Hashing passwords and securing endpoints
- Using JWT authentication with React and Spring Security
- Managing React state and client-side routes
- Enforcing resource ownership rules
- Connecting a React frontend to a Spring Boot and MySQL backend

---

## 📄 Disclaimer

SpellOverflow is an educational project inspired by the general Q&A format of Stack Overflow. It is not affiliated with or endorsed by Stack Overflow or Stack Exchange.

<img width="480" height="480" alt="Magic Read GIF" src="https://github.com/user-attachments/assets/50d96894-f725-4838-a8fc-e62bc7f93bfa" />

