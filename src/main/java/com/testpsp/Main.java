package com.testpsp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.*;

public class Main extends Application {

    private Map<String, List<Question>> temas = new HashMap<>();
    private List<Question> preguntas = new ArrayList<>();
    private List<Question> preguntasFalladas = new ArrayList<>();
    private Map<Question, List<Integer>> respuestasUsuario = new HashMap<>();

    private int indiceActual = 0;
    private int puntuacion = 0;

    private Label lblPregunta = new Label();
    private VBox opcionesBox = new VBox(5);

    private Button btnSiguiente = new Button("Siguiente");
    private Button btnRepasar = new Button("Repasar fallos");

    private Button btnMenu = new Button("Volver al menú");

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // Cargar preguntas desde XML
        cargarPreguntasDesdeXML("preguntas.xml");

        // Mostrar pantalla de selección de tema
        mostrarSeleccionTema();
    }

    // =========================
    // SELECCIÓN DE TEMA
    // =========================
    private void mostrarSeleccionTema() {
        VBox root = new VBox(15);
        root.setStyle("-fx-padding: 20");

        Label lbl = new Label("Seleccione un tema para estudiar:");
        root.getChildren().add(lbl);

        for (String tema : temas.keySet()) {
            Button btn = new Button(tema);
            btn.setOnAction(e -> iniciarTema(tema));
            root.getChildren().add(btn);
        }

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Seleccionar Tema - Test Servicios");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void iniciarTema(String temaSeleccionado) {
        preguntas = new ArrayList<>(temas.get(temaSeleccionado));
        Collections.shuffle(preguntas); // Mezclar automáticamente
        indiceActual = 0;
        puntuacion = 0;
        respuestasUsuario.clear();
        preguntasFalladas.clear();
        btnRepasar.setDisable(true);

        mostrarPregunta();
    }

    // =========================
    // CARGAR PREGUNTAS DESDE XML
    // =========================
    private void cargarPreguntasDesdeXML(String archivo) {
        temas.put("Tema 1 - Procesos y Planificación", new ArrayList<>());
        temas.put("Tema 2 - Hilos y Java Concurrency", new ArrayList<>());
        temas.put("Tema 3 - Redes y TCP/IP", new ArrayList<>());

        try {
            File fXmlFile = new File(archivo);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("pregunta");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    int temaNum = Integer.parseInt(eElement.getAttribute("tema"));
                    boolean multiple = Boolean.parseBoolean(eElement.getAttribute("multiple"));
                    String enunciado = eElement.getElementsByTagName("enunciado").item(0).getTextContent();

                    List<String> opciones = new ArrayList<>();
                    NodeList opcionesNodes = eElement.getElementsByTagName("opcion");
                    for (int i = 0; i < opcionesNodes.getLength(); i++) {
                        opciones.add(opcionesNodes.item(i).getTextContent());
                    }

                    List<Integer> respuestasCorrectas = new ArrayList<>();
                    NodeList respuestasNodes = eElement.getElementsByTagName("respuesta");
                    for (int i = 0; i < respuestasNodes.getLength(); i++) {
                        respuestasCorrectas.add(Integer.parseInt(respuestasNodes.item(i).getTextContent()));
                    }

                    Question q = new Question(enunciado, opciones, respuestasCorrectas, multiple);

                    switch (temaNum) {
                        case 1 -> temas.get("Tema 1 - Procesos y Planificación").add(q);
                        case 2 -> temas.get("Tema 2 - Hilos y Java Concurrency").add(q);
                        case 3 -> temas.get("Tema 3 - Redes y TCP/IP").add(q);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al cargar el archivo XML:\n" + e.getMessage());
            alert.showAndWait();
        }
    }

    // =========================
    // MOSTRAR PREGUNTA
    // =========================
    private void mostrarPregunta() {
        if (indiceActual >= preguntas.size()) {
            mostrarResultados();
            return;
        }

        Question q = preguntas.get(indiceActual);
        lblPregunta.setText("Pregunta " + (indiceActual + 1) + ": " + q.getEnunciado());
        opcionesBox.getChildren().clear();

        if (q.isMultiple()) {
            for (int i = 0; i < q.getOpciones().size(); i++) {
                CheckBox cb = new CheckBox(q.getOpciones().get(i));
                cb.setUserData(i);
                opcionesBox.getChildren().add(cb);
            }
        } else {
            ToggleGroup group = new ToggleGroup();
            for (int i = 0; i < q.getOpciones().size(); i++) {
                RadioButton rb = new RadioButton(q.getOpciones().get(i));
                rb.setUserData(i);
                rb.setToggleGroup(group);
                opcionesBox.getChildren().add(rb);
            }
        }

        VBox root = new VBox(15,
                lblPregunta,
                opcionesBox,
                btnSiguiente,
                btnRepasar,
                btnMenu
        );
        root.setStyle("-fx-padding: 20");

        btnRepasar.setDisable(preguntasFalladas.isEmpty());

        Scene scene = new Scene(root, 700, 500);
        primaryStage.setScene(scene);

        btnSiguiente.setOnAction(e -> revisarRespuesta());
        btnRepasar.setOnAction(e -> repasarFallos());

        btnMenu.setOnAction(e -> mostrarSeleccionTema());
    }

    // =========================
    // REVISAR RESPUESTA
    // =========================
    private void revisarRespuesta() {
        Question q = preguntas.get(indiceActual);
        List<Integer> seleccionadas = new ArrayList<>();

        for (var node : opcionesBox.getChildren()) {
            if (node instanceof CheckBox cb && cb.isSelected())
                seleccionadas.add((Integer) cb.getUserData());
            if (node instanceof RadioButton rb && rb.isSelected())
                seleccionadas.add((Integer) rb.getUserData());
        }

        respuestasUsuario.put(q, seleccionadas);

        boolean correcta = new HashSet<>(seleccionadas)
                .equals(new HashSet<>(q.getRespuestasCorrectas()));

        if (correcta) {
            puntuacion++;
        } else {
            preguntasFalladas.add(q);
        }

        // Mostrar corrección inmediata
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Corrección");
        alert.setHeaderText(correcta ? "¡Correcto!" : "Incorrecto");

        StringBuilder sb = new StringBuilder();

        sb.append("Tu respuesta: ");
        if (seleccionadas.isEmpty()) sb.append("No seleccionaste nada");
        else {
            for (int idx : seleccionadas) {
                if (idx >= 0 && idx < q.getOpciones().size()) {
                    sb.append(q.getOpciones().get(idx)).append("; ");
                }
            }
        }

        sb.append("\nRespuesta correcta: ");
        for (int idx : q.getRespuestasCorrectas()) {
            if (idx >= 0 && idx < q.getOpciones().size()) {
                sb.append(q.getOpciones().get(idx)).append("; ");
            }
        }

        alert.setContentText(sb.toString());
        alert.showAndWait();

        indiceActual++;
        mostrarPregunta();
    }

    // =========================
    // REPASAR FALLOS
    // =========================

    private void repasarFallos(){
        if(preguntasFalladas.isEmpty()){
            return;
        }

        preguntas = new ArrayList<>(preguntasFalladas);
        preguntasFalladas.clear();

        indiceActual = 0;
        puntuacion = 0;
        respuestasUsuario.clear();

        Collections.shuffle(preguntas);

        mostrarPregunta();
    }

    private void mostrarResultados() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Resultados");
        alert.setHeaderText("Test finalizado");
        alert.setContentText("Puntuación: " + puntuacion + " / " + preguntas.size());
        alert.showAndWait();

        btnRepasar.setDisable(preguntasFalladas.isEmpty());
    }

    public static void main(String[] args) {
        launch();
    }

}
