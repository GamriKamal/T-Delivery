package tdelivery.mr_irmag.menu_service.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tdelivery.mr_irmag.menu_service.Domain.Entity.Product;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JsonServiceTest {

    @Mock
    private Gson gson;

    @InjectMocks
    private JsonService jsonService;

    @Test
    void fromJsonToPOJO_PositiveCase_ShouldReturnListOfProducts() {
        // Arrange
        MockitoAnnotations.openMocks(this);
        String filePath = "testProducts.json";
        InputStream mockInputStream = mock(InputStream.class);
        InputStreamReader mockReader = new InputStreamReader(mockInputStream);
        List<Product> expectedProducts = List.of(new Product("1", "product1", 10.0, "desc", "url"), new Product("2", "product1", 10.0, "desc", "url"));

        when(getClass().getClassLoader().getResourceAsStream(filePath)).thenReturn(mockInputStream);
        when(gson.fromJson(mockReader, new TypeToken<List<Product>>(){}.getType())).thenReturn(expectedProducts);

        // Act
        List<Product> actualProducts = jsonService.fromJsonToPOJO(filePath);

        // Assert
        assertEquals(expectedProducts, actualProducts);
    }

    @Test
    void fromJsonToPOJO_NegativeCase_ShouldReturnEmptyList() {
        // Arrange
        String filePath = "nonExistingFile.json";
        MockitoAnnotations.openMocks(this);

        when(getClass().getClassLoader().getResourceAsStream(filePath)).thenReturn(null);

        // Act
        List<Product> result = jsonService.fromJsonToPOJO(filePath);

        // Assert
        assertTrue(result.isEmpty());
        verify(gson, never()).fromJson(any(InputStreamReader.class), any());
    }

}


