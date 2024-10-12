package com.example.docspace;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    int PICK_IMAGE_REQUEST = 1;
    String TAG = "ProfileActivity";

    EditText dobEditText, editTextName, editTextGender, editTextBlood, editTextMedicalHistory;
    ImageView imageView3, maleIcon;
    DatabaseReference databaseReference;
    FirebaseUser currentUser;
    TextView username, usermail;
    Button qrButton;
    ImageView qrImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        dobEditText = findViewById(R.id.dob);
        imageView3 = findViewById(R.id.imageView3);
        maleIcon = findViewById(R.id.maleIcon);
        editTextName = findViewById(R.id.edittextname);
        editTextGender = findViewById(R.id.Gender);
        editTextBlood = findViewById(R.id.blood);
        editTextMedicalHistory = findViewById(R.id.historytext);

        username = findViewById(R.id.textView2);
        usermail = findViewById(R.id.textView3);

        qrButton = findViewById(R.id.qrbtn);
        qrImageView = findViewById(R.id.qrcode);

        loadUserData();

        // Set an OnClickListener on the ImageView to navigate to HomeActivity
        imageView3.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        // Set up date picker for dob EditText
        dobEditText.setInputType(InputType.TYPE_NULL); // Disable the keyboard popup
        dobEditText.setOnClickListener(v -> {
            // Get current date
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileActivity.this,
                    (view, year1, month1, dayOfMonth) -> dobEditText.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                    year, month, day);
            datePickerDialog.show();
        });

        // Set up camera icon click listener to allow image selection from gallery
        ImageView camIcon = findViewById(R.id.camicon);
        camIcon.setOnClickListener(v -> {
            // Open gallery using Intent
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Set up save button to save user data
        findViewById(R.id.save).setOnClickListener(v -> {
            Log.d(TAG, "Save button clicked");
            saveUserData();
        });

        // Set up logout button
        findViewById(R.id.logout).setOnClickListener(v -> logout());

        // Set up QR code button
        qrButton.setOnClickListener(v -> {
            // Collect user data
            String name = editTextName.getText().toString().trim();
            String dob = dobEditText.getText().toString().trim();
            String gender = editTextGender.getText().toString().trim();
            String bloodType = editTextBlood.getText().toString().trim();
            String medicalHistory = editTextMedicalHistory.getText().toString().trim();

            // Combine the data into a single string
            String qrData = "Name: " + name + "\n" +
                    "DOB: " + dob + "\n" +
                    "Gender: " + gender + "\n" +
                    "Blood Type: " + bloodType + "\n" +
                    "Medical History: " + medicalHistory;

            try {
                // Generate the QR code
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                BitMatrix bitMatrix = multiFormatWriter.encode(qrData, BarcodeFormat.QR_CODE, 500, 500);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                // Display the QR code in the ImageView
                qrImageView.setImageBitmap(bitmap);

            } catch (WriterException e) {
                e.printStackTrace();
                Toast.makeText(ProfileActivity.this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData() {
        if (currentUser != null) {
            String uid = currentUser.getUid();
            Log.d(TAG, "User ID: " + uid); // Log the user ID

            // Reference to 'users' folder in the database
            DatabaseReference userRef = databaseReference.child(uid);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Log.d(TAG, "Snapshot exists: " + snapshot.toString());

                        // fetch the User object
                        User userDetail = snapshot.getValue(User.class);
                        if (userDetail != null) {
                            username.setText(userDetail.username != null ? userDetail.username : "No username");
                            usermail.setText(userDetail.email != null ? userDetail.email : "No email");
                            editTextName.setText(userDetail.name != null ? userDetail.name : "");
                            dobEditText.setText(userDetail.dob != null ? userDetail.dob : "");
                            editTextGender.setText(userDetail.gender != null ? userDetail.gender : "");
                            editTextBlood.setText(userDetail.bloodType != null ? userDetail.bloodType : "");
                            editTextMedicalHistory.setText(userDetail.medicalHistory != null ? userDetail.medicalHistory : "");

                            Log.d(TAG, "User data loaded successfully");
                        } else {
                            Log.e(TAG, "User detail is null");
                            Toast.makeText(ProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Snapshot does not exist");
                        Toast.makeText(ProfileActivity.this, "No user data found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Failed to load personal details!", error.toException());
                    Toast.makeText(ProfileActivity.this, "Failed to load personal details!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "Current user is null");
        }
    }

    private void saveUserData() {
        if (currentUser != null) {
            String uid = currentUser.getUid();

            // map to store the user details
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("name", editTextName.getText().toString().trim());
            userMap.put("dob", dobEditText.getText().toString().trim());
            userMap.put("gender", editTextGender.getText().toString().trim());
            userMap.put("bloodType", editTextBlood.getText().toString().trim());
            userMap.put("medicalHistory", editTextMedicalHistory.getText().toString().trim());

            // Reference to 'users' folder
            DatabaseReference patientDetailRef = databaseReference.child(uid);

            // Update the user details in the database
            patientDetailRef.updateChildren(userMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                // selected image to maleIcon
                maleIcon.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();

        clearFields();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void clearFields() {
        editTextName.setText("");
        dobEditText.setText("");
        editTextGender.setText("");
        editTextBlood.setText("");
        editTextMedicalHistory.setText("");
    }
}
