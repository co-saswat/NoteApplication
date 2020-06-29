package com.saswat.mynoteapplication.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.saswat.mynoteapplication.MainActivity;
import com.saswat.mynoteapplication.R;

public class RegisterActivity extends AppCompatActivity {
    EditText rUser_name, rEmail, rPassword, rConfirm_password;
    Button rBtn_create;
    TextView rTv_login;
    ProgressBar progressBar;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // getSupportActionBar().setTitle("Create New User");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fAuth = FirebaseAuth.getInstance();

        //Todo access the EditText , TextView , Button and ProgressBar
        rUser_name = findViewById(R.id.et_register_user_name);
        rEmail = findViewById(R.id.et_register_user_email);
        rPassword = findViewById(R.id.et_register_password);
        rConfirm_password = findViewById(R.id.et_register_password_confirm);
        rBtn_create = findViewById(R.id.btn_register_create_account);
        rTv_login = findViewById(R.id.tv_register_login);
        progressBar = findViewById(R.id.register_progressBar);

        rTv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        rBtn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Todo getting the values from the represented edittext fields
                final String username = rUser_name.getText().toString();
                String email = rEmail.getText().toString();
                String password = rPassword.getText().toString();
                String confirm_password = rConfirm_password.getText().toString();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm_password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please Fillup the Represented Field", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirm_password)) {
                    rConfirm_password.setError("Not Matching");
                }

                progressBar.setVisibility(View.VISIBLE);
                //Todo creating credential for the annoymous user
                AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                fAuth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(RegisterActivity.this, "Note Sync", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        //Todo getting the username from firebase auth
                        FirebaseUser user = fAuth.getCurrentUser();
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build();
                        user.updateProfile(request);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Try Again !!!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }
}