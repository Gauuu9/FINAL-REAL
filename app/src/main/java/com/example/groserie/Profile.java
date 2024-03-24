package com.example.groserie;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.view.View;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Profile extends AppCompatActivity {
    TextView name, email;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userId;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.textView14);
        email = findViewById(R.id.textView23);
        logout = findViewById(R.id.button8); // Assuming you have a button with id logoutButton in your layout

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = auth.getCurrentUser().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("name").getValue(String.class);
                    String userEmail = dataSnapshot.child("email").getValue(String.class);
                    if (userName != null && userEmail != null) {
                        name.setText(userName);
                        email.setText(userEmail);
                    } else {
                        // Handle null values for name or email
                        Toast.makeText(Profile.this, "User data is null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case where data for this user does not exist
                    Toast.makeText(Profile.this, "User data does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Failed to fetch user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });

        logout.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                FirebaseAuth.getInstance().signOut();
                // Displaying a toast message
                Toast.makeText(Profile.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                // Redirecting the user to the login screen
                startActivity(new Intent(Profile.this, Login.class));
                finish(); // Finishing the current activity
            }

        });
    }
}

