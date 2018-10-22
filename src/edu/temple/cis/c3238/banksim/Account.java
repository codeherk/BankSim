package edu.temple.cis.c3238.banksim;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Cay Horstmann
 * @author Paul Wolfgang
 * @author Charles Wang
 * @author Modified by Byron Jenkins
 * @author Modified by Victor Dang
 */
public class Account {

    private volatile int balance;
    private final int id;
    private final Bank myBank;
    
    public Account(Bank myBank, int id, int initialBalance) {
        this.myBank = myBank;
        this.id = id;
        balance = initialBalance;
    }

    public int getBalance() {
        return balance;
    }

     public synchronized boolean withdraw(int amount) {   
        if (amount <= balance) {
            int currentBalance = balance;
            Thread.yield(); // Try to force collision
            int newBalance = currentBalance - amount;
            balance = newBalance;
            return true;
        } else {
            return false;
        }
    }
 

    public synchronized void deposit(int amount) {
  
        int currentBalance = balance;
        Thread.yield();   // Try to force collision
        int newBalance = currentBalance + amount;
        balance = newBalance;
        notifyAll(); // wakes up all sleeping threads that are waiting on object's monitor
    }
    
    @Override
    public String toString() {
        return String.format("Account[%d] balance %d", id, balance);
    }

    
   public synchronized void waitForAvailableFunds(int amount){
        //if bank is open and current balance is less than or equal to the amount, wait
        while(myBank.isOpen() && balance <= amount){
            try{
                wait(); // causes the thread to wait for another thread to call notify() or notifyAll()
                //fundsAvailableCondition.await();
            }catch(InterruptedException ex){
            }
        }
    }
}
