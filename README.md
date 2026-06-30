# PRODIGY_SD_03 — Contact Management System

![Task](https://img.shields.io/badge/Task-03-6366f1?style=for-the-badge)
![Internship](https://img.shields.io/badge/Prodigy%20InfoTech-Software%20Development-0ea5e9?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Completed-22c55e?style=for-the-badge)

![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=flat&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=flat&logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=flat&logo=javascript&logoColor=black)
![Java](https://img.shields.io/badge/Java-007396?style=flat&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/Java-Swing-F89820?style=flat&logo=java&logoColor=white)

> Task 03 of the **Prodigy InfoTech Software Development Internship** by **Mansi** ([@Mansi-028](https://github.com/Mansi-028)).
>
> A simple Contact Management System that lets users **add, view, edit, search and delete** contacts, with **persistent storage** so the data sticks around between runs.

---

## 📋 Problem Statement

> Develop a program that allows users to store and manage contact information.
> The program should provide options to add a new contact by entering their name, phone number, and email address.
> It should also allow users to view their contact list, edit existing contacts, and delete contacts if needed.
> The program should store the contacts in memory or in a file for persistent storage.

---

## ✨ Features

- ➕ **Add** contacts with name, phone and email
- 📖 **View** all contacts in a clean list / table
- ✏️ **Edit** existing contacts
- 🗑️ **Delete** contacts with confirmation
- 🔍 **Search** by name, phone or email
- ✅ **Validation** for email format and phone length
- 💾 **Persistent storage** (file on disk for Java, `localStorage` for the web app)

---

## 🧩 Implementations

This task ships in **three flavors**:

| Folder | Tech | What it is |
| --- | --- | --- |
| `web/` | HTML5 + CSS3 + Vanilla JavaScript | Browser app, contacts saved to `localStorage` |
| `java-console/` | Java (CLI) | Terminal menu, contacts saved to `contacts.txt` |
| `java-swing/` | Java Swing (GUI) | Desktop window, contacts saved to `contacts.txt` |

---

## 🚀 How to Run

### 🌐 Web version
```bash
cd web
# just open index.html in any browser
```

### 💻 Java console version
```bash
cd java-console
javac ContactManager.java
java ContactManager
```

### 🖥️ Java Swing GUI
```bash
cd java-swing
javac ContactManagerSwing.java
java ContactManagerSwing
```

> Requires **JDK 8+**.

---

## 📁 Project Structure
```
PRODIGY_SD_03/
├── web/
│   ├── index.html
│   ├── styles.css
│   └── script.js
├── java-console/
│   └── ContactManager.java
├── java-swing/
│   └── ContactManagerSwing.java
└── README.md
```

---

## 🧠 What I Learned
- Designing the same feature across three very different stacks
- Persisting data with the **File I/O API** in Java and **`localStorage`** on the web
- Building a usable **CRUD UI** with Java Swing (`JTable`, `DefaultTableModel`, `JOptionPane`)
- Input validation patterns (regex for email, digit count for phone)
- Keeping logic **separate from presentation** for easier maintenance

---

## 👩‍💻 Author

**Mansi** — B.Tech student
🔗 GitHub: [@Mansi-028](https://github.com/Mansi-028)
🎓 Prodigy InfoTech Software Development Internship — Task 03

---

⭐ If you found this useful, drop a star on the repo!
