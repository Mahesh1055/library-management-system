package com.library.service;

import com.library.dao.MemberDAO;
import com.library.model.Member;
import com.library.util.ConsoleColors;

import java.util.Scanner;

public class AuthService {

    private final MemberDAO memberDAO = new MemberDAO();
    private final Scanner   sc        = new Scanner(System.in);

    // LOGIN
    public Member login() {
        ConsoleColors.printHeader("LOGIN");
        System.out.print("  Email   : ");
        String email = sc.nextLine().trim();
        System.out.print("  Password: ");
        String password = sc.nextLine().trim();

        Member member = memberDAO.login(email, password);
        if (member != null) {
            ConsoleColors.printSuccess("Login successful! Welcome, " + member.getName() +
                " [" + member.getRole() + "]");
        } else {
            ConsoleColors.printError("Invalid email or password.");
        }
        return member;
    }

    // REGISTER
    public void register() {
        ConsoleColors.printHeader("REGISTER NEW MEMBER");
        System.out.print("  Name    : "); String name  = sc.nextLine().trim();
        System.out.print("  Email   : "); String email = sc.nextLine().trim();
        System.out.print("  Phone   : "); String phone = sc.nextLine().trim();
        System.out.print("  Password: "); String pass  = sc.nextLine().trim();

        Member m = new Member(name, email, phone, "USER", pass);
        if (memberDAO.registerMember(m)) {
            ConsoleColors.printSuccess("Registration successful! You can now log in.");
        } else {
            ConsoleColors.printError("Registration failed. Email may already be in use.");
        }
    }
}
