package task1_CSVtoJSON;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        //Task 1
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> staff = parseCSV(Employee.class, columnMapping, "data.csv");
        String employeesJsonString1 = listToJson(staff);
        writeString(employeesJsonString1, "data.json");

        //Task 2
        List<Employee> staff2 = parseXML("data.xml");
        String employeesJsonString2 = listToJson(staff2);
        writeString(employeesJsonString2, "data2.json");

        //Task 3
        List<Employee> employees = parseJSON("data.json");
        for (Employee emp : employees) {
            System.out.println(emp);
        }
    }

    private static <T> List<T> parseCSV(Class<T> type, String[] columnMapping, String inputFileName) {
        List<T> tList = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(inputFileName))) {
            ColumnPositionMappingStrategy<T> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(type);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<T> csv = new CsvToBeanBuilder<T>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();

            tList = csv.parse();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return tList;
    }

    private static <T> String listToJson(List<T> tList) {
        Type listType = new TypeToken<List<T>>() {
        }.getType();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.toJson(tList, listType);
    }

    private static void writeString(String string, String outputFileName) {
        try (FileWriter writer = new FileWriter(outputFileName)) {
            writer.write(string);
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static List<Employee> parseXML(String xmlPath) {
        List<Employee> empList = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(xmlPath));

            Node root = doc.getDocumentElement();
            readXML(root, empList);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return empList;
    }

    private static void readXML(Node node, List<Employee> empList) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node1 = nodeList.item(i);
            if (Node.ELEMENT_NODE == node1.getNodeType()) {
                Element elem = (Element) node1;
                if ("employee".equals(node1.getNodeName())) {
                    long id = Long.parseLong(elem.getElementsByTagName("id").item(0).getTextContent());
                    String firstName = elem.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = elem.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = elem.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(elem.getElementsByTagName("age").item(0).getTextContent());
                    empList.add(new Employee(id, firstName, lastName, country, age));
                }
                readXML(node1, empList);
            }
        }
    }

    private static List<Employee> parseJSON(String jsonPath) {
        List<Employee> employees = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(jsonPath))) {
            StringBuilder jsonString = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                jsonString.append(str);
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Employee>>() {
            }.getType();
            employees = gson.fromJson(String.valueOf(jsonString), listType);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return employees;
    }
}
