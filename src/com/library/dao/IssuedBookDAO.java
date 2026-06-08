package com.library.dao;

import com.library.model.IssuedBook;
import com.library.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for IssuedBook operations.
 * Handles issuing, returning, fine calculation, and history.
 */
public class IssuedBookDAO {

    private static final double FINE_PER_DAY = 2.0;   // ₹2 per overdue day
    private Connection conn = DBConnection.getConnection();

    // ─────────────────────────────────────────────────────────────
    // ISSUE A BOOK  (due date = 14 days from today)
    // ─────────────────────────────────────────────────────────────
    public boolean issueBook(int bookId, int memberId) {
        LocalDate today   = LocalDate.now();
        LocalDate dueDate = today.plusDays(14);
        String sql = "INSERT INTO issued_books (book_id, member_id, issue_date, due_date, status) VALUES (?,?,?,?,'ISSUED')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt (1, bookId);
            ps.setInt (2, memberId);
            ps.setDate(3, Date.valueOf(today));
            ps.setDate(4, Date.valueOf(dueDate));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("IssueBook Error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // RETURN A BOOK  (calculates fine automatically)
    // ─────────────────────────────────────────────────────────────
    public double returnBook(int issueId) {
        // 1. Fetch the record
        IssuedBook record = getIssuedBookById(issueId);
        if (record == null) return -1;

        LocalDate returnDate = LocalDate.now();
        double fine = 0.0;

        // 2. Calculate fine if overdue
        if (returnDate.isAfter(record.getDueDate())) {
            long overdueDays = returnDate.toEpochDay() - record.getDueDate().toEpochDay();
            fine = overdueDays * FINE_PER_DAY;
        }

        // 3. Update the record
        String sql = "UPDATE issued_books SET return_date=?, fine_amount=?, status='RETURNED' WHERE issue_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate  (1, Date.valueOf(returnDate));
            ps.setDouble(2, fine);
            ps.setInt   (3, issueId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ReturnBook Error: " + e.getMessage());
            return -1;
        }
        return fine;
    }

    // ─────────────────────────────────────────────────────────────
    // GET ISSUED BOOKS FOR A SPECIFIC MEMBER (only ISSUED status)
    // ─────────────────────────────────────────────────────────────
    public List<IssuedBook> getIssuedBooksByMember(int memberId) {
        List<IssuedBook> list = new ArrayList<>();
        String sql = """
            SELECT ib.*, b.title AS book_title, m.name AS member_name
            FROM issued_books ib
            JOIN books b   ON ib.book_id   = b.book_id
            JOIN members m ON ib.member_id = m.member_id
            WHERE ib.member_id = ? AND ib.status = 'ISSUED'
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("GetIssuedByMember Error: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────────
    // GET ALL ISSUED BOOKS (Admin view — all ISSUED records)
    // ─────────────────────────────────────────────────────────────
    public List<IssuedBook> getAllIssuedBooks() {
        List<IssuedBook> list = new ArrayList<>();
        String sql = """
            SELECT ib.*, b.title AS book_title, m.name AS member_name
            FROM issued_books ib
            JOIN books b   ON ib.book_id   = b.book_id
            JOIN members m ON ib.member_id = m.member_id
            WHERE ib.status = 'ISSUED'
            ORDER BY ib.due_date
            """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("GetAllIssued Error: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────────
    // CHECK: Is a specific book already issued to a member?
    // ─────────────────────────────────────────────────────────────
    public boolean isBookAlreadyIssuedToMember(int bookId, int memberId) {
        String sql = "SELECT COUNT(*) FROM issued_books WHERE book_id=? AND member_id=? AND status='ISSUED'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setInt(2, memberId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("IsBookIssued Error: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────────────────────
    // GET BOOK_ID from issue_id (needed for incrementing copies)
    // ─────────────────────────────────────────────────────────────
    public int getBookIdFromIssueId(int issueId) {
        String sql = "SELECT book_id FROM issued_books WHERE issue_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("book_id");
        } catch (SQLException e) {
            System.err.println("GetBookIdFromIssue Error: " + e.getMessage());
        }
        return -1;
    }

    // ─────────────────────────────────────────────────────────────
    // HELPER: get single IssuedBook record by issue_id
    // ─────────────────────────────────────────────────────────────
    private IssuedBook getIssuedBookById(int issueId) {
        String sql = "SELECT ib.*, b.title AS book_title, m.name AS member_name " +
                     "FROM issued_books ib " +
                     "JOIN books b ON ib.book_id = b.book_id " +
                     "JOIN members m ON ib.member_id = m.member_id " +
                     "WHERE ib.issue_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("GetIssuedById Error: " + e.getMessage());
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────
    // HELPER: Map ResultSet → IssuedBook
    // ─────────────────────────────────────────────────────────────
    private IssuedBook mapRow(ResultSet rs) throws SQLException {
        IssuedBook ib = new IssuedBook();
        ib.setIssueId   (rs.getInt   ("issue_id"));
        ib.setBookId    (rs.getInt   ("book_id"));
        ib.setMemberId  (rs.getInt   ("member_id"));
        ib.setBookTitle (rs.getString("book_title"));
        ib.setMemberName(rs.getString("member_name"));
        ib.setStatus    (rs.getString("status"));
        ib.setFineAmount(rs.getDouble("fine_amount"));

        Date issued = rs.getDate("issue_date");
        Date due    = rs.getDate("due_date");
        Date ret    = rs.getDate("return_date");
        if (issued != null) ib.setIssueDate (issued.toLocalDate());
        if (due    != null) ib.setDueDate   (due.toLocalDate());
        if (ret    != null) ib.setReturnDate(ret.toLocalDate());
        return ib;
    }
}
