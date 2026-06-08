package com.library.model;

import java.time.LocalDate;

/**
 * Model class representing a book issuance record.
 */
public class IssuedBook {

    private int        issueId;
    private int        bookId;
    private int        memberId;
    private String     bookTitle;    // joined field for display
    private String     memberName;   // joined field for display
    private LocalDate  issueDate;
    private LocalDate  dueDate;
    private LocalDate  returnDate;
    private double     fineAmount;
    private String     status;       // "ISSUED" or "RETURNED"

    // ─── Constructors ─────────────────────────────────────────────
    public IssuedBook() {}

    public IssuedBook(int bookId, int memberId, LocalDate issueDate, LocalDate dueDate) {
        this.bookId    = bookId;
        this.memberId  = memberId;
        this.issueDate = issueDate;
        this.dueDate   = dueDate;
        this.status    = "ISSUED";
    }

    // ─── Getters & Setters ────────────────────────────────────────
    public int       getIssueId()               { return issueId; }
    public void      setIssueId(int id)         { this.issueId = id; }

    public int       getBookId()                { return bookId; }
    public void      setBookId(int id)          { this.bookId = id; }

    public int       getMemberId()              { return memberId; }
    public void      setMemberId(int id)        { this.memberId = id; }

    public String    getBookTitle()             { return bookTitle; }
    public void      setBookTitle(String t)     { this.bookTitle = t; }

    public String    getMemberName()            { return memberName; }
    public void      setMemberName(String n)    { this.memberName = n; }

    public LocalDate getIssueDate()             { return issueDate; }
    public void      setIssueDate(LocalDate d)  { this.issueDate = d; }

    public LocalDate getDueDate()               { return dueDate; }
    public void      setDueDate(LocalDate d)    { this.dueDate = d; }

    public LocalDate getReturnDate()            { return returnDate; }
    public void      setReturnDate(LocalDate d) { this.returnDate = d; }

    public double    getFineAmount()            { return fineAmount; }
    public void      setFineAmount(double f)    { this.fineAmount = f; }

    public String    getStatus()                { return status; }
    public void      setStatus(String s)        { this.status = s; }

    @Override
    public String toString() {
        return String.format(
            "| %-4d | %-25s | %-15s | %-12s | %-12s | %-10s | %-8.2f |",
            issueId, bookTitle, memberName, issueDate, dueDate, status, fineAmount
        );
    }
}
