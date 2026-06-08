-- ============================================
--   LIBRARY MANAGEMENT SYSTEM - DATABASE SETUP
-- ============================================

CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- ----------------------------
-- TABLE: books
-- ----------------------------
CREATE TABLE IF NOT EXISTS books (
    book_id       INT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(200) NOT NULL,
    author        VARCHAR(100) NOT NULL,
    genre         VARCHAR(50),
    isbn          VARCHAR(20) UNIQUE NOT NULL,
    total_copies  INT NOT NULL DEFAULT 1,
    available_copies INT NOT NULL DEFAULT 1,
    added_date    DATE DEFAULT (CURRENT_DATE)
);

-- ----------------------------
-- TABLE: members
-- ----------------------------
CREATE TABLE IF NOT EXISTS members (
    member_id     INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(100) UNIQUE NOT NULL,
    phone         VARCHAR(15),
    role          ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER',
    password      VARCHAR(100) NOT NULL,
    joined_date   DATE DEFAULT (CURRENT_DATE)
);

-- ----------------------------
-- TABLE: issued_books
-- ----------------------------
CREATE TABLE IF NOT EXISTS issued_books (
    issue_id      INT AUTO_INCREMENT PRIMARY KEY,
    book_id       INT NOT NULL,
    member_id     INT NOT NULL,
    issue_date    DATE NOT NULL DEFAULT (CURRENT_DATE),
    due_date      DATE NOT NULL,
    return_date   DATE,
    fine_amount   DECIMAL(10,2) DEFAULT 0.00,
    status        ENUM('ISSUED', 'RETURNED') DEFAULT 'ISSUED',
    FOREIGN KEY (book_id)   REFERENCES books(book_id),
    FOREIGN KEY (member_id) REFERENCES members(member_id)
);

-- ----------------------------
-- SAMPLE DATA
-- ----------------------------

-- Default admin account  (password: admin123)
INSERT INTO members (name, email, phone, role, password) VALUES
('Admin User', 'admin@gmail.com', '9000000000', 'ADMIN', 'admin123');

-- Default user account   (password: user123)
INSERT INTO members (name, email, phone, role, password) VALUES
('John Doe', 'user@gmail.com', '9111111111', 'USER', 'user123');

-- Sample books
INSERT INTO books (title, author, genre, isbn, total_copies, available_copies) VALUES
('The Great Gatsby',        'F. Scott Fitzgerald', 'Fiction',         '978-0743273565', 3, 3),
('To Kill a Mockingbird',   'Harper Lee',          'Fiction',         '978-0061935466', 2, 2),
('Clean Code',              'Robert C. Martin',    'Technology',      '978-0132350884', 4, 4),
('Effective Java',          'Joshua Bloch',        'Technology',      '978-0134685991', 3, 3),
('The Alchemist',           'Paulo Coelho',        'Fiction',         '978-0062315007', 5, 5),
('Atomic Habits',           'James Clear',         'Self-Help',       '978-0735211292', 3, 3),
('1984',                    'George Orwell',       'Dystopian',       '978-0451524935', 2, 2),
('Head First Java',         'Kathy Sierra',        'Technology',      '978-0596009205', 4, 4),
('The Pragmatic Programmer','Andrew Hunt',         'Technology',      '978-0201616224', 2, 2),
('Rich Dad Poor Dad',       'Robert Kiyosaki',     'Finance',         '978-1612680194', 3, 3);
