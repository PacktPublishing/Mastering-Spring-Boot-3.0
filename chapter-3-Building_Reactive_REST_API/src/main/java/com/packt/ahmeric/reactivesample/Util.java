package com.packt.ahmeric.reactivesample;
public class Util {
    public static void main(String[] args){
        // This is a pseudo-code representation for generating SQL statements.
// Use a scripting language or SQL generator to create these statements.

        for (int i = 1; i <= 10000; i++) {
            System.out.println("INSERT INTO users (id, name, email) VALUES (" + i + ", 'User" + i + "', 'user" + i + "@example.com');");
        }

    }
}
