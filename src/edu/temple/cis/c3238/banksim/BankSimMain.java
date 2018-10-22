package edu.temple.cis.c3238.banksim;

/**
 * @author Cay Horstmann
 * @author Paul Wolfgang
 * @author Charles Wang
 * @author Modified by Byron Jenkins
 * @author Modified by Victor Dang
 */

public class BankSimMain {

    public static final int NACCOUNTS = 10;
    public static final int INITIAL_BALANCE = 10000;

    public static void main(String[] args) {
        Bank b = new Bank(NACCOUNTS, INITIAL_BALANCE);
        Thread[] threads = new Thread[NACCOUNTS];
        Thread testThread = new TestThread(b);
        
        // Start a thread for each account
        for (int i = 0; i < NACCOUNTS; i++) {
            threads[i] = new TransferThread(b, i, INITIAL_BALANCE);
            threads[i].start();
        }
        testThread.start();

         //b.test();
          System.out.printf("Bank transfer is in the process.\n");
    }
}


