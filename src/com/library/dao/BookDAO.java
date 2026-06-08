package com.library.dao;

import com.library.model.Book;
import com.library.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class BookDAO {

    private Connection conn = DBConnection.getConnection();

    // ─────────────────────────────────────────────────────────────
    // ADD BOOK
    // ─────────────────────────────────────────────────────────────
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (title, author, genre, isbn, total_copies, available_copies) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getGenre());
            ps.setString(4, book.getIsbn());
            ps.setInt   (5, book.getTotalCopies());
            ps.setInt   (6, book.getTotalCopies());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("AddBook Error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // VIEW ALL BOOKS
    // ─────────────────────────────────────────────────────────────
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY book_id";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) books.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("GetAllBooks Error: " + e.getMessage());
        }
        return books;
    }

    // ─────────────────────────────────────────────────────────────
    // SEARCH BOOK (by title OR author OR isbn)
    // ─────────────────────────────────────────────────────────────
    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) books.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("SearchBooks Error: " + e.getMessage());
        }
        return books;
    }

    // ─────────────────────────────────────────────────────────────
    // GET BOOK BY ID
    // ─────────────────────────────────────────────────────────────
    public Book getBookById(int bookId) {
        String sql = "SELECT * FROM books WHERE book_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("GetBookById Error: " + e.getMessage());
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────
    // UPDATE BOOK
    // ─────────────────────────────────────────────────────────────
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title=?, author=?, genre=?, isbn=?, total_copies=?, available_copies=? WHERE book_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getGenre());
            ps.setString(4, book.getIsbn());
            ps.setInt   (5, book.getTotalCopies());
            ps.setInt   (6, book.getAvailableCopies());
            ps.setInt   (7, book.getBookId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("UpdateBook Error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // DELETE BOOK
    // ─────────────────────────────────────────────────────────────
    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DeleteBook Error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // DECREMENT available_copies (on issue)
    // ─────────────────────────────────────────────────────────────
    public boolean decrementAvailableCopies(int bookId) {
        String sql = "UPDATE books SET available_copies = available_copies - 1 WHERE book_id = ? AND available_copies > 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DecrementCopies Error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // INCREMENT available_copies (on return)
    // ─────────────────────────────────────────────────────────────
    public boolean incrementAvailableCopies(int bookId) {
        String sql = "UPDATE books SET available_copies = available_copies + 1 WHERE book_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("IncrementCopies Error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // GET BOOKS SORTED BY TITLE
    // ─────────────────────────────────────────────────────────────
    public List<Book> getBooksSortedBy(String column) {
        List<Book> books = new ArrayList<>();
        // Whitelist allowed columns to prevent SQL injection
        List<String> allowed = List.of("title", "author", "genre", "available_copies");
        if (!allowed.contains(column)) column = "title";

        String sql = "SELECT * FROM books ORDER BY " + column;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) books.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("SortBooks Error: " + e.getMessage());
        }
        return books;
    }

    // ─────────────────────────────────────────────────────────────
    // HELPER: Map ResultSet row → Book object
    // ─────────────────────────────────────────────────────────────
    private Book mapRow(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setBookId          (rs.getInt   ("book_id"));
        b.setTitle           (rs.getString("title"));
        b.setAuthor          (rs.getString("author"));
        b.setGenre           (rs.getString("genre"));
        b.setIsbn            (rs.getString("isbn"));
        b.setTotalCopies     (rs.getInt   ("total_copies"));
        b.setAvailableCopies (rs.getInt   ("available_copies"));
        Date d = rs.getDate("added_date");
        if (d != null) b.setAddedDate(d.toLocalDate());
        return b;
    }
}
