package com.library.service;

import com.library.dao.BookDAO;
import com.library.dao.IssuedBookDAO;
import com.library.model.Book;
import com.library.model.IssuedBook;
import com.library.model.Member;
import com.library.util.ConsoleColors;

import java.util.List;
import java.util.Scanner;

/**
 * Service class handling all User (member) operations.
 */
public class UserService {

    private final BookDAO       bookDAO   = new BookDAO();
    private final IssuedBookDAO issuedDAO = new IssuedBookDAO();
    private final Scanner       sc        = new Scanner(System.in);
    private final Member        member;

    public UserService(Member member) {
        this.member = member;
    }

    // ─────────────────────────────────────────────────────────────
    // USER MENU
    // ─────────────────────────────────────────────────────────────
    public void showUserMenu() {
        boolean running = true;
        while (running) {
            ConsoleColors.printHeader("USER PANEL  —  Welcome, " + member.getName());
            System.out.println("  1. Browse All Books");
            System.out.println("  2. Issue a Book");
            System.out.println("  3. Return a Book");
            System.out.println("  4. View My Issued Books");
            System.out.println("  5. Search Book");
            System.out.println("  0. Logout");
            ConsoleColors.printDivider();
            System.out.print("  Enter choice: ");

            int choice = getIntInput();
            switch (choice) {
                case 1 -> browseBooks();
                case 2 -> issueBook();
                case 3 -> returnBook();
                case 4 -> viewMyIssuedBooks();
                case 5 -> searchBook();
                case 0 -> { running = false; ConsoleColors.printInfo("Logged out."); }
                default -> ConsoleColors.printWarning("Invalid option. Try again.");
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 1. BROWSE ALL BOOKS
    // ─────────────────────────────────────────────────────────────
    private void browseBooks() {
        ConsoleColors.printHeader("LIBRARY CATALOGUE");
        List<Book> books = bookDAO.getAllBooks();
        if (books.isEmpty()) {
            ConsoleColors.printWarning("No books available.");
            return;
        }
        printBookTableHeader();
        for (Book b : books) {
            if (b.getAvailableCopies() > 0) {
                System.out.println(b);
            } else {
                System.out.println(ConsoleColors.RED + b + ConsoleColors.RESET + " [NOT AVAILABLE]");
            }
        }
        ConsoleColors.printDivider();
    }

    // ─────────────────────────────────────────────────────────────
    // 2. ISSUE BOOK
    // ─────────────────────────────────────────────────────────────
    private void issueBook() {
        ConsoleColors.printHeader("ISSUE BOOK");
        browseBooks();

        System.out.print("  Enter Book ID to issue: ");
        int bookId = getIntInput();

        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            ConsoleColors.printError("Book not found.");
            return;
        }
        if (book.getAvailableCopies() <= 0) {
            ConsoleColors.printError("Sorry, no copies available for: " + book.getTitle());
            return;
        }
        if (issuedDAO.isBookAlreadyIssuedToMember(bookId, member.getMemberId())) {
            ConsoleColors.printWarning("You have already issued this book.");
            return;
        }

        // Issue the book
        if (issuedDAO.issueBook(bookId, member.getMemberId())) {
            bookDAO.decrementAvailableCopies(bookId);
            ConsoleColors.printSuccess("Book issued: \"" + book.getTitle() + "\"");
            ConsoleColors.printInfo("Due date: " + java.time.LocalDate.now().plusDays(14));
            ConsoleColors.printWarning("Fine: ₹2 per day after due date.");
        } else {
            ConsoleColors.printError("Issue failed. Try again.");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 3. RETURN BOOK
    // ─────────────────────────────────────────────────────────────
    private void returnBook() {
        ConsoleColors.printHeader("RETURN BOOK");
        List<IssuedBook> myBooks = issuedDAO.getIssuedBooksByMember(member.getMemberId());

        if (myBooks.isEmpty()) {
            ConsoleColors.printInfo("You have no books to return.");
            return;
        }

        printIssuedTableHeader();
        myBooks.forEach(ib -> System.out.println(ib));
        ConsoleColors.printDivider();

        System.out.print("  Enter Issue ID to return: ");
        int issueId = getIntInput();

        // Validate that this issue belongs to the current user
        boolean belongs = myBooks.stream().anyMatch(ib -> ib.getIssueId() == issueId);
        if (!belongs) {
            ConsoleColors.printError("Invalid Issue ID.");
            return;
        }

        int bookId = issuedDAO.getBookIdFromIssueId(issueId);
        double fine = issuedDAO.returnBook(issueId);

        if (fine >= 0) {
            bookDAO.incrementAvailableCopies(bookId);
            ConsoleColors.printSuccess("Book returned successfully!");
            if (fine > 0) {
                ConsoleColors.printWarning(String.format("Late return fine: ₹%.2f", fine));
            } else {
                ConsoleColors.printInfo("No fine. Returned on time!");
            }
        } else {
            ConsoleColors.printError("Return failed. Contact admin.");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 4. VIEW MY ISSUED BOOKS
    // ─────────────────────────────────────────────────────────────
    private void viewMyIssuedBooks() {
        ConsoleColors.printHeader("MY ISSUED BOOKS");
        List<IssuedBook> myBooks = issuedDAO.getIssuedBooksByMember(member.getMemberId());

        if (myBooks.isEmpty()) {
            ConsoleColors.printInfo("You have no currently issued books.");
            return;
        }

        printIssuedTableHeader();
        for (IssuedBook ib : myBooks) {
            // Highlight overdue books in red
            if (java.time.LocalDate.now().isAfter(ib.getDueDate())) {
                System.out.println(ConsoleColors.RED + ib + "  ← OVERDUE" + ConsoleColors.RESET);
            } else {
                System.out.println(ib);
            }
        }
        ConsoleColors.printDivider();
    }

    // ─────────────────────────────────────────────────────────────
    // 5. SEARCH BOOK
    // ─────────────────────────────────────────────────────────────
    private void searchBook() {
        ConsoleColors.printHeader("SEARCH BOOK");
        System.out.print("  Enter title / author / ISBN: ");
        String keyword = sc.nextLine().trim();
        List<Book> results = bookDAO.searchBooks(keyword);
        if (results.isEmpty()) {
            ConsoleColors.printWarning("No results for: " + keyword);
        } else {
            printBookTableHeader();
            results.forEach(b -> System.out.println(b));
            ConsoleColors.printDivider();
        }
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
        try { return Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }
}
