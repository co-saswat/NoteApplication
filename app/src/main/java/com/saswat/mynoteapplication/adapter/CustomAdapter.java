package com.saswat.mynoteapplication.adapter;

import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.saswat.mynoteapplication.R;
import com.saswat.mynoteapplication.noteactivity.DetailActivity;

import java.util.ArrayList;
import java.util.Random;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    ArrayList<String> title;
    ArrayList<String> contents;

    public CustomAdapter(ArrayList<String> title, ArrayList<String> contents) {
        this.title = title;
        this.contents = contents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_notes_view, parent, false);

        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.noteTitle.setText(title.get(position));
        holder.noteContents.setText(contents.get(position));
        final int code = getRandomColour();
        holder.mCardView.setCardBackgroundColor(holder.view.getResources().getColor(code));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetailActivity.class);
                intent.putExtra("title", title.get(position));
                intent.putExtra("content", contents.get(position));
                intent.putExtra("code", code);
                view.getContext().startActivity(intent);
            }
        });
    }


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
    public int getItemCount() {
        return title.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle;
        TextView noteContents;
        View view;
        CardView mCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.titles);
            noteContents = itemView.findViewById(R.id.content);
            mCardView = itemView.findViewById(R.id.card_note_view);
            view = itemView;

        }
    }
}
