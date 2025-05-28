package com.zxcjabka;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Загружаем XML файл
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File("data.xml"));

            // Выполняем задания
            task1(doc);
            saveDocument(doc, "output-1.xml");

            doc = builder.parse(new File("data.xml")); // Загружаем оригинал снова
            task2(doc);
            saveDocument(doc, "output-2.xml");

            doc = builder.parse(new File("data.xml"));
            Document task3Doc = task3(doc);
            saveDocument(task3Doc, "output-3.xml");

            doc = builder.parse(new File("data.xml"));
            Document task4Doc = task4(doc);
            saveDocument(task4Doc, "output-4.xml");

            doc = builder.parse(new File("data.xml"));
            task5(doc);
            saveDocument(doc, "output-5.xml");

            System.out.println("Все задания выполнены!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Задание 1
    private static void task1(Document doc) {
        // 1.1 Удалить узлы Procedure и Function без дочерних элементов
        NodeList procedures = doc.getElementsByTagName("Procedure");
        NodeList functions = doc.getElementsByTagName("Function");

        removeEmptyNodes(procedures);
        removeEmptyNodes(functions);

        // 1.2 Удалить все ПУСТЫЕ атрибуты java_package
        removeEmptyAttributes(doc, "java_package");
    }

    // Задание 2
    private static void task2(Document doc) {
        NodeList nodes = doc.getElementsByTagName("*");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            if (element.hasAttribute("target_type") &&
                    element.getAttribute("target_type").isEmpty()) {
                element.setAttribute("target_type", "UNKNOWN");
            }
        }
    }

    // Задание 3
    private static Document task3(Document originalDoc) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document newDoc = builder.newDocument();

        Element root = newDoc.createElement("root");
        newDoc.appendChild(root);

        Element functionsElem = newDoc.createElement("Functions");
        Element proceduresElem = newDoc.createElement("Procedures");
        root.appendChild(functionsElem);
        root.appendChild(proceduresElem);

        // Находим дубликаты Function
        Map<String, List<Element>> functionMap = new HashMap<>();
        NodeList functions = originalDoc.getElementsByTagName("Function");
        collectDuplicates(functions, functionMap, "Name");

        // Добавляем дубликаты в новый документ
        addDuplicatesToElement(newDoc, functionsElem, functionMap);

        // Находим дубликаты Procedure
        Map<String, List<Element>> procedureMap = new HashMap<>();
        NodeList procedures = originalDoc.getElementsByTagName("Procedure");
        collectDuplicates(procedures, procedureMap, "Name");

        // Добавляем дубликаты в новый документ
        addDuplicatesToElement(newDoc, proceduresElem, procedureMap);

        return newDoc;
    }

    // Задание 4
    private static Document task4(Document originalDoc) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document newDoc = builder.newDocument();

        Element objectsElem = newDoc.createElement("Objects");
        newDoc.appendChild(objectsElem);

        // Собираем Object с четными ID и сортируем
        List<Element> evenObjects = new ArrayList<>();
        NodeList objects = originalDoc.getElementsByTagName("Object");

        for (int i = 0; i < objects.getLength(); i++) {
            Element object = (Element) objects.item(i);
            int id = Integer.parseInt(object.getAttribute("ID"));
            if (id % 2 == 0) {
                evenObjects.add(object);
            }
        }

        // Сортируем по ID
        evenObjects.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getAttribute("ID"))));

        // Импортируем в новый документ
        for (Element object : evenObjects) {
            Node imported = newDoc.importNode(object, true);
            objectsElem.appendChild(imported);
        }

        return newDoc;
    }

    // Задание 5
    private static void task5(Document doc) {
        // Собираем все Param и группируем по Name
        Map<String, List<Element>> paramMap = new HashMap<>();
        NodeList params = doc.getElementsByTagName("Param");

        for (int i = 0; i < params.getLength(); i++) {
            Element param = (Element) params.item(i);
            String name = param.getAttribute("Name");
            paramMap.computeIfAbsent(name, k -> new ArrayList<>()).add(param);
        }

        // Для повторяющихся Param изменяем Name
        for (List<Element> paramList : paramMap.values()) {
            if (paramList.size() > 1) {
                for (Element param : paramList) {
                    String newName = param.getAttribute("Name") + param.getAttribute("pos");
                    param.setAttribute("Name", newName);
                }
            }
        }
    }

    // ===== Вспомогательные методы =====

    private static void removeEmptyNodes(NodeList nodes) {
        for (int i = nodes.getLength() - 1; i >= 0; i--) {
            Node node = nodes.item(i);
            if (!node.hasChildNodes()) {
                node.getParentNode().removeChild(node);
            }
        }
    }

    private static void removeEmptyAttributes(Document doc, String attrName) {
        NodeList nodes = doc.getElementsByTagName("*");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            if (element.hasAttribute(attrName) && element.getAttribute(attrName).isEmpty()) {
                element.removeAttribute(attrName);
            }
        }
    }

    private static void collectDuplicates(NodeList nodes, Map<String, List<Element>> map, String attrName) {
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            String key = element.getAttribute(attrName);
            if (!key.isEmpty()) {
                map.computeIfAbsent(key, k -> new ArrayList<>()).add(element);
            }
        }
    }

    private static void addDuplicatesToElement(Document newDoc, Element parent, Map<String, List<Element>> map) {
        for (List<Element> elements : map.values()) {
            if (elements.size() > 1) {
                for (Element element : elements) {
                    Node imported = newDoc.importNode(element, true);
                    parent.appendChild(imported);
                }
            }
        }
    }

    private static void saveDocument(Document doc, String filename) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filename));
        transformer.transform(source, result);
    }
}