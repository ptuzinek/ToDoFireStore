package com.example.todofirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.todofirestore.Model.ToDo;
import com.example.todofirestore.data.ToDoAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ToDoAdapter toDoAdapter;
    private List<ToDo> toDoList;
    private FloatingActionButton fab;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private LayoutInflater inflater;
    private RecyclerView recyclerView;
    private EditText titleEditText, toDoEditText;
    private Button saveButton;
    public static final String KEY_TITLE = "title";
    public static final String KEY_TODO = "to do";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference toDoTodayPath = db.collection("ToDo").document("Today");
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inflater = LayoutInflater.from(this);

        recyclerView = findViewById(R.id.recyclerView2);
        fab = findViewById(R.id.floatingActionButton);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toDoList = new ArrayList<>();
        getToDoList();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder = new AlertDialog.Builder(MainActivity.this);

                View popup = inflater.inflate(R.layout.popup, null, false);

                titleEditText = popup.findViewById(R.id.title_popup);
                toDoEditText = popup.findViewById(R.id.to_do_popup);
                saveButton = popup.findViewById(R.id.save_button_popup);

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String title = titleEditText.getText().toString().trim();
                        String toDo = toDoEditText.getText().toString().trim();

                        LocalDateTime now = LocalDateTime.now();
                        String day = String.valueOf(now.getDayOfWeek());
                        String hours = String.valueOf(now.getHour());
                        String minutes = String.valueOf(now.getMinute());
                        String seconds = String.valueOf(now.getSecond());

                        id = day + " " +  hours + ":" + minutes + ":" + seconds;

                        Map<String, Object> data = new HashMap<>();
                        data.put("date", id);
                        data.put(KEY_TITLE, title);
                        data.put(KEY_TODO, toDo);

                        db.collection("ToDo").document(id).set(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this, "Added ToDo", Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
                            }
                        });

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                startActivity(new Intent(MainActivity.this, MainActivity.class));
                                finish();
                            }
                        },1000);
                    }
                });
                builder.setView(popup);
                dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void getToDoList() {
        toDoList.clear();
        db.collection("ToDo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot doc : task.getResult()) {

                            String id = doc.getString("date");
                            String title = doc.getString(KEY_TITLE);
                            String toDo = doc.getString(KEY_TODO);

                            ToDo toDoInst = new ToDo(id, title, toDo);
                            toDoList.add(toDoInst);
                        }
                        toDoAdapter = new ToDoAdapter(MainActivity.this, toDoList);

                        recyclerView.setAdapter(toDoAdapter);
                        toDoAdapter.notifyDataSetChanged();
                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                        itemTouchHelper.attachToRecyclerView(recyclerView);
                    }
                });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("DELETE")) {
            deleteToDos(item.getOrder());
            Toast.makeText(MainActivity.this, "deleted",Toast.LENGTH_LONG).show();
        }

        return super.onContextItemSelected(item);
    }

    private void deleteToDos(int index) {
        db.collection("ToDo")
                .document(toDoList.get(index).getId())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getToDoList();
            }
        });

    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN
            | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            closeContextMenu();

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(toDoList, fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            deleteToDos(viewHolder.getAdapterPosition());
            toDoAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
        }
    };
}