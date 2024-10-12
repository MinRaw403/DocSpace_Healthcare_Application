package com.example.docspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    Button loginbtn;
    EditText loginemail;
    EditText loginpassword;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Linking to the login activity layout

        // Initializing UI components by linking them with their respective XML IDs
        loginbtn = findViewById(R.id.loginbtn);
        loginemail = findViewById(R.id.loginemail);
        loginpassword = findViewById(R.id.loginpassword);

        // Setting up the login button click event listener
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If email or password validation fails, do nothing
                if (!validateEmail() | !validatePassword()){

                }else {
                    checkUser(); // If both fields are valid, check user credentials
                }
            }
        });


        // Set an OnClickListener to sign-in text
        TextView signInTextView = findViewById(R.id.textView12);
        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to switch from LoginActivity to RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent); // Start the new activity
            }
        });

    }
    // Method to validate the email field
    public Boolean validateEmail(){
        String val = loginemail.getText().toString();
        if (val.isEmpty()){ // If email field is empty, set error message
            loginemail.setError("Email or Password can't be Empty");
            return false;
        }else {
            loginemail.setError(null); // Clear any previous errors
            return true;
        }
    }

    // Method to validate the password field
    public Boolean validatePassword(){
        String val = loginpassword.getText().toString();
        if (val.isEmpty()){ // If password field is empty, set error message
            loginpassword.setError("Email can't be Empty");
            return false;
        }else {
            loginpassword.setError(null); // Clear any previous errors
            return true;
        }
    }

    // Method to check if user exists in Firebase database
    public void checkUser() {
        String usermail = loginemail.getText().toString().trim(); // Retrieve user email
        String userpassword = loginpassword.getText().toString().trim(); // Retrieve user password

        // Reference to the "users" node in Firebase Realtime Database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        // to find a user with the entered email in database
        Query checkUserDatabase = reference.orderByChild("email").equalTo(usermail);

        // Adding a listener to query results
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // If user exists in the database
                if (snapshot.exists()) {
                    loginemail.setError(null); // Clear any email error

                    // Get the password from the database
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                    String passwordFromDB = userSnapshot.child("password").getValue(String.class);

                    // Check if the entered password matches the password from the database
                    if (Objects.equals(passwordFromDB, userpassword)) {
                        loginemail.setError(null); // Clear any password error
                        // Intent to switch to HomeActivity if login is successful
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // If password is incorrect, show an error
                        loginpassword.setError("Invalid Credentials");
                        loginpassword.requestFocus();
                    }
                } else {
                    // If user does not exist in the database, show an error
                    loginemail.setError("User does not exist");
                    loginemail.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error if any (can add logging or show a message)
            }
        });
    }

}
