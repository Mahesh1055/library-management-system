package com.library.model;

import java.time.LocalDate;

/**
 * Model class representing a Book entity.
 */
public class Book {

    private int       bookId;
    private String    title;
    private String    author;
    private String    genre;
    private String    isbn;
    private int       totalCopies;
    private int       availableCopies;
    private LocalDate addedDate;

    // ─── Constructors ─────────────────────────────────────────────
    public Book() {}

    public Book(String title, String author, String genre, String isbn, int totalCopies) {
        this.title           = title;
        this.author          = author;
        this.genre           = genre;
        this.isbn            = isbn;
        this.totalCopies     = totalCopies;
        this.availableCopies = totalCopies;
    }

    // ─── Getters & Setters ────────────────────────────────────────
    public int       getBookId()           { return bookId; }
    public void      setBookId(int id)     { this.bookId = id; }

    public String    getTitle()            { return title; }
    public void      setTitle(String t)    { this.title = t; }

    public String    getAuthor()           { return author; }
    public void      setAuthor(String a)   { this.author = a; }

    public String    getGenre()            { return genre; }
    public void      setGenre(String g)    { this.genre = g; }

    public String    getIsbn()             { return isbn; }
    public void      setIsbn(String i)     { this.isbn = i; }

    public int       getTotalCopies()      { return totalCopies; }
    public void      setTotalCopies(int t) { this.totalCopies = t; }

    public int       getAvailableCopies()         { return availableCopies; }
    public void      setAvailableCopies(int a)    { this.availableCopies = a; }

    public LocalDate getAddedDate()               { return addedDate; }
    public void      setAddedDate(LocalDate d)    { this.addedDate = d; }

    // ─── Display ──────────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format(
            "| %-4d | %-30s | %-20s | %-12s | %-5d | %-9d |",
            bookId, title, author, genre, totalCopies, availableCopies
        );
    }
}
