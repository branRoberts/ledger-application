package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Capstone skeleton – personal finance tracker.
 * ------------------------------------------------
 * File format  (pipe-delimited)
 *     yyyy-MM-dd|HH:mm:ss|description|vendor|amount
 * A deposit has a positive amount; a payment is stored
 * as a negative amount.
 */
public class FinancialTracker {

    /* ------------------------------------------------------------------
       Shared data and formatters
       ------------------------------------------------------------------ */
    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    /* ------------------------------------------------------------------
       Main menu
       ------------------------------------------------------------------ */
    public static void main(String[] args) {
        loadTransactions(FILE_NAME);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
        scanner.close();
    }

    /* ------------------------------------------------------------------
       File I/O
       ------------------------------------------------------------------ */

    /**
     * Load transactions from FILE_NAME.
     * • If the file doesn’t exist, create an empty one so that future writes succeed.
     * • Each line looks like: date|time|description|vendor|amount
     */
    public static void loadTransactions(String fileName) {
        // TODO: create file if it does not exist, then read each line,
        //       parse the five fields, build a Transaction object,
        //       and add it to the transactions list.
        try {
            FileWriter fw = new FileWriter(fileName, true);
            fw.close();
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                LocalDate importedDate = LocalDate.parse(data[0], DATE_FMT);
                LocalTime importedTime = LocalTime.parse(data[1], TIME_FMT);
                String importedDescription = data[2];
                String importedVendor = (data[3]);
                double importedAmount = Double.parseDouble(data[4]);


                transactions.add(new Transaction(importedDate, importedTime, importedDescription, importedVendor, importedAmount));
            }
            reader.close();
        }catch (Exception e) {
            System.out.println("Error opening file: " + fileName);
        }
        transactions.sort((Transaction t1, Transaction t2) -> t2.getDate().compareTo(t1.getDate()));
    }

    /* ------------------------------------------------------------------
       Add new transactions
       ------------------------------------------------------------------ */

    /**
     * Prompt for ONE date+time string in the format
     * "yyyy-MM-dd HH:mm:ss", plus description, vendor, amount.
     * Validate that the amount entered is positive.
     * Store the amount as-is (positive) and append to the file.
     */
    private static void addDeposit(Scanner scanner) {
        String[] userInput = userInputAdd(scanner);
        LocalDateTime formattedDateTime = LocalDateTime.parse(userInput[0], DATETIME_FMT);
        LocalDate formattedDate = formattedDateTime.toLocalDate();
        LocalTime formattedTime = formattedDateTime.toLocalTime();

        double addAmount = 0;
        do{
            System.out.println("Enter amount:");
            addAmount = Double.parseDouble(scanner.nextLine());
            if (addAmount <= 0){
                System.out.println("Deposited amount: " + addAmount + " is invalid, input a positive number");
            }else {
                System.out.println("Deposited amount: " + addAmount + " is valid");
            }
        }while (addAmount <= 0);
        transactions.add(new Transaction(formattedDate, formattedTime, userInput[1], userInput[2], addAmount));
        try {
            FileWriter fw = new FileWriter(FILE_NAME,true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(formattedDate + "|" + formattedTime + "|" + userInput[1] + "|" + userInput[2] + "|" + addAmount + "\n");
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        transactions.sort((Transaction t1, Transaction t2) -> t2.getDate().compareTo(t1.getDate()));
    }

    /**
     *
     * @param scanner
     */
    private static void addPayment(Scanner scanner) {
        // TODO
        String[] userInput = userInputAdd(scanner);
        LocalDateTime formattedDateTime = LocalDateTime.parse(userInput[0], DATETIME_FMT);
        LocalDate formattedDate = formattedDateTime.toLocalDate();
        LocalTime formattedTime = formattedDateTime.toLocalTime();
        double inputPaymentAmount = 0;
        do{
            System.out.println("Enter amount:");
            inputPaymentAmount = Double.parseDouble(scanner.nextLine());
            if (inputPaymentAmount <= 0){
                System.out.println("Payment amount: " + inputPaymentAmount + " is invalid, input a positive number");
            }else {
                System.out.println("Payment amount: " + inputPaymentAmount + " is valid");
            }
        }while (inputPaymentAmount <= 0);
        transactions.add(new Transaction(formattedDate, formattedTime, userInput[1], userInput[2], -inputPaymentAmount));
        try {
            FileWriter fw = new FileWriter(FILE_NAME,true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(formattedDate + "|" + formattedTime + "|" + userInput[1] + "|" + userInput[2] + "|" + -inputPaymentAmount + "\n");
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        transactions.sort((Transaction t1, Transaction t2) -> t2.getDate().compareTo(t1.getDate()));
    }
    private static String[] userInputAdd (Scanner scanner) {
        System.out.println("Enter Date: (yyyy-mm-dd and HH:mm:ss)");
        String addDateTime = scanner.nextLine();
        System.out.println("Enter description:");
        String addDescription = scanner.nextLine();
        System.out.println("Enter vendor:");
        String addVendor = scanner.nextLine();

        return new String[]{addDateTime,addDescription,addVendor};
    }
    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {

        boolean running = true;
        while (running) {
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A" -> displayLedger(transactions);
                case "D" -> displayDeposits(transactions);
                case "P" -> displayPayments(transactions);
                case "R" -> reportsMenu(scanner);
                case "H" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Display helpers: show data in neat columns
       ------------------------------------------------------------------ */
    private static void displayLedger(ArrayList<Transaction> transactions) { /* TODO – print all transactions in column format */
    for (Transaction displayTransaction : transactions) {
        System.out.println(displayTransaction.toString());
    }
    }

    private static void displayDeposits(ArrayList<Transaction> transactions) { /* TODO – only amount > 0               */
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > 0){
                System.out.println(transaction.toString());
            }
        }
    }

    private static void displayPayments(ArrayList<Transaction> transactions) { /* TODO – only amount < 0               */
    for (Transaction transaction : transactions) {
        if (transaction.getAmount() < 0){
            System.out.println(transaction.toString());
        }

    }
    }

    /* ------------------------------------------------------------------
       Reports menu
       ------------------------------------------------------------------ */
    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();


            switch (input) {
                case "1" -> {/* TODO – month-to-date report */
                    LocalDate localDateEnd = LocalDate.now();
                    LocalDate localDateStart = LocalDate.now().withDayOfMonth(1);
                    filterTransactionsByDate(localDateStart, localDateEnd);

                }
                case "2" -> {/* TODO – previous month report */
                    LocalDate localDatePreviousMonthStart =LocalDate.now().minusMonths(1).withDayOfMonth(1);
                    LocalDate localDatePreviousMonthEnd = LocalDate.now().minusMonths(1).withDayOfMonth(localDatePreviousMonthStart.lengthOfMonth());
                    filterTransactionsByDate(localDatePreviousMonthStart, localDatePreviousMonthEnd);
                }
                case "3" -> {/* TODO – year-to-date report   */
                    LocalDate localDateYearStart =LocalDate.now().withDayOfYear(1);
                    LocalDate localDateEnd = LocalDate.now();
                    filterTransactionsByDate(localDateYearStart, localDateEnd);
                }
                case "4" -> {/* TODO – previous year report  */
                    LocalDate localDatePreviousYearStart = LocalDate.now().minusYears(1).withDayOfYear(1);
                    LocalDate localDatePreviousYearEnd =  localDatePreviousYearStart.withDayOfYear(localDatePreviousYearStart.lengthOfYear());
                    filterTransactionsByDate(localDatePreviousYearStart, localDatePreviousYearEnd);
                }
                case "5" -> {/* TODO – prompt for vendor then report */
                    System.out.println("Please enter a Vendor:");
                    String vendorName = scanner.nextLine().trim();
                    filterTransactionsByVendor(vendorName);
                }
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Reporting helpers
       ------------------------------------------------------------------ */
    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {
        // TODO – iterate transactions, print those within the range
        for (Transaction transaction : transactions) {
            if (transaction.getDate().compareTo(start) >= 0 && transaction.getDate().compareTo(end) <= 0) {
                System.out.println(transaction.toString());
            }
        }
    }

    private static void filterTransactionsByVendor(String vendor) {
        // TODO – iterate transactions, print those with matching vendor
        for (Transaction transaction : transactions) {
            if (transaction.getVendor().equalsIgnoreCase(vendor)) {
                System.out.println(transaction.toString());
            }
        }
    }

    private static void customSearch(Scanner scanner) {
        // TODO – prompt for any combination of date range, description,
        //        vendor, and exact amount, then display matches
    }

    /* ------------------------------------------------------------------
       Utility parsers (you can reuse in many places)
       ------------------------------------------------------------------ */
    private static LocalDate parseDate(String s) {
        /* TODO – return LocalDate or null */
        return null;
    }

    private static Double parseDouble(String s) {
        /* TODO – return Double   or null */
        return null;
    }
}
