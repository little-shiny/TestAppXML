package com.testpsp;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
public class XMLReader {

    // Ahora el método recibe un objeto File por parámetro
    public static List<Tema> cargarTemasDesdeXML(File archivo) {
        Map<String, Tema> mapaTemas = new HashMap<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Usamos el archivo que nos pasan desde el selector
            Document doc = builder.parse(archivo);

            NodeList listaPreguntas = doc.getElementsByTagName("pregunta");

            for (int i = 0; i < listaPreguntas.getLength(); i++) {
                Element pElement = (Element) listaPreguntas.item(i);

                String temaId = pElement.getAttribute("tema");
                boolean multiple = Boolean.parseBoolean(pElement.getAttribute("multiple"));

                String enunciado = pElement
                        .getElementsByTagName("enunciado")
                        .item(0)
                        .getTextContent();

                NodeList opcionesXML = pElement.getElementsByTagName("opcion");
                List<String> opciones = new ArrayList<>();

                for (int j = 0; j < opcionesXML.getLength(); j++) {
                    opciones.add(opcionesXML.item(j).getTextContent());
                }

                NodeList respuestasXML = pElement.getElementsByTagName("respuesta");
                List<Integer> respuestasCorrectas = new ArrayList<>();

                for (int j = 0; j < respuestasXML.getLength(); j++) {
                    respuestasCorrectas.add(
                            Integer.parseInt(respuestasXML.item(j).getTextContent())
                    );
                }

                Question pregunta = new Question(
                        enunciado,
                        opciones,
                        respuestasCorrectas,
                        multiple,
                        temaId
                );

                mapaTemas.putIfAbsent(temaId, new Tema(temaId));
                mapaTemas.get(temaId).addPregunta(pregunta);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>(mapaTemas.values());
    }
}