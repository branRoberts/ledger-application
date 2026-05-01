package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;


public class FinancialTracker {

    // Shared constants - stores the transactions list, CSV file name, and date/time formatters used throughout the appS
    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    // Main menu loop - displays options and allowing to navigate through the app depending on their input
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
    // Loads transactions from CSV - creates file if missing, parses each line into Transaction objects
    public static void loadTransactions(String fileName) {
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
    // Collects user input via userInputAdd, checks if it's a positive amount or not, creates a new deposit Transaction and saves it to the CSV file
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
    // Collects user input via userInputAdd, checks if it's a positive amount or not,then makes the amount negative, creates a new payment Transaction and saves it to the CSV file
    private static void addPayment(Scanner scanner) {
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
// userInputAdd method takes the user's input and returns it as array of strings to be later parsed
    private static String[] userInputAdd (Scanner scanner) {
        System.out.println("Enter Date: (yyyy-mm-dd and HH:mm:ss)");
        String addDateTime = scanner.nextLine();
        System.out.println("Enter description:");
        String addDescription = scanner.nextLine();
        System.out.println("Enter vendor:");
        String addVendor = scanner.nextLine();

        return new String[]{addDateTime,addDescription,addVendor};
    }
    // Ledger menu loop - displays options and allowing to navigate through the app depending on their input
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

    //the displayLedger loops through every transaction in the list and prints each one
    private static void displayLedger(ArrayList<Transaction> transactions) {
    for (Transaction displayTransaction : transactions) {
        System.out.println(displayTransaction.toString());
    }
    }
    //the displayDeposits loops through every transaction in the list and prints each one if the amount is greater than 0
    private static void displayDeposits(ArrayList<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > 0){
                System.out.println(transaction);
            }
        }
    }
    //the displayPayments loops through every transaction in the list and prints each one if the amount is less than 0
    private static void displayPayments(ArrayList<Transaction> transactions) {
    for (Transaction transaction : transactions) {
        if (transaction.getAmount() < 0){
            System.out.println(transaction);
        }

    }
    }

    // Reports menu case switch loop - displays options and allowing to navigate through the app depending on their input
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
                //Gets the first day of the current month as the starting point and gets the current day as the stopping point
                case "1" -> {
                    LocalDate localDateEnd = LocalDate.now();
                    LocalDate localDateStart = LocalDate.now().withDayOfMonth(1);
                    filterTransactionsByDate(localDateStart, localDateEnd);

                }
                //Gets the first day of the previous month as the starting point and gets the length of month and stops on that day as the stopping point
                case "2" -> {
                    LocalDate localDatePreviousMonthStart =LocalDate.now().minusMonths(1).withDayOfMonth(1);
                    LocalDate localDatePreviousMonthEnd = LocalDate.now().minusMonths(1).withDayOfMonth(localDatePreviousMonthStart.lengthOfMonth());
                    filterTransactionsByDate(localDatePreviousMonthStart, localDatePreviousMonthEnd);
                }
                //Gets the first day of the current year as the starting point and gets the current day as the stopping point
                case "3" -> {
                    LocalDate localDateYearStart =LocalDate.now().withDayOfYear(1);
                    LocalDate localDateEnd = LocalDate.now();
                    filterTransactionsByDate(localDateYearStart, localDateEnd);
                }
                //Gets the first day of the previous year as the starting point and gets the length of previous year and stops on that day as the stopping point
                case "4" -> {
                    LocalDate localDatePreviousYearStart = LocalDate.now().minusYears(1).withDayOfYear(1);
                    LocalDate localDatePreviousYearEnd =  localDatePreviousYearStart.withDayOfYear(localDatePreviousYearStart.lengthOfYear());
                    filterTransactionsByDate(localDatePreviousYearStart, localDatePreviousYearEnd);
                }
                // Asks the user to input a vendor to search for and stores it in the vendorName, calls the filterTransactionVendor and passing the variable as argument
                case "5" -> {
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

// filterTransactionByDate method loops through each Transaction checking if the date fall between the starting date and ending date and prints it
    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {
        for (Transaction transaction : transactions) {
            if (!transaction.getDate().isBefore(start) && !transaction.getDate().isAfter(end)) {
                System.out.println(transaction);
            }
        }
    }
    // filterTransactionByVendor method loops through each Transaction checking if the user input matches the vendor in each object and prints it
    private static void filterTransactionsByVendor(String vendor) {
        for (Transaction transaction : transactions) {
            if (transaction.getVendor().equalsIgnoreCase(vendor)) {
                System.out.println(transaction);
            }
        }
    }

    private static void customSearch(Scanner scanner) {
        // TODO – prompt for any combination of date range, description,
        //        vendor, and exact amount, then display matches
    }

    private static LocalDate parseDate(String s) {
        /* TODO – return LocalDate or null */
        return null;
    }

    private static Double parseDouble(String s) {
        /* TODO – return Double   or null */
        return null;
    }
}
