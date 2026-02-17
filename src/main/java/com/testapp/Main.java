package com.testapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser; // Importante para elegir archivos
import javafx.stage.Stage;
import java.io.File;
import java.util.*;

public class Main extends Application {

    private List<Tema> temas = new ArrayList<>();
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
        mostrarPantallaInicial();
    }

    // =========================
    // PANTALLA DE CARGA DE ARCHIVO
    // =========================
    private void mostrarPantallaInicial() {
        VBox root = new VBox(20);
        root.setStyle("-fx-padding: 30; -fx-alignment: center;");

        Label lbl = new Label("Bienvenido al Test de Servicios");
        lbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button btnCargar = new Button("Seleccionar Archivo XML de Preguntas");
        btnCargar.setStyle("-fx-padding: 10 20;");

        btnCargar.setOnAction(e -> seleccionarYCargarXML());

        root.getChildren().addAll(lbl, btnCargar);

        Scene scene = new Scene(root, 400, 200);
        primaryStage.setTitle("Cargar Test - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void seleccionarYCargarXML() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir archivo de preguntas XML");

        // Definición de ruta por defecto
        File rutaDefault = new File(System.getProperty("user.dir"));

        // Se verifica que la ruta existe
        if(rutaDefault.exists()){
            fileChooser.setInitialDirectory(rutaDefault);
        }

        // Filtro para que solo se vean archivos XML
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos XML", "*.xml")
        );

        File seleccionado = fileChooser.showOpenDialog(primaryStage);

        if (seleccionado != null) {
            try {
                temas = XMLReader.cargarTemasDesdeXML(seleccionado);

                if (temas.isEmpty()) {
                    mostrarError("El archivo no contiene temas válidos.");
                } else {
                    mostrarSeleccionTema();
                }
            } catch (Exception ex) {
                mostrarError("Error al leer el archivo XML: " + ex.getMessage());
            }
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // =========================
    // SELECCIÓN DE TEMA
    // =========================
    private void mostrarSeleccionTema() {
        VBox root = new VBox(15);
        root.setStyle("-fx-padding: 20");

        Label lbl = new Label("Seleccione un tema para estudiar:");
        root.getChildren().add(lbl);

        // Botón para cambiar de archivo si el usuario se equivoca
        Button btnCambiarArchivo = new Button("<- Cambiar de archivo XML");
        btnCambiarArchivo.setOnAction(e -> mostrarPantallaInicial());
        root.getChildren().add(btnCambiarArchivo);
        root.getChildren().add(new Separator());

        temas.stream()
                .sorted(Comparator.comparing(Tema::getId))
                .forEach(tema -> {
                    Button btn = new Button("Tema " + tema.getId());
                    btn.setMaxWidth(Double.MAX_VALUE); // Botones anchos
                    btn.setOnAction(e -> iniciarTema(tema));
                    root.getChildren().add(btn);
                });

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setTitle("Seleccionar Tema - Test Servicios");
        primaryStage.setScene(scene);
    }
    private void iniciarTema(Tema temaSeleccionado) {
        preguntas = new ArrayList<>(temaSeleccionado.getPreguntas());

        Collections.shuffle(preguntas); // Mezclar automáticamente
        indiceActual = 0;
        puntuacion = 0;
        respuestasUsuario.clear();
        preguntasFalladas.clear();
        btnRepasar.setDisable(true);

        mostrarPregunta();
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
