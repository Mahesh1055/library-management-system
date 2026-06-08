package com.library.main;

import com.library.model.Member;
import com.library.service.AdminService;
import com.library.service.AuthService;
import com.library.service.UserService;
import com.library.util.ConsoleColors;
import com.library.util.DBConnection;

import java.util.Scanner;


public class LibraryApp {

    static Scanner sc = new Scanner(System.in);
    static AuthService authService = new AuthService();

    public static void main(String[] args) {

        printBanner();

        boolean running = true;

        while (running) {
            ConsoleColors.printHeader("MAIN MENU");
            System.out.println("  1. Login");
            System.out.println("  2. Register");
            System.out.println("  0. Exit");
            ConsoleColors.printDivider();
            System.out.print("  Enter choice: ");

            String input = sc.nextLine().trim();

            switch (input) {
                case "1" -> {
                    Member member = authService.login();
                    if (member != null) {
                        if ("ADMIN".equalsIgnoreCase(member.getRole())) {
                            new AdminService().showAdminMenu();
                        } else {
                            new UserService(member).showUserMenu();
                        }
                    }
                }
                case "2" -> authService.register();
                case "0" -> {
                    running = false;
                    DBConnection.closeConnection();
                    ConsoleColors.printSuccess("Thank you for using Library Management System. Goodbye!");
                }
                default -> ConsoleColors.printWarning("Invalid option. Enter 0, 1, or 2.");
            }
        }
    }

    private static void printBanner() {
        System.out.println(ConsoleColors.BOLD + ConsoleColors.CYAN);
        System.out.println("  ╔══════════════════════════════════════════════════════╗");
        System.out.println("  ║         LIBRARY MANAGEMENT SYSTEM                   ║");
        System.out.println("  ║         Built with Core Java + JDBC + MySQL          ║");
        System.out.println("  ╚══════════════════════════════════════════════════════╝");
        System.out.println(ConsoleColors.RESET);
    }
}
