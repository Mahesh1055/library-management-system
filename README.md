# 📚 Library Management System
> Console-based Java application using Core Java, OOP, Collections, JDBC, and MySQL.

---

## 🗂️ Project Structure

```
LibraryManagementSystem/
│
├── database/
│   └── library_db.sql              ← Run this first in MySQL
│
├── src/
│   └── com/library/
│       ├── main/
│       │   └── LibraryApp.java     ← Entry point
│       ├── model/
│       │   ├── Book.java
│       │   ├── Member.java
│       │   └── IssuedBook.java
│       ├── dao/
│       │   ├── BookDAO.java
│       │   ├── MemberDAO.java
│       │   └── IssuedBookDAO.java
│       ├── service/
│       │   ├── AdminService.java
│       │   ├── UserService.java
│       │   └── AuthService.java
│       └── util/
│           ├── DBConnection.java
│           └── ConsoleColors.java
│
├── lib/
│   └── mysql-connector-j-8.x.x.jar  ← Download separately
│
└── README.md
```

---

## ⚙️ Setup Instructions

### Step 1: Prerequisites
- Java JDK 17 or above installed
- MySQL 8.x installed and running
- Any IDE: IntelliJ IDEA, Eclipse, or VS Code

### Step 2: Download MySQL JDBC Driver
1. Go to: https://dev.mysql.com/downloads/connector/j/
2. Select **Platform Independent** → download the ZIP
3. Extract and copy `mysql-connector-j-x.x.x.jar` into the `lib/` folder

### Step 3: Setup Database
1. Open MySQL Workbench or terminal
2. Run the SQL file:
```sql
SOURCE /path/to/LibraryManagementSystem/database/library_db.sql;
```
Or paste the contents of `library_db.sql` directly into MySQL Workbench and execute.

### Step 4: Configure DB Credentials
Open `src/com/library/util/DBConnection.java` and update:
```java
private static final String USERNAME = "root";    // Your MySQL username
private static final String PASSWORD = "root";    // Your MySQL password
```

---

## 🚀 Running the Project

### Option A: IntelliJ IDEA (Recommended)
1. Open IntelliJ → **File → Open** → select `LibraryManagementSystem/`
2. Right-click the `lib/` folder → **Add as Library**
   (Or: File → Project Structure → Libraries → + → Java → select the .jar)
3. Run `LibraryApp.java`

### Option B: Eclipse
1. **File → Import → Existing Projects into Workspace**
2. Right-click project → **Build Path → Add External Archives** → select the .jar
3. Run `LibraryApp.java`

### Option C: Command Line
```bash
# Compile
javac -cp "lib/mysql-connector-j-8.x.x.jar" -d out/ \
  src/com/library/util/*.java \
  src/com/library/model/*.java \
  src/com/library/dao/*.java \
  src/com/library/service/*.java \
  src/com/library/main/*.java

# Run
java -cp "out:lib/mysql-connector-j-8.x.x.jar" com.library.main.LibraryApp
# On Windows use semicolons:
java -cp "out;lib/mysql-connector-j-8.x.x.jar" com.library.main.LibraryApp
```

---

## 👤 Default Login Credentials

| Role  | Email               | Password  |
|-------|---------------------|-----------|
| Admin | admin@gmail.com   | admin123  |
| User  | user@gmail.com    | user123   |

---

## ✅ Features

### Admin Panel
| # | Feature       | Description                              |
|---|---------------|------------------------------------------|
| 1 | Add Book      | Add new book with title, author, ISBN    |
| 2 | View All Books| See complete library catalogue           |
| 3 | Search Book   | Search by title, author, or ISBN         |
| 4 | Update Book   | Edit book details                        |
| 5 | Delete Book   | Remove a book from the system            |
| 6 | All Issued    | View all currently issued books          |
| 7 | Sort Books    | Sort by title, author, genre, or copies  |

### User Panel
| # | Feature          | Description                          |
|---|------------------|--------------------------------------|
| 1 | Browse Books     | View all available books             |
| 2 | Issue Book       | Borrow a book (14-day due period)    |
| 3 | Return Book      | Return a book, see fine if overdue   |
| 4 | My Issued Books  | View your currently borrowed books   |
| 5 | Search Book      | Find books by keyword                |

### Additional
- **Fine Calculation**: ₹2 per day after due date (auto-calculated on return)
- **Duplicate Check**: Can't issue the same book twice
- **Overdue Highlight**: Red highlight for overdue books
- **Availability Guard**: Can't issue books with 0 copies

---

## 🧠 OOP Concepts Used
- **Encapsulation** — Model classes with private fields & getters/setters
- **Abstraction** — DAO layer abstracts all DB operations
- **Separation of Concerns** — Model → DAO → Service → Main
- **Collections** — `List<Book>`, `Comparator` for sorting
- **Singleton Pattern** — `DBConnection` ensures single DB connection

---

## 📦 Java Topics Covered
- JDBC (PreparedStatement, ResultSet, Connection)
- Java Collections Framework (ArrayList, List, Comparator)
- LocalDate API (java.time)
- try-with-resources for safe DB cleanup
- Enum-style role management
- String formatting with printf
