package com.library.util;

/**
 * ANSI color codes for colored console output.
 */
public class ConsoleColors {
    public static final String RESET   = "\u001B[0m";
    public static final String RED     = "\u001B[31m";
    public static final String GREEN   = "\u001B[32m";
    public static final String YELLOW  = "\u001B[33m";
    public static final String BLUE    = "\u001B[34m";
    public static final String CYAN    = "\u001B[36m";
    public static final String WHITE   = "\u001B[37m";
    public static final String BOLD    = "\u001B[1m";

    public static void printSuccess(String msg) {
        System.out.println(GREEN + "✔ " + msg + RESET);
    }

    public static void printError(String msg) {
        System.out.println(RED + "✘ " + msg + RESET);
    }

    public static void printInfo(String msg) {
        System.out.println(CYAN + "ℹ " + msg + RESET);
    }

    public static void printWarning(String msg) {
        System.out.println(YELLOW + "⚠ " + msg + RESET);
    }

    public static void printHeader(String title) {
        String line = "=".repeat(55);
        System.out.println(BOLD + BLUE + "\n" + line);
        System.out.println("   " + title);
        System.out.println(line + RESET);
    }

    public static void printDivider() {
        System.out.println(BLUE + "-".repeat(55) + RESET);
    }
}
