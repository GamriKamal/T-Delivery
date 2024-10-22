package tdelivery.mr_irmag.menu_service.Service;

import com.opencsv.CSVReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import tdelivery.mr_irmag.menu_service.Domain.Entity.Product;
import tdelivery.mr_irmag.menu_service.Service.CSVService;
import tdelivery.mr_irmag.menu_service.Service.ProductService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class CSVServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private CSVService csvService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void parseCSV_ValidCSV_ReturnsTrue() throws Exception {
        // Arrange
        String csvContent = "name,price,description,imageUrl\n" +
                "Product1,10.99,Description1,http://example.com/image1.jpg\n" +
                "Product2,20.49,Description2,http://example.com/image2.jpg";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        when(file.getInputStream()).thenReturn(inputStream);

        // Act
        boolean result = csvService.parseCSV(file);

        // Assert
        assertTrue(result);
        verify(productService, times(2)).saveProduct(any(Product.class));
    }

    @Test
    void parseCSV_InvalidCSV_ThrowsException() throws Exception {
        // Arrange
        String csvContent = "name,price,description,imageUrl\n" +
                "Product1,invalid_price,Description1,http://example.com/image1.jpg";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        when(file.getInputStream()).thenReturn(inputStream);

        // Act & Assert
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            csvService.parseCSV(file);
        });

        verify(productService, never()).saveProduct(any(Product.class));
    }
}
