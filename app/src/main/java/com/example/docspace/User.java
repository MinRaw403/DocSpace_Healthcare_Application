package com.example.docspace;

public class User {
    // Fields to store user information
    public String username;
    public String email;
    public String name;
    public String dob;
    public String gender;
    public String bloodType;
    public String medicalHistory;
    
    public User() {
    }

    // Parameterized constructor to initialize all fields
    public User(String username, String email, String name, String dob, String gender, String bloodType, String medicalHistory) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.bloodType = bloodType;
        this.medicalHistory = medicalHistory;
    }
}
