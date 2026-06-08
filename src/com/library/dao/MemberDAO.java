package com.library.dao;

import com.library.model.Member;
import com.library.util.DBConnection;

import java.sql.*;

/**
 * Data Access Object for Member operations.
 */
public class MemberDAO {

    private Connection conn = DBConnection.getConnection();

    // ─────────────────────────────────────────────────────────────
    // REGISTER NEW MEMBER
    // ─────────────────────────────────────────────────────────────
    public boolean registerMember(Member member) {
        String sql = "INSERT INTO members (name, email, phone, role, password) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.setString(4, member.getRole());
            ps.setString(5, member.getPassword());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("RegisterMember Error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // LOGIN VALIDATION
    // Returns the Member object if credentials match, else null.
    // ─────────────────────────────────────────────────────────────
    public Member login(String email, String password) {
        String sql = "SELECT * FROM members WHERE email = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("Login Error: " + e.getMessage());
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────
    // GET MEMBER BY ID
    // ─────────────────────────────────────────────────────────────
    public Member getMemberById(int id) {
        String sql = "SELECT * FROM members WHERE member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("GetMember Error: " + e.getMessage());
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────
    // HELPER: Map ResultSet → Member
    // ─────────────────────────────────────────────────────────────
    private Member mapRow(ResultSet rs) throws SQLException {
        Member m = new Member();
        m.setMemberId (rs.getInt   ("member_id"));
        m.setName     (rs.getString("name"));
        m.setEmail    (rs.getString("email"));
        m.setPhone    (rs.getString("phone"));
        m.setRole     (rs.getString("role"));
        m.setPassword (rs.getString("password"));
        Date d = rs.getDate("joined_date");
        if (d != null) m.setJoinedDate(d.toLocalDate());
        return m;
    }
}
