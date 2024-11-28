package tdelivery.mr_irmag.menu_service.eventListener;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import nl.altindag.log.LogCaptor;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.MongoDBContainer;
import tdelivery.mr_irmag.menu_service.domain.Entity.Product;
import tdelivery.mr_irmag.menu_service.service.JsonService;
import tdelivery.mr_irmag.menu_service.service.ProductService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ReplenishDBTest {

    @Mock
    private JsonService jsonService;
    @Mock
    private ProductService productService;
    @Mock
    private MongoClient mongoClient;
    @Mock
    private MongoDatabase mongoDatabase;
    @Mock
    private MongoCollection<Document> mongoCollection;

    @InjectMocks
    private ReplenishDB replenishDB;

    @BeforeEach
    void setUp() {
        replenishDB = new ReplenishDB(jsonService, productService, mongoClient);
        ReflectionTestUtils.setField(replenishDB, "hardeesPath", "/path/to/mock.json");
    }

    @Test
    void initDB_CollectionDoesNotExist_ShouldCreateCollection() {
        // Arrange
        when(mongoClient.getDatabase("menu-service")).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection("menu-service-collection")).thenReturn(null);

        // Act
        replenishDB.initDB();

        // Assert
        verify(mongoDatabase).createCollection("menu-service-collection");
    }

    @Test
    void initDB_CollectionAlreadyExists_ShouldNotCreateCollection() {
        // Arrange
        when(mongoClient.getDatabase("menu-service")).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection("menu-service-collection")).thenReturn(mongoCollection);

        // Act
        replenishDB.initDB();

        // Assert
        verify(mongoDatabase, never()).createCollection("menu-service-collection");
    }

    @Test
    void initDB_MongoDBConnectionFails_ShouldRetryFiveTimes() {
        LogCaptor logCaptor = LogCaptor.forClass(ReplenishDB.class);
        // Arrange
        when(mongoClient.getDatabase("menu-service")).thenThrow(new RuntimeException("Connection error"));

        // Act
        replenishDB.initDB();

        // Assert
        verify(mongoClient, times(5)).getDatabase("menu-service");
        assertTrue(logCaptor.getErrorLogs().stream()
                .anyMatch(log -> log.contains("Failed to connect to MongoDB. Retrying...")));
    }

    @Test
    void addDataToDb_ValidData_ShouldSaveAllProducts() {
        // Arrange
        List<Product> mockProductList = List.of(new Product("Burger"), new Product("Fries"));
        when(jsonService.fromJsonToPOJO("/path/to/mock.json")).thenReturn(mockProductList);

        // Act
        replenishDB.addDataToDb();

        // Assert
        verify(productService, times(2)).saveProduct(any(Product.class));
    }

    @Test
    void addDataToDb_ExceptionThrown_ShouldLogError() {
        // Arrange
        when(jsonService.fromJsonToPOJO("/path/to/mock.json")).thenThrow(new RuntimeException("Mock exception"));

        // Act
        replenishDB.addDataToDb();

        // Assert
        verify(productService, never()).saveProduct(any());
    }

    @Test
    void initDB_CollectionDoesNotExist_ShouldLogCreationMessage() {
        // Arrange
        LogCaptor logCaptor = LogCaptor.forClass(ReplenishDB.class);
        when(mongoClient.getDatabase("menu-service")).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection("menu-service-collection")).thenReturn(null);

        // Act
        replenishDB.initDB();

        // Assert
        verify(mongoDatabase).createCollection("menu-service-collection");
        assertTrue(logCaptor.getInfoLogs().stream()
                .anyMatch(log -> log.contains("Collection menu-service-collection created")));
    }


}

