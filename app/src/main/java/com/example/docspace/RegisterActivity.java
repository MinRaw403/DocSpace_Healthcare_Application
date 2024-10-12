package com.example.docspace;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    // Declaring EditTexts and TextView used in the layout
    EditText registerName, registerMail, registerPassword, dob, gendar, blood, historytext;
    TextView logintotext;
    FirebaseAuth mAuth; // FirebaseAuth instance for handling authentication

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initializing all EditText and TextView elements by linking them with their XML counterparts
        registerMail = findViewById(R.id.registerMail);
        registerPassword = findViewById(R.id.registerPassword);
        registerName = findViewById(R.id.registerName);
        dob = findViewById(R.id.dob);
        gendar = findViewById(R.id.gendar);
        blood = findViewById(R.id.blood);
        historytext = findViewById(R.id.historytext);
        mAuth = FirebaseAuth.getInstance();
        logintotext = findViewById(R.id.logintotext);

        // Date picker setup for dob
        dob.setInputType(InputType.TYPE_NULL);
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // current date
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);


                // Create a DatePickerDialog to let the user pick their date of birth
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Set selected date to dob EditText
                                dob.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        // Set click listener to Sign In text
        logintotext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the LoginActivity when the Sign In text is clicked
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent); // Navigate to the LoginActivity
            }
        });

        // Set click listener for the register button to call registerUser method
        findViewById(R.id.registerbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();// Call registerUser() when the register button is clicked
            }
        });
    }

    // Method to register a new user
    public void registerUser() {
        // Retrieve data from input fields
        String email = registerMail.getText().toString().trim();
        String password = registerPassword.getText().toString().trim();
        String username = registerName.getText().toString().trim();
        String dateOfBirth = dob.getText().toString().trim();
        String gender = gendar.getText().toString().trim();
        String bloodType = blood.getText().toString().trim();
        String medicalHistory = historytext.getText().toString().trim();

        // Check if all fields are filled, if not, show a message and return
        if (email.isEmpty() || password.isEmpty() || username.isEmpty() ||
                dateOfBirth.isEmpty() || gender.isEmpty() || bloodType.isEmpty() ||
                medicalHistory.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If registration is successful
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid(); // Retrieve unique user ID

                            //map to store user details
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("email", email);
                            userMap.put("password", password);
                            userMap.put("username", username);
                            userMap.put("dob", dateOfBirth);
                            userMap.put("gender", gender);
                            userMap.put("bloodType", bloodType);
                            userMap.put("medicalHistory", medicalHistory);

                            // Save user details in Firebase
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
                            userRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // If saving user data in database is successful, show a success message
                                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class)); // Navigate to LoginActivity
                                        finish(); // Close the current activity
                                    } else {
                                        // If saving user data in database fails, show an error message
                                        Toast.makeText(RegisterActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            // If registration fails, show an error message
                            Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
