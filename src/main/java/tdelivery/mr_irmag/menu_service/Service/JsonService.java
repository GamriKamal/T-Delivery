package tdelivery.mr_irmag.menu_service.Service;

import com.google.gson.reflect.TypeToken;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import tdelivery.mr_irmag.menu_service.Domain.Entity.Product;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

@Service
@Log4j2
public class JsonService {
    private Gson gson;

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public List<Product> fromJsonToPOJO(String filePath) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
             InputStreamReader reader = new InputStreamReader(inputStream)) {

            if (inputStream == null) {
                log.error("File not found: {}", filePath);
                return Collections.emptyList();
            }

            Type productListType = new TypeToken<List<Product>>(){}.getType();
            return gson.fromJson(reader, productListType);
        } catch (Exception e) {
            log.error("Failed to parse JSON file: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}