package tdelivery.mr_irmag.menu_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import tdelivery.mr_irmag.menu_service.domain.DTO.ProductResponse;
import tdelivery.mr_irmag.menu_service.domain.Entity.Product;
import tdelivery.mr_irmag.menu_service.service.CSVService;
import tdelivery.mr_irmag.menu_service.service.ProductService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private CSVService csvService;

    @Test
    void getMenuItems_shouldReturnListOfProducts() throws Exception {
        // Arrange
        List<ProductResponse> products = List.of(
                ProductResponse.builder()
                        .name("Product 1")
                        .price(100.0)
                        .description("Description 1")
                        .build(),
                ProductResponse.builder()
                        .name("Product 2")
                        .price(200.0)
                        .description("Description 2")
                        .build()
        );

        Mockito.when(productService.getAllProducts()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/menu/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[0].price").value(100.0))
                .andExpect(jsonPath("$[1].name").value("Product 2"))
                .andExpect(jsonPath("$[1].price").value(200.0));
    }

    @Test
    void createProduct_shouldCreateAndReturnProduct() throws Exception {
        // Arrange
        Product product = Product.builder()
                .id("1")
                .name("Product 1")
                .price(100.0)
                .description("Description 1")
                .imageUrl("http://example.com/image.jpg")
                .build();

        Mockito.when(productService.saveProduct(Mockito.any(Product.class))).thenReturn(product);

        // Act & Assert
        mockMvc.perform(post("/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "name": "Product 1",
                        "price": 100.0,
                        "description": "Description 1",
                        "imageUrl": "http://example.com/image.jpg"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Product 1"))
                .andExpect(jsonPath("$.price").value(100.0))
                .andExpect(jsonPath("$.description").value("Description 1"))
                .andExpect(jsonPath("$.imageUrl").value("http://example.com/image.jpg"));
    }

    @Test
    void uploadCSVFile_shouldReturnTrueWhenUploadSuccessful() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "products.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "name,price,description\nProduct 1,100.0,Description 1".getBytes()
        );

        Mockito.when(csvService.parseCSV(Mockito.any(MultipartFile.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(multipart("/menu/upload-csv-file").file(file))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));
    }
}
