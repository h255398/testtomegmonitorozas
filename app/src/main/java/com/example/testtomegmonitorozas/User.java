package com.example.testtomegmonitorozas;

import java.util.ArrayList;

public class User {

    private String email;

    private ArrayList<Integer> kg;

    public String getEmail() {
        return email;
    }

    public ArrayList<Integer> getKg() {
        return kg;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setKg(ArrayList<Integer> kg) {
        this.kg = kg;
    }

    public User(String email, ArrayList<Integer> kg) {
        this.email = email;
        this.kg = kg;
    }
}
