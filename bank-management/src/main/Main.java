package src.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import src.main.model.Bank;
import src.main.model.Transaction;
import src.main.model.account.Account;
import src.main.model.account.Chequing;
import src.main.model.account.Loan;
import src.main.model.account.Savings;

public class Main {

   static String ACCOUNTS_FILE = "src/main/data/accounts.txt";            
   static String TRANSACTIONS_FILE = "src/main/data/transactions.txt";

   static Bank bank = new Bank();

    public static void main(String[] args) {
        try {
            ArrayList<Account> accounts = returnAccounts(); // grab every single account from accounts.txt
            loadAccounts(accounts); // loading all these accounts into the bank object 

            ArrayList<Transaction> transactions = returnTransactions(); // grabbing every single transaction from transaction.txt
            runTransactions(transactions); //executing all transactions from bank object 
            //then after all withdrawals and deposits have been made the bank deducts taxes 
            bank.deductTaxes(); // 
            for (Account account : accounts) { // then running through every single account in accounts arraylist
                System.out.println("\n\t\t\t\t\t ACCOUNT\n\n\t"+account+"\n\n"); // we r printing tostring of that account
                transactionHistory(account.getId()); // then printing history of all transactions that occured at this account
            }
            
         } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
    //Chequing,f84c43f4-a634-4c57-a644-7602f8840870,Michael Scott,1524.51
    public static Account createObject(String[] values) { //throws Exception dont use here because u r catching exception inside the function
        //switch (values[0]) {
            //case "Chequing": return new Chequing(values[1], values[2], Double.parseDouble(values[3]));
            //case "Savings": return new Savings(values[1], values[2], Double.parseDouble(values[3]));
            //case "Loan": return new Loan(values[1], values[2], Double.parseDouble(values[3]));
            //or use a more dynamic way of creating objects
        // we are catching the exception inside the function here so function is not gonna throw exception anymore 
        try {
            return (Account)Class.forName("src.main.model.account." + values[0])
            .getConstructor(String.class, String.class, double.class)
            .newInstance(values[1], values[2], Double.parseDouble(values[3]));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

            // first u want to specify the class that you want to create an object from, the class name 
            // includes the directories that you have to drill into untill you get to it
            // its the dot not the slash for that path like thing src.main.model.account.Chequing -> "src.main.model.account." + first_element
            // Class.forName("src.main.model.account." + values[0]) here we are specifying the class from which to create object from
            // .getConstructors(String.class, String.class, double.class) and then we wanna get the constructor for that class
            // lucky that chequing savings and loan all share same constructor
            // and this line is where we paa the parameters to the constructor 
            // if u dont typecaste the above statement java wont know what type of object is being returned 
            // now u get a lot of exceptions we r not going throw all of these exceptions separately
            // instead consider exception class is base class for every single exception
            //  whether its checked or unchecked , in other words we can trean every exception in java as type exception
        //}
    }

    public static void loadAccounts(ArrayList<Account> accounts) {
        for (Account account : accounts) {
            bank.addAccount(account);
        }
    }

    public static ArrayList<Account> returnAccounts() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(ACCOUNTS_FILE);
        Scanner scan = new Scanner(fis);

        ArrayList<Account> accounts = new ArrayList<Account>();

        while (scan.hasNextLine()) {
            accounts.add(createObject(scan.nextLine().split(",")));  // scan.nextLine() that is each line in python, and we will use comma to split the line into array of string values
        }
        scan.close();
        return accounts;
    }

    public static ArrayList<Transaction> returnTransactions() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(TRANSACTIONS_FILE);
        Scanner scan = new Scanner(fis);

        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        while (scan.hasNextLine()) {
            String[] values = scan.nextLine().split(",");
            transactions.add(new Transaction(Transaction.Type.valueOf(values[1]), Long.parseLong(values[0]), values[2], Double.parseDouble(values[3])));
        }
        scan.close();
        // before returning sort arraylist based on time stamp
        Collections.sort(transactions);
        
        return transactions;

    }

    public static void runTransactions(ArrayList<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            bank.executeTransaction(transaction);
        }
    }

    public static void transactionHistory(String id) {
        System.out.println("\t\t\t\t   TRANSACTION HISTORY\n\t");
        for (Transaction transaction : bank.getTransactions(id)) {
            wait(300);
            System.out.println("\t"+transaction+"\n");            
        }
        System.out.println("\n\t\t\t\t\tAFTER TAX\n");
        System.out.println("\t" + bank.getAccount(id) +"\n\n\n\n");
    }

    /**
     * Function name: wait
     * @param milliseconds
     * 
     * Inside the function:
     *  1. Makes the code sleep for X milliseconds.
     */

     public static void wait(int milliseconds) {
         try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
         } catch (InterruptedException e) {
             System.out.println(e.getMessage());
         }
     }

}
