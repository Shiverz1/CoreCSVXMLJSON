import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        // Колонки в CSV файле
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        // Имя считываемого файла
        String fileName = "data.csv";
        // Список сотрудников
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        String fileNameJson = "data.json";
        writeString(json, fileNameJson);
        // имя считываемого файла
        String fileXml = "data.xml";
        List<Employee> list2 = parseXML(fileXml);
        json = listToJson(list2);
        fileNameJson = "data2.json";
        writeString(json, fileNameJson);

      /*  String json2 = readString("new_data.json");
        List<Employee> list2 = jsonToList(json2);
        System.out.println(list2);
        */
    }


    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> people = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            // тип
            strategy.setType(Employee.class);
            // тип колонок
            strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            people = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return people;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        // тип списка
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    // запись полученного JSON в файл
    public static void writeString(String json, String fileName) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(json);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<Employee> parseXML(String fileXml) {
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(new File(fileXml));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        // Получаем корневой узел
        Node rootNode = doc.getDocumentElement();
//        Извлекаем список узлов
        NodeList nodeList = rootNode.getChildNodes();

        List<Employee> employeeList = new ArrayList<>();
//      Столбцы
        long id = 0;
        String firstName = "";
        String lastName = "";
        String country = "";
        int age = 0;

//      Пробегаемся по списку узлов
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (!nodeList.item(i).getNodeName().equals("employee")) {
                continue;
            }

            NodeList employeeChilds = nodeList.item(i).getChildNodes();
            for (int j = 0; j < employeeChilds.getLength(); j++) {
                if (employeeChilds.item(j).getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                switch (employeeChilds.item(j).getNodeName()) {
                    case "id": {
                        id = Long.valueOf(employeeChilds.item(j).getTextContent());
                        break;
                    }
                    case "firstName": {
                        firstName = employeeChilds.item(j).getTextContent();
                        break;
                    }
                    case "lastName": {
                        lastName = employeeChilds.item(j).getTextContent();
                        break;
                    }
                    case "country": {
                        country = employeeChilds.item(j).getTextContent();
                        break;
                    }
                    case "age": {
                        age = Integer.valueOf(employeeChilds.item(j).getTextContent());
                        break;
                    }
                }
            }
            Employee employee = new Employee(id, firstName, lastName, country, age);
            employeeList.add(employee);
        }

        return employeeList;
    }
}



