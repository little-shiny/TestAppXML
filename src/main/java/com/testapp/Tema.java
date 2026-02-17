package com.testapp;

import java.util.ArrayList;
import java.util.List;

public class Tema {
    private String id;
    private List<Question> preguntas;

    public Tema(String id){
        this.id = id;
        this.preguntas = new ArrayList<>();
    }

    public String getId(){return id;}

    public List<Question> getPreguntas(){return preguntas;}

    public void addPregunta(Question p){
        preguntas.add(p);
    }


}
