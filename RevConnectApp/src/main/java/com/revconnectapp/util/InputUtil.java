package com.revconnectapp.util;

import java.util.Scanner;

public class InputUtil {
    private static Scanner scanner = new Scanner(System.in);

    public static String getString() {
        return scanner.nextLine().trim();
    }

    public static int getInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }

    public static String getChoice(String... options) {
        while (true) {
            System.out.println("Choose an option:");
            for (int i = 0; i < options.length; i++) {
                System.out.println((i + 1) + ". " + options[i]);
            }
            int choice = getInt();
            if (choice > 0 && choice <= options.length) {
                return options[choice - 1];
            }
            System.out.println("Invalid option!");
        }
    }
}
