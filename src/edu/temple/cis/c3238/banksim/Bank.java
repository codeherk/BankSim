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

public class Bank {

    public static final int NTEST = 10;
    private final Account[] accounts;
    private long ntransacts = 0;
    private final int initialBalance;
    private final int numAccounts;
    private boolean open; 
    private ReentrantLock aLock;
    //private Condition fundsAvailable;
    
    public Bank(int numAccounts, int initialBalance) {
        this.open = true; //initially open the bank
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        accounts = new Account[numAccounts];
        aLock = new ReentrantLock();
        //fundsAvailable = aLock.newCondition();
        
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(this, i, initialBalance);
        }
        ntransacts = 0;
    }

    public void transfer(int from, int to, int amount){
        accounts[from].waitForAvailableFunds(amount);
        
        //check if the bank is open or close before transferring funds
        if(!open){
            return;
        }

        /* Thread attempts to acquire the lock object and if the lock is not held by another thread,
         * the current thread gets exclusive ownership on the lock object. If the lock is currently held by another thread, 
         * then the current thread blocks and waits until the lock is released.
        */
        aLock.lock(); 
        try {
            // System.out.println("$" + amount + " from account " + from + " to account " + to); 
            accounts[from].withdraw(amount);
            accounts[to].deposit(amount);
            
            if (shouldTest()) test();
            
        }finally{
            aLock.unlock();
        }
    }

    public void test() {
        aLock.lock();
        try{
            int sum = 0;
            for (Account account : accounts) {
                System.out.printf("%s %s%n", 
                        Thread.currentThread().toString(), account.toString());
                sum += account.getBalance();
            }
            System.out.println(Thread.currentThread().toString() + 
                    " Sum: " + sum);
            if (sum != numAccounts * initialBalance) {
                System.out.println(Thread.currentThread().toString() + 
                        " Money was gained or lost");
                System.exit(1);
            } else {
                System.out.println(Thread.currentThread().toString() + 
                        " The bank is in balance");
            }
        }finally{
            aLock.unlock();
        }
    }

    public int size() {
        return accounts.length;
    }
    
    
    public synchronized boolean shouldTest() {
        return ++ntransacts % NTEST == 0;
    }
    
    // is the bank open
    public synchronized boolean isOpen(){
        return open;
    }
    
    // closing the bank
    public void close(){
        synchronized(this){
            open = false;
        }
        //notify each account that the banks has closed
        for (Account account : accounts){
            synchronized(account){
                account.notifyAll();
            }   
        }
    }

}
