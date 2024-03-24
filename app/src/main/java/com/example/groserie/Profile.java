package com.example.groserie;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    Button deleteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.textView14);
        email = findViewById(R.id.textView23);
        logout = findViewById(R.id.button8);

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
        Button button22 = findViewById(R.id.button99);
        button22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, UpdateProfile.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });


        deleteButton = findViewById(R.id.deleteButton); // Add this line

        deleteButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                deleteUserAndData();
            }
        });


    }


    private void deleteUserAndData() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            // Reference to the user data in the Realtime Database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

            // Delete user account
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Account deletion successful
                        Toast.makeText(Profile.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();

                        // Delete user data from Realtime Database
                        userRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // User data deletion successful
                                    Toast.makeText(Profile.this, "User data deleted successfully", Toast.LENGTH_SHORT).show();

                                    // Redirect to login or any other appropriate action
                                    startActivity(new Intent(Profile.this, Login.class));
                                    finish();
                                } else {
                                    // User data deletion failed
                                    Toast.makeText(Profile.this, "Failed to delete user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        // Account deletion failed
                        Toast.makeText(Profile.this, "Failed to delete account: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // No user logged in
            Toast.makeText(Profile.this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }
}



