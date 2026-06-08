package com.library.model;

import java.time.LocalDate;

/**
 * Model class representing a Library Member (Admin or User).
 */
public class Member {

    private int       memberId;
    private String    name;
    private String    email;
    private String    phone;
    private String    role;       // "ADMIN" or "USER"
    private String    password;
    private LocalDate joinedDate;

    // ─── Constructors ─────────────────────────────────────────────
    public Member() {}

    public Member(String name, String email, String phone, String role, String password) {
        this.name     = name;
        this.email    = email;
        this.phone    = phone;
        this.role     = role;
        this.password = password;
    }

    // ─── Getters & Setters ────────────────────────────────────────
    public int       getMemberId()           { return memberId; }
    public void      setMemberId(int id)     { this.memberId = id; }

    public String    getName()               { return name; }
    public void      setName(String n)       { this.name = n; }

    public String    getEmail()              { return email; }
    public void      setEmail(String e)      { this.email = e; }

    public String    getPhone()              { return phone; }
    public void      setPhone(String p)      { this.phone = p; }

    public String    getRole()               { return role; }
    public void      setRole(String r)       { this.role = r; }

    public String    getPassword()           { return password; }
    public void      setPassword(String pw)  { this.password = pw; }

    public LocalDate getJoinedDate()         { return joinedDate; }
    public void      setJoinedDate(LocalDate d) { this.joinedDate = d; }

    @Override
    public String toString() {
        return String.format("| %-4d | %-20s | %-25s | %-12s | %-5s |",
            memberId, name, email, phone, role);
    }
}
