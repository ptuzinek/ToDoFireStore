package com.example.todofirestore.data;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todofirestore.Model.ToDo;
import com.example.todofirestore.R;
import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    LayoutInflater inflater;
    Context context;
    List<ToDo> list;

    public ToDoAdapter(Context context, List<ToDo> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.to_do_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.title.setText(list.get(position).getTitle());
        holder.toDo.setText(list.get(position).getToDo());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnClickListener {
        TextView title;
        TextView toDo;
        CardView cardView;
        AdapterView.OnItemSelectedListener onItemSelectedListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_row);
            toDo = itemView.findViewById(R.id.toDo_row);
            cardView = itemView.findViewById(R.id.card_view);
            cardView.setOnCreateContextMenuListener(this);


        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Choose the action : ");
            contextMenu.add(0,0,getAdapterPosition(), "DELETE");


        }

        @Override
        public void onClick(View view) {

        }
    }
}