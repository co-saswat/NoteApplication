package com.saswat.mynoteapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.saswat.mynoteapplication.adapter.CustomAdapter;
import com.saswat.mynoteapplication.adapter.Notes;
import com.saswat.mynoteapplication.auth.LoginActivity;
import com.saswat.mynoteapplication.auth.RegisterActivity;
import com.saswat.mynoteapplication.noteactivity.AddActivity;
import com.saswat.mynoteapplication.noteactivity.DetailActivity;
import com.saswat.mynoteapplication.noteactivity.EditActivity;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView view;
    Toolbar toolbar;
    RecyclerView recyclerView;
    CustomAdapter adapter;
    FirebaseFirestore firestore;
    FirestoreRecyclerAdapter<Notes, NotesViewHolder> noteAdapter;
    FirebaseUser user;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar_contain);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.rc_view);

        //Todo access the firestore and retrive the data from firebase database and store and display in recyclerview
        firestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        //Todo notes > userid > mynotes > allmynotes
        Query query = firestore.collection("notes").document(user.getUid()).collection("mynotes").orderBy("title", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Notes> all_notes = new FirestoreRecyclerOptions.Builder<Notes>()
                .setQuery(query, Notes.class)
                .build();


        noteAdapter = new FirestoreRecyclerAdapter<Notes, NotesViewHolder>(all_notes) {
            @Override
            protected void onBindViewHolder(@NonNull NotesViewHolder notesViewHolder, final int i, @NonNull final Notes notes) {
                notesViewHolder.noteTitle.setText(notes.getTitle());
                notesViewHolder.noteContents.setText(notes.getContent());
                final int code = getRandomColour();
                notesViewHolder.mCardView.setCardBackgroundColor(notesViewHolder.view.getResources().getColor(code));
                final String doc_id = noteAdapter.getSnapshots().getSnapshot(i).getId();
                notesViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), DetailActivity.class);
                        intent.putExtra("title", notes.getTitle());
                        intent.putExtra("content", notes.getContent());
                        intent.putExtra("code", code);
                        //Todo to get the postion of the note we have sent the nodeId to DetailActivity
                        intent.putExtra("noteId", doc_id);
                        view.getContext().startActivity(intent);
                    }
                });
                //Todo access the menu icon and giving some activity
                final ImageView menuIcon = notesViewHolder.view.findViewById(R.id.menuIcon);
                menuIcon.setOnClickListener(new View.OnClickListener() {
                    //                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(final View view) {
                        final String doc_id = noteAdapter.getSnapshots().getSnapshot(i).getId();
                        //Todo first option is edit , clicking it the user can edit the note
                        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
//                        int gravity = GravityCompat.END;
//                        popupMenu.setGravity(gravity);
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                Intent intent = new Intent(view.getContext(), EditActivity.class);
                                intent.putExtra("title", notes.getTitle());
                                intent.putExtra("content", notes.getContent());
                                //Todo senting the noteId to EditActivity for the getting current postion of the note.
                                intent.putExtra("noteId", doc_id);
                                startActivity(intent);
                                return false;
                            }
                        });
                        //Todo second option is delete , by clicking it the user can delete the note
                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                //Todo we are getting the reference from the firebase then delete the elements
                                DocumentReference reference = firestore.collection("notes").document(doc_id);
                                reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //delete the note
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Error it Delete the Note", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        //Todo show the popup menu
                        popupMenu.show();
                    }
                });

            }


            @NonNull
            @Override
            public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_notes_view, parent, false);
                return new NotesViewHolder(view);
            }
        };


        drawerLayout = findViewById(R.id.dropdown_menu);
        view = findViewById(R.id.nav_view);
        view.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        //Todo testing purpose of the app
//        ArrayList<String> title = new ArrayList<>();
//        ArrayList<String> contents = new ArrayList<>();
//        title.add("First Note");
//        contents.add("First Contents");
//        title.add("Second Note");
//        contents.add("Second Contents");
//        title.add("Third Note");
//        contents.add("Third Contents");

        //adapter = new CustomAdapter(title, contents);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(noteAdapter);

        //Todo setting the user name and email in nav_header
        View headerView = view.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.tv_username);
        TextView useremail = headerView.findViewById(R.id.user_email);

        if (user.isAnonymous()) {
            username.setText("Temporary User");
            useremail.setVisibility(View.GONE);
        } else {
            username.setText(user.getDisplayName());
            useremail.setText(user.getEmail());
        }

        FloatingActionButton fab_main = findViewById(R.id.fab_main);
        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), AddActivity.class));
            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.add_notes:
                startActivity(new Intent(this, AddActivity.class));
                break;
            case R.id.sync_user:
                if (user.isAnonymous()) {
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    Toast.makeText(this, "Your Already Connected", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.logout:
                checkUser();
                break;
            default:
                Toast.makeText(this, "Coming Soon!!!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void checkUser() {
        //Todo check the user if the user is a verified user or anonymous user
        if (user.isAnonymous()) {
            displayAlert();
        } else {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), SplashActivity.class));
            finish();
        }
    }
//    Todo alert to user if the user is an anonymous user and try to logout ,which provide a message to the user that we are going the delete data from the app and you can't access them again
//    Todo else you can sign up

    private void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are you Sure ?")
                .setMessage("You are logged in with a Temporary Account.Logged out will delete all the Notes?")
                .setPositiveButton("Sync Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                        finish();
                    }
                }).setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Todo Delete the notes create by anonymous user

                        //Todo the delete anonymous user
                        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                                finish();
                            }
                        });
                    }
                });
        warning.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    //Todo giving random colors to the notes
    private int getRandomColour() {
        ArrayList<Integer> color_code = new ArrayList<>();
        color_code.add(R.color.blue);
        color_code.add(R.color.lightPurple);
        color_code.add(R.color.yellow);
        color_code.add(R.color.lightGreen);
        color_code.add(R.color.pink);
        color_code.add(R.color.sky_blue);
        color_code.add(R.color.gray);
        color_code.add(R.color.red);
        color_code.add(R.color.green_light);
        color_code.add(R.color.pickys_violet);

        Random random_color_code = new Random();
        int n = random_color_code.nextInt(color_code.size());
        return color_code.get(n);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle;
        TextView noteContents;
        View view;
        CardView mCardView;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.titles);
            noteContents = itemView.findViewById(R.id.content);
            mCardView = itemView.findViewById(R.id.card_note_view);
            view = itemView;
        }
    }
}