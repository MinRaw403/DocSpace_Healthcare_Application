package com.example.docspace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class HomeActivity extends AppCompatActivity {

    ImageButton profileButton;
    FirebaseAuth mAuth;
    TextView greetingName;
    CardView cardView3, maleDocCardView;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");


        profileButton = findViewById(R.id.profilebutton);
        greetingName = findViewById(R.id.Greetingname);
        cardView3 = findViewById(R.id.cardView3);
        maleDocCardView = findViewById(R.id.maleDocCardView);

        // Set onClickListener for the profile button
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Set onClickListener for the doctor card view
        maleDocCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, DoctorActivity.class);
                startActivity(intent);
            }
        });

        // Set onClickListener for the map card view
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        // Get user and display their username
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference userRef = databaseReference.child(uid);

            userRef.child("username").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String username = snapshot.getValue(String.class);
                        greetingName.setText(username != null ? username : "User");
                    } else {
                        greetingName.setText("User");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    greetingName.setText("User");
                }
            });
        }
    }
}
