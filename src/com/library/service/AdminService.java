package com.library.service;

import com.library.dao.BookDAO;
import com.library.dao.IssuedBookDAO;
import com.library.model.Book;
import com.library.model.IssuedBook;
import com.library.util.ConsoleColors;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Service class handling all Admin operations.
 */
public class AdminService {

    private final BookDAO      bookDAO      = new BookDAO();
    private final IssuedBookDAO issuedDAO   = new IssuedBookDAO();
    private final Scanner       sc          = new Scanner(System.in);

    // ─────────────────────────────────────────────────────────────
    // ADMIN MENU
    // ─────────────────────────────────────────────────────────────
    public void showAdminMenu() {
        boolean running = true;
        while (running) {
            ConsoleColors.printHeader("ADMIN PANEL");
            System.out.println("  1. Add Book");
            System.out.println("  2. View All Books");
            System.out.println("  3. Search Book");
            System.out.println("  4. Update Book");
            System.out.println("  5. Delete Book");
            System.out.println("  6. View All Issued Books");
            System.out.println("  7. Sort Books");
            System.out.println("  0. Logout");
            ConsoleColors.printDivider();
            System.out.print("  Enter choice: ");

            int choice = getIntInput();
            switch (choice) {
                case 1 -> addBook();
                case 2 -> viewAllBooks();
                case 3 -> searchBook();
                case 4 -> updateBook();
                case 5 -> deleteBook();
                case 6 -> viewAllIssuedBooks();
                case 7 -> sortBooks();
                case 0 -> { running = false; ConsoleColors.printInfo("Logged out."); }
                default -> ConsoleColors.printWarning("Invalid option. Try again.");
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 1. ADD BOOK
    // ─────────────────────────────────────────────────────────────
    private void addBook() {
        ConsoleColors.printHeader("ADD NEW BOOK");
        System.out.print("  Title       : "); String title  = sc.nextLine().trim();
        System.out.print("  Author      : "); String author = sc.nextLine().trim();
        System.out.print("  Genre       : "); String genre  = sc.nextLine().trim();
        System.out.print("  ISBN        : "); String isbn   = sc.nextLine().trim();
        System.out.print("  Total Copies: "); int copies    = getIntInput();

        Book book = new Book(title, author, genre, isbn, copies);
        if (bookDAO.addBook(book)) {
            ConsoleColors.printSuccess("Book added successfully!");
        } else {
            ConsoleColors.printError("Failed to add book. ISBN may already exist.");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 2. VIEW ALL BOOKS
    // ─────────────────────────────────────────────────────────────
    public void viewAllBooks() {
        ConsoleColors.printHeader("ALL BOOKS");
        List<Book> books = bookDAO.getAllBooks();
        if (books.isEmpty()) {
            ConsoleColors.printWarning("No books found in the library.");
            return;
        }
        printBookTableHeader();
        books.forEach(b -> System.out.println(b));
        ConsoleColors.printDivider();
        ConsoleColors.printInfo("Total books: " + books.size());
    }

    // ─────────────────────────────────────────────────────────────
    // 3. SEARCH BOOK
    // ─────────────────────────────────────────────────────────────
    private void searchBook() {
        ConsoleColors.printHeader("SEARCH BOOK");
        System.out.print("  Enter title / author / ISBN: ");
        String keyword = sc.nextLine().trim();

        List<Book> results = bookDAO.searchBooks(keyword);
        if (results.isEmpty()) {
            ConsoleColors.printWarning("No books found for: " + keyword);
        } else {
            printBookTableHeader();
            results.forEach(b -> System.out.println(b));
            ConsoleColors.printDivider();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 4. UPDATE BOOK
    // ─────────────────────────────────────────────────────────────
    private void updateBook() {
        ConsoleColors.printHeader("UPDATE BOOK");
        System.out.print("  Enter Book ID to update: ");
        int id = getIntInput();

        Book existing = bookDAO.getBookById(id);
        if (existing == null) {
            ConsoleColors.printError("Book not found with ID: " + id);
            return;
        }

        System.out.println("  Current title  : " + existing.getTitle());
        System.out.print("  New title (Enter to keep): ");
        String title = sc.nextLine().trim();
        if (!title.isEmpty()) existing.setTitle(title);

        System.out.println("  Current author : " + existing.getAuthor());
        System.out.print("  New author (Enter to keep): ");
        String author = sc.nextLine().trim();
        if (!author.isEmpty()) existing.setAuthor(author);

        System.out.println("  Current genre  : " + existing.getGenre());
        System.out.print("  New genre (Enter to keep): ");
        String genre = sc.nextLine().trim();
        if (!genre.isEmpty()) existing.setGenre(genre);

        System.out.println("  Current copies : " + existing.getTotalCopies());
        System.out.print("  New total copies (0 to keep): ");
        int copies = getIntInput();
        if (copies > 0) {
            int diff = copies - existing.getTotalCopies();
            existing.setTotalCopies(copies);
            existing.setAvailableCopies(Math.max(0, existing.getAvailableCopies() + diff));
        }

        if (bookDAO.updateBook(existing)) {
            ConsoleColors.printSuccess("Book updated successfully!");
        } else {
            ConsoleColors.printError("Update failed.");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 5. DELETE BOOK
    // ─────────────────────────────────────────────────────────────
    private void deleteBook() {
        ConsoleColors.printHeader("DELETE BOOK");
        System.out.print("  Enter Book ID to delete: ");
        int id = getIntInput();

        Book b = bookDAO.getBookById(id);
        if (b == null) { ConsoleColors.printError("Book not found."); return; }

        System.out.println("  Book: " + b.getTitle() + " by " + b.getAuthor());
        System.out.print("  Confirm delete? (yes/no): ");
        String confirm = sc.nextLine().trim().toLowerCase();

        if (confirm.equals("yes")) {
            if (bookDAO.deleteBook(id)) ConsoleColors.printSuccess("Book deleted.");
            else                        ConsoleColors.printError("Cannot delete — book may be currently issued.");
        } else {
            ConsoleColors.printInfo("Delete cancelled.");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 6. VIEW ALL ISSUED BOOKS (Admin)
    // ─────────────────────────────────────────────────────────────
    private void viewAllIssuedBooks() {
        ConsoleColors.printHeader("ALL ISSUED BOOKS");
        List<IssuedBook> list = issuedDAO.getAllIssuedBooks();
        if (list.isEmpty()) {
            ConsoleColors.printInfo("No books are currently issued.");
            return;
        }
        printIssuedTableHeader();
        list.forEach(ib -> System.out.println(ib));
        ConsoleColors.printDivider();
    }

    // ─────────────────────────────────────────────────────────────
    // 7. SORT BOOKS
    // ─────────────────────────────────────────────────────────────
    private void sortBooks() {
        ConsoleColors.printHeader("SORT BOOKS");
        System.out.println("  Sort by:");
        System.out.println("  1. Title (A-Z)");
        System.out.println("  2. Author (A-Z)");
        System.out.println("  3. Genre");
        System.out.println("  4. Available Copies (High to Low)");
        System.out.print("  Choice: ");
        int choice = getIntInput();

        List<Book> books = bookDAO.getAllBooks();
        switch (choice) {
            case 1 -> books.sort(Comparator.comparing(Book::getTitle));
            case 2 -> books.sort(Comparator.comparing(Book::getAuthor));
            case 3 -> books.sort(Comparator.comparing(Book::getGenre));
            case 4 -> books.sort(Comparator.comparingInt(Book::getAvailableCopies).reversed());
            default -> { ConsoleColors.printWarning("Invalid sort option."); return; }
        }

        printBookTableHeader();
        books.forEach(b -> System.out.println(b));
        ConsoleColors.printDivider();
    }

    // ─────────────────────────────────────────────────────────────
    // PRINT HELPERS
    // ─────────────────────────────────────────────────────────────
    private void printBookTableHeader() {
        ConsoleColors.printDivider();
        System.out.printf("| %-4s | %-30s | %-20s | %-12s | %-5s | %-9s |%n",
            "ID", "Title", "Author", "Genre", "Total", "Available");
        ConsoleColors.printDivider();
    }

    private void printIssuedTableHeader() {
        ConsoleColors.printDivider();
        System.out.printf("| %-4s | %-25s | %-15s | %-12s | %-12s | %-10s | %-8s |%n",
            "ID", "Book Title", "Member", "Issue Date", "Due Date", "Status", "Fine(₹)");
        ConsoleColors.printDivider();
    }

    private int getIntInput() {
        try {
            String line = sc.nextLine().trim();
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
