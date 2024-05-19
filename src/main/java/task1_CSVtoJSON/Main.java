package task1_CSVtoJSON;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> staff = parseCSV(Employee.class, columnMapping, "data.csv");

        String employeesJsonString = listToJson(staff);

        writeString(employeesJsonString, "data.json");
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
}
