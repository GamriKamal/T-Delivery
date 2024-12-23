package tdelivery.mr_irmag.menu_service.eventListener;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tdelivery.mr_irmag.menu_service.service.JsonService;
import tdelivery.mr_irmag.menu_service.service.ProductService;

@Service
@Log4j2
public class ReplenishDB {

    public static final String RESET = "\033[0m";
    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m";
    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";
    private final JsonService jsonService;
    private final ProductService productService;
    private final MongoClient mongoClient;
    @Value("${products.hardees.path}")
    private String hardeesPath;
    @Autowired
    public ReplenishDB(JsonService jsonService, ProductService productService, MongoClient mongoClient) {
        this.jsonService = jsonService;
        this.productService = productService;
        this.mongoClient = mongoClient;
    }

    @PostConstruct
    public void initDB() {
        log.info("{}Started init db script... {}", GREEN_BOLD_BRIGHT, RESET);
        String databaseName = "menu-service";
        String collectionName = "menu-service-collection";
        int retries = 5;

        while (retries > 0) {
            try {
                MongoDatabase database = mongoClient.getDatabase(databaseName);
                MongoCollection<Document> collection = database.getCollection(collectionName);
                if (collection == null) {
                    database.createCollection(collectionName);
                    log.info(GREEN_BOLD_BRIGHT + "Collection " + collectionName + " created." + RESET);
                } else {
                    log.warn(YELLOW_BOLD_BRIGHT + "Collection " + collectionName + " already exists." + RESET);
                }
                break;
            } catch (Exception e) {
                log.error("Failed to connect to MongoDB. Retrying...", e);
                retries--;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        addDataToDb();
    }

    public void addDataToDb() {
        log.info("{}Started adding data to db... {}", GREEN_BOLD_BRIGHT, RESET);
        try {
            var productList = jsonService.fromJsonToPOJO(hardeesPath);
            productList.forEach(item -> productService.saveProduct(item));

        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
    }
}
