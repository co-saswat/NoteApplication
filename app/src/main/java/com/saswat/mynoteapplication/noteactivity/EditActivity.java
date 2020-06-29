package com.saswat.mynoteapplication.noteactivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.saswat.mynoteapplication.MainActivity;
import com.saswat.mynoteapplication.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditActivity extends AppCompatActivity {

    Intent data;
    EditText edit_note_title, edit_note_content;
    FirebaseFirestore firestore;
    ProgressBar bar;
    FirebaseUser user;
    FirebaseAuth mfAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        //Todo if user the have edit a note then we have to save that edited note in the firebase database
        firestore = FirebaseFirestore.getInstance();
        mfAuth = FirebaseAuth.getInstance();
        user = mfAuth.getCurrentUser();

        Toolbar toolbar = findViewById(R.id.edit_toolbar_title);
        setSupportActionBar(toolbar);

        bar = findViewById(R.id.edit_progressBar);


        edit_note_title = findViewById(R.id.toolbar_edit_title);
        edit_note_content = findViewById(R.id.et_text_editor);
        //Todo access the editText and access the data in represented edittext
        data = getIntent();
        String note_title = data.getStringExtra("title");
        String note_content = data.getStringExtra("content");

        edit_note_title.setText(note_title);
        edit_note_content.setText(note_content);

        //Todo when the user click the floating button the edited data will be store in the firebase database
        FloatingActionButton fab_edit = findViewById(R.id.edit_fab);
        fab_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO first checking if the fields are fillup or not else Store the data in the Firebase Database
                String mtitle = edit_note_title.getText().toString();
                String mcontent = edit_note_content.getText().toString();
                //Todo checking the fields
                if (mtitle.isEmpty() || mcontent.isEmpty()) {
                    Toast.makeText(EditActivity.this, "Please Fill up the Fields ", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Todo visible the progress bar
                bar.setVisibility(View.VISIBLE);
                //Todo else edited data store in the firebase database and to get the current postion of the notes
                DocumentReference doc_ref = firestore.collection("notes").document(user.getUid()).collection("mynotes").document(Objects.requireNonNull(data.getStringExtra("noteId")));
                Map<String, Object> note = new HashMap<>();
                note.put("title", mtitle);
                note.put("content", mcontent);
                doc_ref.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditActivity.this, "Note Edited", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditActivity.this, "Failed to Edit the note", Toast.LENGTH_SHORT).show();
                        bar.setVisibility(View.VISIBLE);
                    }
                });

            }
        });

    }
}