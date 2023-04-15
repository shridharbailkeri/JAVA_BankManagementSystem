package src.main.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Filter;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import src.main.model.account.Account;
import src.main.model.account.Chequing;
import src.main.model.account.impl.Taxable;

public class Bank {

    private ArrayList<Account> accounts;
    private ArrayList<Transaction> transactions;

    public Bank() {
        this.accounts = new ArrayList<Account>();
        this.transactions = new ArrayList<Transaction>();
    }

    // return an array of transactions that match a particular account
    public Transaction[] getTransactions(String accountId) {
        // u might think of a forloop that goes through all transaction in transactions.txt/transactions arraylist
        // if accountId matches the transactionId then add it to array and return that
        // stream?
        List<Transaction> list = this.transactions.stream()
        .filter((transaction) -> transaction.getId().equals(accountId))
        .collect(Collectors.toList()); // this terminal operation is going to return filtered sequence of elements to a list
        return list.toArray(new Transaction[list.size()]); // problem is toArray returns array of objects, but it can receive an argument, 
        //inside argument u need to define an array of type transactions, thats how it will know i need to return array of that custom type
    }

    // getting the account for a particular transaction
    public Account getAccount(String transactionId) {
        return accounts.stream()
        .filter((account) -> account.getId().equals(transactionId)) // there should be only single element that matches transactionId 
        // so use find first
        .findFirst()
        .orElse(null);
    }

    public void addAccount(Account account) {
        this.accounts.add(account.clone());
        //clone method what it does is retunrs copy of the object that calls it , useful when class is an abstract class
    }

    // this method should not be accessible outside this class 
    private void addTransaction(Transaction transaction) { //private because we dont want to add transactions from outside of this class
        // its the bank that cann add successfull transactions or ignore the failed transactions
        this.transactions.add(new Transaction(transaction));
    }

    public void executeTransaction(Transaction transaction) {
        switch(transaction.getType()) {
            case WITHDRAW: withdrawTransaction(transaction); break;
            case DEPOSIT: depositTransaction(transaction); break;
        } 
    }

    private void withdrawTransaction(Transaction transaction) { // 
        if (getAccount(transaction.getId()).withdraw(transaction.getAmount())) { // getaccount and withdraw from that
            addTransaction(transaction); // thats why addTransaction is private 
            // if withdraw gives true then addTransaction inside bank.java        
        }
    }

    private void depositTransaction(Transaction transaction) { // getaccount and deposit to it 
        getAccount(transaction.getId()).deposit(transaction.getAmount()); // deposit doesnt retunr any boolean, so just make deposit and add that transaction
        addTransaction(transaction); 
    }

    private double getIncome(Taxable account) {
        Transaction[] transactions = getTransactions(((Chequing)account).getId()); // what if transaction method is with draw then value should be -ve 
        return Arrays.stream(transactions)   // then map this array to double, map every single element in array to double value mapToDouble
               .mapToDouble((transaction) -> {
                    switch (transaction.getType()) {
                        case WITHDRAW: return -transaction.getAmount();
                        case DEPOSIT: return transaction.getAmount();
                        default: return 0;
                    }
               }).sum(); // sum all transaction amounts and return that value , terminal operation will return sum of every single element in stream
    }
    
    public void deductTaxes() {
        // create a for each loop that goes through every account in accounts list
        
        for (Account account: accounts) {
            // how do i check if something implements taxable interface 
            // basically we r checking if this type of account implements taxable interface  
            // then typecasting the account to taxable 
            if (Taxable.class.isAssignableFrom(account.getClass())) {
                Taxable taxable = (Taxable)account;
                taxable.tax(getIncome(taxable));
            }

        }

    }


}
