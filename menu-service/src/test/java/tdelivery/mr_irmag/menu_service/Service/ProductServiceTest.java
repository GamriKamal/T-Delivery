package tdelivery.mr_irmag.menu_service.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import tdelivery.mr_irmag.menu_service.Domain.DTO.ProductResponse;
import tdelivery.mr_irmag.menu_service.Domain.Entity.Product;
import tdelivery.mr_irmag.menu_service.Exception.ProductAlreadyExistsException;
import tdelivery.mr_irmag.menu_service.Exception.ProductNotFoundException;
import tdelivery.mr_irmag.menu_service.Repository.ProductRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for getAllProducts method
    @Test
    void getAllProducts_PositiveCase_ShouldReturnListOfProducts() {
        // Arrange
        Product product1 = new Product("1", "Product 1", 100.0, "Description 1", "url");
        Product product2 = new Product("2", "Product 2", 150.0, "Description 2", "url");
        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        // Act
        List<ProductResponse> result = productService.getAllProducts();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Product 1", result.get(0).getName());
        assertEquals(100.0, result.get(0).getPrice());
    }

    @Test
    void getAllProducts_NegativeCase_ShouldThrowProductNotFoundException() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.getAllProducts();
        });

        assertEquals("No products were found.", exception.getMessage());
    }

    @Test
    void getProductById_PositiveCase_ShouldReturnProduct() {
        // Arrange
        String id = "1";
        Product product = new Product(id, "Product 1", 100.0, "Description 1", "url");
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        // Act
        Product result = productService.getProductById(id);

        // Assert
        assertEquals("Product 1", result.getName());
    }

    @Test
    void getProductById_NegativeCase_ShouldThrowProductNotFoundException() {
        // Arrange
        String id = "2";
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(id);
        });

        assertEquals("Error! The product was not found with an ID: " + id, exception.getMessage());
    }

    @Test
    void saveProduct_PositiveCase_ShouldReturnSavedProduct() {
        // Arrange
        Product product = new Product(null, "Product 1", 100.0, "Description 1", "url");
        when(productRepository.save(product)).thenReturn(product);

        // Act
        Product result = productService.saveProduct(product);

        // Assert
        assertEquals("Product 1", result.getName());
    }

    @Test
    void saveProduct_NegativeCase_ExistingProduct_ShouldThrowProductAlreadyExistsException() {
        // Arrange
        Product product = new Product("1", "Product 1", 100.0, "Description 1", "url");
        when(productRepository.existsById(product.getId())).thenReturn(true);

        // Act & Assert
        ProductAlreadyExistsException exception = assertThrows(ProductAlreadyExistsException.class, () -> {
            productService.saveProduct(product);
        });

        assertEquals("A product with this ID already exists: " + product.getId(), exception.getMessage());
    }

    // Test for deleteProductById method
    @Test
    void deleteProductById_PositiveCase_ShouldReturnTrue() {
        // Arrange
        String id = "1";
        when(productRepository.existsById(id)).thenReturn(true);

        // Act
        boolean result = productService.deleteProductById(id);

        // Assert
        assertTrue(result);
        verify(productRepository).deleteById(id);
    }

    @Test
    void deleteProductById_NegativeCase_ProductNotFound_ShouldThrowProductNotFoundException() {
        // Arrange
        String id = "2";
        when(productRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.deleteProductById(id);
        });

        assertEquals("Error! The product with this ID was not found: " + id, exception.getMessage());
    }
}
