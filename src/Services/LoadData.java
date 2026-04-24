package Services;

import Classes.Course;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

public class LoadData {

    public List<Course> loadCourses(String path) {
        try {
            Gson gson = new Gson();

            Type type = new TypeToken<List<Course>>() {}.getType();

            return gson.fromJson(new FileReader(path), type);

        } catch (Exception e) {
            throw new RuntimeException("Error loading JSON", e);
        }
    }
}