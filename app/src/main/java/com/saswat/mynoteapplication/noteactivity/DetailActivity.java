package com.saswat.mynoteapplication.noteactivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.saswat.mynoteapplication.R;

public class DetailActivity extends AppCompatActivity {
    Intent data_intent;

    //@RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data_intent = getIntent();

        TextView content = findViewById(R.id.title_content);
        TextView title = findViewById(R.id.toolbar_title);
        content.setMovementMethod(new ScrollingMovementMethod());
        content.setText(data_intent.getStringExtra("content"));
        title.setText(data_intent.getStringExtra("title"));
        int code = data_intent.getIntExtra("code", 0);
        content.setBackgroundColor(getResources().getColor(code));

        FloatingActionButton fab = findViewById(R.id.detail_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditActivity.class);
                intent.putExtra("title", data_intent.getStringExtra("title"));
                intent.putExtra("content", data_intent.getStringExtra("content"));
                //Todo senting the noteId to EditActivity for the getting current postion of the note.
                intent.putExtra("noteId", data_intent.getStringExtra("noteId"));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}