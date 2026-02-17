package com.testpsp;
import java.util.List;

public class Question {

    private String enunciado;
    private List<String> opciones;
    private List<Integer> respuestasCorrectas;
    private boolean multiple;

    public Question(String enunciado, List<String> opciones,
                    List<Integer> respuestasCorrectas, boolean multiple) {
        this.enunciado = enunciado;
        this.opciones = opciones;
        this.respuestasCorrectas = respuestasCorrectas;
        this.multiple = multiple;
    }

    public String getEnunciado() { return enunciado; }
    public List<String> getOpciones() { return opciones; }
    public List<Integer> getRespuestasCorrectas() { return respuestasCorrectas; }
    public boolean isMultiple() { return multiple; }
}
