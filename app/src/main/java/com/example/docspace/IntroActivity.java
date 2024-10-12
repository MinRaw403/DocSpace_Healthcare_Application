package com.example.docspace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class IntroActivity extends AppCompatActivity {

    // Declare variables for the UI elements
    AppCompatButton introButton;
    TextView signInText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);  // Set the layout for this activity

        // Initialize views from the XML layout
        introButton = findViewById(R.id.introbtn);  // Button with ID "introbtn"
        signInText = findViewById(R.id.textView12); // TextView with ID "textView12"

        // Set an OnClickListener for the intro button
        introButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to open the RegisterActivity when the button is clicked
                Intent intent = new Intent(IntroActivity.this, RegisterActivity.class);
                startActivity(intent); // Start the RegisterActivity
            }
        });

        // Set an OnClickListener for the sign-in text
        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to open the LoginActivity when the text is clicked
                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(intent); // Start the LoginActivity
            }
        });
    }
}
