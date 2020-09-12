package com.example.todofirestore.Model;

public class ToDo {

    private String id;
    private String title;
    private String toDo;

    public ToDo() {
    }

    public ToDo(String id, String title, String toDo) {
        this.id = id;
        this.title = title;
        this.toDo = toDo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getToDo() {
        return toDo;
    }

    public void setToDo(String toDo) {
        this.toDo = toDo;
    }
}
