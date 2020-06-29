package com.saswat.mynoteapplication.auth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saswat.mynoteapplication.MainActivity;
import com.saswat.mynoteapplication.R;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText lEmail, lPassword;
    Button btn_login;
    TextView l_forget_password, l_create_account;
    ProgressBar progressBar;

    FirebaseAuth fAuth;
    FirebaseFirestore firestore;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getSupportActionBar().setTitle("Login");
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        lEmail = findViewById(R.id.et_login_email);
        lPassword = findViewById(R.id.et_login_password);
        btn_login = findViewById(R.id.btn_login_btn);
        l_forget_password = findViewById(R.id.tv_login_forgot_pasword);
        l_create_account = findViewById(R.id.tv_login_create_account);
        progressBar = findViewById(R.id.login_progressBar);

        showWarning();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = lEmail.getText().toString();
                String password = lPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please Fill up the Necessary Fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //Todo check the user is annoymous user or not ; if yes, then delete the exiting notes
                if (Objects.requireNonNull(fAuth.getCurrentUser()).isAnonymous()) {
                    user = fAuth.getCurrentUser();

                    firestore.collection("notes").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(LoginActivity.this, "Delete all Temporary Accounts", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //Todo delete the user
                    user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(LoginActivity.this, "Temporary user is delete", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


                fAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(LoginActivity.this, "SuccessFull LoggedIn", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void showWarning() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are you Sure ?")
                .setMessage("Lining the exiting Account will delete all the temp notes.Create a new Account.")
                .setPositiveButton("Save it", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                    }
                }).setNegativeButton("It's ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Todo Delete the notes create by anonymous user

                        //Todo the delete anonymous user
                        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getApplicationContext(), SplashActivity.class));
//                                finish();
//                            }
//                        });
                    }
                });
        warning.show();
    }
}