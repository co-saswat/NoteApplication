package com.saswat.mynoteapplication.noteactivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saswat.mynoteapplication.R;

import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private EditText contentTitle, note_content;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        contentTitle = findViewById(R.id.add_note_title);
        note_content = findViewById(R.id.add_notes_content);

        bar = findViewById(R.id.progressBar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO first checking if the fields are fillup or not else Store the data in the Firebase Database
                String mtitle = contentTitle.getText().toString();
                String mcontent = note_content.getText().toString();
                //Todo checking the fields
                if (mtitle.isEmpty() || mcontent.isEmpty()) {
                    Toast.makeText(AddActivity.this, "Please Fill up the Fields ", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Todo visible the progress bar
                bar.setVisibility(View.VISIBLE);
                //else store in the firebase database
                DocumentReference doc_ref = firestore.collection("notes").document(user.getUid()).collection("mynotes").document();
                Map<String, Object> note = new HashMap<>();
                note.put("title", mtitle);
                note.put("content", mcontent);
                doc_ref.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddActivity.this, "Note Added", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddActivity.this, "Failed to Add the note", Toast.LENGTH_SHORT).show();
                        bar.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.close_nave, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.close_note) {
            Toast.makeText(this, "Note isn't Save", Toast.LENGTH_SHORT).show();
            onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }
}