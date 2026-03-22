package com.employeemanagement;

import java.util.Arrays;

public class abc {

    public static void main(String[] args) {
    }
}

public class PaymentSingleton {
    private static Singleton instance;

    private Singleton() {

    }

    public synchronized static Singleton getInstance() {
        if(instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}

when 1 lakh threads came at same time what will happen ?