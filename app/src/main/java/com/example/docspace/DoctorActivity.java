package com.example.docspace;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DoctorActivity extends AppCompatActivity {

    EditText editTextUserName, editTextDateTime, editTextNumber;
    Button buttonSubmitAppointment;
    DatabaseReference databaseReference;
    ImageView arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        databaseReference = FirebaseDatabase.getInstance().getReference("Doctor Appointments");

        editTextUserName = findViewById(R.id.editTextUserName);
        editTextDateTime = findViewById(R.id.editTextDateTime);
        editTextNumber = findViewById(R.id.editTextNumber);
        buttonSubmitAppointment = findViewById(R.id.buttonSubmitAppointment);
        arrow = findViewById(R.id.arrow);

        editTextDateTime.setInputType(InputType.TYPE_NULL); // Disable the keyboard popup
        editTextDateTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(DoctorActivity.this,
                    (view, year1, month1, dayOfMonth) -> editTextDateTime.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                    year, month, day);
            datePickerDialog.show();
        });

        // Set onClickListener for the arrow ImageView
        arrow.setOnClickListener(v -> {
            // Open HomeActivity when the arrow is clicked
            Intent intent = new Intent(DoctorActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        // Set onClickListener for the button
        buttonSubmitAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAppointmentData();
            }
        });
    }

    private void saveAppointmentData() {
        String userName = editTextUserName.getText().toString().trim();
        String dateTime = editTextDateTime.getText().toString().trim();
        String phoneNumber = editTextNumber.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            editTextUserName.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(dateTime)) {
            editTextDateTime.setError("Date & Time are required");
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            editTextNumber.setError("Phone Number is required");
            return;
        }

        // unique key for each appointment
        String appointmentId = databaseReference.push().getKey();

        //map to store appointment data
        Map<String, String> appointmentData = new HashMap<>();
        appointmentData.put("userName", userName);
        appointmentData.put("dateTime", dateTime);
        appointmentData.put("phoneNumber", phoneNumber);

        if (appointmentId != null) {
            // Save data under appointment ID
            databaseReference.child(appointmentId).setValue(appointmentData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(DoctorActivity.this, "Appointment booked successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DoctorActivity.this, "Failed to book appointment", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
