package com.example.docspace;

public class HelperClass {

    // Private fields to store the user's name, email, and password
    String name, email, password;

    // Getter method for the 'name' field
    public String getName() {
        return name;
    }

    // Setter method for the 'name' field
    public void setName(String name) {
        this.name = name;
    }

    // Setter method for the 'name' field
    public String getEmail() {
        return email;
    }

    // Setter method for the 'email' field
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter method for the 'password' field
    public String getPassword() {
        return password;
    }

    // Setter method for the 'password' field
    public void setPassword(String password) {
        this.password = password;
    }

    // Constructor that initializes the class with name, email, and password
    public HelperClass(String name, String email, String password) {
        this.name = name; // Assign the provided name to the 'name' field
        this.email = email; // Assign the provided email to the 'email' field
        this.password = password; // Assign the provided password to the 'password' field
    }


    public HelperClass() {
        // This constructor allows creating an empty object without setting fields initially
    }


}
